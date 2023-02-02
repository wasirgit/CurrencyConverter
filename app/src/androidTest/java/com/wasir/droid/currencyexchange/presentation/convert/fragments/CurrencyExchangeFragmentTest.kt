package com.wasir.droid.currencyexchange.presentation.convert.fragments

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.FragmentFactory
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAddCurrencyUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetCurrencyListUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetConvertCurrencyUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetConvertedRateUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetExchangeUseCases
import com.wasir.droid.currencyexchange.launchFragmentInHiltContainer
import com.wasir.droid.currencyexchange.presentation.convert.viewmodels.CurrencyExchangeViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class CurrencyExchangeFragmentTest {
    private val TAG = "CurrencyExchangeFragmen"

    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    lateinit var getAccountUseCases: GetAccountUseCases
    lateinit var getExchangeUseCases: GetExchangeUseCases
    lateinit var dispatcherProvider: CoroutineDispatcherProvider

    lateinit var viewModel: CurrencyExchangeViewModel


    lateinit var accountRepo: AccountRepo
    lateinit var exchangeRateRepo: ExchangeRateRepo
    lateinit var roomDatabaseDao: RoomDatabaseDao
    lateinit var formatUtils: FormatUtils

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
        formatUtils = FormatUtils()
        roomDatabaseDao = MockDataRepo()
        accountRepo = AccountRepoImpl(roomDatabaseDao)
        exchangeRateRepo = ExchangeRateRepoImpl(roomDatabaseDao, formatUtils)
        getAccountUseCases = GetAccountUseCases(
            GetAddCurrencyUseCase(accountRepo),
            GetAccountUseCase(accountRepo), GetCurrencyListUseCase(accountRepo)
        )
        getExchangeUseCases = GetExchangeUseCases(
            GetConvertedRateUseCase(exchangeRateRepo),
            GetConvertCurrencyUseCase(exchangeRateRepo)
        )
        dispatcherProvider = CoroutineDispatcherProvider()
        viewModel =
            CurrencyExchangeViewModel(getAccountUseCases, getExchangeUseCases, dispatcherProvider)
    }

    @Test
    fun currencyFragmentDisplayUI() {
        launchFragment()
        onView(withId(R.id.sellText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.sellText)).perform(waitFor(5000));
    }

    @Test
    fun loadAccountsAndConvert(): Unit = runBlocking {
        launchFragment()
        viewModel.getAccounts()

        onView(withId(R.id.amountET)).perform(ViewActions.typeText("1"))
        onView(withId(R.id.submitBtn)).perform(click())

        onView(withText(R.string.done))
            .inRoot(isDialog())
            .check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(5000));
        onView(withText(R.string.done))
            .inRoot(isDialog())
            .perform(click())

    }

    @Test
    fun convertForNegativeBalance(): Unit = runBlocking {
        launchFragment()

        onView(withId(R.id.amountET)).perform(ViewActions.typeText("1000"))
        onView(withId(R.id.submitBtn)).perform(click())

        onView(withText(R.string.balance_cannot_fall_below_zero))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(isRoot()).perform(waitFor(5000));
    }

    @Test
    fun convertUSDtoEUR(): Unit = runBlocking {
        viewModel.base = "USD"
        viewModel.symbols = "EUR"
        launchFragment()
        onView(withId(R.id.amountET)).perform(ViewActions.typeText("10"))
        onView(withId(R.id.submitBtn)).perform(click())

        onView(withText(R.string.done))
            .inRoot(isDialog())
            .check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(5000));
        onView(withText(R.string.done))
            .inRoot(isDialog())
            .perform(click())
    }

    @Test
    fun checkUIVisibilityAndMatchText() {
        launchFragment()
        onView(withId(R.id.sellText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.sellText))
            .check(matches(ViewMatchers.withText("Sell")))
        onView(withId(R.id.receiveText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.receiveText))
            .check(matches(ViewMatchers.withText("Receive")))
        onView(withId(R.id.myBalanceTv))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.myBalanceTv))
            .check(matches(ViewMatchers.withText("MY BALANCES")))
        onView(withId(R.id.currencyExchangeTv))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.currencyExchangeTv))
            .check(matches(ViewMatchers.withText("CURRENCY EXCHANGE")))
    }

    @Test
    fun openSellDialogAndVerify() {
        launchFragment()
        onView(withId(R.id.sellCurrencyTv)).perform(click())
        onView(withText(R.string.choose_currency))
            .inRoot(isDialog())
            .check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(5000));
    }

    @Test
    fun openReceiverDialogAndVerify() {
        launchFragment()
        onView(withId(R.id.receiveCurrencyTv)).perform(click())
        onView(withText(R.string.choose_currency))
            .inRoot(isDialog())
            .check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(5000));
    }

    private fun launchFragment() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<CurrencyExchangeFragment>(
            factory = fragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun testNavigationFromArtDetailsToImageAPI() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<CurrencyExchangeFragment>(
            factory = fragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.settingBtn)).perform(click())
        Mockito.verify(navController).navigate(
            CurrencyExchangeFragmentDirections.actionCurrencyFragmentToSettingsFragment()
        )
    }

    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}