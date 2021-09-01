package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.bytebuddy.implementation.FixedValue.nullValue
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.jetbrains.annotations.NotNull
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource
    //TODO: provide testing to the SaveReminderView and its live data objects


    @Before
    fun startKoinForTest() {
        stopKoin()// stop the original app koin, which is launched when the application starts (in "MyApp")
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), fakeDataSource
        )
    }


    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {

        val myReminderDataItem = getMyReminder()
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(myReminderDataItem)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    fun getMyReminder(): ReminderDataItem {
        return ReminderDataItem(
            title = "title", description = "description", location = "location",
            latitude = 0.0, longitude = 0.0
        )
    }

    //check_loading
    //shouldReturnError
    @Test
    fun saveReminder_noTitle_ReturnError() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getMyReminder()
        reminderDataItem.title = ""

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }

    @Test
    fun saveReminder_noLocation_ReturnError() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getMyReminder()
        reminderDataItem.location = ""

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }

    @Test
    fun saveReminder_success_showToast() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getMyReminder()
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }


    @After
    fun stopKoinAfterTest() = stopKoin()
}