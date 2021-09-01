package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun showLoadingReminders() = mainCoroutineRule.runBlockingTest {
        //when
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        //then
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        //when
        mainCoroutineRule.resumeDispatcher()
        //then
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_emptyOrNullList_showNoData() = mainCoroutineRule.runBlockingTest {
        //when
        fakeDataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()
        //then
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_unAvailable_ShowError() = mainCoroutineRule.runBlockingTest {
        //when
        fakeDataSource.setShouldReturnError(true)
        remindersListViewModel.loadReminders()
        //then
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("No reminders found")
        )

    }


}