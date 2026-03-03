package com.mattrition.qmart.order

import com.mattrition.qmart.cart.CartItemService
import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.order.mapper.OrderMapper
import com.mattrition.qmart.orderitem.OrderItemRepository
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import com.mattrition.qmart.user.BalanceService
import com.mattrition.qmart.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userRepository: UserRepository,
    private val cartItemService: CartItemService,
    private val balanceService: BalanceService,
) {
    /** Retrieves all orders bought by a specified user. */
    fun getOrdersBoughtBy(buyerId: UUID): List<OrderDto> = orderRepository.findOrdersByBuyerId(buyerId).map { OrderMapper.toDto(it) }

    /**
     * Retrieves all orders bought by a user via username.
     *
     * @throws NotFoundException If the username does not exist.
     */
    fun getOrdersBoughtBy(username: String): List<OrderDto> {
        val user =
            userRepository.findByUsernameIgnoreCase(username)
                ?: throw NotFoundException("User $username not found")

        return getOrdersBoughtBy(user.id!!)
    }

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
    fun createOrder(orderInfo: OrderDto): OrderDto {
        val cartItems = cartItemService.getCartItemsByUserId(orderInfo.buyerId)
        cartItems.ifEmpty { throw BadRequestException("Order has no items!") }

        // Take the users money
        balanceService.deductBalance(orderInfo.buyerId, orderInfo.totalPaid)

        val orderEntity = OrderMapper.asNewEntity(orderInfo)

        cartItems.forEach { cartItem ->
            val itemDto = OrderItemMapper.fromCartItemDto(cartItem)
            val itemEntity = OrderItemMapper.asNewEntity(itemDto)

            itemEntity.order = orderEntity
            orderEntity.orderItems.add(itemEntity)
        }

        val savedOrder = orderRepository.save(orderEntity)

        // Clear the users cart
        cartItemService.deleteCartItemsByUserId(orderInfo.buyerId)

        return OrderMapper.toDto(savedOrder)
    }
}
