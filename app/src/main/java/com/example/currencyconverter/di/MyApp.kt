package com.example.currencyconverter.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/* sve aplikacije koje koriste Hilt moraju da sadrze Application klasu
 Ovo ce nam pomoci da generisemo sve potrebne sifre, kao i osnovnu klasu */
@HiltAndroidApp
class MyApp : Application(){

}