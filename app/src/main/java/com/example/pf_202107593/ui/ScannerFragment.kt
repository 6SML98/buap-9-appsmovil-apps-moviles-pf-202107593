package com.example.pf_202107593.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pf_202107593.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.google.zxing.BarcodeFormat

class ScannerFragment : Fragment() {

        private lateinit var barcodeView: DecoratedBarcodeView
        private lateinit var tvResultado: TextView
        private val db = Firebase.firestore

        private var procesando = false
        private var ultimoCodigo: String? = null
        private var ultimaLecturaTs: Long = 0

        companion object {
            const val REQ_CAMERA = 101
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val v = inflater.inflate(R.layout.fragment_scanner, container, false)
            barcodeView = v.findViewById(R.id.zxing_barcode_scanner)
            tvResultado = v.findViewById(R.id.tvResultado)

            // Decodificar solo QR para evitar confusión con otros formatos
            barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))

            barcodeView.decodeContinuous(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        val codigo = it.text.trim()
                        val ahora = System.currentTimeMillis()

                        // Evitar procesar el mismo código repetido en breve
                        if (procesando) return
                        if (ultimoCodigo != null && ultimoCodigo == codigo && ahora - ultimaLecturaTs < 3000) return

                        procesando = true
                        ultimoCodigo = codigo
                        ultimaLecturaTs = ahora

                        tvResultado.text = "Leído: $codigo"

                        // Pausar temporalmente el scanner mientras procesamos
                        try { barcodeView.pause() } catch (_: Exception) {}

                        processMatricula(codigo)
                    }
                }

                override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
            })

            return v
        }

        override fun onResume() {
            super.onResume()
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQ_CAMERA)
            } else {
                try { barcodeView.resume() } catch (_: Exception) {}
            }
        }

        override fun onPause() {
            try { barcodeView.pause() } catch (_: Exception) {}
            super.onPause()
        }

        private fun processMatricula(matricula: String) {
            Toast.makeText(requireContext(), "Buscando alumno: $matricula", Toast.LENGTH_SHORT).show()

            // Buscar alumno en Firestore
            db.collection("alumnos").whereEqualTo("matricula", matricula).get()
                .addOnSuccessListener { qs ->
                    if (!qs.isEmpty) {
                        val doc = qs.documents[0]
                        val alumno = doc.data ?: mapOf<String, Any>()

                        val fecha = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
                        val hora = java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())
                        val asistenciaId = "${matricula}_$fecha"

                        val asistenciaRef = db.collection("asistencias").document(asistenciaId)

                        asistenciaRef.get().addOnSuccessListener { aDoc ->
                            if (aDoc.exists()) {
                                tvResultado.text = "Ya registraste tu asistencia hoy\nNombre: ${alumno["nombre"]}\nMatrícula: ${alumno["matricula"]}"
                                scheduleResumeScanner()
                            } else {
                                val data = hashMapOf(
                                    "nombre" to (alumno["nombre"] ?: ""),
                                    "matricula" to (alumno["matricula"] ?: matricula),
                                    "carrera" to (alumno["carrera"] ?: ""),
                                    "grupo" to (alumno["grupo"] ?: ""),
                                    "fecha" to fecha,
                                    "hora" to hora
                                )

                                asistenciaRef.set(data)
                                    .addOnSuccessListener {
                                        tvResultado.text = "Asistencia registrada\nNombre: ${alumno["nombre"]}\nMatrícula: ${alumno["matricula"]}"
                                        scheduleResumeScanner()
                                    }
                                    .addOnFailureListener { e ->
                                        tvResultado.text = "Error registrando asistencia: ${e.message}"
                                        scheduleResumeScanner()
                                    }
                            }
                        }.addOnFailureListener { e ->
                            tvResultado.text = "Error comprobando asistencia: ${e.message}"
                            scheduleResumeScanner()
                        }
                    } else {
                        tvResultado.text = "Alumno no encontrado"
                        scheduleResumeScanner()
                    }
                }
                .addOnFailureListener { e ->
                    tvResultado.text = "Error buscando alumno: ${e.message}"
                    scheduleResumeScanner()
                }
        }

        private fun scheduleResumeScanner(delayMs: Long = 3000) {
            // Re-enable scanning después de delay
            view?.postDelayed({
                try { barcodeView.resume() } catch (_: Exception) {}
                procesando = false
            }, delayMs)
        }

    }
