package tech.summerly.smshelper.utils

import tech.summerly.smshelper.bean.Message
import tech.summerly.smshelper.data.dao.SmsConfigDao
import tech.summerly.smshelper.data.source.SmsConfigDbHelper
import tech.summerly.smshelper.utils.extention.DelegateExt
import java.util.regex.Pattern

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object SmsCodeHelper {


    private val NAME_KEYWORD = "keyword"

    fun parse(message: Message) {
        if (!isCodeContain(message)) {
            return
        }

        //向数据库请求该手机号码对应的正则表达式
        val regex: String = SmsConfigDao.getRegexByNumber(message.number)

        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(message.content)

        //如果找到了,就直接结束解析
        if (matcher.find()) {
            message.code = matcher.group()
            return
        }
    }

    /**
     * 判断是否是验证码短信
     */
    fun isCodeContain(message: Message): Boolean {
        val keyword by DelegateExt.preference(NAME_KEYWORD, "码\\code")
        keyword.split("\\").forEach {
            if (message.content.contains(it)) {
                message.isCodeMessage = true
                return true
            }
        }
        return false
    }

}