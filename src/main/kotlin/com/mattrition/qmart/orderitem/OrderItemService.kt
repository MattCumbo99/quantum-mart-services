package com.mattrition.qmart.orderitem

import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.notification.NotificationService
import com.mattrition.qmart.orderitem.dto.OrderItemDto
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class OrderItemService(
    private val orderItemRepository: OrderItemRepository,
    private val notificationService: NotificationService,
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

        ensureAuthUserIsSeller(orderItem.sellerId)
        ensureStatusCanChange(orderItem.status)
        ensureNewStatusIsAllowed(newStatus)

        orderItem.status = newStatus

        if (newStatus == OrderItemStatus.SHIPPED) {
            orderItem.shippedOn = OffsetDateTime.now()

            orderItem.order?.let { notifyBuyer(it.buyerId, OrderItemMapper.toDto(orderItem)) }
        }

        val orderItemDto = OrderItemMapper.toDto(orderItem)

        return orderItemDto
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
        val principal = authPrincipal()

        if (sellerId != principal.id) {
            throw ForbiddenException("User not authorized.")
        }
    }

    /** Sends a notification to a buyer telling them an order item has shipped. */
    private fun notifyBuyer(
        buyerId: UUID,
        orderItem: OrderItemDto,
    ) {
        notificationService.createNotification(
            userId = buyerId,
            message = "Your order for ${orderItem.listingTitle} has shipped.",
            route = "/purchases",
        )
    }

    companion object {
        private val ALLOWED_STATUSES = listOf(OrderItemStatus.SHIPPED, OrderItemStatus.CANCELED)
    }
}
