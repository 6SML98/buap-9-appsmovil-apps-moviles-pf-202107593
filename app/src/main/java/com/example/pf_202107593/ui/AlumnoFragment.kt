package com.example.pf_202107593.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pf_202107593.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class AlumnoFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_alumno, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etMatricula = view.findViewById<EditText>(R.id.etMatriculaAlumno)
        val btnGenerar = view.findViewById<Button>(R.id.btnGenerarQrAlumno)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreAlumno)
        val tvCarreraGrupo = view.findViewById<TextView>(R.id.tvCarreraGrupoAlumno)
        val ivQr = view.findViewById<ImageView>(R.id.ivQrAlumno)
        ivQr.visibility = View.GONE

        btnGenerar.setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            if (matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa matrícula", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    ivQr.setImageBitmap(null)
                    ivQr.visibility = View.GONE
                }
        }
    }

    private fun generateQr(text: String): Bitmap? {
        return try {
            val encoder = BarcodeEncoder()
            encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 700, 700)
        } catch (e: Exception) {
            null
        }
    }
}
