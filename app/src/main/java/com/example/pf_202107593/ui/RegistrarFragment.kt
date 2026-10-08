package com.example.pf_202107593.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pf_202107593.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrarFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_registrar, container, false)

        val etNombre = v.findViewById<EditText>(R.id.etNombre)
        val etMatricula = v.findViewById<EditText>(R.id.etMatricula)
        val etCarrera = v.findViewById<EditText>(R.id.etCarrera)
        val etGrupo = v.findViewById<EditText>(R.id.etGrupo)
        val btnGuardar = v.findViewById<Button>(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val carrera = etCarrera.text.toString().trim()
            val grupo = etGrupo.text.toString().trim()

            if (nombre.isEmpty() || matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Complete los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = hashMapOf(
                "nombre" to nombre,
                "matricula" to matricula,
                "carrera" to carrera,
                "grupo" to grupo,
                "qr" to matricula,
                "fechaRegistro" to FieldValue.serverTimestamp()
            )

            db.collection("alumnos").add(data)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Alumno registrado", Toast.LENGTH_SHORT).show()
                    etNombre.text.clear()
                    etMatricula.text.clear()
                    etCarrera.text.clear()
                    etGrupo.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return v
    }

}
