package com.mattrition.qmart.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID> {
    @Query(
        """
            SELECT c FROM Category c
            WHERE c.isActive = true
        """,
    )
    fun findAllActive(): List<Category>

    @Query(
        """
            SELECT c FROM Category c
            WHERE c.slug != 'uncategorized'
        """,
    )
    fun findAllCategories(): List<Category>

    fun findCategoryBySlug(slug: String): Category?
}
