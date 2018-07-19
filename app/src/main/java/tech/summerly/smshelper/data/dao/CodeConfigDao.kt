package tech.summerly.smshelper.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import tech.summerly.smshelper.data.entity.SmsConfig

@Dao
abstract class CodeConfigDao {

    @Query(value = "select * from config")
    abstract fun allConfigs(): LiveData<List<SmsConfig>>

    @Insert
    abstract fun insertConfig(config: SmsConfig)

    @Delete
    abstract fun removeConfig(config: SmsConfig)
}