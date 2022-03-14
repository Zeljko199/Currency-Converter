package com.example.currencyconverter.viewmodel

import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.ApiCurrency
import com.example.currencyconverter.network.ApiDataSource
import com.example.currencyconverter.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*ova klasa je u interakciji sa ApiDataSource koja komunicira sa internetom,
* prikupljeni podaci se mogu prikupljati u viewModel-u i na kraju se mogu koristiti u View*/
class MainRepository @Inject constructor(private val apiDataSource: ApiDataSource) : BaseDataSource() {

    //koriste se coroutine za dobijanje odgovora
    suspend fun getConvertedData(access_key: String, from: String, to: String, amount: Double): Flow<Resource<ApiCurrency>> {
        return flow {
            emit(apiCall { apiDataSource.getConvertedRate(access_key, from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }
}