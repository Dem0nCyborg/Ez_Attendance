package com.example.ez_attendance.professor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ez_attendance.GlobalData
import com.example.ez_attendance.MainActivity
import com.example.ez_attendance.R
import com.example.ez_attendance.databinding.ActivityProfDetailsBinding

class profDetails : AppCompatActivity() {

    private lateinit var binding: ActivityProfDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val departments = resources.getStringArray(R.array.Departments)
        val depAdapter = ArrayAdapter(this, R.layout.dropdown_item, departments)
        binding.autoCompleteTextView.setAdapter(depAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedDepartment = parent.getItemAtPosition(position).toString()
            GlobalData.deparment = selectedDepartment
        }


        val classes = resources.getStringArray(R.array.Class)
        val classAdapter = ArrayAdapter(this, R.layout.dropdown_item, classes)
        binding.classtextView.setAdapter(classAdapter)

        binding.classtextView.setOnItemClickListener() {parent, view, position, id ->
            if (GlobalData.deparment == "") {
                Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show()
            } else {
                val selectedClass = parent.getItemAtPosition(position).toString()
                GlobalData.classes= selectedClass
                val subjectsTE = resources.getStringArray(R.array.Subject)
                val subjectsElse = resources.getStringArray(R.array.subjectElse)
                val subjectAdapter = if (GlobalData.classes == "Third Year-TE") {
                    ArrayAdapter(this, R.layout.dropdown_item, subjectsTE)
                } else {
                    ArrayAdapter(this, R.layout.dropdown_item, subjectsElse)
                }
                binding.subjectextView.setAdapter(subjectAdapter)

            }
        }



        binding.subjectextView.setOnItemClickListener() {parent, view, position, id ->
            if (GlobalData.classes == "") {
                Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show()
            } else {
                val selectedSubject = parent.getItemAtPosition(position).toString()
                GlobalData.subject = selectedSubject
                Toast.makeText(this, GlobalData.subject, Toast.LENGTH_SHORT).show()
            }
        }

        val timeings = resources.getStringArray(R.array.Time)
        val timeAdapter = ArrayAdapter(this, R.layout.dropdown_item, timeings)
        binding.timetextView.setAdapter(timeAdapter)

        binding.timetextView.setOnItemClickListener() {parent, view, position, id ->
            val selectedTime = parent.getItemAtPosition(position).toString()
            GlobalData.time = selectedTime
            Toast.makeText(this, GlobalData.time, Toast.LENGTH_SHORT).show()
        }

        binding.btnSubmit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



    }
}