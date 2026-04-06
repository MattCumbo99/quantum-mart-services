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
class User(
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(nullable = false) var username: String,
    @Column(name = "password_hash", nullable = false) var passwordHash: String,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var email: String? = null,
    @Column(nullable = false) var balance: BigDecimal = BigDecimal(1000),
    @Column(nullable = false) var role: String = UserRole.USER.lowercase(),
)
