package com.example.currencyconverter.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.helper.LiveEvent
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.model.ApiCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel(), LifecycleObserver {

    private val _data = LiveEvent<Resource<ApiCurrency>>()

    val data = _data
    val convertedRate = MutableLiveData<Double>()

    //funkcija koja dobija rezultat konverzije
    fun getConvertedData(access_key: String, from: String, to: String, amount: Double){
        viewModelScope.launch {
            mainRepository.getConvertedData(access_key, from, to, amount).collect {
                data.value = it
            }
        }
    }
}