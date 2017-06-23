package tech.summerly.smshelper.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import tech.summerly.smshelper.R
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.receiver.MessageReceiver

class SmsNotifyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMessage(intent)
    }

    var dialog: Dialog? = null

    fun displayMessage(intentOfContext: Intent): Unit {
        val message = intentOfContext.getSerializableExtra(MessageReceiver.NAME_MESSAGE) as Message?
        message?.let {

            //添加通知处理操作
            val intent = Intent(this, NotificationHandleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(MessageReceiver.NAME_MESSAGE, message)

            //action : 复制验证码
            val copyIntent = Intent(intent)
            copyIntent.putExtra(MessageReceiver.NAME_ACTION, NotificationHandleActivity.ACTION_COPY)


            //action : 修改当前号码对应的正则表达式
            val updateIntent = Intent(intent)
            updateIntent.putExtra(MessageReceiver.NAME_ACTION, NotificationHandleActivity.ACTION_UPDATE_REGEX)

            dialog?.dismiss()
            dialog = AlertDialog.Builder(this, R.style.SmsNotifyDialog)
                    .setTitle("来自: " + it.number)
                    .setMessage("验证码: " + it.code)
                    .setOnCancelListener {
                        finish()
                    }
                    .setNegativeButton(R.string.notification_action_update_regex) { _, _ ->
                        startActivity(updateIntent)
                        finish()
                    }
                    .setPositiveButton(R.string.notification_action_copy) { _, _ ->
                        startActivity(copyIntent)
                        finish()
                    }.create()
            dialog?.show()
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        displayMessage(intent)
    }

}
