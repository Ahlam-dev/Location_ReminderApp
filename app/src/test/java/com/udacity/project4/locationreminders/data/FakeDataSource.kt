package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.lang.reflect.Array

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var myReminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {
    private var shouldReturnError = false

    //    TODO: Create a fake data source to act as a double to the real data source
    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) return Result.Error("No reminders found")
        return Result.Success(ArrayList(myReminders))
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        myReminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) return Result.Error("No reminders found")

        val reminder = myReminders?.find {
            it.id == id
        }
        return if (reminder != null) {
            Result.Success(reminder)
        } else {
            Result.Error("Reminder not found")
        }
    }

    override suspend fun deleteAllReminders() {
        myReminders?.clear()
    }


}