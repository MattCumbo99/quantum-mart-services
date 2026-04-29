package com.mattrition.qmart.category.mapper

import com.mattrition.qmart.category.Category
import com.mattrition.qmart.category.dto.CategoryDto

object CategoryMapper {
    fun toDto(category: Category) =
        CategoryDto(
            id = category.id!!,
            name = category.name,
            slug = category.slug,
            isActive = category.isActive,
            createdAt = category.createdAt,
        )
}
