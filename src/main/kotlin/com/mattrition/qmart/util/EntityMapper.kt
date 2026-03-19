package com.mattrition.qmart.util

interface EntityMapper<T, K> {
    /** Converts a database entity into its data transfer object. */
    fun toDto(entity: T): K

    /** Constructs a savable database entry from a transfer object. */
    fun asNewEntity(dto: K): T
}
