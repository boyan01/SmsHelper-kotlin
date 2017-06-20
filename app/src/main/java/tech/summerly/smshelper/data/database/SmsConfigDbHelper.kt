package tech.summerly.smshelper.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import tech.summerly.smshelper.AppContext

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SmsConfigDbHelper constructor(context: Context = AppContext.instance) : SQLiteOpenHelper(context, NAME_FILE, null, VERSION) {

    companion object {
        private val NAME_FILE = "smsConfig.db"
        private val VERSION = 1

        val NAME_TABLE = "config"
        val ID = "_id"
        val NUMBER = "number"
        val REGEX = "regex"
        val CONTENT = "content"
        val instance by lazy { SmsConfigDbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "create table if not exists $NAME_TABLE(" +
                "$ID integer primary key autoincrement," + //id
                "$NUMBER varchar(20)," + //电话号码
                "$CONTENT varchar(200)," + // 内容
                "$REGEX varchar(50)" + //正则表达式
                ")"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}
