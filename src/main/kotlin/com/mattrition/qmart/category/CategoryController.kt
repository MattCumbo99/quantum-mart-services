package com.mattrition.qmart.category

import com.mattrition.qmart.category.dto.CategoryDto
import com.mattrition.qmart.category.dto.CreateCategoryDto
import com.mattrition.qmart.category.dto.PatchCategoryDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService,
) {
    @GetMapping fun getActive() = categoryService.getAllActiveCategories()

    @GetMapping("/all")
    @RolesAllowed(UserRole.ADMIN)
    fun getAll() = categoryService.getAllCategories()

    @PostMapping
    @RolesAllowed(UserRole.ADMIN)
    fun createCategory(
        @RequestBody req: CreateCategoryDto,
    ): ResponseEntity<CategoryDto> {
        val saved = categoryService.createCategory(req)

        return ResponseEntity(saved, HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    @RolesAllowed(UserRole.ADMIN)
    fun updateCategory(
        @PathVariable id: UUID,
        @RequestBody req: PatchCategoryDto,
    ): ResponseEntity<Void> = categoryService.updateCategory(id, req)
}
