package tech.summerly.smshelper.data.database

import tech.summerly.smshelper.R
import tech.summerly.smshelper.data.SmsConfig
import tech.summerly.smshelper.data.database.SmsConfigDbHelper.Companion.CONTENT
import tech.summerly.smshelper.data.database.SmsConfigDbHelper.Companion.ID
import tech.summerly.smshelper.data.database.SmsConfigDbHelper.Companion.NAME_TABLE
import tech.summerly.smshelper.data.database.SmsConfigDbHelper.Companion.NUMBER
import tech.summerly.smshelper.data.database.SmsConfigDbHelper.Companion.REGEX
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.string

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/22
 *     desc   :
 *     version:
 * </pre>
 */
object SmsConfigDB : SmsConfigDataSource {


    //默认的正则
    private val DEFAULT_REGEX: String by lazy {
        val regex by DelegateExt.preference(
                string(R.string.key_setting_default_regex), string(R.string.default_regex))
        regex
    }

    /**
     * 通过号码查找对应的正则表达式
     * @return 如果没有,就返回默认的验证码正则模版
     */
    override fun getRegexByNumber(number: String): String = with(SmsConfigDbHelper.instance) {
        val (_, _, _, regex) = getConfigByNumber(number = number)
        if (regex.isEmpty()) {
            return@with DEFAULT_REGEX
        } else {
            return@with regex
        }
    }


    /**
     * 更新一条记录的内容和正则
     */
    override fun update(smsConfig: SmsConfig) = with(smsConfig) {
        val sql = "update $NAME_TABLE set $CONTENT = ? ,$REGEX = ? where $NUMBER = ?"
        SmsConfigDbHelper.instance.writableDatabase.execSQL(sql, arrayOf(content, regex, number))
    }


    /**
     * 将一条不存在的记录插入数据库中
     * 如果 id 不为 -1,则 插入到原来的位置
     */
    override fun insert(smsConfig: SmsConfig) = with(smsConfig) {
        val sql = "insert into ${SmsConfigDbHelper.NAME_TABLE} values(?,?,?,?) "
        SmsConfigDbHelper.instance.writableDatabase.execSQL(sql, arrayOf(if (id == -1) null else id, number, content, regex))

    }


    override fun getAll(): List<SmsConfig> {
        val configs = mutableListOf<SmsConfig>()
        val sql = "select * from $NAME_TABLE"
        val cursor = SmsConfigDbHelper.instance.readableDatabase.rawQuery(sql, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val number = cursor.getString(cursor.getColumnIndex(NUMBER))
                val regex = cursor.getString(cursor.getColumnIndex(REGEX))
                val content = cursor.getString(cursor.getColumnIndex(CONTENT))
                configs.add(SmsConfig(id = id, number = number, content = content, regex = regex))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return configs.toList()
    }

    /**
     * 通过号码删除记录
     */
    override fun deleteByNumber(number: String) =
            SmsConfigDbHelper.instance.writableDatabase.delete(NAME_TABLE, "$NUMBER = ?", arrayOf(number))

    /**
     * 通过号码获取一条记录
     * @return 如果不存在,则返回一个 SmsConfig 对象,
     *         其 ID为 -1, content为空, regex 为 DEFAULT_REGEX, number为传入的 number
     */
    override fun getConfigByNumber(number: String): SmsConfig {
        val sql = "select * from $NAME_TABLE where $NUMBER = ?"
        val cursor = SmsConfigDbHelper.instance.readableDatabase.rawQuery(sql, arrayOf(number))
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(ID))
            val regex = cursor.getString(cursor.getColumnIndex(REGEX))
            val content = cursor.getString(cursor.getColumnIndex(CONTENT))
            cursor.close()
            return SmsConfig(id = id, number = number, content = content, regex = regex)
        }
        cursor?.close()
        return SmsConfig(number = number, regex = DEFAULT_REGEX)
    }

}