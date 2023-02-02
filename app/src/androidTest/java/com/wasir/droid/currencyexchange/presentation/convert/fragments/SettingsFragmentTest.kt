package com.wasir.droid.currencyexchange.presentation.convert.fragments

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.FragmentFactory
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import com.wasir.droid.currencyexchange.data.repository.SettingsRepoImpl
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAddCurrencyUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetCurrencyListUseCase
import com.wasir.droid.currencyexchange.domain.usecase.settings.SettingsUseCase
import com.wasir.droid.currencyexchange.launchFragmentInHiltContainer
import com.wasir.droid.currencyexchange.presentation.convert.viewmodels.SettingsViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SettingsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Inject
    lateinit var fragmentFactory: FragmentFactory
    lateinit var settingsUseCase: SettingsUseCase
    lateinit var getAccountUseCases: GetAccountUseCases
    lateinit var dispatcherProvider: CoroutineDispatcherProvider

    lateinit var viewModel: SettingsViewModel


    lateinit var accountRepo: AccountRepo
    lateinit var roomDatabaseDao: RoomDatabaseDao
    lateinit var settingsRepo: SettingsRepo
    lateinit var formatUtils: FormatUtils

    @Inject
    lateinit var appConfigSync: AppConfigSync

    @Before
    fun setup() {
        hiltRule.inject()

        formatUtils = FormatUtils()
        roomDatabaseDao = MockDataRepo()
        accountRepo = AccountRepoImpl(roomDatabaseDao)
        settingsRepo = SettingsRepoImpl(roomDatabaseDao, appConfigSync)
        getAccountUseCases = GetAccountUseCases(
            GetAddCurrencyUseCase(accountRepo),
            GetAccountUseCase(accountRepo), GetCurrencyListUseCase(accountRepo)
        )
        settingsUseCase = SettingsUseCase(settingsRepo)
        dispatcherProvider = CoroutineDispatcherProvider()

        viewModel = SettingsViewModel(settingsUseCase, getAccountUseCases, dispatcherProvider)
    }

    @Test
    fun launchSettingsFragmentUI() {
        launchFragment()
        Espresso.onView(ViewMatchers.withText(R.string.settings))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    private fun launchFragment() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<SettingsFragment>(
            factory = fragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
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