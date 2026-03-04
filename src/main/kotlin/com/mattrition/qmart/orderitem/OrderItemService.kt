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

        ensureAuthUserIsSeller(orderItem.sellerId!!)
        ensureStatusCanChange(orderItem.status)
        ensureNewStatusIsAllowed(newStatus)

        if (newStatus == OrderItemStatus.SHIPPED) {
            orderItem.shippedOn = OffsetDateTime.now()
        }

        orderItem.status = newStatus

        return OrderItemMapper.toDto(orderItem)
    }

    private fun ensureStatusCanChange(currentStatus: OrderItemStatus) {
        if (currentStatus != OrderItemStatus.PAID_PENDING_SHIPMENT) {
            throw ForbiddenException("Cannot update item status (already modified).")
        }
    }

    private fun ensureNewStatusIsAllowed(newStatus: OrderItemStatus) {
        if (newStatus !in ALLOWED_STATUSES) {
            throw ForbiddenException("New status must be allowed.")
        }
    }

    private fun ensureAuthUserIsSeller(sellerId: UUID) {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal as CustomUserDetails

        if (sellerId != principal.id) {
            throw ForbiddenException("User not authorized.")
        }
    }

    companion object {
        private val ALLOWED_STATUSES = listOf(OrderItemStatus.SHIPPED, OrderItemStatus.CANCELLED)
    }
}
