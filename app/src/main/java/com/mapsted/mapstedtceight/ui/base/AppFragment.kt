package com.mapsted.mapstedtceight.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.mapsted.mapstedtceight.BuildConfig
import com.mapsted.mapstedtceight.AppMain
import com.mapsted.mapstedtceight.ui.MainActivity
import com.mapsted.mapstedtceight.network.RequestError
import com.mapsted.mapstedtceight.session.AppDataStore
import com.mapsted.mapstedtceight.session.SessionPreferences
import com.mapsted.mapstedtceight.session.AppPreferences
import com.mapsted.mapstedtceight.utils.OnBackPressed
import com.mapsted.mapstedtceight.utils.ViewInflate
import com.mapsted.mapstedtceight.utils.isInternetConnected
import javax.inject.Inject

abstract class AppFragment<B : ViewBinding>(private val bindingReference: ViewInflate<B>) : Fragment() {

    abstract val overrideBackPress: Boolean

    @field:Inject
    lateinit var appDataStore: AppDataStore

    @field:Inject
    lateinit var appPreferences: AppPreferences

    @field:Inject
    lateinit var sessionPreferences: SessionPreferences

    lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        if (BuildConfig.DEBUG) {
            println("HeliosComponent: Fragment: ${this::class.java.simpleName}")
        }
        return if (::binding.isInitialized) {
            setup()
            binding.root
        } else {
            binding = bindingReference.invoke(layoutInflater, container, false)
            setup()
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (overrideBackPress) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner, onBackPressedCallback
            )
        }
    }

    open fun setup() {}

    fun isViewCreated() = ::binding.isInitialized

    fun isInternetConnected(): Boolean = requireContext().isInternetConnected()

    open fun onBackPressed() {
        findNavController().popBackStack()
    }

    fun showLoader() = mainActivity().showLoader()

    fun hideLoader() = mainActivity().hideLoader()

    fun showApiError(error: RequestError) = mainActivity().showApiError(error)

    fun appMain() = requireActivity().application as AppMain

    fun mainActivity() = requireActivity() as MainActivity

    override fun onDestroyView() {
        if (overrideBackPress) {
            onBackPressedCallback.remove()
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }

    private val onBackPressedCallback = OnBackPressed {
        onBackPressed()
    }
}