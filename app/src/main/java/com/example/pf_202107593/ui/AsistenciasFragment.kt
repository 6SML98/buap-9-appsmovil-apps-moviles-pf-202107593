package com.example.pf_202107593.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pf_202107593.R
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AsistenciasFragment : Fragment() {

    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_asistencias, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.rvAsistencias)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = AsistenciasAdapter(mutableListOf(), ::onDelete)
        subscribeAsistencias(rv)

        return v
    }

    private fun subscribeAsistencias(rv: RecyclerView) {
        listener = db.collection("asistencias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val items = snapshot?.documents?.map { doc ->
                    val m = doc.data ?: mapOf<String, Any>()
                    ItemAsistencia(doc.id, m)
                } ?: emptyList()

                val adapter = rv.adapter
                if (adapter is AsistenciasAdapter) {
                    adapter.updateItems(items)
                } else {
                    rv.adapter = AsistenciasAdapter(items.toMutableList(), ::onDelete)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.remove()
        listener = null
    }

    private fun onDelete(id: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Borrar asistencia")
            .setMessage("¿Seguro que deseas borrar esta asistencia?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                db.collection("asistencias").document(id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Asistencia eliminada", Toast.LENGTH_SHORT).show()
                        // La lista se refresca sola por el snapshot listener
                    }
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    data class ItemAsistencia(val id: String, val data: Map<String, Any>)

    class AsistenciasAdapter(
        private val items: MutableList<ItemAsistencia>,
        private val onDelete: (String) -> Unit
    ) : RecyclerView.Adapter<AsistenciasAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvInfo = v.findViewById<android.widget.TextView>(R.id.itemInfo)
            val btnDel = v.findViewById<android.widget.Button>(R.id.btnDeleteA)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_asistencia, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            val nombre = item.data["nombre"]?.toString() ?: ""
            val matricula = item.data["matricula"]?.toString() ?: ""
            val fecha = item.data["fecha"]?.toString() ?: ""
            val hora = item.data["hora"]?.toString() ?: ""
            holder.tvInfo.text = "$nombre — $matricula\n$fecha $hora"
            holder.btnDel.setOnClickListener { onDelete(item.id) }
        }

        override fun getItemCount(): Int = items.size

        fun updateItems(newItems: List<ItemAsistencia>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }
    }

}
