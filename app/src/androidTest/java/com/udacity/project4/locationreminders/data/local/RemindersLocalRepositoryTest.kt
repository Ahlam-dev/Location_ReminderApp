package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private var reminder1 = ReminderDTO(
        title = "title1",
        description = "description1",
        location = "location1",
        latitude = 0.0,
        longitude = 0.0
    )

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository
    private lateinit var remindersDAO: RemindersDao

    @Before
    fun createRepository() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = remindersDatabase.reminderDao()

        repository = RemindersLocalRepository(remindersDAO, Dispatchers.Unconfined)
    }


    @Test
    fun saveReminder_getReminderById_ExistInCashe() {
        runBlocking {
            repository.saveReminder(reminder1)
            val result = repository.getReminder(reminder1.id) as Result.Success
            assertThat(result.data, `is`(reminder1))
        }
    }

    @Test
    fun getReminderById_NotExistInCache() {
        runBlocking {
            val result =
                repository.getReminder("123") as Result.Error// no reminder exist with this id
            assertThat(result.message, `is`("Reminder not found!"))
        }
    }

    @Test
    fun deleteReminders_EmptyListFetched() {
        runBlocking {
            repository.deleteAllReminders()
            val result = repository.getReminders() as Result.Success
            assertThat(result.data.count(), `is`(0))

        }
    }
}