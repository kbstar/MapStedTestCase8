package com.mapsted.mapstedtceight.ui.dashboard

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mapsted.mapstedtceight.R
import com.mapsted.mapstedtceight.databinding.FragmentDashboardBinding
import com.mapsted.mapstedtceight.network.ApiCallState
import com.mapsted.mapstedtceight.ui.base.AppFragment
import com.mapsted.mapstedtceight.utils.showWarning
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : AppFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    override val overrideBackPress: Boolean
        get() = true

    private val viewModel: DashboardViewModel by viewModels()

    override fun setup() {
        setupComponents()

        collectUpdates()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getBuildingsAnalyticalData()
            }
        }
    }

    private fun setupComponents() {
        binding.txtManufacturer.setOnClickListener {
            val values = viewModel.manufactures.sorted().toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.manufacturer))
                .setItems(values) { _, which ->
                    viewModel.selManufacturer = values[which]
                    binding.txtManufacturer.text = values[which]
                }
                .show()
        }

        binding.txtCategory.setOnClickListener {
            val values = viewModel.categories.sorted().map { it.toString() }.toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.category))
                .setItems(values) { _, which ->
                    viewModel.selItemCategoryId = values[which].toLong()
                    binding.txtCategory.text = values[which]
                }
                .show()
        }

        binding.txtCountry.setOnClickListener {
            val values = viewModel.countryStates.keys.toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.country))
                .setItems(values) { _, which ->
                    viewModel.selCountry = viewModel.countryStates.entries.elementAt(which)
                    binding.txtCountry.text = values[which]
                }
                .show()
        }

        binding.txtState.setOnClickListener {
            viewModel.selCountry?.value?.let { states ->
                val values = states.sorted().toTypedArray()
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(resources.getString(R.string.state))
                    .setItems(values) { _, which ->
                        viewModel.selState = values[which]
                        binding.txtState.text = values[which]
                    }
                    .show()
            } ?: run {
                binding.root.showWarning("Please Select Country first!!")
                return@setOnClickListener
            }
        }
    }

    private fun collectUpdates() {
        lifecycleScope.launch {
            viewModel.stateBuildingsAnalyticalData.collect { state ->
                when (state) {
                    is ApiCallState.Idle -> {
                    }

                    is ApiCallState.Loading -> {
                        showLoader()
                    }

                    is ApiCallState.Success -> {
                        hideLoader()
                    }

                    is ApiCallState.Error -> {
                        hideLoader()
                        showApiError(state.error)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}

