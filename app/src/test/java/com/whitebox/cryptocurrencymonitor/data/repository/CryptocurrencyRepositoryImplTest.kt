package com.whitebox.cryptocurrencymonitor.data.repository

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.data.local.FakeLocalDao
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAsset
import com.whitebox.cryptocurrencymonitor.data.mapper.toLocalAsset
import com.whitebox.cryptocurrencymonitor.data.remote.FakeAssetApi
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import com.whitebox.cryptocurrencymonitor.util.HttpErrorParser
import com.whitebox.cryptocurrencymonitor.util.HttpErrorParserImpl
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

internal class CryptocurrencyRepositoryImplTest {

    private val assetEntity1 = listOf(
        AssetEntity(
            assetId = "BTC",
            name = "Bitcoin",
            typeIsCrypto = 1,
            iconUrl = "https://amazonaws.com/1b8e1f4c4f5d4c0a9f8c5a8c0b7f0f4c.png",
            isFavourite = true,
            priceUsd = "100"
        ),
        AssetEntity(
            assetId = "USD",
            name = "US Dollar",
            typeIsCrypto = 0,
            iconUrl = "https://amazonaws.com/1b8e1f4d4c0a9f8c5a8c0b7f0f4c.png",
            isFavourite = false,
            priceUsd = "1"
        ),
        AssetEntity(
            assetId = "PLN",
            name = "Zloty",
            typeIsCrypto = 1,
            iconUrl = "https://amazonaws.com/1b8e1f4c4f5d4c0a9f8c5a8c0b7f0f4c.png",
            isFavourite = false,
            priceUsd = "100"
        )
    )

    private lateinit var remoteDataSource: FakeAssetApi
    private lateinit var localDataSource: FakeLocalDao
    private lateinit var repository: CryptocurrencyRepository
    private lateinit var httpErrorParser: HttpErrorParser

    private val gson: Gson = mock()

    @Before
    fun setUp() {
        remoteDataSource = FakeAssetApi()
        localDataSource = FakeLocalDao()
        httpErrorParser = HttpErrorParserImpl(gson = gson)
        repository =
            CryptocurrencyRepositoryImpl(remoteDataSource, localDataSource, httpErrorParser)
    }

    @Test
    fun `getAssets should return Loading result first`() = runBlocking {
        // Given
        localDataSource.upsertAssets(assetEntity1)

        // When
        val resultFlow = repository.getAssets(fetchFromRemote = false)
        val result = resultFlow.first()

        // Then
        // show loading
        assertThat(result is WorkResult.Loading).isTrue()
    }

    @Test
    fun `getAssets should return local data with Success result when available`() = runBlocking {
        // Given
        localDataSource.upsertAssets(assetEntity1)

        // When
        val resultFlow = repository.getAssets(false)
        val result = resultFlow.first()
        val secondResult = resultFlow.drop(1).first()

        // Then
        assertThat(result is WorkResult.Loading).isTrue()
        assertThat(assetEntity1.map { it.toDomainAsset() }).isEqualTo((secondResult as WorkResult.Success).data)
    }

    @Test
    fun `getAssets should return remote data with Success result`() = runBlocking {
        // Given
        val remoteAssets = remoteDataSource.getAssets().map { it.toDomainAsset() }

        // When
        val resultFlow = repository.getAssets(true)
        val secondResult = resultFlow.drop(1).first()

        // Then
        val updatedLocalAssets = remoteAssets.map { it.toLocalAsset().toDomainAsset() }
        assertThat(updatedLocalAssets).isEqualTo((secondResult as WorkResult.Success).data)
    }

    @Test
    fun addFavouriteAsset() = runBlocking {
        // Given
        localDataSource.upsertAssets(assetEntity1)

        // When
        localDataSource.addFavouriteAsset("BTC")

        // Then
        val updatedAsset = localDataSource.getAssetById("BTC")?.toDomainAsset()
        assertThat(updatedAsset?.isFavourite).isTrue()
        assertThat(updatedAsset).isNotNull()
    }

    @Test
    fun removeFavouriteAsset() = runBlocking {
        // Given
        localDataSource.upsertAssets(assetEntity1)

        // When
        localDataSource.addFavouriteAsset("BTC")
        localDataSource.removeFavouriteAsset("BTC")

        // Then
        val updatedAsset = localDataSource.getAssetById("BTC")?.toDomainAsset()
        assertThat(updatedAsset?.isFavourite).isFalse()
        assertThat(updatedAsset).isNotNull()
    }

}