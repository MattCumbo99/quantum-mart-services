package com.mattrition.qmart.orderitems

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.jobs.OrderItemAutoCompleteJob
import com.mattrition.qmart.notification.NotificationRepository
import com.mattrition.qmart.order.Order
import com.mattrition.qmart.order.OrderRepository
import com.mattrition.qmart.orderitem.OrderItem
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.OrderItemStatus
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime

class OrderItemAutoCompleteJobTest : BaseH2Test() {
    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var job: OrderItemAutoCompleteJob

    @Autowired lateinit var orderRepository: OrderRepository

    @Autowired lateinit var notificationRepository: NotificationRepository

    private lateinit var sampleListing: ItemListing
    private lateinit var sampleOrder: Order

    @BeforeEach
    fun beforeEach() {
        val listings = super.initListings()

        sampleListing = listings.first()

        val testOrder =
            Order(
                buyerId = TestUsers.user.id!!,
                totalPaid = sampleListing.price,
                shippingFirstname = "test",
                shippingLastname = "test",
                shippingAddress1 = "test",
                shippingAddress2 = "test",
                shippingCity = "test",
                shippingState = "test",
                shippingZip = "test",
                shippingPhone = "test",
            )

        sampleOrder = orderRepository.save(testOrder)
    }

    @Test
    fun `autoCompleteShippedOrders completes eligible items and increments quantity sold`() {
        val now = OffsetDateTime.now()
        val shippedYesterday = now.minusDays(1).minusHours(1)

        // Create an order item referencing the listing
        val item =
            orderItemRepository.save(
                OrderItem(
                    order = sampleOrder,
                    listingId = sampleListing.id!!,
                    sellerId = sampleListing.sellerId,
                    quantity = 2,
                    status = OrderItemStatus.SHIPPED,
                    paidAt = now.minusDays(2),
                    listingTitle = sampleListing.title,
                    listingDescription = sampleListing.description,
                    listingPrice = sampleListing.price,
                    shippedOn = shippedYesterday,
                ),
            )

        // Run the job
        job.autoCompleteShippedOrders()

        // Reload from DB
        val updatedItem = orderItemRepository.findById(item.id!!).get()
        val updatedListing = itemListingRepository.findById(sampleListing.id!!).get()

        // Assertions
        updatedItem.status shouldBe OrderItemStatus.COMPLETED
        updatedItem.completedOn.shouldNotBeNull()

        updatedListing.quantitySold shouldBe 2

        // Ensure notification was sent
        val userNotifs = notificationRepository.findByUser(TestUsers.user.id!!)
        userNotifs shouldHaveSize 1
    }
}
