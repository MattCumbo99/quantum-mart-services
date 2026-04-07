package com.mattrition.qmart.itemlisting

import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/item-listings")
class ItemListingController(
    private val service: ItemListingService,
) {
    @GetMapping fun getItemListings(): List<ItemListingDto> = service.getAllListings()

    @GetMapping("/seller/{sellerUsername}")
    fun getItemListingsByUsername(
        @PathVariable sellerUsername: String,
    ): List<ItemListingDto> = service.getListingsByUsername(sellerUsername)

    @GetMapping("/{listingId}")
    fun getItemListing(
        @PathVariable listingId: UUID,
    ): ItemListingDto = service.getListingById(listingId)

    @RolesAllowed(UserRole.USER)
    @PostMapping
    fun createListing(
        @RequestBody itemListing: ItemListingDto,
    ): ResponseEntity<ItemListingDto> {
        val item = service.createListing(itemListing)

        return ResponseEntity(item, HttpStatus.CREATED)
    }

    @RolesAllowed(UserRole.USER)
    @DeleteMapping("/{listingId}")
    fun deleteListingById(
        @PathVariable listingId: UUID,
    ) = service.deleteListingById(listingId)
}
