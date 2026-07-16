package com.utp.mediconecta

import android.app.Application
import com.utp.mediconecta.data.AppDatabase
import com.utp.mediconecta.data.AppRepository
import com.utp.mediconecta.util.SessionManager

class MediConectaApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: AppRepository
        private set
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = AppRepository(database)
        sessionManager = SessionManager(this)
    }
}
