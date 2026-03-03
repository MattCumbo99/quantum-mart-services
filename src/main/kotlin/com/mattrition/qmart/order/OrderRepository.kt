package com.mattrition.qmart.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {
    fun findOrdersByBuyerId(userId: UUID): List<Order>

    @Query(
        """
            SELECT o FROM Order o
            JOIN o.orderItems oi
            WHERE oi.sellerId = :sellerId
                AND oi.status = 'PAID_PENDING_SHIPMENT'
            ORDER BY o.createdAt ASC
        """,
    )
    fun findUnfinishedOrdersFromSeller(sellerId: UUID): List<Order>

    @Query(
        """
            SELECT o FROM Order o
            JOIN o.orderItems oi
            WHERE oi.sellerId = :sellerId
                AND NOT EXISTS (
                    SELECT 1 FROM OrderItem oi2
                    WHERE oi2.order = o
                        AND oi2.sellerId = :sellerId
                        AND oi2.status = 'PAID_PENDING_SHIPMENT'
                )
            ORDER BY o.createdAt DESC
        """,
    )
    fun findFinishedOrdersFromSeller(sellerId: UUID): List<Order>
}
