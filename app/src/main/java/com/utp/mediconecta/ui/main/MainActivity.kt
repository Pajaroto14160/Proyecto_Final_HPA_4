package com.utp.mediconecta.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.R
import com.utp.mediconecta.databinding.ActivityMainBinding
import com.utp.mediconecta.ui.auth.LoginActivity
import com.utp.mediconecta.ui.citas.AppointmentsFragment
import com.utp.mediconecta.ui.historial.HistoryFragment
import com.utp.mediconecta.ui.home.HomeFragment
import com.utp.mediconecta.ui.mapas.LocationsFragment
import com.utp.mediconecta.ui.medicamentos.MedicationsFragment
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeDetector: GestureDetector
    private var currentTab: Int = R.id.nav_home
    private val tabOrder = intArrayOf(
        R.id.nav_home,
        R.id.nav_appointments,
        R.id.nav_history,
        R.id.nav_medications,
        R.id.nav_locations
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!(application as MediConectaApp).sessionManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
        setupSwipeNavigation()

        currentTab = savedInstanceState?.getInt(STATE_TAB)
            ?: intent.getIntExtra(EXTRA_TAB, R.id.nav_home)
        updateSelectedTab(currentTab)

        if (savedInstanceState == null) {
            openTab(currentTab)
        }
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener { selectTab(R.id.nav_home) }
        binding.navAppointments.setOnClickListener { selectTab(R.id.nav_appointments) }
        binding.navHistory.setOnClickListener { selectTab(R.id.nav_history) }
        binding.navMedications.setOnClickListener { selectTab(R.id.nav_medications) }
        binding.navLocations.setOnClickListener { selectTab(R.id.nav_locations) }
    }

    private fun setupSwipeNavigation() {
        swipeDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y
                val horizontalSwipe = abs(deltaX) > abs(deltaY) &&
                    abs(deltaX) >= SWIPE_MIN_DISTANCE &&
                    abs(velocityX) >= SWIPE_MIN_VELOCITY

                if (!horizontalSwipe) return false

                if (deltaX > 0) {
                    selectNextTab()
                } else {
                    selectPreviousTab()
                }
                return true
            }
        })
    }

    fun selectTab(menuId: Int) {
        if (currentTab == menuId) {
            updateSelectedTab(menuId)
            return
        }
        currentTab = menuId
        updateSelectedTab(menuId)
        openTab(menuId)
    }

    private fun updateSelectedTab(menuId: Int) {
        val items: Map<Int, View> = mapOf(
            R.id.nav_home to binding.navHome,
            R.id.nav_appointments to binding.navAppointments,
            R.id.nav_history to binding.navHistory,
            R.id.nav_medications to binding.navMedications,
            R.id.nav_locations to binding.navLocations
        )
        items.forEach { (id, view) ->
            view.isSelected = id == menuId
        }
    }

    private fun openTab(menuId: Int) {
        val fragment: Fragment = when (menuId) {
            R.id.nav_appointments -> AppointmentsFragment()
            R.id.nav_history -> HistoryFragment()
            R.id.nav_medications -> MedicationsFragment()
            R.id.nav_locations -> LocationsFragment()
            else -> HomeFragment()
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        swipeDetector.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    private fun selectNextTab() {
        val index = tabOrder.indexOf(currentTab)
        if (index in 0 until tabOrder.lastIndex) {
            selectTab(tabOrder[index + 1])
        }
    }

    private fun selectPreviousTab() {
        val index = tabOrder.indexOf(currentTab)
        if (index > 0) {
            selectTab(tabOrder[index - 1])
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_TAB, currentTab)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val EXTRA_TAB = "extra_tab"
        private const val STATE_TAB = "state_tab"
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_MIN_VELOCITY = 180
    }
}
