package com.mattrition.qmart.order

import com.mattrition.qmart.orderitem.OrderItem
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id: UUID? = null,
    @Column(name = "buyer_id", nullable = false) val buyerId: UUID? = null,
    val status: String = OrderStatus.PENDING,
    @Column(name = "total_paid", nullable = false) val totalPaid: BigDecimal = BigDecimal.ZERO,
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "shipping_firstname", nullable = false) val shippingFirstname: String = "",
    @Column(name = "shipping_lastname", nullable = false) val shippingLastname: String = "",
    @Column(name = "shipping_address1", nullable = false) val shippingAddress1: String = "",
    @Column(name = "shipping_address2") val shippingAddress2: String? = null,
    @Column(name = "shipping_city", nullable = false) val shippingCity: String = "",
    @Column(name = "shipping_state", nullable = false) val shippingState: String = "",
    @Column(name = "shipping_zip", nullable = false) val shippingZip: String = "",
    @Column(name = "shipping_phone", nullable = false) val shippingPhone: String = "",
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orderItems: MutableList<OrderItem> = mutableListOf(),
)
