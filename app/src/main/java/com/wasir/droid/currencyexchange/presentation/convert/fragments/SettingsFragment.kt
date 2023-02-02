package com.wasir.droid.currencyexchange.presentation.convert.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.common.SnackFactory
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.databinding.FragmentSettingsBinding
import com.wasir.droid.currencyexchange.presentation.convert.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG = "SettingsFragment"
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var appConfig: AppConfigSync

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_pref, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefView: View? = super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        binding.root.addView(prefView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAddCurrencyUpdate()
        getAddCommissionUpdate()
        binding.toolBar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }

        val addCurrencyEditText: EditTextPreference? = findPreference("addNewCurrencyKey")
        addCurrencyEditText?.setDefaultValue("")

        addCurrencyEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty())
                viewModel.addCurrency(newValue.toString().uppercase())

            true
        }
        val updateCommissionEditText: EditTextPreference? =
            findPreference("updateCommission")
        updateCommissionEditText?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        updateCommissionEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty())
                try {
                    viewModel.updateCommission(newValue.toString().toDouble())
                } catch (e: Exception) {
                    SnackFactory.showError(
                        getContentView(),
                        getString(R.string.invalid_input_error)
                    )
                }
            true
        }

        val updateSyncTimeEditText: EditTextPreference? =
            findPreference("updateSyncTime")
        updateSyncTimeEditText?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        updateSyncTimeEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty()) {
                viewModel.updateSyncTime(newValue.toString().trim().toInt())
            }
            true
        }

        val updateConversionPositionEditText: EditTextPreference? =
            findPreference("updateConversionPosition")
        updateConversionPositionEditText?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        updateConversionPositionEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty()) {
                viewModel.updateFreeConversionPosition(newValue.toString().trim().toInt())
            }
            true
        }

        val updateMaxFreeAmountEditText: EditTextPreference? =
            findPreference("updateMaxFreeAmount")

        updateMaxFreeAmountEditText?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        updateMaxFreeAmountEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty()) {
                viewModel.updateMaxFreeAmount(newValue.toString().trim().toDouble())
            }
            true
        }

        val numberOfFreeConversionEditText: EditTextPreference? =
            findPreference("updateTotalNumOfFreeConversion")

        numberOfFreeConversionEditText?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        numberOfFreeConversionEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty()) {
                viewModel.updateNumberOfFreeConversion(newValue.toString().trim().toInt())
            }
            true
        }
    }

    private fun getAddCurrencyUpdate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addCurrencyStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            SnackFactory.showMessage(
                                getContentView(),
                                getString(R.string.currency_add_success_message)
                            )
                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }
                        }
                        is Resource.Loading -> {

                        }
                    }
                }
            }
        }
    }

    private fun getAddCommissionUpdate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateCommissionStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            SnackFactory.showMessage(
                                getContentView(),
                                getString(R.string.commission_added_msg)
                            )
                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }
                        }
                        is Resource.Loading -> {

                        }
                    }
                }
            }
        }
    }


    private fun getContentView(): View? {
        return requireActivity().findViewById(android.R.id.content)
    }
}