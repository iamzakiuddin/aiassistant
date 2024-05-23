package com.app.aiassistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.app.aiassistant.utils.SharedPreferenceHelper

class MainActivity : AppCompatActivity() {

    private var name: EditText? = null
    private var about: EditText? = null
    private var interests: EditText? = null
    private var hobbies: EditText? = null

    private var save: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(this,"isUserSaved",false)){
            startActivity(Intent(this,AIActivity::class.java))
            finish()
        }else{
            setContentView(R.layout.activity_main)
            initViews()
        }
    }

    private fun saveLocallyAndNavigate() {
        SharedPreferenceHelper.setSharedPreferenceBoolean(this,"isUserSaved",true)
        SharedPreferenceHelper.setSharedPreferenceString(this,"name",name?.text?.toString() ?: "")
        SharedPreferenceHelper.setSharedPreferenceString(this,"about",about?.text?.toString() ?: "")
        SharedPreferenceHelper.setSharedPreferenceString(this,"interests",hobbies?.text?.toString() ?: "")
        SharedPreferenceHelper.setSharedPreferenceString(this,"hobbies",hobbies?.text?.toString() ?: "")

        startActivity(Intent(this,AIActivity::class.java))
        finish()
    }

    private fun initViews() {
        name = findViewById(R.id.name)
        about = findViewById(R.id.about)
        interests = findViewById(R.id.interests)
        hobbies = findViewById(R.id.hobbies)
        save = findViewById(R.id.done)

        save?.setOnClickListener {
            saveLocallyAndNavigate()
        }

    }
}