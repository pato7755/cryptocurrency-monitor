package com.whitebox.cryptocurrencymonitor.di

import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.data.repository.CryptocurrencyRepositoryImpl
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCryptocurrencyRepository(
        api: AssetApi,
        dao: CryptocurrencyDao
    ): CryptocurrencyRepository = CryptocurrencyRepositoryImpl(api, dao)

}
