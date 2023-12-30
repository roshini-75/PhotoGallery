package com.example.myproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.myproject.R
import com.google.android.material.button.MaterialButton

class OptionsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.options_activity)
        val phbutton = findViewById<View>(R.id.phbutton) as Button

        val locationButton = findViewById<View>(R.id.locationButton) as Button

        val logoutButton= findViewById<View>(R.id.logoutButton) as Button


        phbutton.setOnClickListener{

            val intent = Intent(this@OptionsActivity, ImageGridActivity::class.java)

            startActivity(intent)

        }

        logoutButton.setOnClickListener{

            val mainIntent = Intent(this@OptionsActivity, MainActivity::class.java)
            mainIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(mainIntent)
            finish()

        }

        locationButton.setOnClickListener{

            val locationIntent = Intent(this@OptionsActivity, MapActivity::class.java)

            startActivity(locationIntent)

        }
    }
}