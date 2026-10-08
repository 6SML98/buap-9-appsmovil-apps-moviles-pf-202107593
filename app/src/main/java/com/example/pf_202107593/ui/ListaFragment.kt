package com.example.pf_202107593.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pf_202107593.R
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaFragment : Fragment() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_lista, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.rvAlumnos)
        rv.layoutManager = LinearLayoutManager(requireContext())
        // Inicializar adapter vacío y suscribirse a cambios en tiempo real
        rv.adapter = AlumnosAdapter(mutableListOf(), ::onEdit, ::onDelete)
        subscribeAlumnos(rv)

        return v
    }

    private fun subscribeAlumnos(rv: RecyclerView) {
        listener = db.collection("alumnos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val items = snapshot?.documents?.map { doc ->
                    val m = doc.data ?: mapOf<String, Any>()
                    ItemAlumno(doc.id, m)
                } ?: emptyList()

                val adapter = rv.adapter
                if (adapter is AlumnosAdapter) {
                    adapter.updateItems(items)
                } else {
                    rv.adapter = AlumnosAdapter(items.toMutableList(), ::onEdit, ::onDelete)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.remove()
        listener = null
    }

    private fun onEdit(id: String, data: MutableMap<String, Any>) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_alumno, null)
        val etNombre = view.findViewById<EditText>(R.id.etEditNombre)
        val etMat = view.findViewById<EditText>(R.id.etEditMatricula)
        val etCar = view.findViewById<EditText>(R.id.etEditCarrera)
        val etGru = view.findViewById<EditText>(R.id.etEditGrupo)

        etNombre.setText(data["nombre"]?.toString() ?: "")
        etMat.setText(data["matricula"]?.toString() ?: "")
        etCar.setText(data["carrera"]?.toString() ?: "")
        etGru.setText(data["grupo"]?.toString() ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Alumno")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val newData = mapOf(
                    "nombre" to etNombre.text.toString(),
                    "matricula" to etMat.text.toString(),
                    "carrera" to etCar.text.toString(),
                    "grupo" to etGru.text.toString(),
                    "qr" to etMat.text.toString()
                )
                db.collection("alumnos").document(id).update(newData)
                    .addOnSuccessListener { Toast.makeText(requireContext(), "Alumno actualizado", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun onDelete(id: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar alumno")
            .setMessage("¿Eliminar alumno?")
            .setPositiveButton("Sí") { _, _ ->
                db.collection("alumnos").document(id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Alumno eliminado", Toast.LENGTH_SHORT).show()
                        // La suscripción en tiempo real actualizará la lista automáticamente
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    data class ItemAlumno(val id: String, val data: Map<String, Any>)

    class AlumnosAdapter(
        private val items: MutableList<ItemAlumno>,
        private val onEdit: (String, MutableMap<String, Any>) -> Unit,
        private val onDelete: (String) -> Unit
    ) : RecyclerView.Adapter<AlumnosAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvNombre = v.findViewById<android.widget.TextView>(R.id.itemNombre)
            val tvMat = v.findViewById<android.widget.TextView>(R.id.itemMatricula)
            val btnEdit = v.findViewById<Button>(R.id.btnEdit)
            val btnDel = v.findViewById<Button>(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_alumno, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            val nombre = item.data["nombre"]?.toString() ?: ""
            val mat = item.data["matricula"]?.toString() ?: ""
            holder.tvNombre.text = nombre
            holder.tvMat.text = mat
            holder.btnEdit.setOnClickListener { onEdit(item.id, item.data.toMutableMap()) }
            holder.btnDel.setOnClickListener { onDelete(item.id) }
        }

        override fun getItemCount(): Int = items.size

        fun updateItems(newItems: List<ItemAlumno>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }
    }

}
