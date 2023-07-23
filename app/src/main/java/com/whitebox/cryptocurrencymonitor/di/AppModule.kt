package com.whitebox.cryptocurrencymonitor.di

import android.app.Application
import androidx.room.Room
import com.whitebox.cryptocurrencymonitor.common.Constants.BASE_URL
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDatabase
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.data.repository.CryptocurrencyRepositoryImpl
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
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
    fun provideCryptocurrencyRepository(
        api: AssetApi,
        dao: CryptocurrencyDao
    ): CryptocurrencyRepository = CryptocurrencyRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideAssetApi(): AssetApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AssetApi::class.java)

    @Provides
    @Singleton
    fun provideCryptocurrencyDatabase(app: Application) = Room.databaseBuilder(
        app,
        CryptocurrencyDatabase::class.java,
        "cryptocurrency_database"
    ).build()

}
