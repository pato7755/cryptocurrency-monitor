package com.whitebox.cryptocurrencymonitor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rate", indices = [Index(value = ["asset_id_base"], unique = true)])
data class ExchangeRateEntity(
    @ColumnInfo(name = "asset_id_base")
    val assetIdBase: String,
    @ColumnInfo(name = "asset_id_quote")
    val assetIdQuote: String,
    val rate: Double,
    val time: String,
    @PrimaryKey val id: Int? = null
)
