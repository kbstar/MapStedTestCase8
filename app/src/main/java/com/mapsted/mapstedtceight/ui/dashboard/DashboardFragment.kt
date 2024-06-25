package com.mapsted.mapstedtceight.ui.dashboard

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mapsted.mapstedtceight.databinding.FragmentDashboardBinding
import com.mapsted.mapstedtceight.network.ApiCallState
import com.mapsted.mapstedtceight.ui.base.AppFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : AppFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    override val overrideBackPress: Boolean
        get() = true

    private val viewModel: DashboardViewModel by viewModels()

    override fun setup() {
        collectUpdates()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getBuildingsAnalyticalData()
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

