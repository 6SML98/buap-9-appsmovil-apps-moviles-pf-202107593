package com.example.pf_202107593.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pf_202107593.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs = view.findViewById<TabLayout>(R.id.tabsAdmin)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerAdmin)

        val fragments = listOf(
            RegistrarFragment(),
            ScannerFragment(),
            ListaFragment(),
            AsistenciasFragment()
        )

        viewPager.adapter = AdminPagerAdapter(this, fragments)

        val titles = listOf("Registrar", "Escanear", "Lista", "Asistencias")
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
