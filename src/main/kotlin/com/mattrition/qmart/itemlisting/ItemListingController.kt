package com.mattrition.qmart.itemlisting

import com.mattrition.qmart.itemlisting.dto.CreateListingRequest
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.UpdateListingRequest
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
@RequestMapping("/api/item-listings")
class ItemListingController(
    private val service: ItemListingService,
) {
    @GetMapping fun getItemListings(): List<ItemListingDto> = service.getAllListings()

    @GetMapping("/seller/{sellerId}")
    fun getItemListingsByUsername(
        @PathVariable sellerId: UUID,
    ): List<ItemListingDto> = service.getListingsByUserId(sellerId)

    @GetMapping("/{listingId}")
    fun getItemListing(
        @PathVariable listingId: UUID,
    ): ItemListingDto = service.getListingById(listingId)

    @RolesAllowed(UserRole.USER)
    @PostMapping
    fun createListing(
        @RequestBody request: CreateListingRequest,
    ): ResponseEntity<ItemListingDto> {
        val item = service.createListing(request)

        return ResponseEntity(item, HttpStatus.CREATED)
    }

    @RolesAllowed(UserRole.USER)
    @PatchMapping("/{listingId}")
    fun editListing(
        @PathVariable listingId: UUID,
        @RequestBody updateListing: UpdateListingRequest,
    ) = service.updateListing(listingId, updateListing)
}
