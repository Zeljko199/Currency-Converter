package com.example.currencyconverter.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager

//klasa za skrivanje tastature, provera da li je mreza dostupna...
object Utility {

    //skrivanje tastature
    fun hideKeyboard(activity: Activity){
        val input: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //pretraga trenutnog view-a, tako da se moze uzeti ispravan token iz njega
        var view: View? = activity.currentFocus
        //Ako nijedan view-a trenutno nema fokus, napraviti novi samo da bi uzeli token iz njega
        if (view == null){
            view = View(activity)
        }
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //provera internet konekcije
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null){
            return false
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
    fun makeStatusBarTransparent(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decor = activity.window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            val w = activity.window
            w.statusBarColor = Color.TRANSPARENT
        }
    }
}