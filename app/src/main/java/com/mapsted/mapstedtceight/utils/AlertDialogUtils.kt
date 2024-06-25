package com.mapsted.mapstedtceight.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

fun Context.showAlert(
    title: String? = null,
    message: String? = null,
    namePositiveButton: String? = null,
    onPositiveButtonTap: () -> Unit = {},
    nameNegativeButton: String? = null,
    onNegativeButtonTap: () -> Unit = {},
    nameNeutralButton: String? = null,
    onNeutralButtonTap: () -> Unit = {},
    onDismiss: () -> Unit = {},
    cancelable: Boolean = true,
    showImmediately: Boolean = true,
): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setOnDismissListener {
            onDismiss()
        }

    if (title != null) {
        builder.setTitle(title)
    }

    if (message != null) {
        builder.setMessage(message)
    }

    if (namePositiveButton != null) {
        builder.setPositiveButton(namePositiveButton) { _, _ ->
            onPositiveButtonTap()
        }
    }

    if (nameNegativeButton != null) {
        builder.setNegativeButton(nameNegativeButton) { _, _ ->
            onNegativeButtonTap()
        }
    }

    if (nameNeutralButton != null) {
        builder.setNeutralButton(nameNeutralButton) { _, _ ->
            onNeutralButtonTap()
        }
    }

    val alert = builder.create()

    if (showImmediately) {
        alert.show()
    }

    return alert
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun View.showSnackBar(message: String, color: Int): Snackbar {
    return Snackbar.make(this, message, Snackbar.LENGTH_LONG).apply {
        setActionTextColor(Color.argb(0.5f, 0f, 0f, 0f))
        view.setBackgroundColor(color)

        val resourceId: Int = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val otherMargins = 10.toDP()
            val layoutParams = view.layoutParams as MarginLayoutParams
            layoutParams.setMargins(
                otherMargins,
                otherMargins,
                otherMargins,
                otherMargins + resources.getDimensionPixelSize(resourceId)
            )
            view.layoutParams = layoutParams
        }

        val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.maxLines = 5
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.setOnClickListener {
            dismiss()
        }
        show()
    }
}

fun View.showSuccess(message: String): Snackbar = showSnackBar(message, Color.parseColor("#43A047"))

fun View.showError(message: String): Snackbar = showSnackBar(message, Color.parseColor("#F44336"))

fun View.showWarning(message: String): Snackbar = showSnackBar(message, Color.parseColor("#FB8C00"))