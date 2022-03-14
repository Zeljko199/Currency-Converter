package com.example.currencyconverter.helper

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

const val URL = "https://api.getgeoapi.com/v2/currency/"
const val KEY = "8a8228276c0e87b67190cc941cec477be2c1c21a"

class EndPointsTest {
    @Test
    fun isEqualsBASE_URL() = runBlocking {

        val result = EndPoints.BASE_URL

        assertEquals(URL, result)
    }
    @Test
    fun isEqualsAPI_KEY() = runBlocking {
        val result = EndPoints.API_KEY

        assertEquals(KEY,result)
    }

}