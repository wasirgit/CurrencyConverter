package com.wasir.droid.currencyexchange.presentation.convert.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasir.droid.currencyexchange.App
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.databinding.CurrencyExchangeFragmentLayoutBinding
import com.wasir.droid.currencyexchange.presentation.convert.adapter.AccountsAdapter
import com.wasir.droid.currencyexchange.presentation.convert.viewmodels.CurrencyExchangeViewModel
import com.wasir.droid.currencyexchange.presentation.dialogs.ConversionSimpleDialog
import com.wasir.droid.currencyexchange.presentation.dialogs.CurrencyChooseDialog
import com.wasir.droid.currencyexchange.utils.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.wasir.android.dev.presentation.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyExchangeFragment : BaseFragment() {
    private val TAG = "CurrencyExchangeFragmen"
    private var binding: CurrencyExchangeFragmentLayoutBinding? = null
    private val currencyExchangeViewModel: CurrencyExchangeViewModel by viewModels()
    private lateinit var adapter: AccountsAdapter
    private var currencyRateResponse: CurrencyRateResponse? = null
    private var base: String = ""
    private var symbols: String = ""
    lateinit var latestRateSyncHandler: Handler

    @Inject
    lateinit var inputFilter: Array<InputFilter>

    @Inject
    lateinit var appConfig: AppConfig

    @Inject
    lateinit var formatUtils: FormatUtils

    @Inject
    @ApplicationContext
    lateinit var app: App

    private var currencies: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        latestRateSyncHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = CurrencyExchangeFragmentLayoutBinding.inflate(layoutInflater)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setUpRecyclerView()
        subscribeDataStream()
        currencyExchangeViewModel.getAccounts()
        currencyExchangeViewModel.getCurrencyList()

    }


    override fun onPause() {
        super.onPause()
        latestRateSyncHandler.removeCallbacks(rateSyncRunnable)
    }

    override fun onResume() {
        super.onResume()
        latestRateSyncHandler.post(rateSyncRunnable)
    }

    private fun setupUI() {
        binding?.sellItem?.amountET?.filters = inputFilter
        binding?.sellItem?.sellCurrencyTv?.requestFocus()
        binding?.sellItem?.sellCurrencyTv?.clickWithDebounce {
            currencies?.let {
                CurrencyChooseDialog.CurrencyChooseDialogBuilder(requireContext())
                    .setCurrencies(currencyList = it as MutableList<String>)
                    .setTitle(requireContext().getString(R.string.choose_currency))
                    .setCurrencySelectListener(sellCurrencySelectListener)
                    .build()
            }
        }
        binding?.sellItem?.amountET?.addTextChangedListener(addTextChangedListener)

        binding?.receiveItem?.receiveCurrencyTv?.clickWithDebounce {
            currencies?.let {
                CurrencyChooseDialog.CurrencyChooseDialogBuilder(requireContext())
                    .setCurrencies(currencyList = it as MutableList<String>)
                    .setTitle(requireContext().getString(R.string.choose_currency))
                    .setCurrencySelectListener(receiveCurrencySelectListener)
                    .build()
            }
        }
        binding?.submitBtn?.clickWithDebounce {
            if (binding?.sellItem?.amountET?.text.toString().trim().isNotEmpty()) {
                val sellAmount = binding?.sellItem?.amountET?.text.toString().toDouble()
                val rate = currencyRateResponse?.rates?.get(symbols)
                rate?.let {
                    currencyExchangeViewModel.convertCurrency(
                        sellAmount,
                        base,
                        symbols,
                        it
                    )
                }
            }
        }
        binding?.settingBtn?.clickWithDebounce {
            Navigation.findNavController(binding!!.root).navigate(R.id.settingsFragment)
        }
    }

    private val sellCurrencySelectListener =
        object : CurrencyChooseDialog.OnCurrencyChangeLister {
            override fun onCurrencyChange(currency: String) {
                base = currency
                binding?.sellItem?.sellCurrencyTv?.text = base
                if (symbols.isNotEmpty())
                    makeSyncLatestRateApiRequest(base, symbols)
            }
        }
    private val receiveCurrencySelectListener =
        object : CurrencyChooseDialog.OnCurrencyChangeLister {
            override fun onCurrencyChange(currency: String) {
                symbols = currency
                binding?.receiveItem?.receiveCurrencyTv?.text = symbols
                if (base.isNotEmpty())
                    makeSyncLatestRateApiRequest(base, symbols)
            }

        }
    private val addTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // beforeTextChanged
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().trim().isNotEmpty()) {
                currencyRateResponse?.rates?.get(symbols)?.let {
                    Log.d(TAG, "afterTextChanged: $it")
                    binding?.submitBtn?.isEnabled = true
                    calculateReceiverAmount(p0.toString().trim().toDouble(), it)
                }
            } else {
                binding?.submitBtn?.isEnabled = false
                binding?.receiveItem?.exchangedAmountTv?.text = "+0.00"
            }
        }
    }

    private fun getCurrencyList() {

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

    private fun calculateReceiverAmount(sellAmount: Double, exchangeRate: Double) {
        currencyExchangeViewModel.calculateReceiverAmount(sellAmount, exchangeRate)
    }

    private fun subscribeDataStream() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeViewModel.currenciesStateFlow.collect { data ->
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
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeViewModel.accountStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            data.data?.let {
                                renderUser(it)
                            }
                            Log.d(TAG, "Success: ")
                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }

                            Log.d(TAG, "Error: ")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "Loading: ")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeViewModel.rateStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            currencyRateResponse = data.data
                            Log.d(TAG, "Success: $currencyRateResponse")
                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }

                            Log.d(TAG, "Error: ")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "Loading: ")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeViewModel.receiverAmountStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            Log.d(TAG, "Success: ${data.data}")
                            data.data?.let {
                                binding?.receiveItem?.exchangedAmountTv?.text =
                                    formatUtils.formatAmountWithSign(it)
                            }

                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }
                            Log.d(TAG, "Error: ")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "Loading: ")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeViewModel.convertStateFlow.collect { data ->
                    when (data) {
                        is Resource.Success -> {
                            data.data?.let {
                                ConversionSimpleDialog.SimpleBuilder(requireContext())
                                    .setTitle(getString(R.string.currency_converted))
                                    .setMessage(it)
                                    .build()
                            }

                        }
                        is Resource.Error -> {
                            data.message?.let {
                                SnackFactory.showError(getContentView(), it)
                            }
                            Log.d(TAG, "Error: ")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG, "Loading: ")
                        }
                    }
                }
            }
        }
    }

    private val rateSyncRunnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "run: $base $symbols")
            if (base.isNotEmpty() && symbols.isNotEmpty()) {
                makeSyncLatestRateApiRequest(base, symbols)
            }
            latestRateSyncHandler.postDelayed(this, appConfig.LATEST_EXCHANGE_RATE_SYNC_INTERVAL)
        }
    }

    private fun makeSyncLatestRateApiRequest(base: String, symbols: String) {
        currencyExchangeViewModel.getRateByBaseSymbol(base, symbols)
    }


}