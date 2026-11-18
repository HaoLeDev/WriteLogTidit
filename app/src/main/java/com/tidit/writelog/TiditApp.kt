package com.tidit.writelog

import android.app.Application
import com.tidit.writelog.data.db.AppDatabase
import com.tidit.writelog.data.repository.TiditRepository

class TiditApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: TiditRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        repository = TiditRepository(accessLogDao = database.accessLogDao())
    }

    companion object {
        lateinit var instance: TiditApp
            private set
    }
}
