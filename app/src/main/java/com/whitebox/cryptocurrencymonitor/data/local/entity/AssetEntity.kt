package com.whitebox.cryptocurrencymonitor.data.local.entity

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset")
data class AssetEntity(
    @ColumnInfo(name = "asset_id")
    val assetId: String,
    val name: String,
    @ColumnInfo(name = "type_is_crypto")
    val typeIsCrypto: Int,
    @ColumnInfo(name = "url")
    val iconUrl: String? = null,
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,
    @PrimaryKey val id: Int? = null
)




//fun AssetEntity.toAsset(): Asset {
//    return Asset(
//        assetId = assetId,
//        name = name,
//        typeIsCrypto = typeIsCrypto
//    )
//}
