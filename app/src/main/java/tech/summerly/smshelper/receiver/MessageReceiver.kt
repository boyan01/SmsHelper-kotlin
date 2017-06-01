package tech.summerly.smshelper.receiver

import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.telephony.SmsMessage
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.NotificationHandleActivity
import tech.summerly.smshelper.data.entity.Message
import tech.summerly.smshelper.utils.SmsCodeHelper
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.log

class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pdus = intent.extras["pdus"] as Array<*>?
        val message = parseMessage(pdus, intent.getStringExtra("format"))
        message?.let {
            SmsCodeHelper.parse(it)
            if (!it.code.isEmpty()) {
                showContentInfo(it, context)
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

    /**
     * 弹出验证码解析结果的 notification
     */
    private fun showContentInfo(message: Message, context: Context) {


        //添加通知处理操作
        val intent = Intent(context, NotificationHandleActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(NAME_MESSAGE, message)

        //action : 复制验证码
        val copy = Intent(intent)
        copy.putExtra(NAME_ACTION, NotificationHandleActivity.ACTION_COPY)
        val copyIntent = getActivity(context, 100,
                copy, FLAG_UPDATE_CURRENT)

        //如果打开了自动复制选项
        val isAutoCopy by DelegateExt.preference(context.getString(R.string.key_setting_auto_copy), false)
        if (isAutoCopy) {
            log("自动复制...")
            context.startActivity(copy)
            return
        }

        //action : 修改当前号码对应的正则表达式
        val update = Intent(intent)
        update.putExtra(NAME_ACTION, NotificationHandleActivity.ACTION_UPDATE_REGEX)
        val updateIntent = getActivity(context, 99,
                update, FLAG_UPDATE_CURRENT)

        with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setStyle(android.support.v7.app.NotificationCompat.DecoratedCustomViewStyle())
                    .setContentTitle(message.number)
                    .setContentIntent(copyIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .addAction(R.drawable.ic_content_copy_24dp, context.getString(R.string.notification_action_copy), copyIntent)
                    .addAction(R.drawable.ic_edit_black_24dp, context.getString(R.string.notification_action_update_regex), updateIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setFullScreenIntent(copyIntent, true)
                    .setContentText("验证码:" + if (message.code.isEmpty()) "解析失败" else message.code
                    )
            notify(ID_NOTIFICATION_CODE, builder.build())
        }
    }

}
