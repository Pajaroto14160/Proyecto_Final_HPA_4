package com.utp.mediconecta.ui.historial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.R
import com.utp.mediconecta.databinding.FragmentHistoryBinding
import com.utp.mediconecta.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter = HistoryAdapter { item ->
        startActivity(Intent(requireContext(), HistoryDetailActivity::class.java).putExtra(HistoryDetailActivity.EXTRA_HISTORY_ID, item.id))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerHistory.adapter = adapter
        binding.chipAll.setOnClickListener { viewModel.setFilter("Todos") }
        binding.chip2026.setOnClickListener { viewModel.setFilter("2026") }
        binding.chip2025.setOnClickListener { viewModel.setFilter("2025") }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    adapter.submitList(state.visible)
                    binding.tvEmpty.visibility = if (!state.loading && state.visible.isEmpty()) View.VISIBLE else View.GONE
                    updateChips(state.filter)
                }
            }
        }
        viewModel.load()
    }

    private fun updateChips(selected: String) {
        val pairs = listOf(binding.chipAll to "Todos", binding.chip2026 to "2026", binding.chip2025 to "2025")
        pairs.forEach { (view, value) ->
            val active = value == selected
            view.setBackgroundResource(if (active) R.drawable.bg_filter_selected else R.drawable.bg_filter_unselected)
            view.setTextColor(ContextCompat.getColor(requireContext(), if (active) R.color.white else R.color.gray_600))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
