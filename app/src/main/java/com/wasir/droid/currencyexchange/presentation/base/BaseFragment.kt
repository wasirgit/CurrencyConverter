package me.wasir.android.dev.presentation.base


import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    open fun getContentView(): View? {
        return requireActivity().findViewById(android.R.id.content)
    }
}