package com.mattrition.qmart.user.mapper

import com.mattrition.qmart.user.User
import com.mattrition.qmart.user.dto.UserDto
import com.mattrition.qmart.util.EntityMapper

object UserMapper : EntityMapper<User, UserDto> {
    override fun toDto(entity: User): UserDto =
        UserDto(
            id = entity.id!!,
            username = entity.username,
            email = entity.email,
            createdAt = entity.createdAt,
            balance = entity.balance,
            role = entity.role,
        )

    @Deprecated(message = "Method is not implemented.", level = DeprecationLevel.ERROR)
    override fun asNewEntity(dto: UserDto): User = throw NotImplementedError()
}
