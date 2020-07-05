package com.adamian.daypicture.ui.main

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.adamian.daypicture.R
import com.adamian.daypicture.ui.DataStateListener
import com.adamian.daypicture.util.DataState
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    DataStateListener
{

    private var FIRST_OPEN = 0
    private val PREF_NAME = "firstOpen"

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, FIRST_OPEN)

        var tade:Boolean = sharedPref.getBoolean(PREF_NAME, false)

        println("isFirstOpen: $tade" )

        if(!sharedPref.getBoolean(PREF_NAME, false)) {
            val editor = sharedPref.edit()
            editor.putBoolean(PREF_NAME, true)
            editor.apply()

            initTranslations()
            triggerInfoDialog()
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        showMainFragment()


    }

    fun initTranslations(){
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.EL)
            .build()
        val englishElTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishElTranslator.downloadModelIfNeeded();

        val optionsSpanish = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.ES)
            .build()
        val englishSpTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(optionsSpanish)
        englishSpTranslator.downloadModelIfNeeded();

    }

    fun triggerInfoDialog(){
        onCreateInfoDialog()
    }

    fun onCreateInfoDialog(): AlertDialog {

        return let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Welcome to AstroPicture! ")
                .setMessage(
                    "Seems you open the application for the first time. Two important things for the new users: \n\n" +
                            "1. For the application operations, is necessary the internet connection.\n\n" +
                            "2. The application just started to download data packages for the translations, this process will be done only once. The translation operation will be ready after the successful download of these packages.\n\n"+
                            "Enjoy...")

            // On click listener for dialog buttons
            val dialogClickListener = DialogInterface.OnClickListener{builder,which ->
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> builder.dismiss()// dismiss
                }
            }
            builder.setPositiveButton("Understood",dialogClickListener)

            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun showMainFragment(){
        if(supportFragmentManager.fragments.size == 0){
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MainFragment(),
                    "MainFragment"
                )
                .commit()
        }
    }

    fun handleDataStateChange(dataState: DataState<*>?){
        dataState?.let{
            // Handle loading
            showProgressBar(dataState.loading)

            // Handle Message
            dataState.message?.let{ event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showProgressBar(isVisible: Boolean){
        if(isVisible){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.INVISIBLE
        }
    }


}























