package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.annotations.NotNull
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase


    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminderDTO = ReminderDTO(
            title = "title", description = "description",
            location = "location",
            longitude = 0.0,
            latitude = 0.0
        )
        database.reminderDao().saveReminder(reminderDTO)
        val returnedReminder = database.reminderDao().getReminderById(reminderDTO.id)
        assertThat<ReminderDTO>(returnedReminder as ReminderDTO, (notNullValue()))
        assertThat(returnedReminder.title, `is`(reminderDTO.title))
        assertThat(returnedReminder.description, `is`(reminderDTO.description))
        assertThat(returnedReminder.location, `is`(reminderDTO.location))
        assertThat(returnedReminder.longitude, `is`(reminderDTO.longitude))
        assertThat(returnedReminder.latitude, `is`(reminderDTO.latitude))

    }

    @Test
    fun getReminderById_NotExist() = runBlockingTest {
        val Id = "123"
        val obtainedReminder = database.reminderDao().getReminderById(Id)
        Assert.assertNull(obtainedReminder)
        assertThat(obtainedReminder, `is`(nullValue()))
    }

    @After
    fun closeDB() = database.close()


}

