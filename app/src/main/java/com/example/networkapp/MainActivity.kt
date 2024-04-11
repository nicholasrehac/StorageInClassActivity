package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.lang.StringBuilder

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private val saveFileName = "savefile"
    private lateinit var saveFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        saveFile = File(filesDir, saveFileName)

        loadComicFile()?.also {
            showComic(it)
        }

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString()) {comicJSON ->
                comicJSON?.also {
                    writeComicFile(it)
                    showComic(it)
                }
            }
        }

    }

    private fun downloadComic (comicId: String, returnCallback : (comicJSON: JSONObject?) -> Unit) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {returnCallback(it)}, {
                returnCallback(null)
            })
        )
    }

    private fun loadComicFile() : JSONObject? {
        if(!saveFile.canRead()) return null
        val comicJson = JSONObject(saveFile.readText())
        return comicJson
    }

    private fun writeComicFile(comicObject: JSONObject) {
        if(!saveFile.exists()) saveFile.createNewFile()
        if(!saveFile.canWrite()) return
        saveFile.writeText(comicObject.toString())
    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }


}