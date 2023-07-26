package com.whitebox.cryptocurrencymonitor.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whitebox.cryptocurrencymonitor.BuildConfig
import com.whitebox.cryptocurrencymonitor.CryptocurrencyApp
import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
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
class NetworkModule {

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

    private val READ_TIMEOUT = 30
    private val WRITE_TIMEOUT = 30
    private val CONNECTION_TIMEOUT = 10
    private val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
//        cache: Cache
    ): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
//        okHttpClientBuilder.cache(cache)
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

//    @Provides
//    @Singleton
//    internal fun provideCache(context: Context): Cache {
//        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
//        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
//    }
}
