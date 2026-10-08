package com.example.pf_202107593

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pf_202107593.ui.AsistenciasFragment
import com.example.pf_202107593.ui.ListaFragment
import com.example.pf_202107593.ui.RegistrarFragment
import com.example.pf_202107593.ui.ScannerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerAdmin)
        val tabs = findViewById<TabLayout>(R.id.tabsAdmin)

        val fragments: List<Fragment> = listOf(
            RegistrarFragment(),
            ScannerFragment(),
            ListaFragment(),
            AsistenciasFragment()
        )

        viewPager.adapter = MainPagerAdapter(this, fragments)
        val titles = listOf("Registrar", "Escanear", "Lista", "Asistencias")

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
