package com.example.myproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myproject.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageAdapter(private val context: Context) : BaseAdapter() {

    private val imageUrls = mutableListOf<String>()
    private val maxCacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8 // 1/8th of available memory

    private val images = LruCache<String, Bitmap>(maxCacheSize)
    private val displayedImages = mutableListOf<String>()
    private val maxImagesPerPage = 16 // You can adjust this value as needed
    private var currentPage = 0

    fun setImageUrls(urls: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(urls)
        loadNextPage()
    }

    fun loadNextPage() {
        val startIndex = currentPage * maxImagesPerPage
        val endIndex = minOf((currentPage + 1) * maxImagesPerPage, imageUrls.size)

        if (startIndex < endIndex) {
            displayedImages.addAll(imageUrls.subList(startIndex, endIndex))
            notifyDataSetChanged()
            currentPage++
        }
    }

    override fun getCount(): Int {
        return displayedImages.size
    }

    override fun getItem(position: Int): Any {
        val imageUrl = displayedImages[position]
        val image = images.get(imageUrl)
        return image ?: BitmapFactory.decodeResource(context.resources, R.drawable.placeholder_image)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private fun downloadImageAsync(position: Int, imageUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    images.put(imageUrl, bitmap)
                    GlobalScope.launch(Dispatchers.Main) {
                        notifyDataSetChanged()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle download failure here
            }
        }
    }

    // Inside the getView method of ImageAdapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ImageViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
            holder = ImageViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ImageViewHolder
        }

        val imageUrl = displayedImages[position]
        val image = images.get(imageUrl)

        if (image != null) {
            holder.imageView.setImageBitmap(image)
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image)
            downloadImageAsync(position, imageUrl)
        }

        // Extract and set the number from the "thumbnailUrl"
        val number = extractNumberFromImageUrl(imageUrl)

        // Set the text for the TextView in the layout
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = number

        // Load more images when reaching the end
        if (position == displayedImages.size - 1) {
            loadNextPage()
        }

        return view
    }

    // Add a function to extract the number from the "thumbnailUrl"
    // Update the extractNumberFromImageUrl function to return the full "150/92c952" string
    private fun extractNumberFromImageUrl(imageUrl: String): String {
        // Assuming "thumbnailUrl" has a fixed format like "https://via.placeholder.com/150/92c952"
        val parts = imageUrl.split("/")
        return if (parts.size >= 5) {
            parts[4] // This will give you the "150/92c952" part of the URL
        } else {
            "Image Label"
        }
    }



    private class ImageViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
