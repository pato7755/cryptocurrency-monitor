package com.whitebox.cryptocurrencymonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity

@Dao
interface CryptocurrencyDao {

    @Upsert
    fun upsertAssets(assets: List<AssetEntity>)

    @Query("UPDATE Asset SET is_bookmarked = 1 WHERE asset_id = :assetId")
    fun addFavouriteAsset(assetId: AssetEntity): Long

    @Query("UPDATE Asset SET is_bookmarked = 0 WHERE asset_id = :assetId")
    fun removeFavouriteAsset(assetId: String): Int

    @Query("SELECT * FROM Asset WHERE is_bookmarked = 1")
    suspend fun getFavouriteAssets(): List<AssetEntity>

    @Query("SELECT * FROM Asset WHERE asset_id = :assetId")
    suspend fun getAssetById(assetId: String): AssetEntity?

    @Query("SELECT * FROM Asset")
    suspend fun getAllAssets(): List<AssetEntity>

    @Update
    fun updateAsset(asset: AssetEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    @Query("SELECT * FROM exchange_rate WHERE asset_id_base = :assetIdBase")
    suspend fun getExchangeRate(assetIdBase: String): ExchangeRateEntity?

    @Update
    suspend fun updateExchangeRate(exchangeRate: ExchangeRateEntity)
}