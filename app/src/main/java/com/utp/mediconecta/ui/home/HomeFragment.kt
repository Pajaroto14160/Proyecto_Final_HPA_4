package com.utp.mediconecta.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.R
import com.utp.mediconecta.databinding.FragmentHomeBinding
import com.utp.mediconecta.ui.citas.AppointmentFormActivity
import com.utp.mediconecta.ui.main.MainActivity
import com.utp.mediconecta.ui.perfil.ProfileActivity
import com.utp.mediconecta.util.DateUtils
import com.utp.mediconecta.viewmodel.HomeUiState
import com.utp.mediconecta.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val main = requireActivity() as MainActivity
        binding.tvInitials.setOnClickListener { startActivity(Intent(requireContext(), ProfileActivity::class.java)) }
        binding.cardAppointments.setOnClickListener { main.selectTab(R.id.nav_appointments) }
        binding.cardHistory.setOnClickListener { main.selectTab(R.id.nav_history) }
        binding.cardMedications.setOnClickListener { main.selectTab(R.id.nav_medications) }
        binding.cardLocations.setOnClickListener { main.selectTab(R.id.nav_locations) }
        binding.quickMedications.setOnClickListener { main.selectTab(R.id.nav_medications) }
        binding.quickLocations.setOnClickListener { main.selectTab(R.id.nav_locations) }
        binding.quickNewAppointment.setOnClickListener { startActivity(Intent(requireContext(), AppointmentFormActivity::class.java)) }
        binding.cardNextAppointment.setOnClickListener { main.selectTab(R.id.nav_appointments) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun render(state: HomeUiState) {
        state.user?.let { user ->
            binding.tvName.text = "${user.nombre} ${user.apellido}".trim()
            binding.tvInitials.text = listOf(user.nombre.firstOrNull(), user.apellido.firstOrNull()).filterNotNull().joinToString("").uppercase().ifBlank { "MC" }
        }
        val appointment = state.nextAppointment
        binding.tvNoAppointment.visibility = if (appointment == null && !state.loading) View.VISIBLE else View.GONE
        binding.tvNextTitle.visibility = if (appointment == null) View.GONE else View.VISIBLE
        binding.tvNextSubtitle.visibility = if (appointment == null) View.GONE else View.VISIBLE
        binding.groupNextDate.visibility = if (appointment == null) View.GONE else View.VISIBLE
        appointment?.let {
            binding.tvNextTitle.text = it.especialidadNombre
            binding.tvNextSubtitle.text = "${it.doctorNombre} · ${it.hospitalNombre}"
            binding.tvNextDate.text = DateUtils.pretty(it.fecha)
            binding.tvNextTime.text = it.hora
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
