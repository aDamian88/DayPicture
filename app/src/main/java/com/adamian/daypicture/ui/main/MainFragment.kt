package com.adamian.daypicture.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adamian.daypicture.R
import com.adamian.daypicture.model.DayPicture
import com.adamian.daypicture.ui.DataStateListener
import com.adamian.daypicture.ui.main.state.MainStateEvent.GetDataEvent
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val TAG: String = "AppDebug"

    lateinit var viewModel: MainViewModel

    lateinit var dataStateHandler: DataStateListener

    lateinit var currentDayPicture: DayPicture

    var currentSavedInstanceState: Bundle? = null

    var currentLanguage = FirebaseTranslateLanguage.EN

    lateinit var viewFragment: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        triggerGetDataEvent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewFragment = view

        currentSavedInstanceState = savedInstanceState
        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        subscribeObservers()
        // Insertion of network observer. When the network is on should trigger the event.
        triggerGetDataEvent()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            // Handle Loading and Message
            dataStateHandler.onDataStateChange(dataState)

            // handle Data<T>
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let { mainViewState ->

                    mainViewState.dayPicture?.let {
                        // set User data
                        viewModel.setUser(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.dayPicture?.let { dayPicture ->
                // set User data to widgets
                println("DEBUG: Setting User data: ${dayPicture}")
                setDayPictureProperties(dayPicture)

            }
        })
    }


    fun setDayPictureProperties(dayPicture: DayPicture) {

        title.setText(dayPicture.title)
        description.setText(dayPicture.explanation)

        currentDayPicture = dayPicture

        var dayPictureUrl: String? = dayPicture.image
        println("dayPictureUrl: $dayPictureUrl")
        if (dayPictureUrl!!.contains("youtube")) {
            image.visibility = View.GONE

            videoView.settings.javaScriptEnabled = true
            videoView.loadUrl(dayPictureUrl)

        } else {
            videoView.visibility = View.GONE
            view?.let {
                Glide.with(it.context)
                    .load(dayPicture.image)
                    .error(R.drawable.baseline_broken_image_black_48)
                    .into(image)

            }

        }

    }


    fun triggerGetDataEvent() {
        viewModel.setStateEvent(GetDataEvent("1"))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sync -> triggerGetDataEvent()
            R.id.action_translate -> triggerTranslateEvent()
            R.id.action_info -> triggerInfoDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    fun triggerInfoDialog() {
        onCreateInfoDialog(currentSavedInstanceState)
    }

    fun onCreateInfoDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Application info")
                .setMessage(
                    "This application displays the \"Astronomy Picture of the Day\", synced by the public data of NASA.\n" +
                            "\n" +
                            "Considering the daily routine, this is a small escape to think out of a box. The application's target is to make the user understand that the world is bigger than a daily problem. By displaying an image that is out of our planet.\n" +
                            "\n" +
                            "Moreover, the image description can be translated into four languages\n" +
                            "(English, Greek, Spanish).\n" +
                            "\n" +
                            "It is an no-profit application developed by using the newest Android technologies like Kotlin, MVI design pattern, Firebase, etc.\n" +
                            "\n" +
                            "The NASA data about the \"Astronomy Picture of the Day\" are synced from the public endpoint API https://api.nasa.gov/planetary/apod. For the translation functionality, is used the ML Kit on Android from the Firebase platform.\n" +
                            "\n" + "Logo Icon \nTitle: Freepik \nFrom: https://www.flaticon.com/ \nMade by: http://www.freepik.com/?__hstc=57440181.f41b9a4defa73ac669876e3d029cd8e1.1584814413019.1593109933804.1593185536238.11&__hssc=57440181.3.1593185536238&__hsfp=1138474084"
                )
            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    var languages =
        arrayOf("English", "Greek", "Spanish")

    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Pick a language")
                .setItems(languages,
                    DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            makeTheTranslation(FirebaseTranslateLanguage.EN)
                        }

                        if (which == 1) {
                            makeTheTranslation(FirebaseTranslateLanguage.EL)
                        }

                        if (which == 2) {
                            makeTheTranslation(FirebaseTranslateLanguage.ES)
                        }
                    })
            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun makeTheTranslation(newLanguage: Int) {

        println(" translator currentLanguage ${currentLanguage}")
        println(" translator newLanguage ${newLanguage}")

        translateDescription(newLanguage)
        translateTitle(newLanguage)
    }

    private fun translateTitle(newLanguage: Int) {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(currentLanguage)
            .setTargetLanguage(newLanguage)
            .build()
        val englishElTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishElTranslator.downloadModelIfNeeded();

        englishElTranslator.translate(currentDayPicture?.title.toString())
            .addOnSuccessListener { translatedText ->
                try {
                    title.setText(translatedText)
                    currentLanguage = newLanguage
                } catch (e: Exception) {
                    println("error title translation ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                println("translator error:  ${exception}")
            }
    }

    private fun translateDescription(newLanguage: Int) {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(currentLanguage)
            .setTargetLanguage(newLanguage)
            .build()
        val englishElTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishElTranslator.downloadModelIfNeeded();

        englishElTranslator.translate(currentDayPicture?.explanation.toString())
            .addOnSuccessListener { translatedText ->
                try {
                    description?.setText(translatedText)
                    currentLanguage = newLanguage
                    Toast.makeText(activity, "Translated", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    println("error exception: ${e.message}")
                }



            }
            .addOnFailureListener { exception ->
                onSNACK(viewFragment)
            }
    }

    fun onSNACK(view: View){
        //Snackbar(view)
        val snackbar = Snackbar.make(view, "Language translation packages are still downloaded...",
            Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
//        snackbarView.setBackgroundColor(Color.LTGRAY)
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.context,R.color.colorPrimary))
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 15f
        snackbar.show()
    }


    private fun triggerTranslateEvent() {
        onCreateDialog(currentSavedInstanceState);
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }

    }
}














