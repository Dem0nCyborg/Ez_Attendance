package com.example.ez_attendance.professor

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ez_attendance.GlobalData
import com.example.ez_attendance.R
import com.example.ez_attendance.databinding.ActivityUpdateTimetableBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

class updateTimetable : AppCompatActivity() {

    lateinit var binding : ActivityUpdateTimetableBinding
    lateinit var day: String
    lateinit var lec: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val days = resources.getStringArray(R.array.Days)
        val dayAdapter = ArrayAdapter(this, R.layout.dropdown_item, days)
        binding.acDay.setAdapter(dayAdapter)

        binding.acDay.setOnItemClickListener { parent, view, position, id ->
            val selectedDay = parent.getItemAtPosition(position).toString()
            day = selectedDay
        }

        val lectures = resources.getStringArray(R.array.lectureTime)
        val lecAdapter = ArrayAdapter(this, R.layout.dropdown_item, lectures)
        binding.acLecture.setAdapter(lecAdapter)

        binding.acLecture.setOnItemClickListener { parent, view, position, id ->
            val selectedLec = parent.getItemAtPosition(position).toString()
            lec = selectedLec
        }



        binding.btnAdd.setOnClickListener {
            val value = binding.etSubject.text.toString().uppercase()
            if (day == null || lec == null || value == null) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                postData(day, lec, value)
                Toast.makeText(this, "Data posted successfully", Toast.LENGTH_SHORT).show()
            }

        }





    }

    private fun postData(day: String, lec: String, value: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val scriptUrl = "https://script.google.com/macros/s/AKfycbzLvQ4m0gdM-laTg7c1BwVNTMyJSIqU96XvfO0c898PlHGgBYvLru2TNdNsOGhvD6a1/exec"
            val url = URL(scriptUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            val postDataParams = HashMap<String, String>()
            postDataParams["day"] = day
            postDataParams["lec"] = lec
            postDataParams["value"] = value

            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Handle success on the main/UI thread
                launch(Dispatchers.Main) {
                    Toast.makeText(this@updateTimetable, "Data posted successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle error on the main/UI thread
                launch(Dispatchers.Main) {
                    Toast.makeText(this@updateTimetable, "Error posting data: $responseCode", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }
}