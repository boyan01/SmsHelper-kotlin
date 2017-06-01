package tech.summerly.smshelper.activity

import android.app.Activity
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.data.entity.Message
import tech.summerly.smshelper.data.entity.SmsConfig
import tech.summerly.smshelper.receiver.MessageReceiver
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.ID_NOTIFICATION_CODE
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.NAME_MESSAGE
import tech.summerly.smshelper.utils.extention.toast

class NotificationHandleActivity : AppCompatActivity() {

    companion object {
        val ACTION_COPY = "tech.summerly.action.copy"
        val ACTION_UPDATE_REGEX = "tech.summerly.action.update_regex"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //收起 notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ID_NOTIFICATION_CODE)


        val message = intent.getSerializableExtra(NAME_MESSAGE) as Message?
        message?.let {
            when (intent.getStringExtra(MessageReceiver.NAME_ACTION)) {

                ACTION_COPY -> copyCodeToClipboard(it.code)//复制验证码

                ACTION_UPDATE_REGEX -> {//修改匹配规则
                    val intent = Intent(this, RegexModifyActivity::class.java)
                    intent.putExtra(NAME_CONFIG, SmsConfig(number = it.number, content = it.content))
                    startActivity(intent)
                }
            }
        }
        finish()
    }

    @Suppress("DEPRECATION")
    private fun copyCodeToClipboard(code: String) = with(getSystemService(Context.CLIPBOARD_SERVICE)) {
        //toast
        toast(getString(R.string.toast_format, code))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            (this as ClipboardManager).primaryClip = ClipData.newPlainText("code", code)
        } else {
            (this as android.text.ClipboardManager).text = code
        }
    }

}
