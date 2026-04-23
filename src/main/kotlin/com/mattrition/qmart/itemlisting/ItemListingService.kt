package com.mattrition.qmart.itemlisting

import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.itemlisting.dto.CreateListingRequest
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.ItemListingMapper
import com.mattrition.qmart.itemlisting.dto.UpdateListingRequest
import com.mattrition.qmart.user.UserRepository
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
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
     * Retrieves every listing being sold by a user matching [userId].
     *
     * @throws NotFoundException If the user does not exist.
     */
    fun getListingsByUserId(userId: UUID): List<ItemListingDto> {
        val user =
            userRepo.findById(userId).getOrElse {
                throw NotFoundException("User with ID $userId not found.")
            }

        return itemListingRepo.findItemListingsBySellerId(userId).map {
            ItemListingMapper.toDto(it, user.username)
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
        req.imageUrl?.let { listing.imageUrl = it }
        req.isActive?.let { listing.isActive = it }

        listing.updatedAt = OffsetDateTime.now()

        itemListingRepo.save(listing)
    }

    private fun authOwnsListing(listing: ItemListing) = listing.sellerId == authPrincipal()!!.id

    /** Saves a new item listing entity to the database and returns the provided information. */
    fun createListing(request: CreateListingRequest): ItemListingDto {
        val authUser = authPrincipal()!!

        val listingEntry =
            ItemListing(
                sellerId = authUser.id,
                title = request.title,
                description = request.description,
                imageUrl = request.imageUrl,
                price = request.price,
            )

        val saved = itemListingRepo.save(listingEntry)

        return ItemListingMapper.toDto(saved, authUser.username)
    }

    /**
     * Increases the quantity sold on an item listing by a specified amount.
     *
     * @param listingId ID of the listing to modify
     * @param amount Amount to increment by. Needs to be greater than 0.
     */
    fun incrementSold(
        listingId: UUID,
        amount: Int = 1,
    ) {
        if (amount <= 0) {
            throw BadRequestException("Increment amount must be greater than 0.")
        }

        val listing =
            itemListingRepo.findById(listingId).getOrElse {
                throw NotFoundException("Item listing with ID $listingId not found")
            }

        listing.quantitySold += amount

        itemListingRepo.save(listing)
    }
}
