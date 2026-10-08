package com.example.pf_202107593

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class AlumnoActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alumno)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alumnoRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etMatricula = findViewById<EditText>(R.id.etMatriculaAlumno)
        val btnGenerar = findViewById<Button>(R.id.btnGenerarQrAlumno)
        val tvNombre = findViewById<TextView>(R.id.tvNombreAlumno)
        val tvCarreraGrupo = findViewById<TextView>(R.id.tvCarreraGrupoAlumno)
        val ivQr = findViewById<ImageView>(R.id.ivQrAlumno)
        ivQr.visibility = View.GONE

        btnGenerar.setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            if (matricula.isEmpty()) {
                Toast.makeText(this, "Ingresa matrícula", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("alumnos").whereEqualTo("matricula", matricula).get()
                .addOnSuccessListener { qs ->
                    if (qs.isEmpty) {
                        tvNombre.text = "Alumno no encontrado"
                        tvCarreraGrupo.text = ""
                        ivQr.setImageBitmap(null)
                        ivQr.visibility = View.GONE
                    } else {
                        val alumno = qs.documents[0].data ?: mapOf<String, Any>()
                        val matriculaQr = (alumno["matricula"]?.toString() ?: matricula).trim()
                        val nombre = alumno["nombre"]?.toString() ?: ""
                        val carrera = alumno["carrera"]?.toString() ?: ""
                        val grupo = alumno["grupo"]?.toString() ?: ""

                        tvNombre.text = nombre
                        tvCarreraGrupo.text = "$carrera\nGrupo $grupo"
                        val qrBitmap = generateQr(matriculaQr)
                        ivQr.setImageBitmap(qrBitmap)
                        ivQr.visibility = if (qrBitmap != null) View.VISIBLE else View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    ivQr.setImageBitmap(null)
                    ivQr.visibility = View.GONE
                }
        }
    }

    private fun generateQr(text: String): Bitmap? {
        return try {
            val encoder = BarcodeEncoder()
            encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 700, 700)
        } catch (_: Exception) {
            null
        }
    }
}
