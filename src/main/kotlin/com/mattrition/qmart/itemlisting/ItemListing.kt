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
data class ItemListing(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: UUID? = null,
    @Column(name = "seller_id", nullable = false) val sellerId: UUID? = null,
    val title: String = "",
    val description: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    @Column(name = "image_url") val imageUrl: String? = null,
    @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
