package com.mattrition.qmart.user.dto

import com.mattrition.qmart.user.UserRole
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String?,
    val createdAt: LocalDateTime,
    val balance: BigDecimal = BigDecimal.ZERO,
    val role: String = UserRole.USER.lowercase(),
)
