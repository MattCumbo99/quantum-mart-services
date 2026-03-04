package com.mattrition.qmart.orderitem

import com.mattrition.qmart.orderitem.dto.OrderItemDto
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/order-items")
class OrderItemController(
    private val orderItemService: OrderItemService,
) {
    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestParam(required = true) newStatus: OrderItemStatus,
    ): OrderItemDto = orderItemService.updateOrderItemStatus(id, newStatus)
}
