package com.mattrition.qmart.images

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class ImageService {
    private val uploadDir: Path = Paths.get("uploads")

    init {
        // Create the directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    fun saveImage(file: MultipartFile): String {
        val filename = "${UUID.randomUUID()}-${file.originalFilename}"
        val target = uploadDir.resolve(filename)

        Files.copy(file.inputStream, target)

        return filename
    }
}
