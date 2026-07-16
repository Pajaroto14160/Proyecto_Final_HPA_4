package com.utp.mediconecta.ui.mapas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.data.LocationItem
import com.utp.mediconecta.databinding.FragmentLocationsBinding
import com.utp.mediconecta.viewmodel.LocationsViewModel
import kotlinx.coroutines.launch

class LocationsFragment : Fragment() {
    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationsViewModel by viewModels()
    private val adapter = LocationAdapter(::openLocation)
    private var items: List<LocationItem> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerLocations.adapter = adapter
        binding.pinHospital1.setOnClickListener { items.firstOrNull { it.type == "HOSPITAL" }?.let(::openLocation) }
        binding.pinHospital2.setOnClickListener { items.filter { it.type == "HOSPITAL" }.getOrNull(1)?.let(::openLocation) }
        binding.pinPharmacy.setOnClickListener { items.firstOrNull { it.type == "FARMACIA" }?.let(::openLocation) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    items = state.items
                    adapter.submitList(state.items)
                }
            }
        }
        viewModel.load()
    }

    private fun openLocation(item: LocationItem) {
        startActivity(LocationDetailActivity.createIntent(requireContext(), item))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
