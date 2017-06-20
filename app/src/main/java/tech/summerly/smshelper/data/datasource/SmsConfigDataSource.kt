package tech.summerly.smshelper.data.datasource

import tech.summerly.smshelper.data.SmsConfig
import tech.summerly.smshelper.data.database.SmsConfigDB
import tech.summerly.smshelper.data.database.SmsConfigDbHelper

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/18
 *     desc   :
 * </pre>
 */
interface SmsConfigDataSource {

    companion object {
        val dataSource: SmsConfigDataSource by lazy {
            SmsConfigDB
        }
    }

    /**
     * 通过号码查找对应的正则表达式
     * @return 如果没有,就返回默认的验证码正则模版
     */
    fun getRegexByNumber(number: String): String


    /**
     * 更新一条记录的内容和正则
     */
    fun update(smsConfig: SmsConfig)

    /**
     * 将一条不存在的记录插入数据库中
     * 如果 id 不为 -1,则 插入到原来的位置
     */
    fun insert(smsConfig: SmsConfig)


    /**
     * 获取所有的记录
     */
    fun getAll(): List<SmsConfig>

    /**
     * 通过号码删除记录
     */
    fun deleteByNumber(number: String) =
            SmsConfigDbHelper.instance.writableDatabase.delete(SmsConfigDbHelper.NAME_TABLE, "${SmsConfigDbHelper.NUMBER} = ?", arrayOf(number))

    /**
     * 通过号码获取一条记录
     * @return 如果不存在,则返回一个 SmsConfig 对象,
     *         其 ID为 -1, content为空, regex 为 DEFAULT_REGEX, number为传入的 number
     */
    fun getConfigByNumber(number: String): SmsConfig

}