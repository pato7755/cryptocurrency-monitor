package com.whitebox.cryptocurrencymonitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity

/**
 * Data Access Object for the Asset and Exchange Rate tables.
 */
@Dao
interface CryptocurrencyDao {

    /**
     * Insert if it doesn't exist or ignore if it does
     *
     * @param assets
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun upsertAssets(assets: List<AssetEntity>)

    /**
     * Update assets with icon URLs
     *
     * @param assetId
     * @param iconUrl
     */
    @Query("UPDATE Asset SET url = CASE WHEN url IS NULL THEN :iconUrl ELSE url END WHERE asset_id = :assetId")
    fun updateAssetIconUrlIfNull(assetId: String, iconUrl: String)

    /**
     * Update asset as favourite
     *
     * @param assetId
     */
    @Query("UPDATE Asset SET is_favourite = 1 WHERE asset_id = :assetId")
    fun addFavouriteAsset(assetId: String)

    /**
     * Update asset as not favourite
     *
     * @param assetId
     */
    @Query("UPDATE Asset SET is_favourite = 0 WHERE asset_id = :assetId")
    fun removeFavouriteAsset(assetId: String)

    /**
     * Get all assets flagged as favourites
     *
     * @return list of favourite assets
     */
    @Query("SELECT * FROM Asset WHERE is_favourite = 1")
    suspend fun getFavouriteAssets(): List<AssetEntity>

    /**
     * Get asset by id
     *
     * @param assetId
     * @return list of local assets
     */
    @Query("SELECT * FROM Asset WHERE asset_id = :assetId")
    suspend fun getAssetById(assetId: String): AssetEntity?

    /**
     * Get all assets
     *
     * @return list of local assets
     */
    @Query("SELECT * FROM Asset")
    suspend fun getAllAssets(): List<AssetEntity>

    /**
     * Get asset icon url
     *
     * @param assetId
     * @return asset icon url
     */
    @Query("SELECT URL FROM Asset WHERE asset_id = :assetId")
    fun getAssetIconUrl(assetId: String): String?

    /**
     * Insert exchange rate if it doesn't exist or ignore
     *
     * @param exchangeRate
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertExchangeRate(exchangeRate: ExchangeRateEntity)

    /**
     * Get exchange rate between the asset and EUR
     *
     * @param assetIdBase
     * @return exchange rate
     */
    @Query("SELECT * FROM exchange_rate WHERE asset_id_base = :assetIdBase")
    suspend fun getExchangeRate(assetIdBase: String): ExchangeRateEntity?

    /**
     * Search for local assets
     *
     * @param searchString
     * @return list of assets that match search string
     */
    @Query("SELECT * FROM asset WHERE asset_id LIKE '%' || :searchString || '%'")
    suspend fun searchAssets(searchString: String): List<AssetEntity>
}
