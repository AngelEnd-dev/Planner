package com.example.planner.data.di

import android.app.Application
import com.example.planner.data.datasource.AuthenticationLocalDataSource
import com.example.planner.data.datasource.AuthenticationLocalDataSourcelmpl
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.data.datasource.UserRegistrationLocalDataSourcelmpl

//é o contexto
object MainServiceLocator {

    private var _application: Application? = null // nao foi iniciado ainda
    private val application: Application get() = _application!! //foi iniciado com certeza

    val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy { //by lazy
        UserRegistrationLocalDataSourcelmpl(applicationContext = application.applicationContext )
    } //acessar o armazenamento do celular

    val authenticationLocalDataSource : AuthenticationLocalDataSource by lazy {
        AuthenticationLocalDataSourcelmpl(applicationContext = application.applicationContext)
    }


    //ciclo de vida:
    fun initialize(application: Application){
        _application = application
    }
    fun clear(){
        _application = null
    }
}