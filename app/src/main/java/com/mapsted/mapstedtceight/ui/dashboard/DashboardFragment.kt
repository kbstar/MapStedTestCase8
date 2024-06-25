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
import java.util.Locale

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
                    viewModel.calculateTotalPurchases()
                }
                .show()
        }

        binding.txtCategory.setOnClickListener {
            if (!isValidSelection(1)) {
                return@setOnClickListener
            }
            val values = viewModel.categories.keys.map { it.toString() }.toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.category))
                .setItems(values) { _, which ->
                    viewModel.selCategory = viewModel.categories.entries.elementAt(which)
                    binding.txtCategory.text = values[which]
                    viewModel.calculateTotalPurchases()
                }
                .show()
        }

        binding.txtCountry.setOnClickListener {
            if (!isValidSelection(2)) {
                return@setOnClickListener
            }
            val values = viewModel.countryStates.keys.toTypedArray()
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.country))
                .setItems(values) { _, which ->
                    viewModel.selCountry = viewModel.countryStates.entries.elementAt(which)
                    binding.txtCountry.text = values[which]
                    viewModel.calculateTotalPurchases()
                }
                .show()
        }

        binding.txtState.setOnClickListener {
            if (!isValidSelection(3)) {
                return@setOnClickListener
            }
            viewModel.selCountry?.value?.let { states ->
                val values = states.sorted().toTypedArray()
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(resources.getString(R.string.state))
                    .setItems(values) { _, which ->
                        viewModel.selState = values[which]
                        binding.txtState.text = values[which]
                        viewModel.calculateTotalPurchases()
                    }
                    .show()
            }
        }

        binding.txtItem.setOnClickListener {
            if (!isValidSelection(4)) {
                return@setOnClickListener
            }
            viewModel.selCategory?.value?.let { items ->
                val values = items.sorted().map { it.toString() }.toTypedArray()
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(resources.getString(R.string.state))
                    .setItems(values) { _, which ->
                        viewModel.selItemId = values[which].toLong()
                        binding.txtItem.text = values[which]
                        viewModel.calculateTotalPurchases()
                    }
                    .show()
            }
        }
    }

    private fun isValidSelection(step: Int): Boolean {
        when {
            step <= 1 && viewModel.selManufacturer == null -> {
                binding.root.showWarning("Please Select Manufacturer first!!")
                return false
            }

            step <= 2 && viewModel.selCategory == null -> {
                binding.root.showWarning("Please Select Category first!!")
                return false
            }

            step <= 3 && viewModel.selCountry == null -> {
                binding.root.showWarning("Please Select Manufacturer first!!")
                return false
            }

            step <= 4 && viewModel.selState == null -> {
                binding.root.showWarning("Please Select Manufacturer first!!")
                return false
            }

            else -> return true
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
        lifecycleScope.launch {
            viewModel.statePurchaseDetails.collect { purchaseDetails ->
                viewModel.selManufacturer?.let { manufacturer ->
                    binding.txtManufacturerCost.text = String.format(
                        Locale.ENGLISH,
                        "$%.2f", (purchaseDetails.totalByManufactures[manufacturer] ?: 0.0).toFloat()
                    )
                }
                viewModel.selCategory?.key?.let { categoryId ->
                    binding.txtCategoryCost.text = String.format(
                        Locale.ENGLISH,
                        "$%.2f", (purchaseDetails.totalByCategory[categoryId] ?: 0.0).toFloat()
                    )
                }
                viewModel.selItemId.let { itemId ->
                    binding.txtItemCount.text = (purchaseDetails.itemPurchaseCount[itemId] ?: 0).toString()
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}

