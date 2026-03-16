package com.mattrition.qmart.jobs

import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.OrderItemStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class OrderItemAutoCompleteJob(
    private val orderItemRepository: OrderItemRepository,
) {
    private val log = LoggerFactory.getLogger(OrderItemAutoCompleteJob::class.java)

    @Scheduled(cron = "0 0 10 * * *")
    fun autoCompleteShippedOrders() {
        log.info("AutoCompleteJob started")

        val now = OffsetDateTime.now()
        val cutoff = now.minusDays(1)

        val itemsToComplete = orderItemRepository.findAllToComplete(cutoff)

        log.info("Found ${itemsToComplete.size} items to complete.")

        itemsToComplete.forEach { item ->
            item.status = OrderItemStatus.COMPLETED
            item.completedOn = now
        }

        orderItemRepository.saveAll(itemsToComplete)

        log.info("AutoCompleteJob completed")
    }
}
