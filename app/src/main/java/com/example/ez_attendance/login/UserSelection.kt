package com.example.ez_attendance.login

import ReadNFC
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ez_attendance.R
import com.example.ez_attendance.databinding.ActivityUserSelectionBinding
import com.example.ez_attendance.professor.profDetails
import com.example.ez_attendance.professor.updateTimetable
import com.example.ez_attendance.students.studentsDetails

class UserSelection : AppCompatActivity() {

    private lateinit var binding: ActivityUserSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityUserSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.cvProfessor.setOnClickListener {
            startActivity(Intent(this, updateTimetable::class.java))
        }

        binding.cvStudent.setOnClickListener {
            startActivity(Intent(this, studentsDetails::class.java))
        }

    }
}