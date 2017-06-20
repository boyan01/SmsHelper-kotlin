package tech.summerly.smshelper.utils

import tech.summerly.smshelper.R
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.string
import tech.summerly.smshelper.utils.extention.log
import java.util.regex.Pattern

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.1
 * </pre>
 */
object SmsCodeHelper {


    fun parse(message: Message) {
        if (!isCodeContain(message)) {
            return
        }
        if (message.content.length >= 1000) {//防止短信内容过大,导致正则搜索时间过长而无响应
            log("message content is too large")
            return
        }
        //向数据库请求该手机号码对应的正则表达式
        val regex: String = SmsConfigDataSource.dataSource.getRegexByNumber(message.number)

        try {
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(message.content)
            if (matcher.find()) {        //如果找到了,就直接结束解析
                message.code = matcher.group()
                return
            }
        } catch (e: Exception) {//正则匹配出错
            log(e.message)
        }

    }

    /**
     * 判断是否是验证码短信
     */
    fun isCodeContain(message: Message): Boolean {
        //从 shared preference 获取 keyword ,如果不存在,则使用默认的 keyword
        val keyword by DelegateExt.preference(
                string(R.string.key_setting_default_keyword), string(R.string.default_keyword))
        keyword.split("\\").forEach {
            log("keyword $keyword split $it")
            if (message.content.contains(it)) {
                return true
            }
        }
        return false
    }

}