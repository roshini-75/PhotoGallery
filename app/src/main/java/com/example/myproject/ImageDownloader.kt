package com.example.myproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloader {

    interface ImageDownloadListener {
        fun onImageDownloaded(result: Bitmap)
        fun onImageDownloadFailed(e: Exception)
    }

    fun downloadImage(imageUrl: String, listener: ImageDownloadListener) {
        val downloadTask = DownloadTask(listener)
        downloadTask.execute(imageUrl)
    }

    private inner class DownloadTask(private val listener: ImageDownloadListener) :
        AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageUrl = params[0]
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                listener.onImageDownloadFailed(e)
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                listener.onImageDownloaded(result)
            }
        }
    }
}
