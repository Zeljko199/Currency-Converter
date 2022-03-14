package com.example.currencyconverter.network

import android.util.Log
import com.example.currencyconverter.helper.Resource
import retrofit2.Response

// Ova klasa pomaze u hendlovanju stanja (uspesno, neuspesno ili ucitavanje),
// kako bi preduzeli odgovarajucu radnju ili prikazali poruku o gresci
//Radnje koje se preduzimaju ako se desi greska ili uspesno prodje kod API-ja
abstract class BaseDataSource {
    suspend fun <T> apiCall(apiCall: suspend () -> Response<T>): Resource<T>{

        try{
            val response = apiCall()
            if (response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Resource.success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception){
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T>{
        Log.e("remoteDataSource", message)
        return Resource.error("Povezivanje na internet nije uspelo, zbog: $message")
    }
}