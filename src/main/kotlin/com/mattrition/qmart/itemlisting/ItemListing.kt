package com.mattrition.qmart.itemlisting

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "item_listings")
class ItemListing(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "seller_id", nullable = false) var sellerId: UUID,
    @Column(nullable = false) var title: String,
    var description: String? = null,
    @Column(nullable = false) var price: BigDecimal = BigDecimal.ZERO,
    @Column(name = "image_url") var imageUrl: String? = null,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "is_active", nullable = false) var isActive: Boolean = true,
    @Column(name = "quantity_sold", nullable = false) var quantitySold: Int = 0,
    @Column(name = "review_count", nullable = false) var reviewCount: Int = 0,
    @Column(name = "average_score", nullable = false)
    var averageScore: BigDecimal = BigDecimal("0.0"),
)
