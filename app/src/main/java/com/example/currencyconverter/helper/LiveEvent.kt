package com.example.currencyconverter.helper

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

//Pomocna klasa za rukovanje pojedinacnim dogadjajima, viewLifeCycle
class LiveEvent <T>: MutableLiveData<T>() {

    private val le = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        //hasActiveObservers() - vraca true ako je observer active
        if (hasActiveObservers()){
            Log.w("LiveEvent", "Vise posmatraca je registrovano")
        }

        //Observe MutableLiveData
        //dodaje posmatraca na listu posmatraca u okviru tog Lifecycle
        super.observe(owner, Observer<T> { t ->
            // compareAndSet - uporedjuje vrednosti sa ocekivanim i zamenjuje je novom,
            // ako se podudaraju vrednosti*/
            if (le.compareAndSet(true,false)){
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(value: T?) {
        le.set(true)
        super.setValue(value)
    }

    //za pozive
    @MainThread
    fun call() {
        value = null
    }
}