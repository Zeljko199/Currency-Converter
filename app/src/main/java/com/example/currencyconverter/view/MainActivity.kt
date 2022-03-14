package com.example.currencyconverter.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.helper.EndPoints
import com.example.currencyconverter.helper.Resource
import com.example.currencyconverter.helper.Utility
import com.example.currencyconverter.model.Rates
import com.example.currencyconverter.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //viewBinding za reference na view
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var item1: String? = "AFG"
    private var item2: String? = "AFG"

    //ViewModel
    private val mainViewModel: MainViewModel by viewModels()
//poziva se kada se startuje neka aplikacija, u tom trenutku možemo definisati šta želimo da se desi kada se startuje app.
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        //statusna traka
        Utility.makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //inicijalizacija oba spinera
        initSpinner()

        setUpClickListener()
    }

    /* ova metoda radi sve oko rukovanja sa padajucom listom(spinerom), prikazuje listu zemalja i
    * event koji se desi klikom */
    private fun initSpinner() {
        val spinner1 = binding.spn1
        //postavljanje drzava u spiner
        spinner1.setItems(getAllCountries())

        //sakrivanje tastature kada se spiner otvori
        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }

        //dobijamo item i cuvamo u selectedItem1
        spinner1.setOnItemSelectedListener { view, position, id, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            item1 = currencySymbol
            binding.txtFirstCurrencyName.setText(item1)
        }


        val spinner2 = binding.spn2
        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }
        spinner2.setItems( getAllCountries() )
        spinner2.setOnItemSelectedListener { view, position, id, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            item2 = currencySymbol
            binding.txtFirstCurrencyName.setText(item2)
        }
    }

    //preko koda drzave dobijamo valutu drzave
    private fun getSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode
            ) return Currency.getInstance(availableLocales[i]).currencyCode
        }
        return ""
    }

    //dobijamo kod drzave in naziva
    private fun getCountryCode(countryName: String) = Locale.getISOCountries().find { Locale("", it).displayCountry == countryName
    }

    //funkcija za dobijanje svih drzava
    private fun getAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }

    //funkcija za hendlovanje event-ima u UI
    private fun setUpClickListener() {
        binding.btn.setOnClickListener {
            //provera da li je unos prazan
            val provera = binding.etFirstCurrency.text.toString()
            if (provera.isEmpty() || provera == "0"){
                Snackbar.make(binding.mainLayout,"Unesite vrednost u tekstualno polje", Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //provera interneta
            else if (!Utility.isNetworkAvailable(this)){
                Snackbar.make(binding.mainLayout, "Niste povezani na internet", Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }
            else{
                doConversion()
            }
        }
    }
    /* funkcija koja vrsi konverziju komunicirajuci sa API-jem na osnovu unetih podataka*/
    private fun doConversion(){

        //skriva tastaturu
        Utility.hideKeyboard(this)
        //vidljivost progress bara
        binding.prg.visibility = View.VISIBLE
        //vidljivost dugmeta
        binding.btn.visibility = View.GONE

        val apiKey = EndPoints.API_KEY
        val from = item1.toString()
        val to = item2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        //konverzija
        mainViewModel.getConvertedData(apiKey, from, to, amount)

        //posmatranje promena u interfejsu
        observeUI()
    }

    //koriscenjem coroutina primecuju se promene i dobijaju odgovori od API-ja
    private fun observeUI(){
        mainViewModel.data.observe(this, androidx.lifecycle.Observer { r ->
            when(r.status){

                Resource.Status.SUCCESS -> {
                    if(r.data?.status == "success"){
                        val map: Map<String, Rates>
                        map = r.data.rates
                        map.keys.forEach {
                            val rateForAmount = map[it]?.rate_for_amount
                            mainViewModel.convertedRate.value = rateForAmount

                            //formatiranje dobijene vrednosti
                            val format = String.format("%,.2f", mainViewModel.convertedRate.value)

                            //setovanje vrednosti u drugom text fildu
                            binding.etSecondCurrency.setText(format)
                        }
                        binding.prg.visibility = View.GONE
                        //prikazi dugme
                        binding.btn.visibility = View.VISIBLE
                    }
                    else if(r.data?.status == "greska"){
                        val layout = binding.mainLayout
                        Snackbar.make(layout, "Nesto nije kako treba, pokusaj ponovo", Snackbar.LENGTH_LONG)
                            .withColor(ContextCompat.getColor(this, R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()

                        binding.prg.visibility = View.GONE
                        binding.btn.visibility = View.VISIBLE
                    }
                }
                Resource.Status.ERROR -> {
                    val layout = binding.mainLayout
                    Snackbar.make(layout, "Nesto nije kako treba, pokusaj ponovo", Snackbar.LENGTH_LONG)
                        .withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                        .show()

                    binding.prg.visibility = View.GONE
                    binding.btn.visibility = View.VISIBLE
                }
                Resource.Status.LOADING -> {
                    binding.prg.visibility = View.VISIBLE
                    binding.btn.visibility = View.GONE
                }
            }
        })
    }
    //funkcija za promenu boje pozadine
    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }


}