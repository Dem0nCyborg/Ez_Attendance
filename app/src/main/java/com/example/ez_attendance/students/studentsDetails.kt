package com.example.ez_attendance.students

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ez_attendance.R
import com.example.ez_attendance.TTAdapter
import com.example.ez_attendance.databinding.ActivityStudentsDetailsBinding
import com.example.ez_attendance.login.UserSelection
import com.example.ez_attendance.timetable
import com.google.android.material.navigation.NavigationBarView

class studentsDetails : AppCompatActivity() {

    lateinit var binding : ActivityStudentsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    Toast.makeText(this, "Already There", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.item_2 -> {
                    startActivity(Intent(this, attendance::class.java))
                    //Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> false
            }

        }

        val lectures = ArrayList<timetable>()

        val queue = Volley.newRequestQueue(this)
        val url = "https://script.google.com/macros/s/AKfycbzLvQ4m0gdM-laTg7c1BwVNTMyJSIqU96XvfO0c898PlHGgBYvLru2TNdNsOGhvD6a1/exec"
        val jsonObjectRequest= object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val jsonArray = response.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val day = jsonObject.getString("day")
                    val lec1 = jsonObject.getString("lec1")
                    val lec2 = jsonObject.getString("lec2")
                    val lec3 = jsonObject.getString("lec3")
                    val lec4 = jsonObject.getString("lec4")
                    val lec5 = jsonObject.getString("lec5")
                    val lec6 = jsonObject.getString("lec6")
                    val lec7 = jsonObject.getString("lec7")
                    val lec8 = jsonObject.getString("lec8")
                    lectures.add(timetable(day, lec1, lec2, lec3, lec4, lec5, lec6, lec7, lec8))
                }
                val rvtimetable = binding.recyclerView
                rvtimetable.layoutManager = LinearLayoutManager(this)
                rvtimetable.setHasFixedSize(true)
                rvtimetable.adapter = TTAdapter(lectures)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        queue.add(jsonObjectRequest)




    }
}