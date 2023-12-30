package com.example.myproject

import android.os.AsyncTask
import android.os.Bundle
import android.widget.AbsListView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.example.myproject.R
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ImageGridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_grid)

        // Add your image grid code here
        val jsonPlaceholderUrl = "https://jsonplaceholder.typicode.com/photos"
        FetchImageUrlsTask().execute(jsonPlaceholderUrl)

        val gridView = findViewById<GridView>(R.id.gridView)
        val adapter = ImageAdapter(this)

        // Set the adapter initially
        gridView.adapter = adapter

        // Set up an OnScrollListener to detect when the user is near the end of the grid
        gridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastVisibleItem = firstVisibleItem + visibleItemCount
                if (lastVisibleItem == totalItemCount) {
                    adapter.loadNextPage() // Load the next page when the user reaches the end
                }
            }

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                // Not needed for pagination
            }
        })
    }
    private inner class FetchImageUrlsTask : AsyncTask<String, Void, List<String>>() {
        override fun doInBackground(vararg params: String): List<String> {
            val imageUrlList = mutableListOf<String>()
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }


                val jsonArray = JSONArray(stringBuilder.toString())

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val imageUrl = jsonObject.getString("thumbnailUrl")
                    imageUrlList.add(imageUrl)
                }

                return imageUrlList
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return imageUrlList
        }

        override fun onPostExecute(result: List<String>) {
            val gridView = findViewById<GridView>(R.id.gridView)
            val adapter = gridView.adapter as ImageAdapter // Get the existing adapter
            adapter.setImageUrls(result) // Set the image URLs in the adapter
        }
    }
}
