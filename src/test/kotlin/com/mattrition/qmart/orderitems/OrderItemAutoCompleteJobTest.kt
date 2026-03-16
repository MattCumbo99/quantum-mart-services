package com.mattrition.qmart.orderitems

import com.mattrition.qmart.jobs.OrderItemAutoCompleteJob
import com.mattrition.qmart.orderitem.OrderItem
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.OrderItemStatus
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
class OrderItemAutoCompleteJobTest {
    @MockitoBean lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var job: OrderItemAutoCompleteJob

    @Test
    fun `autoCompleteShippedOrders completes eligible items`() {
        val now = OffsetDateTime.now()
        val shippedYesterday = now.minusDays(1).minusHours(1)

        val item =
            OrderItem(
                id = UUID.randomUUID(),
                listingId = UUID.randomUUID(),
                sellerId = UUID.randomUUID(),
                quantity = 1,
                status = OrderItemStatus.SHIPPED,
                paidAt = now.minusDays(2),
                listingTitle = "test title",
                listingDescription = "test description",
                listingPrice = BigDecimal.ZERO,
                shippedOn = shippedYesterday,
            )

        // Stub the repository to return this item
        Mockito
            .`when`(
                orderItemRepository.findAllToComplete(
                    ArgumentMatchers.any(OffsetDateTime::class.java) ?: OffsetDateTime.now(),
                ),
            ).thenAnswer { listOf(item) }

        job.autoCompleteShippedOrders()

        item.status shouldBe OrderItemStatus.COMPLETED
        item.completedOn.shouldNotBeNull()

        Mockito.verify(orderItemRepository).saveAll(listOf(item))
    }
}
