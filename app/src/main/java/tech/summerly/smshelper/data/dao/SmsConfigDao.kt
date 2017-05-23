package tech.summerly.smshelper.data.dao

import tech.summerly.smshelper.data.source.SmsConfigDbHelper
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.log

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object SmsConfigDao {

    val DEFAULT_REGEX: String by lazy {
        val str by DelegateExt.preference("default_regex", "\\d{4,8}")
        return@lazy str
    }

    fun getRegexByNumber(number: String): String = with(SmsConfigDbHelper) {
        val sql = "select * from $NAME_TABLE where $NUMBER = ?"
        val cursor = SmsConfigDbHelper().readableDatabase.rawQuery(sql, arrayOf(number))
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(REGEX)
            if (index < 0) {
                log("有相应列表项,但没找到对应的正则表达式")
                return@with DEFAULT_REGEX
            }
            val regex = cursor.getString(index)
            cursor.close()
            return regex
        }
        cursor.close()
        DEFAULT_REGEX//如果没有,就返回默认的验证码正则模版
    }

    fun save(number: String, content: String = "", regex: String) {
        val dbHelper = SmsConfigDbHelper()
        val sql = "insert into ${SmsConfigDbHelper.NAME_TABLE} values(" +
                "null," +
                "'$number'," +
                "'$content'," +
                "'$regex'" +
                ")"
        log(sql)
        dbHelper.writableDatabase.execSQL(sql)
        dbHelper.writableDatabase.close()
    }


}