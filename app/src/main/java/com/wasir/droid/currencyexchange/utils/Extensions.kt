package com.wasir.droid.currencyexchange.utils

import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun View.clickWithDebounce(debounceTime: Long = 1000L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime)
                return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }

    })
}

fun TextInputEditText.getAmountChangeStateFlow(): StateFlow<String> {
    val query = MutableStateFlow("")

    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // beforeTextChanged
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            query.value = p0.toString()
        }

        override fun afterTextChanged(p0: Editable?) {
            // afterTextChanged
        }
    })
    return query
}