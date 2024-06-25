package com.mapsted.mapstedtceight.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mapsted.mapstedtceight.R
import com.mapsted.mapstedtceight.databinding.ActivityMainBinding
import com.mapsted.mapstedtceight.network.RequestError
import com.mapsted.mapstedtceight.session.SessionPreferences
import com.mapsted.mapstedtceight.ui.base.ProgressLoaderDialog
import com.mapsted.mapstedtceight.session.AppPreferences
import com.mapsted.mapstedtceight.utils.showError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @field:Inject
    lateinit var appPreferences: AppPreferences

    @field:Inject
    lateinit var sessionPreferences: SessionPreferences

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val progressLoaderDialog: ProgressLoaderDialog by lazy {
        ProgressLoaderDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
    }

    fun showLoader() {
        binding.root.post {
            if (!progressLoaderDialog.isShowing) {
                progressLoaderDialog.show()
            }
        }
    }

    fun hideLoader() {
        binding.root.post {
            if (progressLoaderDialog.isShowing) {
                progressLoaderDialog.dismiss()
            }
        }
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        view.postDelayed({
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showApiError(error: RequestError) {
        when (error) {
            is RequestError.NoInternetConnection -> {
                binding.root.showError(message = getString(R.string.no_internet_connection))
            }

            is RequestError.HostUnreachable -> {
                binding.root.showError(message = getString(R.string.server_connect_error))
            }

            is RequestError.ConnectionTimeout -> {
                binding.root.showError(message = getString(R.string.server_connection_timeout_error))
            }

            else -> {
                binding.root.showError(message = getString(R.string.something_went_wrong))
            }
        }
    }
}