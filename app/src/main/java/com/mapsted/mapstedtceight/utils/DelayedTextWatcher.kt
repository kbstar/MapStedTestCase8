package com.mapsted.mapstedtceight.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Timer
import java.util.TimerTask

class DelayedTextWatcher(private val editText: EditText, private val afterTextChanged: (text: String) -> Unit) : TextWatcher {

    companion object {
        const val DEFAULT_DELAY: Long = 200L
    }

    init {
        editText.addTextChangedListener(this)
    }

    private var timer = Timer()

    var allowDelayedTextChangeCall = true

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {

    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {

    }

    override fun afterTextChanged(s: Editable) {
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (allowDelayedTextChangeCall) {
                    editText.post {
                        afterTextChanged.invoke(editText.text.toString().trim())
                    }
                } else {
                    allowDelayedTextChangeCall = true
                }
            }
        }, DEFAULT_DELAY)
    }
}