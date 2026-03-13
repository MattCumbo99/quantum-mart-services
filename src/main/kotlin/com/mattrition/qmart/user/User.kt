package com.mattrition.qmart.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Represents a "user" database entry.
 *
 * @property id Primary key ID.
 * @property username Unique string identifier.
 * @property passwordHash Encrypted password.
 * @property createdAt Date this user was created.
 * @property email
 * @property balance How much money the user has.
 * @property role Privilege level of the user (user, moderator, admin).
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false) val username: String = "",
    @Column(name = "password_hash", nullable = false) val passwordHash: String = "",
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val email: String? = null,
    @Column(nullable = false) var balance: BigDecimal = BigDecimal(1000),
    @Column(nullable = false) val role: String = UserRole.USER.lowercase(),
)
