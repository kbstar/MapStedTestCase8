package com.mapsted.mapstedtceight.ui.base

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.mapsted.mapstedtceight.R

class ProgressLoaderDialog(val activity: Activity) : Dialog(activity, R.style.LoadingDialogTheme) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window?.let { window ->
            window.setDimAmount(0f)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.decorView.background = ColorDrawable(Color.TRANSPARENT)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(activity.layoutInflater.inflate(R.layout.component_loader, null, false))
    }
}