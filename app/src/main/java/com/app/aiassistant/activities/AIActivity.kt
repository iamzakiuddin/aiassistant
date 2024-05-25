package com.app.aiassistant.activities

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.app.aiassistant.NetworkConnectionManager
import com.app.aiassistant.R
import com.app.aiassistant.network.NetworkResources
import com.app.aiassistant.viewmodels.AIViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class AIActivity : AppCompatActivity() {

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager
    private var speechRecognizerIntent: Intent? = null
    private val aiViewModel : AIViewModel by viewModels()
    var doneBtn : Button? = null
    var loadingView: ProgressBar? =  null
    private var isComingFromSettings: Boolean = false
    val RecordAudioRequestCode = 1
    private var speechRecognizer: SpeechRecognizer? = null
    var textToSpeech: TextToSpeech? = null
    private var constraintLayout: ConstraintLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_activity)
        //networkConnectionManager.startListenNetworkState()
        initViews()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission()
        }
        initializeTextToSpeech()
        setupSpeechRecognizerListeners()
        setupClickListener()
    }
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
              /*  val locale = Locale("ar")
                textToSpeech?.setLanguage(locale)
                val voiceName: String = locale.toLanguageTag()
                val voice =
                    Voice(voiceName, locale, Voice.QUALITY_HIGH, Voice.LATENCY_NORMAL, false, null)
                textToSpeech?.setVoice(voice)*/
                textToSpeech?.setLanguage(Locale.US)
            }
        }
        textToSpeech?.setSpeechRate(1.0f)
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.d("TTS", "Utterance started: $utteranceId")
            }

            override fun onDone(utteranceId: String) {
                Log.d("TTS", "Utterance completed: $utteranceId")
            }

            override fun onError(utteranceId: String) {
                Log.e("TTS", "Utterance error: $utteranceId")
            }

            override fun onStop(utteranceId: String, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
                Log.d("TTS", "Utterance stopped: $utteranceId, interrupted: $interrupted")
            }
        })
    }

    private fun setupClickListener() {
        doneBtn?.setOnClickListener {
            if (isNetworkConnected()){
                if (textToSpeech?.isSpeaking == true){
                    textToSpeech?.stop()
                }
                speechRecognizer?.startListening(speechRecognizerIntent)
            } else{
                val snackbar =
                    constraintLayout?.let { it1 -> Snackbar.make(it1, "No Internet available!", Snackbar.LENGTH_SHORT) }
                snackbar?.show()
            }
        }

        aiViewModel.chatResponse().observe(this, Observer { result ->
            when (result.status) {
                NetworkResources.NetworkStatus.LOADING -> {
                    loadingView?.visibility =  View.VISIBLE
                }

                NetworkResources.NetworkStatus.SUCCESS -> {
                    loadingView?.visibility =  View.GONE
                    val speechText = result.data?.choices?.get(0)?.message?.content
                    if (!speechText.isNullOrEmpty()){
                        textToSpeech?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }

                NetworkResources.NetworkStatus.ERROR -> {
                    loadingView?.visibility =  View.GONE
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    loadingView?.visibility =  View.GONE
                    Toast.makeText(this,"Some thing went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupSpeechRecognizerListeners() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, ComponentName(
            "com.google.android.tts",
            "com.google.android.apps.speech.tts.googletts.service.GoogleTTSRecognitionService"
        )
        )

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
               loadingView?.visibility = View.VISIBLE
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {
                Log.e("End of speech","onEndOfSpeech")
            }
            override fun onError(i: Int) {
                loadingView?.visibility = View.GONE
                Toast.makeText(this@AIActivity,"Speech is not captured",Toast.LENGTH_SHORT).show()
            }
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val inputText = data!![0]
                inputText?.let {
                    aiViewModel.getChatCompletion(it)
                }
              }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    private fun initViews() {
        constraintLayout = findViewById(R.id.rootLayout)
        doneBtn = findViewById(R.id.done)
        loadingView = findViewById(R.id.loading)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty()){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        showNotificationPermissionRationale()
                    } else {
                        showSettingDialog()
                    }
                }
            }else {
                Toast.makeText(this,"Audio permission granted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotificationPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage("This app needs permission to convert speech to text. If denied, this feature may not work properly. Thank you for understanding.")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialogInterface, i -> checkPermission()})
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> finish()})
            .show()
    }

    private fun showSettingDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Audio record permission is required, Please allow audio permission from setting")
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.ok
            ) { dialogInterface, i ->
                isComingFromSettings = true
                openAppSettings()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> finish() }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
        networkConnectionManager.stopListenNetworkState()
    }

    override fun onPause() {
        super.onPause()
        textToSpeech?.stop()
        speechRecognizer?.cancel()
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}