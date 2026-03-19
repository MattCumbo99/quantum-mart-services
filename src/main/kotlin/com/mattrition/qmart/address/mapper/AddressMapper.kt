package com.mattrition.qmart.address.mapper

import com.mattrition.qmart.address.Address
import com.mattrition.qmart.address.dto.AddressDto
import com.mattrition.qmart.util.EntityMapper

object AddressMapper : EntityMapper<Address, AddressDto> {
    override fun toDto(entity: Address) = AddressDto(
        id = entity.id,
        userId = entity.userId!!,
        isPrimary = entity.isPrimary,
        firstName = entity.firstName,
        lastName = entity.lastName,
        addressLine1 = entity.addressLine1,
        addressLine2 = entity.addressLine2,
        city = entity.city,
        state = entity.state,
        zip = entity.zip,
        createdAt = entity.createdAt,
    )

    override fun asNewEntity(dto: AddressDto) = Address(
        userId = dto.userId,
        firstName = dto.firstName,
        lastName = dto.lastName,
        addressLine1 = dto.addressLine1,
        addressLine2 = dto.addressLine2,
        city = dto.city,
        state = dto.state,
        zip = dto.zip,
    )
}
