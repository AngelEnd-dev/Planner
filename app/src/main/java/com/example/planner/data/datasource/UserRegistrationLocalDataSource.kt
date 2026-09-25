package com.example.planner.data.datasource

import com.example.planner.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface UserRegistrationLocalDataSource {

    fun getIsUserRegistered(): Boolean //usuario ja se cadastrou?

    fun saveIsUserRegistered(isUserRegistered : Boolean)//salva esse valor

     val profile: Flow<Profile>

    suspend fun saveProfile(profile: Profile)
}
