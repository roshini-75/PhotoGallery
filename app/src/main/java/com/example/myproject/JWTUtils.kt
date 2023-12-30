package com.example.myproject

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException

object JWTUtils {

    fun decoded(JWTEncoded: String) {
        try {
            val split = JWTEncoded.split("\\.".toRegex()).toTypedArray()
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]))
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            // Handle error
        }
    }

    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return try {
            String(decodedBytes, charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            // Handle error
            ""
        }
    }
}