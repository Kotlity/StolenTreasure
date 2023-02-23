package net.peakgames.ginrummyplu

import android.app.Application
import org.greenrobot.greendao.database.Database

class FlamingCoinsApplication: Application() {

    private lateinit var helper: DaoMaster.DevOpenHelper
    private lateinit var database: Database
    lateinit var daoSession: DaoSession

    override fun onCreate() {
        super.onCreate()
        OneSignalStuff.setDebugLogLevelOneSignal()
        OneSignalStuff.initOneSignal(this)
        helper = DaoMaster.DevOpenHelper(this, "devOpenHelper", null)
        database = helper.writableDb
        daoSession = DaoMaster(database).newSession()
    }
}