package com.whitebox.cryptocurrencymonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity

@Dao
interface CryptocurrencyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun upsertAssets(assets: List<AssetEntity>)

    @Query("UPDATE Asset SET is_favourite = 1 WHERE asset_id = :assetId")
    fun addFavouriteAsset(assetId: String)

    @Query("UPDATE Asset SET is_favourite = 0 WHERE asset_id = :assetId")
    fun removeFavouriteAsset(assetId: String)

    @Query("SELECT * FROM Asset WHERE is_favourite = 1")
    suspend fun getFavouriteAssets(): List<AssetEntity>

    @Query("SELECT * FROM Asset WHERE asset_id = :assetId")
    suspend fun getAssetById(assetId: String): AssetEntity?

    @Query("SELECT * FROM Asset")
    suspend fun getAllAssets(): List<AssetEntity>

    @Query("SELECT URL FROM Asset WHERE asset_id = :assetId")
    fun getAssetIconUrl(assetId: String): String?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertExchangeRate(exchangeRate: ExchangeRateEntity)

    @Query("SELECT * FROM exchange_rate WHERE asset_id_base = :assetIdBase")
    suspend fun getExchangeRate(assetIdBase: String): ExchangeRateEntity?

    @Query("SELECT * FROM asset WHERE asset_id LIKE '%' || :searchString || '%'")
    suspend fun searchAssets(searchString: String): List<AssetEntity>
}