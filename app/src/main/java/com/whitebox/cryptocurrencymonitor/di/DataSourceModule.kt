package com.whitebox.cryptocurrencymonitor.di

import android.content.Context
import androidx.room.Room
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun providesCryptocurrencyDao(db: CryptocurrencyDatabase) = db.cryptocurrencyDao()

    @Provides
    @Singleton
    fun provideCryptocurrencyDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context = context,
        CryptocurrencyDatabase::class.java,
        "cryptocurrency_database"
    ).build()

}
