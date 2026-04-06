package com.mattrition.qmart.cart

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "cart_items")
class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "user_id", nullable = false) var userId: UUID,
    @Column(name = "listing_id", nullable = false) var listingId: UUID,
    @Column(nullable = false) var quantity: Int = 1,
)
