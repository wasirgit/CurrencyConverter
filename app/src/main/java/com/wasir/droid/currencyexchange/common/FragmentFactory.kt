package com.wasir.droid.currencyexchange.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.wasir.droid.currencyexchange.presentation.convert.fragments.CurrencyExchangeFragment
import com.wasir.droid.currencyexchange.presentation.convert.fragments.SettingsFragment
import javax.inject.Inject

class FragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            SettingsFragment::class.java.name -> SettingsFragment()
            CurrencyExchangeFragment::class.java.name -> CurrencyExchangeFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}