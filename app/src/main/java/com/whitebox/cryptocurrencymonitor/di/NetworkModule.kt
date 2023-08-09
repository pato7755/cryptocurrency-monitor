package com.whitebox.cryptocurrencymonitor.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whitebox.cryptocurrencymonitor.BuildConfig
import com.whitebox.cryptocurrencymonitor.CryptocurrencyApp
import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.util.NetworkConnectivityService
import com.whitebox.cryptocurrencymonitor.util.NetworkConnectivityServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): CryptocurrencyApp {
        return app as CryptocurrencyApp
    }

    @Provides
    @Singleton
    fun provideGsonConverter(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        client: OkHttpClient,
    ): Retrofit.Builder = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())

    @Provides
    @Singleton
    fun providesCryptocurrencyApi(retrofitBuilder: Retrofit.Builder): AssetApi {
        return retrofitBuilder
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(AssetApi::class.java)
    }

    private const val READ_TIMEOUT = 30
    private const val WRITE_TIMEOUT = 30
    private const val CONNECTION_TIMEOUT = 10

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor
    ): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(headerInterceptor)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            requestBuilder.addHeader("Content-Type", "application/json")
            requestBuilder.addHeader("X-CoinAPI-Key", BuildConfig.API_KEY)
            it.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityService(
        @ApplicationContext context: Context,
    ): NetworkConnectivityService {
        return NetworkConnectivityServiceImpl(context = context)
    }

}
