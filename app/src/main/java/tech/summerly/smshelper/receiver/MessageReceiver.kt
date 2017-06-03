package tech.summerly.smshelper.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import tech.summerly.smshelper.data.entity.Message
import tech.summerly.smshelper.utils.SmsCodeHelper
import tech.summerly.smshelper.utils.extention.log
import tech.summerly.smshelper.utils.extention.showContentInfo

class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pdus = intent.extras["pdus"] as Array<*>?
        val message = parseMessage(pdus, intent.getStringExtra("format"))
        message?.let {
            SmsCodeHelper.parse(it)
            if (!it.code.isEmpty()) {
                context.showContentInfo(it)
            } else {
                log("不是验证码短信,不进行处理.")
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


}
