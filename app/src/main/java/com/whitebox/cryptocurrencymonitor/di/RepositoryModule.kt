package com.whitebox.cryptocurrencymonitor.di

import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.data.repository.CryptocurrencyRepositoryImpl
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import com.whitebox.cryptocurrencymonitor.util.HttpErrorParser
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
        dao: CryptocurrencyDao,
        httpErrorParser: HttpErrorParser
    ): CryptocurrencyRepository = CryptocurrencyRepositoryImpl(api, dao, httpErrorParser)

}
