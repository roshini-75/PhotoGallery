package com.example.myproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.myproject.R
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<View>(R.id.username) as TextView
        val password = findViewById<View>(R.id.password) as TextView
        val loginbtn = findViewById<View>(R.id.loginbtn) as MaterialButton

        //dmin and admin
        loginbtn.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val loginRequest = LoginRequest(username.text.toString(), password.text.toString())

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.login(loginRequest)
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            // Login successful, navigate to the next screen

                            val jwtString = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTUsInVzZXJuYW1lIjoia21pbmNoZWxsZSIsImVtYWlsIjoia21pbmNoZWxsZUBxcS5jb20iLCJmaXJzdE5hbWUiOiJKZWFubmUiLCJsYXN0TmFtZSI6IkhhbHZvcnNvbiIsImdlbmRlciI6ImZlbWFsZSIsImltYWdlIjoiaHR0cHM6Ly9yb2JvaGFzaC5vcmcvYXV0cXVpYXV0LnBuZyIsImlhdCI6MTY5OTExMTE4OSwiZXhwIjoxNjk5MTE0Nzg5fQ.Yd8RY9hKP2f7zuqxQFt4DB7RViiI8eicbaXz0OMEFPk"

// Decode and log the JWT
                            JWTUtils.decoded(jwtString)

                            val intent = Intent(this@MainActivity, OptionsActivity ::class.java)
                            intent.putExtra("user_token", loginResponse.token)
                            startActivity(intent)


                        } else {
                            // Handle invalid response
                        }
                    } else {
                        val errorResponse = response.errorBody()
                        if (errorResponse != null) {
                            val error = Gson().fromJson(errorResponse.string(), ErrorResponse::class.java)
                            // Handle error response (e.g., show a toast with the error message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle network or other errors
                }
            }
        }

    }}
