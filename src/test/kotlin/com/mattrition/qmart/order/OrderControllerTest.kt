package com.mattrition.qmart.order

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.cart.CartItem
import com.mattrition.qmart.cart.CartItemRepository
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.user.BalanceService
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

class OrderControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/orders"
    }

    @Autowired lateinit var cartItemRepository: CartItemRepository

    @Autowired lateinit var orderRepository: OrderRepository

    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var balanceService: BalanceService

    private lateinit var sampleListing1: ItemListing
    private lateinit var sampleListing2: ItemListing

    @BeforeEach
    fun addItemListing() {
        sampleListing1 =
            itemListingRepository.save(
                ItemListing(
                    sellerId = TestUsers.superadmin.id!!,
                    title = "Sample Listing by superadmin",
                    price = BigDecimal(100),
                ),
            )

        sampleListing2 =
            itemListingRepository.save(
                ItemListing(
                    sellerId = TestUsers.superadmin.id!!,
                    title = "Second Sample Listing by superadmin",
                    price = BigDecimal(250),
                ),
            )
    }

    @AfterEach
    fun checkItemListing() {
        itemListingRepository.findItemListingsBySellerId(TestUsers.superadmin.id!!) shouldHaveSize 2
    }

    @Nested
    inner class CreateOrder {
        @Test
        fun `should prevent requests not belonging to buyer and return 400 forbidden`() {
            val sampleOrder =
                orderWithAddress(buyerId = TestUsers.user.id!!, totalPaid = BigDecimal(100))

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.admin,
                body = sampleOrder,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `creating an order with no cart items should return 403 bad request`() {
            val sampleOrder = orderWithAddress(TestUsers.user.id!!, BigDecimal(100))
            val userCartItems = cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!)
            userCartItems shouldHaveSize 0

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = sampleOrder,
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should return status 400 forbidden when user has insufficient funds`() {
            cartItemRepository.save(
                CartItem(
                    userId = TestUsers.user.id!!,
                    listingId = sampleListing1.id!!,
                    quantity = 500, // Expensive
                ),
            )

            val sampleOrder = orderWithAddress(TestUsers.user.id!!, BigDecimal(5000))
            TestUsers.user.balance shouldBeLessThan BigDecimal(5000)

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = sampleOrder,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `creating an order abides by all business rules`() {
            // Add an item to the cart
            cartItemRepository.saveAll(
                listOf(
                    CartItem(
                        userId = TestUsers.user.id!!,
                        listingId = sampleListing1.id!!,
                        quantity = 2,
                    ),
                    CartItem(
                        userId = TestUsers.user.id!!,
                        listingId = sampleListing2.id!!,
                        quantity = 1,
                    ),
                ),
            )

            // Add an item to a different user's cart
            cartItemRepository.save(
                CartItem(
                    userId = TestUsers.admin.id!!,
                    listingId = sampleListing1.id!!,
                    quantity = 1,
                ),
            )

            fun adminCartItems() = cartItemRepository.findCartItemsByUserId(TestUsers.admin.id!!)

            fun userCartItems() = cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!)

            fun getTestUser() = userRepository.findById(TestUsers.user.id!!).getOrNull().shouldNotBeNull()

            adminCartItems() shouldHaveSize 1
            userCartItems() shouldHaveSize 2

            // Ensure we have enough to buy the items
            var initialBalance = getTestUser().balance.shouldNotBeNull()
            if (initialBalance < BigDecimal(450)) {
                initialBalance = balanceService.setBalance(TestUsers.user.id!!, BigDecimal(1000))
            }

            val sampleOrder = orderWithAddress(TestUsers.user.id!!, BigDecimal(450))

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = sampleOrder,
            ).andExpect(status().isCreated)

            // Test business rules:
            // Cart items cleared for user only
            userCartItems() shouldHaveSize 0
            adminCartItems() shouldHaveSize 1

            // Money deducted
            val newBalance = initialBalance - sampleOrder.totalPaid
            getTestUser().balance shouldBeEqual newBalance

            // User has 1 order
            val userOrders = orderRepository.findOrdersByBuyerId(TestUsers.user.id!!)
            userOrders shouldHaveSize 1

            // Verify integrity of data
            val userOrder = userOrders.first()
            userOrder.totalPaid shouldBe sampleOrder.totalPaid

            // Order items associated with the order were created
            val userOrderItems = orderItemRepository.findOrderItemsByOrderId(userOrder.id!!)
            userOrderItems shouldHaveSize 2 // Two items were in the cart
        }
    }
}
