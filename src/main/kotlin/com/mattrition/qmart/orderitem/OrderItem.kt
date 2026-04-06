package com.mattrition.qmart.orderitem

import com.mattrition.qmart.order.Order
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "listing_id", nullable = false) var listingId: UUID,
    @Column(name = "seller_id", nullable = false) var sellerId: UUID,
    @Column(nullable = false) var quantity: Int = 0,
    @Column(name = "listing_price", nullable = false)
    var listingPrice: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderItemStatus = OrderItemStatus.PAID_PENDING_SHIPMENT,
    @Column(name = "paid_at", nullable = false) var paidAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "listing_title", nullable = false) var listingTitle: String,
    @Column(name = "listing_description") var listingDescription: String? = null,
    @Column(name = "listing_image_url") var listingImageUrl: String? = null,
    @Column(name = "shipped_on") var shippedOn: OffsetDateTime? = null,
    @Column(name = "completed_on") var completedOn: OffsetDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") var order: Order? = null,
)
