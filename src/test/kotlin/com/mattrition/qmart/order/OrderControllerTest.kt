package com.mattrition.qmart.order

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.cart.CartItem
import com.mattrition.qmart.cart.CartItemRepository
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.notification.NotificationRepository
import com.mattrition.qmart.order.dto.CreateOrderRequestDto
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.orderitem.OrderItemRepository
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.util.UUID

class OrderControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/orders"
    }

    @Autowired lateinit var orderRepository: OrderRepository

    @Autowired lateinit var cartItemRepository: CartItemRepository

    @Autowired lateinit var notificationRepository: NotificationRepository

    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var orderService: OrderService

    private lateinit var listingMod: ItemListing
    private lateinit var listingAdmin: ItemListing

    private val guestId = UUID.randomUUID()

    /** Pre-fills a create order request with shipping information. */
    private fun genCreateOrder(
        buyerId: UUID? = null,
        guestSessionId: UUID? = null,
        guestEmail: String? = null,
        totalPaid: BigDecimal = BigDecimal.ZERO,
    ) = CreateOrderRequestDto(
        buyerId = buyerId,
        guestSessionId = guestSessionId,
        guestEmail = guestEmail,
        totalPaid = totalPaid,
        shippingFirstname = "Test",
        shippingLastname = "Last",
        shippingAddress1 = "123 street",
        shippingAddress2 = null,
        shippingCity = "New York",
        shippingState = "New York",
        shippingZip = "55555",
        shippingPhone = "555-555-5555",
    )

    @BeforeEach
    fun init() {
        val listings = super.initListings()

        listingMod = listings.first()
        listingAdmin = listings.last()

        // Add items to user's cart
        val cartItem1 =
            CartItem(userId = TestUsers.user.id!!, listingId = listingMod.id!!, quantity = 1)

        val cartItem2 =
            CartItem(userId = TestUsers.user.id!!, listingId = listingAdmin.id!!, quantity = 1)

        // Guest cart item
        val guestCartItem =
            CartItem(
                userId = null,
                guestSessionId = guestId,
                listingId = listingMod.id!!,
                quantity = 1,
            )

        cartItemRepository.saveAll(listOf(guestCartItem, cartItem1, cartItem2))
    }

    @Nested
    inner class CreateOrders {
        @Test
        fun `should return 403 forbidden when creating an order using mismatched token`() {
            val req = genCreateOrder(buyerId = TestUsers.user.id!!)

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.superadmin,
                body = req,
            ).andExpect(status().isForbidden)

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                .andExpect(status().isForbidden)
        }

        @Nested
        inner class IntegrityTests {
            private val userRequest =
                genCreateOrder(buyerId = TestUsers.user.id!!, totalPaid = BigDecimal("300.0"))

            @Test
            fun `should deduct balance from user`() {
                val prevBalance = TestUsers.user.balance
                prevBalance shouldBeGreaterThanOrEqualTo BigDecimal("300.0")

                mockRequest(
                    requestType = POST,
                    path = BASE_PATH,
                    token = TestTokens.user,
                    body = userRequest,
                ).andExpect(status().isCreated)

                val expectedBalance = prevBalance - BigDecimal("300.00")
                val user = userRepository.findById(TestUsers.user.id!!).get()
                user.balance shouldBe expectedBalance
            }

            @Test
            fun `should clear buyer's cart items`() {
                val prevItems = cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!)
                prevItems shouldHaveSize 2

                mockRequest(
                    requestType = POST,
                    path = BASE_PATH,
                    token = TestTokens.user,
                    body = userRequest,
                ).andExpect(status().isCreated)

                val userItems = cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!)
                userItems shouldHaveSize 0
            }

            @Test
            fun `should send notification to sellers`() {
                val prevNotifsMod = notificationRepository.findByUser(TestUsers.moderator.id!!)
                prevNotifsMod shouldHaveSize 0

                val prevNotifsAdmin = notificationRepository.findByUser(TestUsers.admin.id!!)
                prevNotifsAdmin shouldHaveSize 0

                mockRequest(
                    requestType = POST,
                    path = BASE_PATH,
                    token = TestTokens.user,
                    body = userRequest,
                ).andExpect(status().isCreated)

                val modNotifs = notificationRepository.findByUser(TestUsers.moderator.id!!)
                modNotifs shouldHaveSize 1

                val adminNotifs = notificationRepository.findByUser(TestUsers.admin.id!!)
                adminNotifs shouldHaveSize 1
            }

            @Test
            fun `should create order items based on items in cart`() {
                mockRequest(
                    requestType = POST,
                    path = BASE_PATH,
                    token = TestTokens.user,
                    body = userRequest,
                ).andExpect(status().isCreated)

                val userOrders = orderRepository.findOrdersByBuyerId(TestUsers.user.id!!)
                userOrders shouldHaveSize 1

                val userOrder = userOrders.first()
                val orderItems = orderItemRepository.findOrderItemsByOrderId(userOrder.id!!)
                orderItems shouldHaveSize 2
                orderItems.forOne { it.listingId shouldBe listingMod.id }
                orderItems.forOne { it.listingId shouldBe listingAdmin.id }
            }
        }
    }

    @Nested
    inner class GetOrders {
        @BeforeEach
        fun init() {
            val userRequest = genCreateOrder(buyerId = TestUsers.user.id!!)

            orderService.createOrder(userRequest)

            val guestRequest =
                genCreateOrder(guestSessionId = guestId, guestEmail = "email@test.com")

            orderService.createOrder(guestRequest)
        }

        @Test
        fun `should get orders by user`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.user,
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(1))
        }

        @Test
        fun `should get orders for seller with relevant items only`() {
            val result =
                mockRequest(
                    requestType = GET,
                    path = "$BASE_PATH/seller/${TestUsers.moderator.id}",
                    token = TestTokens.moderator,
                    params = mapOf("unfinished" to "true"),
                ).andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val orders = objectMapper.readValue<List<OrderDto>>(body)
            orders shouldHaveSize 2

            orders.forEach { order ->
                order.orderItems.forAll { it.sellerId shouldBe TestUsers.moderator.id }
            }
        }
    }
}
