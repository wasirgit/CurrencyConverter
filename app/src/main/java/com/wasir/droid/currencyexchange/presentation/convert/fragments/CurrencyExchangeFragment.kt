package com.wasir.droid.currencyexchange.presentation.convert.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.common.SnackFactory
import com.wasir.droid.currencyexchange.common.clickWithDebounce
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.databinding.CurrencyExchangeFragmentLayoutBinding
import com.wasir.droid.currencyexchange.presentation.convert.adapter.AccountsAdapter
import com.wasir.droid.currencyexchange.presentation.convert.viewmodels.CurrencyExchangeViewModel
import com.wasir.droid.currencyexchange.presentation.dialogs.ConversionSimpleDialog
import com.wasir.droid.currencyexchange.presentation.dialogs.CurrencyChooseDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wasir.android.dev.presentation.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyExchangeFragment : BaseFragment() {
    private val TAG = "CurrencyExchangeFragmen"
    private var binding: CurrencyExchangeFragmentLayoutBinding? = null
    private val viewModel: CurrencyExchangeViewModel by viewModels()
    private lateinit var adapter: AccountsAdapter

    @Inject
    lateinit var inputFilter: Array<InputFilter>


    @Inject
    lateinit var formatUtils: FormatUtils

    @Inject
    lateinit var appConfig: AppConfigSync

    private var currencies: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = CurrencyExchangeFragmentLayoutBinding.inflate(layoutInflater)
            subscribeDataStream()


        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setUpRecyclerView()
        viewModel.getAccounts()
        viewModel.getCurrencyList()
    }

    private fun setupUI() {
        binding?.sellItem?.sellCurrencyTv?.text = viewModel.base
        binding?.receiveItem?.receiveCurrencyTv?.text = viewModel.symbols
        binding?.sellItem?.amountET?.filters = inputFilter
        val initialSellAmount: Double? =
            binding?.sellItem?.amountET?.text?.toString()?.trim()?.length?.toDouble()
        val initialReceiveAmount: Double? =
            binding?.receiveItem?.exchangedAmountTv?.text?.toString()?.trim()?.length?.toDouble()

        if (initialSellAmount != null && initialReceiveAmount != null) {
            binding?.submitBtn?.isEnabled = initialSellAmount > 0 && initialReceiveAmount > 0
        } else {
            binding?.submitBtn?.isEnabled = false
        }

        binding?.sellItem?.sellCurrencyTv?.clickWithDebounce {
            currencies?.let {
                val sellCurrencies = it.toMutableList()
                CurrencyChooseDialog.CurrencyChooseDialogBuilder(requireContext())
                    .setCurrencies(currencyList = sellCurrencies)
                    .setTitle(requireContext().getString(R.string.choose_currency))
                    .setCurrencySelectListener(sellCurrencySelectListener)
                    .build()
            }
        }
        binding?.sellItem?.amountET?.addTextChangedListener(addTextChangedListener)

        binding?.receiveItem?.receiveCurrencyTv?.clickWithDebounce {

            currencies?.let {
                val receiveCurrencies = it.toMutableList()
                CurrencyChooseDialog.CurrencyChooseDialogBuilder(requireContext())
                    .setCurrencies(currencyList = receiveCurrencies)
                    .setTitle(requireContext().getString(R.string.choose_currency))
                    .setCurrencySelectListener(receiveCurrencySelectListener)
                    .build()
            }
        }
        binding?.submitBtn?.clickWithDebounce {
            if (viewModel.symbols.equals(viewModel.base, ignoreCase = true)) {
                SnackFactory.showError(
                    getContentView(),
                    getString(R.string.receiver_same_currency_error)
                )
            } else {
                if (binding?.sellItem?.amountET?.text.toString().trim().isNotEmpty()) {
                    val sellAmount = binding?.sellItem?.amountET?.text.toString().toDouble()
                    viewModel.convertCurrency(
                        sellAmount,
                        viewModel.base,
                        viewModel.symbols
                    )
                }
            }
        }
        binding?.settingBtn?.clickWithDebounce {
            findNavController().navigate(
                CurrencyExchangeFragmentDirections.actionCurrencyFragmentToSettingsFragment()
            )

        }
    }

    fun resetInputField() {
        binding?.sellItem?.amountET?.text?.clear()
    }

    private val sellCurrencySelectListener =
        object : CurrencyChooseDialog.OnCurrencyChangeLister {
            override fun onCurrencyChange(currency: String) {
                viewModel.base = currency
                binding?.sellItem?.sellCurrencyTv?.text = viewModel.base
                val amount = binding?.sellItem?.amountET?.text.toString().trim()
                if (amount.isNotEmpty()) {
                    calculateReceiverAmount(
                        amount.toDouble(),
                        viewModel.base,
                        viewModel.symbols
                    )  // calculate if amount field has value during currency change
                }

            }
        }
    private val receiveCurrencySelectListener =
        object : CurrencyChooseDialog.OnCurrencyChangeLister {
            override fun onCurrencyChange(currency: String) {
                viewModel.symbols = currency
                binding?.receiveItem?.receiveCurrencyTv?.text = viewModel.symbols
                val amount = binding?.sellItem?.amountET?.text.toString().trim()
                if (amount.isNotEmpty()) {
                    calculateReceiverAmount(
                        amount.toDouble(),
                        viewModel.base,
                        viewModel.symbols
                    )   // calculate if amount field has value during currency change
                }
            }

        }
    private val addTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // beforeTextChanged
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (isValidInput(p0.toString())
            ) {
                if (viewModel.base.isNotEmpty() && viewModel.symbols.isNotEmpty()) {
                    binding?.submitBtn?.isEnabled = true
                    calculateReceiverAmount(
                        p0.toString().trim().toDouble(),
                        viewModel.base,
                        viewModel.symbols
                    )
                }
            } else {
                binding?.submitBtn?.isEnabled = false
                binding?.receiveItem?.exchangedAmountTv?.text = "+0.00"
            }
        }
    }

    fun isValidInput(input: String?): Boolean {
        val inputNumber = input.toString().trim()
        return inputNumber.isNotEmpty() && !inputNumber.equals(
            ".",
            ignoreCase = true
        ) && inputNumber.toDouble() > 0
    }

    private fun setUpRecyclerView() {
        binding?.accountRv?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // ArrayList of class ItemsViewModel
        val data = ArrayList<Account>()
        // This will pass the ArrayList to our Adapter
        adapter = AccountsAdapter(data, formatUtils)
        // Setting the Adapter with the recyclerview
        binding?.accountRv?.adapter = adapter
    }


    private fun renderUser(accounts: List<Account>) {
        adapter.addData(accounts)
    }

    private fun calculateReceiverAmount(sellAmount: Double, base: String, symbols: String) {
        if (base.isNotEmpty() && symbols.isNotEmpty()) {
            viewModel.calculateReceiverAmount(sellAmount, base, symbols)
        }
    }

    private fun subscribeDataStream() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currenciesStateFlow.collect { data ->
                        when (data) {
                            is Resource.Success -> {
                                data.data?.let {
                                    currencies = it
                                }
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
                launch {
                    viewModel.accountStateFlow.collect { data ->
                        when (data) {
                            is Resource.Success -> {
                                data.data?.let {
                                    renderUser(it)
                                }
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
                launch {
                    viewModel.receiverAmountStateFlow.collect { data ->
                        when (data) {
                            is Resource.Success -> {
                                data.data?.let {
                                    binding?.receiveItem?.exchangedAmountTv?.text =
                                        formatUtils.formatAmountWithSign(it)
                                }

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

        lifecycleScope.launchWhenStarted {
            viewModel.convertStateFlow.collectLatest { data ->
                when (data) {
                    is Resource.Success -> {
                        data.data?.let {
                            viewModel.getAccounts()
                            ConversionSimpleDialog.SimpleBuilder(requireContext())
                                .setTitle(getString(R.string.currency_converted))
                                .setMessage(it)
                                .setListener(object : ConversionSimpleDialog.ClickOnSimpleDialog {
                                    override fun onDismissDialog() {
                                        resetInputField()
                                    }

                                })
                                .build()
                        }

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