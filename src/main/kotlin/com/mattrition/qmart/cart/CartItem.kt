package com.mattrition.qmart.cart

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "cart_items")
class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "user_id") var userId: UUID? = null,
    @Column(name = "guest_session_id") var guestSessionId: UUID? = null,
    @Column(name = "listing_id", nullable = false) var listingId: UUID,
    @Column(nullable = false) var quantity: Int = 1,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
