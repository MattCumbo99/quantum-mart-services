package com.mattrition.qmart.itemlisting

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ItemListingRepository : JpaRepository<ItemListing, UUID> {
    fun findItemListingsBySellerId(id: UUID): List<ItemListing>

    fun findByCategoryId(categoryId: UUID): List<ItemListing>
}
