package com.whitebox.cryptocurrencymonitor.di

import com.whitebox.cryptocurrencymonitor.common.Constants.BASE_URL
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.data.repository.AssetRepositoryImpl
import com.whitebox.cryptocurrencymonitor.domain.repository.AssetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAssetRepository(api: AssetApi): AssetRepository = AssetRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAssetApi(): AssetApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AssetApi::class.java)

}
