package com.example.mystory.ui.maps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.mystory.R
import com.example.mystory.data.remote.response.StoryItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(val context: Context, val hashMap: MutableMap<String, StoryItem>) : GoogleMap.InfoWindowAdapter {
    private val window: View = LayoutInflater.from(context).inflate(R.layout.info_window_custom, null)

    private fun renderWindowText(marker: Marker, view: View) {
        Log.d("CustomInfoWindowAdapter", context.toString())
        val title = marker.title
        val tvTitle = view.findViewById<TextView>(R.id.tv_infowindow_name)
        if(!title.equals("")) {
            tvTitle.text = title
        }

        val key = marker.snippet
        val storyItem = hashMap[key]
        val tvDescription = view.findViewById<TextView>(R.id.tv_infowindow_description)
        if(storyItem != null) {
            tvDescription.text = cutSnippet(storyItem.description)
        }
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    fun cutSnippet(description: String): String {
        return if(description.length > 20)
            description.take(50) + "..."
        else
            description
    }

    override fun getInfoWindow(p0: Marker): View {
        renderWindowText(p0, window)
        return window
    }
}