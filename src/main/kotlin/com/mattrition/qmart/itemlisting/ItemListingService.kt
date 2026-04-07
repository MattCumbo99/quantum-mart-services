package com.mattrition.qmart.itemlisting

import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.ItemListingMapper
import com.mattrition.qmart.itemlisting.dto.UpdateListingRequest
import com.mattrition.qmart.user.UserRepository
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class ItemListingService(
    private val itemListingRepo: ItemListingRepository,
    private val userRepo: UserRepository,
) {
    /** Retrieves a list containing every item listing in the database. */
    fun getAllListings(): List<ItemListingDto> =
        itemListingRepo.findAll().map { listing ->
            val sellerUsername = userRepo.findById(listing.sellerId).get().username

            ItemListingMapper.toDto(listing, sellerUsername)
        }

    /**
     * Retrieves an item listing by its [ID][ItemListing.id].
     *
     * @throws NotFoundException If no item listing has the provided ID.
     */
    fun getListingById(id: UUID): ItemListingDto {
        val listing =
            itemListingRepo.findById(id).getOrElse {
                throw NotFoundException("Listing with ID not found: $id")
            }
        val sellerUsername = userRepo.findById(listing.sellerId).get().username

        return ItemListingMapper.toDto(listing, sellerUsername)
    }

    /**
     * Retrieves every listing being sold by a user matching [username]. Casing is ignored.
     *
     * @throws NotFoundException If the user does not exist.
     */
    fun getListingsByUsername(username: String): List<ItemListingDto> {
        val userId =
            userRepo.findByUsernameIgnoreCase(username)?.id
                ?: throw NotFoundException("Username does not exist: $username")

        return itemListingRepo.findItemListingsBySellerId(userId).map {
            ItemListingMapper.toDto(it, username)
        }
    }

    @Transactional
    fun updateListing(
        id: UUID,
        req: UpdateListingRequest,
    ) {
        val listing =
            itemListingRepo.findById(id).getOrElse {
                throw NotFoundException("Item listing with ID $id not found")
            }

        if (!authOwnsListing(listing)) {
            throw ForbiddenException("Forbidden")
        }

        req.title?.let { listing.title = it }
        req.description?.let { listing.description = it }
        req.price?.let { price ->
            if (price <= BigDecimal.ZERO) {
                throw BadRequestException("Price must be greater than zero.")
            }

            listing.price = price
        }
        req.isActive?.let { listing.isActive = it }

        itemListingRepo.save(listing)
    }

    private fun authOwnsListing(listing: ItemListing) = listing.sellerId == authPrincipal().id

    /** Saves a new item listing entity to the database and returns the provided information. */
    fun createListing(itemListing: ItemListingDto): ItemListingDto {
        val listingEntry =
            ItemListing(
                sellerId = itemListing.sellerId,
                title = itemListing.title,
                description = itemListing.description,
                price = itemListing.price,
                imageUrl = itemListing.imageUrl,
            )

        return ItemListingMapper.toDto(
            itemListingRepo.save(listingEntry),
            itemListing.sellerUsername,
        )
    }
}
