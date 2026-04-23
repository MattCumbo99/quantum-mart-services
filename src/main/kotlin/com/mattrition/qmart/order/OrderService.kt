package com.mattrition.qmart.order

import com.mattrition.qmart.cart.CartItemService
import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.notification.NotificationService
import com.mattrition.qmart.order.dto.CreateOrderRequestDto
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.order.mapper.OrderMapper
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import com.mattrition.qmart.user.BalanceService
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cartItemService: CartItemService,
    private val balanceService: BalanceService,
    private val notificationService: NotificationService,
) {
    /** Retrieves all orders bought by a specified user. */
    fun getOrdersBoughtBy(buyerId: UUID): List<OrderDto> = orderRepository.findOrdersByBuyerId(buyerId).map { OrderMapper.toDto(it) }

    /**
     * Retrieves all orders with only order items associated by the seller.
     *
     * @param unfinished If the query should only return orders where at least 1 item from the
     *   seller is
     *   [PAID_PENDING_SHIPMENT][com.mattrition.qmart.orderitem.OrderItemStatus.PAID_PENDING_SHIPMENT].
     */
    fun getOrdersForSeller(
        sellerId: UUID,
        unfinished: Boolean,
    ): List<OrderDto> {
        val orders =
            if (unfinished) {
                orderRepository.findUnfinishedOrdersFromSeller(sellerId)
            } else {
                orderRepository.findFinishedOrdersFromSeller(sellerId)
            }.map { order ->
                val sellerItems =
                    orderItemRepository.findByOrderIdAndSellerId(order.id!!, sellerId).map {
                        OrderItemMapper.toDto(it)
                    }

                OrderMapper.toDto(order).copy(orderItems = sellerItems)
            }

        return orders
    }

    /**
     * Saves a new order and its items to the database using the buyer's associated cart items. Once
     * an order is made, it clears the cart items being held by the buyer.
     *
     * @throws BadRequestException If the buyer's cart is empty.
     * @throws ForbiddenException If the buyer doesn't have enough money to make the order.
     */
    @Transactional
    fun createOrder(orderInfo: CreateOrderRequestDto): OrderDto {
        enforceCreationRules(orderInfo)

        val cartItems = retrieveCartItems(orderInfo)
        cartItems.ifEmpty { throw BadRequestException("Order has no items!") }

        // Take the user's money. Guests do not have to pay!
        orderInfo.buyerId?.let { buyerId ->
            balanceService.deductBalance(buyerId, orderInfo.totalPaid)
        }

        val orderEntity = OrderMapper.asNewEntity(orderInfo)

        cartItems.forEach { cartItem ->
            val itemDto = OrderItemMapper.fromCartItemDto(cartItem)
            val itemEntity = OrderItemMapper.asNewEntity(itemDto)

            itemEntity.order = orderEntity
            orderEntity.orderItems.add(itemEntity)

            // Send notification to seller
            val sellerId = cartItem.itemListing.sellerId
            notifySeller(sellerId, cartItem.itemListing)
        }

        val savedOrder = orderRepository.save(orderEntity)

        // Clear guest cart
        orderInfo.guestSessionId?.let { guestSessionId ->
            cartItemService.deleteGuestCartItems(guestSessionId)
        }

        // Clear the users cart
        orderInfo.buyerId?.let { buyerId -> cartItemService.deleteCartItemsByUserId(buyerId) }

        return OrderMapper.toDto(savedOrder)
    }

    private fun retrieveCartItems(orderInfo: CreateOrderRequestDto): List<CartItemWithListingDto> {
        // Guest routine
        orderInfo.guestSessionId?.let { guestId ->
            return cartItemService.getCartItemsByGuestId(guestId)
        }

        return cartItemService.getCartItemsByUserId(orderInfo.buyerId!!)
    }

    /** Runs a group of rules that must pass to allow order creation. */
    private fun enforceCreationRules(orderInfo: CreateOrderRequestDto) {
        val twoOwners = orderInfo.buyerId != null && orderInfo.guestSessionId != null
        val noOwner = orderInfo.buyerId == null && orderInfo.guestSessionId == null

        // Ownership must not conflict between guest and user
        if (twoOwners || noOwner) {
            throw BadRequestException("User and guest identity must not conflict.")
        }

        // Buyer ID must match authentication
        val authUser = authPrincipal()
        if (orderInfo.buyerId != null && (authUser == null || authUser.id != orderInfo.buyerId)) {
            throw ForbiddenException("Forbidden.")
        }

        // Non-user requests must provide guest email
        if (orderInfo.guestSessionId != null && orderInfo.guestEmail == null) {
            throw BadRequestException("Guest orders must provide email.")
        }

        // Guest email should not be provided if from regular user
        if (orderInfo.buyerId != null && orderInfo.guestEmail != null) {
            throw BadRequestException("Guest email field should be null for users.")
        }

        // Total paid must be greater than 0
        if (orderInfo.totalPaid <= BigDecimal.ZERO) {
            throw BadRequestException("Total paid must be greater than 0.")
        }
    }

    /** Sends a notification to the seller telling them their item was sold. */
    private fun notifySeller(
        sellerId: UUID,
        listing: ItemListingDto,
    ) {
        notificationService.createNotification(
            userId = sellerId,
            message = "Your item ${listing.title} has sold!",
            route = "/listing/${listing.id!!}",
        )
    }
}
