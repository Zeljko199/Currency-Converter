package com.example.currencyconverter.network

import com.example.currencyconverter.helper.EndPoints
import com.example.currencyconverter.model.ApiCurrency
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Ovaj interfejs sadrzi sve moguce HTTP operacije koje je potrebno izvrsiti
interface IApiService {
    @GET(EndPoints.CONVERT_URL)
    suspend fun convertCurrency(
        @Query("api_key") access_key: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
        ) : Response<ApiCurrency>
}