package com.example.planner.data.datasource


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.planner.data.model.Profile
import com.example.planner.data.model.ProfileSerializer
import kotlinx.coroutines.flow.Flow

//Esse usuário se cadastrou ou não?

// SharedPreferences -> Status (true/false)
//Proto DataStore -> Perfil Completo (nome,email...)
private const val PROFILE_FILE_NAME = "profile.db"
private const val USER_REGISTRATION_FILE_NAME= "user_registration"

private const val IS_USER_REGISTERED = "is_user_registered" //chave (status)

class UserRegistrationLocalDataSourcelmpl ( // ":" -> interface
    private val applicationContext: Context //veio do mainServicelocator
) : UserRegistrationLocalDataSource {

    val Context.ProfileProtoDataStore: DataStore<Profile> by dataStore(
        fileName = PROFILE_FILE_NAME,
        serializer = ProfileSerializer
    )

    //sharedPreferences (chave-valor)
    val userRegistrationSharedPreferences : SharedPreferences =
        applicationContext.getSharedPreferences(USER_REGISTRATION_FILE_NAME, Context.MODE_PRIVATE)

       override fun getIsUserRegistered(): Boolean { //status
        return userRegistrationSharedPreferences.getBoolean(IS_USER_REGISTERED, false)
    }

    override fun saveIsUserRegistered(isUserRegistered: Boolean) {
        userRegistrationSharedPreferences.edit {
            putBoolean(IS_USER_REGISTERED, isUserRegistered)
        }
    }
//Proto dataStore
    override val profile: Flow<Profile>
        get() = applicationContext.ProfileProtoDataStore.data

    override suspend fun saveProfile(profile: Profile) {
        applicationContext.ProfileProtoDataStore.updateData {
            profile
        }
    }
}
