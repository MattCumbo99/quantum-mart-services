package com.mattrition.qmart.category

import com.mattrition.qmart.category.dto.CategoryDto
import com.mattrition.qmart.category.dto.CreateCategoryDto
import com.mattrition.qmart.category.dto.PatchCategoryDto
import com.mattrition.qmart.category.mapper.CategoryMapper
import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    companion object {
        private val slugRegex = Regex("^[a-z0-9-]+$")
    }

    /** Gets every category. */
    fun getAllCategories(): List<CategoryDto> = categoryRepository.findAllCategories().map { CategoryMapper.toDto(it) }

    /** Gets only categories marked as active. */
    fun getAllActiveCategories(): List<CategoryDto> = categoryRepository.findAllActive().map { CategoryMapper.toDto(it) }

    /** Saves a new category to the database using data from a creation request. */
    fun createCategory(req: CreateCategoryDto): CategoryDto {
        val slug = req.slug.lowercase()

        enforceValidSlug(slug)

        val entity = Category(name = req.name, slug = slug)
        val saved = categoryRepository.save(entity)

        return CategoryMapper.toDto(saved)
    }

    @Transactional
    fun updateCategory(
        categoryId: UUID,
        updates: PatchCategoryDto,
    ): ResponseEntity<Void> {
        val category =
            categoryRepository.findById(categoryId).getOrElse {
                throw NotFoundException("Category with id $categoryId not found")
            }

        var updated = false

        updates.name?.let { newName ->
            if (newName != category.name) {
                category.name = newName
                updated = true
            }
        }

        updates.slug?.let { newSlug ->
            val slug = newSlug.lowercase()

            if (slug != category.slug) {
                enforceValidSlug(slug)
                category.slug = newSlug
                updated = true
            }
        }

        updates.isActive?.let { newIsActive ->
            if (newIsActive != category.isActive) {
                category.isActive = newIsActive
                updated = true
            }
        }

        return if (updated) {
            category.updatedAt = OffsetDateTime.now()

            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    private fun enforceValidSlug(slug: String) {
        val slugExists = categoryRepository.findCategoryBySlug(slug) != null

        if (slugExists) {
            throw ForbiddenException("Slug '$slug' already exists.")
        }

        if (!slugRegex.matches(slug)) {
            throw BadRequestException("Slug '$slug' must match regex: ${slugRegex.pattern}")
        }
    }
}
