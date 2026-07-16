package com.utp.mediconecta.ui.medicamentos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.utp.mediconecta.R
import com.utp.mediconecta.data.HospitalEntity
import com.utp.mediconecta.databinding.FragmentMedicationsBinding
import com.utp.mediconecta.ui.mapas.LocationDetailActivity
import com.utp.mediconecta.viewmodel.MedicationsViewModel
import kotlinx.coroutines.launch

class MedicationsFragment : Fragment() {
    private var _binding: FragmentMedicationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicationsViewModel by viewModels()
    private val adapter = MedicationAdapter { location -> startActivity(LocationDetailActivity.createIntent(requireContext(), location)) }
    private var renderedHospitalIds: List<Long> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMedicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerMedications.adapter = adapter
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { viewModel.search(s?.toString().orEmpty()) }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    adapter.submitList(state.cards)
                    binding.tvEmpty.visibility = if (!state.loading && state.cards.isEmpty()) View.VISIBLE else View.GONE
                    renderHospitalChips(state.hospitals, state.selectedHospitalId)
                }
            }
        }
        viewModel.load()
    }

    private fun renderHospitalChips(hospitals: List<HospitalEntity>, selectedId: Long) {
        if (renderedHospitalIds != hospitals.map { it.id }) {
            renderedHospitalIds = hospitals.map { it.id }
            binding.chipHospitals.removeAllViews()
            hospitals.forEach { hospital ->
                val chip = Chip(requireContext()).apply {
                    id = View.generateViewId()
                    text = hospital.nombre.replace("Hospital ", "H. ")
                    isCheckable = true
                    isCheckedIconVisible = false
                    chipCornerRadius = 12f * resources.displayMetrics.density
                    setOnClickListener { viewModel.selectHospital(hospital.id) }
                    tag = hospital.id
                }
                binding.chipHospitals.addView(chip)
            }
        }
        for (index in 0 until binding.chipHospitals.childCount) {
            val chip = binding.chipHospitals.getChildAt(index) as Chip
            val active = chip.tag == selectedId
            chip.isChecked = active
            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), if (active) R.color.teal_600 else R.color.white)
            chip.setTextColor(ContextCompat.getColor(requireContext(), if (active) R.color.white else R.color.gray_600))
            chip.chipStrokeWidth = 1f * resources.displayMetrics.density
            chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), if (active) R.color.teal_600 else R.color.gray_200)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
