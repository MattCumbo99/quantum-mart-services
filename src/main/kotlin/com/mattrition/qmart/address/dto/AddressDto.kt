package com.mattrition.qmart.address.dto

import java.time.OffsetDateTime
import java.util.UUID

data class AddressDto(
    val id: UUID? = null,
    val userId: UUID,
    val isPrimary: Boolean,
    val firstName: String,
    val lastName: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val zip: String,
    val phone: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
)
