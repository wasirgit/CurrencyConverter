package me.wasir.android.dev.presentation.base

import android.R
import android.view.View
import androidx.fragment.app.Fragment

@Suppress("UNCHECKED_CAST")

open class BaseFragment : Fragment() {

    open fun getContentView(): View? {
        return requireActivity().findViewById(R.id.content)
    }
}