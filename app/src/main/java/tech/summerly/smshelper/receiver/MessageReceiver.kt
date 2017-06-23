package tech.summerly.smshelper.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.telephony.SmsMessage
import org.jetbrains.anko.startActivity
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.SmsNotifyActivity
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.extention.*
import java.util.regex.Pattern

class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pdus = intent.extras["pdus"] as Array<*>?
        val message = parseMessage(pdus, intent.getStringExtra("format"))
        message?.let {
            if (!message.isCodeMessage()) {
                return
            }

            parse(it)

            val isCopyDirectly = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(string(R.string.key_setting_auto_copy), false)

            if (isCopyDirectly) {
                context.copyToClipboard(it.code)//复制验证码
                context.toast(context.getString(R.string.toast_format, it.code))//toast
                return
            }

            val displayByDialog = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(string(R.string.key_setting_display_sms_dialog), true)

            if (displayByDialog) {
                context.startActivity<SmsNotifyActivity>(NAME_MESSAGE to it)
            } else {
                context.showContentInfo(it)
            }

        }
    }

    companion object {
        const val ID_NOTIFICATION_CODE = 10141
        val NAME_MESSAGE = "smsConfig"
        val NAME_ACTION = "action"

    }


    /**
     * 将 pdus 解析成一个 Message 实体
     */
    private fun parseMessage(pdus: Array<*>?, format: String): Message? = pdus?.let {
        val messages = mutableListOf<SmsMessage>()
        it.forEach {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                messages.add(SmsMessage.createFromPdu(it as ByteArray?, format))
            } else {
                @Suppress("DEPRECATION")
                messages.add(SmsMessage.createFromPdu(it as ByteArray))
            }
        }
        //获得发送人
        val number = messages[0].originatingAddress

        val content = StringBuilder()
        for (message in messages) {
            content.append(message.messageBody)
        }
        this.log("发送人: $number \n 内容: $content")
        return Message(number, content.toString())
    }


    fun parse(message: Message) {
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
    private fun Message.isCodeMessage(): Boolean {
        //从 shared preference 获取 keyword ,如果不存在,则使用默认的 keyword
        val keyword by DelegateExt.preference(
                string(R.string.key_setting_default_keyword), string(R.string.default_keyword))
        keyword.split("\\").forEach {
            log("keyword $keyword split $it")
            if (content.contains(it)) {
                return true
            }
        }
        return false
    }

}
