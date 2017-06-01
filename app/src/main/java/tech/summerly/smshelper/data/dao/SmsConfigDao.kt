package tech.summerly.smshelper.data.dao

import tech.summerly.smshelper.R
import tech.summerly.smshelper.data.entity.SmsConfig
import tech.summerly.smshelper.data.source.SmsConfigDbHelper
import tech.summerly.smshelper.data.source.SmsConfigDbHelper.Companion.CONTENT
import tech.summerly.smshelper.data.source.SmsConfigDbHelper.Companion.ID
import tech.summerly.smshelper.data.source.SmsConfigDbHelper.Companion.NAME_TABLE
import tech.summerly.smshelper.data.source.SmsConfigDbHelper.Companion.NUMBER
import tech.summerly.smshelper.data.source.SmsConfigDbHelper.Companion.REGEX
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.string
import tech.summerly.smshelper.utils.extention.log

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/22
 *     desc   :
 *     version:
 * </pre>
 */
object SmsConfigDao {


    /**
     * 通过号码查找对应的正则表达式
     */
    fun getRegexByNumber(number: String): String = with(SmsConfigDbHelper.instance) {

        //默认的验证码提出正则
        val DEFAULT_REGEX: String by lazy {
            val regex by DelegateExt.preference(
                    string(R.string.key_setting_default_regex), string(R.string.default_regex))
            regex
        }

        val sql = "select * from $NAME_TABLE where $NUMBER = ?"
        val cursor = readableDatabase.rawQuery(sql, arrayOf(number))
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(REGEX)
            if (index < 0) {
                log("有相应列表项,但没找到对应的正则表达式")
                return DEFAULT_REGEX
            }
            val regex = cursor.getString(index)
            cursor.close()
            return regex
        }
        cursor.close()
        DEFAULT_REGEX//如果没有,就返回默认的验证码正则模版
    }

    /**
     * 插入一个新的配置到表中
     */
    private fun add(number: String, content: String = "", regex: String) {
        log("正在插入配置表项")
        val dbHelper = SmsConfigDbHelper.instance
        val sql = "insert into ${SmsConfigDbHelper.NAME_TABLE} values(null,?,?,?)"
        dbHelper.writableDatabase.execSQL(sql, arrayOf(number, content, regex))
        dbHelper.writableDatabase.close()
    }

    private fun update(id: Int, number: String, content: String, regex: String) = with(SmsConfigDbHelper.instance) {
        log("正在更新配置表项")
        val sql = "update $NAME_TABLE set $NUMBER = ? ,$CONTENT = ? ,$REGEX = ? where $ID = ?"
        writableDatabase.execSQL(sql, arrayOf(number, content, regex, id))
        writableDatabase.close()
    }

    fun save(config: SmsConfig) = with(config) {
        if (regex == null || regex!!.isEmpty()) {
            return@with
        }
        if (id == -1) {
            add(number = number, content = content, regex = regex!!)
        } else {
            update(id, number, content, regex!!)
        }
    }


    fun getAll(): List<SmsConfig> {
        val configs = mutableListOf<SmsConfig>()
        val dbHelper = SmsConfigDbHelper.instance
        val sql = "select * from $NAME_TABLE"
        val cursor = dbHelper.readableDatabase.rawQuery(sql, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val number = cursor.getString(cursor.getColumnIndex(NUMBER))
                val regex = cursor.getString(cursor.getColumnIndex(REGEX))
                val content = cursor.getString(cursor.getColumnIndex(CONTENT))
                configs.add(SmsConfig(id = id, number = number, content = content, regex = regex))
            } while (cursor.moveToNext())
        }
        cursor.close()
        dbHelper.readableDatabase.close()
        return configs.toList()
    }


}