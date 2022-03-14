package com.example.currencyconverter.di

import android.viewbinding.BuildConfig
import com.example.currencyconverter.helper.EndPoints
import com.example.currencyconverter.network.ApiDataSource
import com.example.currencyconverter.network.IApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Za ubacivanje u interfejse i klase iz spoljnih biblioteka, hilt ce obezbediti instance
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //API Base URL
    @Provides
    fun baseUrl() = EndPoints.BASE_URL

    //Gson pretvaranje JSON u xml
    @Provides
    fun gson() : Gson = GsonBuilder().setLenient().create()

    //Retrofit za internet
    @Provides
    @Singleton
    fun retrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(EndPoints.BASE_URL)
        .client(OkHttpClient.Builder().also { a ->
            if (BuildConfig.DEBUG){
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                a.addInterceptor(logging)
                a.connectTimeout(120, TimeUnit.SECONDS)
                a.readTimeout(120, TimeUnit.SECONDS)
                a.protocols(Collections.singletonList(Protocol.HTTP_1_1))
            }
        }.build()
        )
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    //Api sa retrofit instancom
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : IApiService = retrofit.create(IApiService::class.java)

    //pomocna klasa sa IApiService interfejsom
    @Provides
    @Singleton
    fun provideApiDataSource(apiService: IApiService) = ApiDataSource(apiService)
}