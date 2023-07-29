package com.whitebox.cryptocurrencymonitor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "asset", indices = [Index(value = ["asset_id"], unique = true)])
data class AssetEntity(
    @ColumnInfo(name = "asset_id")
    val assetId: String,
    val name: String,
    @ColumnInfo(name = "type_is_crypto")
    val typeIsCrypto: Int,
    @ColumnInfo(name = "url")
    val iconUrl: String? = null,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean = false,
    @ColumnInfo(name = "price_usd")
    val priceUsd: String,
    @PrimaryKey val id: Int? = null
)

