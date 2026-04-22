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
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "buyer_id") var buyerId: UUID? = null,
    @Column(name = "guest_email") var guestEmail: String? = null,
    var status: String = OrderStatus.PENDING,
    @Column(name = "total_paid", nullable = false) var totalPaid: BigDecimal = BigDecimal.ZERO,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "shipping_firstname", nullable = false) var shippingFirstname: String,
    @Column(name = "shipping_lastname", nullable = false) var shippingLastname: String,
    @Column(name = "shipping_address1", nullable = false) var shippingAddress1: String,
    @Column(name = "shipping_address2") var shippingAddress2: String? = null,
    @Column(name = "shipping_city", nullable = false) var shippingCity: String,
    @Column(name = "shipping_state", nullable = false) var shippingState: String,
    @Column(name = "shipping_zip", nullable = false) var shippingZip: String,
    @Column(name = "shipping_phone", nullable = false) var shippingPhone: String,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderItems: MutableList<OrderItem> = mutableListOf(),
)
