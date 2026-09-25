package com.example.planner.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.datasource.AuthenticationLocalDataSource
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.data.di.MainServiceLocator
import com.example.planner.data.model.Profile
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//token mockado
private val mockToken = """
    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwi
    bmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI
    6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3
    wh7SmqJp-QV30
    """".trimIndent()

class UserRegistrationViewModel :
    ViewModel() { //ponte entre interface e o banco de dados (ViewModel é quem mexe nos dados)
    //gerencia o ciclo de vida


    private val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        MainServiceLocator.userRegistrationLocalDataSource
    }

    private val authenticationLocalDataSource: AuthenticationLocalDataSource by lazy {
        MainServiceLocator.authenticationLocalDataSource
    }

    private val _isProfileValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProfileValid: StateFlow<Boolean> = _isProfileValid.asStateFlow() //privado

    private val _profile: MutableStateFlow<Profile> = MutableStateFlow(Profile())
    val profile: StateFlow<Profile> = _profile.asStateFlow() //publico

    //observação do token
    private val _isTokenValue: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isTokenValid: StateFlow<Boolean?> = _isTokenValue.asStateFlow()

    init {
        viewModelScope.launch {
            launch { //estado da tela
                userRegistrationLocalDataSource.profile.collect { profile ->
                    _profile.value = profile
                }
            }
            launch {
                while (true) {
                    val tokenExpirationDateTime =
                        authenticationLocalDataSource.expirationDateTime.firstOrNull()
                    tokenExpirationDateTime?.let {tokenExpirationDateTime ->
                        val datetime = System.currentTimeMillis()
                        _isTokenValue.value = tokenExpirationDateTime >= datetime
                    }
                    delay(5_000)
                }
            }
        }

    }

    fun getIsUserRegistered(): Boolean { //ta cadastrado?
        return userRegistrationLocalDataSource.getIsUserRegistered()
    }


    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        image: String? = null,

        ) {
        if (name == null && phone == null && image == null && email == null) return

        _profile.update { currenProfile ->
            val updateProfile = currenProfile.copy(
                name = name ?: currenProfile.name,
                email = email ?: currenProfile.email,
                phone = phone ?: currenProfile.phone,
                image = image ?: currenProfile.image
            )

            _isProfileValid.update { updateProfile.isValid() }
            updateProfile
        }
    }

    fun saveProfile(onCompleted: () -> Unit) {
        viewModelScope.launch {
            async {
                userRegistrationLocalDataSource.saveProfile(profile = profile.value)
                userRegistrationLocalDataSource.saveIsUserRegistered(isUserRegistered = true)
                authenticationLocalDataSource.insertToken(token = mockToken)
                _isTokenValue.value = true
            }.await()
            onCompleted() // precisa passar por tudo pra ir pra home
        }
    }

    fun obtainNewToken(){
        viewModelScope.launch {
            authenticationLocalDataSource.insertToken(token = mockToken)
            _isTokenValue.value = true
        }

    }
}