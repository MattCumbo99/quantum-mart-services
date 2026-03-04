package com.mattrition.qmart.orderitem

import com.mattrition.qmart.auth.CustomUserDetails
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.orderitem.dto.OrderItemDto
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class OrderItemService(
    private val orderItemRepository: OrderItemRepository,
) {
    /**
     * Patches an order item's status field.
     *
     * @throws NotFoundException If the order item does not exist.
     * @throws ForbiddenException If the authentication ID does not match the seller ID.
     */
    @Transactional
    fun updateOrderItemStatus(
        orderItemId: UUID,
        newStatus: OrderItemStatus,
    ): OrderItemDto {
        val orderItem =
            orderItemRepository.findById(orderItemId).orElseThrow {
                throw NotFoundException("Order item with id $orderItemId does not exist.")
            }

        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal as CustomUserDetails

        if (orderItem.sellerId != principal.id) {
            throw ForbiddenException("User not authorized.")
        }

        if (newStatus == OrderItemStatus.SHIPPED) {
            orderItem.status = newStatus
            orderItem.shippedOn = OffsetDateTime.now()
        }

        return OrderItemMapper.toDto(orderItem)
    }
}
