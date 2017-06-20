package tech.summerly.smshelper.data.datasource

import tech.summerly.smshelper.data.SmsConfig
import tech.summerly.smshelper.data.database.SmsConfigDB

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
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun insert(smsConfig: SmsConfig): Long


    /**
     * 获取所有的记录
     */
    fun getAll(): List<SmsConfig>

    /**
     * 通过号码删除记录
     * @return the number of rows affected if a whereClause is passed in, 0
     *         otherwise. To remove all rows and get a count pass "1" as the
     *         whereClause.
     */
    fun deleteByNumber(number: String): Int

    /**
     * 通过号码获取一条记录
     * @return 如果不存在,则返回一个 SmsConfig 对象,
     *         其 ID为 -1, content为空, regex 为 DEFAULT_REGEX, number为传入的 number
     */
    fun getConfigByNumber(number: String): SmsConfig

}