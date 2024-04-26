package com.example.ez_attendance.students

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidplot.pie.PieChart
import com.example.ez_attendance.R
import com.example.ez_attendance.databinding.ActivityAttendanceBinding
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class attendance : AppCompatActivity() {

    lateinit var binding: ActivityAttendanceBinding
    var num1: String? = null
    var num2: String? = null
    lateinit var name:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSearch.setOnClickListener {
            name = binding.etName.text.toString()
            Toast.makeText(this, "Searching for $name", Toast.LENGTH_SHORT).show()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val scriptUrl = "https://script.google.com/macros/s/AKfycbwhh4JWkV-_JQThdLAEfGHr8QZUxRzzORkddFNUrciIAJU1tUxFjqk1ewlqHSYbgM7z/exec?name="+name // Replace with your actual script URL
                FetchAttendanceTask().execute(scriptUrl)
                binding.cvSearch.visibility = View.GONE

            }
        }

        binding.fab.setOnClickListener {
            binding.cvSearch.visibility = View.VISIBLE
            binding.etName.text.clear()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etName.windowToken, 0)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    startActivity(Intent(this, studentsDetails::class.java))
                    //Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.item_2 -> {

                    Toast.makeText(this, "Already There", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> false
            }

        }




        // Execute FetchAttendanceTask

    }

    inner class FetchAttendanceTask : AsyncTask<String, Void, Pair<String, String>>() {

        override fun doInBackground(vararg params: String?): Pair<String, String> {
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(params[0])
                urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String? = null
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                bufferedReader.close()
                val result = stringBuilder.toString()
                val jsonArray = JSONArray(result)
                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    num1 = jsonObject.getString("completedLectures")
                    num2 = jsonObject.getString("attendedLectures")
                }
            } catch (e: Exception) {
                Log.e("FetchAttendanceTask", "Error fetching data: ${e.message}")
            } finally {
                urlConnection?.disconnect()
            }
            return Pair(num1 ?: "", num2 ?: "")
        }

        override fun onPostExecute(result: Pair<String, String>?) {
            super.onPostExecute(result)
            // Do something with num1 and num2 (e.g., update UI)
            val num1 = result?.first ?: ""
            val num2 = result?.second ?: ""
            Toast.makeText(this@attendance, "Completed Lectures: $num1, Attended Lectures: $num2", Toast.LENGTH_SHORT).show()

            createGraph(num1.toInt(),num2.toInt())
        }
    }

        fun createGraph(num1: Int, num2: Int) {
            val missed = num1-num2 // Total number of lectures (you may adjust this as needed)

            // Calculate remaining lectures

            // Update pie chart segments
            val seg1 = com.androidplot.pie.Segment("Lecture Completed $num1", num1.toDouble())
            val seg2 = com.androidplot.pie.Segment("Lecture Attended $num2", num2.toDouble())
            val seg3 = com.androidplot.pie.Segment("Missed Lectures $missed", missed.toDouble())

            val sf1 = com.androidplot.pie.SegmentFormatter(Color.GRAY)
            val sf2 = com.androidplot.pie.SegmentFormatter(Color.RED)
            val sf3 = com.androidplot.pie.SegmentFormatter(Color.BLUE)

            val pie = findViewById<PieChart>(R.id.plot)

            pie.clear()
            pie.addSegment(seg1, sf1)
            pie.addSegment(seg2, sf2)
            pie.addSegment(seg3, sf3)

            pie.redraw()

            Toast.makeText(this, "Graph created", Toast.LENGTH_SHORT).show()
        }


}
