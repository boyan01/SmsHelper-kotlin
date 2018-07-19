package tech.summerly.smshelper.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class SmsConfig(
        @PrimaryKey
        val id: Long,
        val number: String,
        val content: String,
        val regex: String
)