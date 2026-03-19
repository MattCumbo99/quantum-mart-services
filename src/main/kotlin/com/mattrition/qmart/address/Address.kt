package com.mattrition.qmart.address

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "addresses")
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false) val userId: UUID,
    @Column(name = "is_primary", nullable = false) var isPrimary: Boolean = false,
    @Column(name = "first_name", nullable = false) var firstName: String,
    @Column(name = "last_name", nullable = false) var lastName: String,
    @Column(name = "address_line1", nullable = false) var addressLine1: String,
    @Column(name = "address_line2") var addressLine2: String? = null,
    @Column(nullable = false) var city: String,
    @Column(nullable = false) var state: String,
    @Column(nullable = false) var zip: String,
    @Column(name = "created_at", nullable = false) val createdAt: OffsetDateTime = OffsetDateTime.now(),
)
