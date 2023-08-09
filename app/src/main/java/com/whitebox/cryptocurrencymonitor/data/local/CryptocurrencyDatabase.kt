package com.whitebox.cryptocurrencymonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity

@Database(
    entities = [AssetEntity::class, ExchangeRateEntity::class],
    version = 1
)
abstract class CryptocurrencyDatabase : RoomDatabase() {
    abstract fun cryptocurrencyDao(): CryptocurrencyDao
}