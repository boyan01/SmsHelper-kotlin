package tech.summerly.smshelper.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.data.dao.CodeConfigDao
import tech.summerly.smshelper.data.entity.SmsConfig

@Database(entities = [SmsConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun codeConfigDao(): CodeConfigDao


    companion object {

        private const val DB_NAME = "f.db"

        val instance: AppDatabase by lazy {
            Room.databaseBuilder(AppContext.instance, AppDatabase::class.java, DB_NAME)
                    .allowMainThreadQueries()
                    .build()
        }

    }

}