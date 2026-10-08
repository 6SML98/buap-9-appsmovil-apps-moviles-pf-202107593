package com.example.pf_202107593

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAdmin = findViewById<Button>(R.id.btnIrAdmin)
        val btnAlumno = findViewById<Button>(R.id.btnIrAlumno)

        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        btnAlumno.setOnClickListener {
            startActivity(Intent(this, AlumnoActivity::class.java))
        }
    }
}