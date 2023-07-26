package com.whitebox.cryptocurrencymonitor.di

import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.AddFavouriteAssetUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetDetailsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetIconsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetFavouriteAssetsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.RemoveFavouriteAssetUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.exchangerate.GetExchangeRateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAssetsUseCase(repository: CryptocurrencyRepository) =
        GetAssetsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAssetDetailsUseCase(repository: CryptocurrencyRepository) =
        GetAssetDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAssetIcons(repository: CryptocurrencyRepository) =
        GetAssetIconsUseCase(repository)

    @Provides
    @Singleton
    fun providesAddFavouriteAssetUseCase(repository: CryptocurrencyRepository) =
        AddFavouriteAssetUseCase(repository)

    @Provides
    @Singleton
    fun provideGetFavouriteAssetsUseCase(repository: CryptocurrencyRepository) =
        GetFavouriteAssetsUseCase(repository)

    @Provides
    @Singleton
    fun provideRemoveFavouriteAssetUseCase(repository: CryptocurrencyRepository) =
        RemoveFavouriteAssetUseCase(repository)

    @Provides
    @Singleton
    fun provideGetExchangeRatesUseCase(repository: CryptocurrencyRepository) =
        GetExchangeRateUseCase(repository)
}
