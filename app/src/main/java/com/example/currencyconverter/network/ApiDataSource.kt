package com.example.currencyconverter.network

import javax.inject.Inject

//preko ove klase moze se koristiti interfejs IApiService koji ce se koristiti u repository-jumu
class ApiDataSource @Inject constructor(private val IApiService: IApiService) {

    suspend fun getConvertedRate(access_key: String, from: String, to: String, amount: Double) =
        IApiService.convertCurrency(access_key, from, to, amount)
}