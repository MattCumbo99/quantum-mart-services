package com.mattrition.qmart.images

import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService,
    @Value($$"${app.public-url}") private val publicUrl: String,
) {
    @PostMapping("/upload")
    @RolesAllowed(UserRole.USER)
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        val savedFilename = imageService.saveImage(file)
        val url = "$publicUrl/uploads/$savedFilename"

        return ResponseEntity.ok(url)
    }
}
