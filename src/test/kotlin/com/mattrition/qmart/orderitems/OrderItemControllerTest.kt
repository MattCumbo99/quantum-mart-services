package com.mattrition.qmart.orderitems

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.cart.CartItem
import com.mattrition.qmart.cart.CartItemRepository
import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import com.mattrition.qmart.itemlisting.dto.toDto
import com.mattrition.qmart.order.OrderService
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.OrderItemStatus
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.PATCH
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.jvm.optionals.getOrNull

class OrderItemControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/order-items"
    }

    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var cartItemRepository: CartItemRepository

    @Autowired lateinit var orderService: OrderService

    private lateinit var order: OrderDto

    @BeforeEach
    fun beforeEach() {
        val listings = super.initListings()

        val cartItem =
            cartItemRepository.save(
                CartItem(
                    userId = TestUsers.user.id!!,
                    listingId = listings.first().id!!,
                    quantity = 1,
                ),
            )

        // Create an order request for the moderator
        order =
            orderService.createOrder(
                super.orderWithAddress(
                    buyerId = TestUsers.user.id!!,
                    totalPaid = BigDecimal(100),
                    orderItems =
                        listOf(
                            OrderItemMapper.fromCartItemDto(
                                CartItemWithListingDto(
                                    cartItemId = cartItem.id!!,
                                    quantity = cartItem.quantity,
                                    itemListing =
                                        listings.first().toDto(TestUsers.moderator.username),
                                ),
                            ),
                        ),
                ),
            )
    }

    @Nested
    inner class PatchOrderItem {
        @Test
        fun `non-seller patching order item should return 403 forbidden`() {
            mockRequest(
                requestType = PATCH,
                path =
                    "$BASE_PATH/${order.orderItems.first().id!!}?newStatus=${OrderItemStatus.SHIPPED}",
                token = TestTokens.user,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should set shipping date and status`() {
            fun orderItem() =
                orderItemRepository
                    .findById(order.orderItems.first().id!!)
                    .getOrNull()
                    .shouldNotBeNull()
            orderItem().status shouldBe OrderItemStatus.PAID_PENDING_SHIPMENT
            orderItem().shippedOn.shouldBeNull()

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${orderItem().id!!}?newStatus=${OrderItemStatus.SHIPPED}",
                token = TestTokens.moderator,
            ).andExpect(status().isOk)

            orderItem().shippedOn.shouldNotBeNull() shouldBeLessThanOrEqualTo OffsetDateTime.now()
            orderItem().status shouldBe OrderItemStatus.SHIPPED
        }
    }
}
