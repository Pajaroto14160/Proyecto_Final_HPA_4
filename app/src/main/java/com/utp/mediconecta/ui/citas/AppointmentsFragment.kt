package com.utp.mediconecta.ui.citas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.databinding.FragmentAppointmentsBinding
import com.utp.mediconecta.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.launch

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppointmentsViewModel by viewModels()

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) viewModel.load()
    }

    private val adapter = AppointmentAdapter(
        onEdit = { item -> openForm(item.id) },
        onCancel = { item ->
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar cita")
                .setMessage("¿Deseas cancelar la cita con ${item.doctorNombre}?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ -> viewModel.cancel(item.id) }
                .show()
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerAppointments.adapter = adapter
        binding.fabAdd.setOnClickListener { openForm(0L) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    adapter.submitList(state.items)
                    binding.tvEmpty.visibility = if (!state.loading && state.items.isEmpty()) View.VISIBLE else View.GONE
                    state.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.consumeMessage()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun openForm(id: Long) {
        val intent = Intent(requireContext(), AppointmentFormActivity::class.java)
            .putExtra(AppointmentFormActivity.EXTRA_APPOINTMENT_ID, id)
        formLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
