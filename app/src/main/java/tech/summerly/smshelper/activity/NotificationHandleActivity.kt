package tech.summerly.smshelper.activity

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.extention.copyToClipboard
import tech.summerly.smshelper.extention.string
import tech.summerly.smshelper.extention.toast
import tech.summerly.smshelper.receiver.MessageReceiver
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.ID_NOTIFICATION_CODE
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.NAME_MESSAGE

/**
 * handle notification event
 *
 * @author YangBin
 */
class NotificationHandleActivity : Activity() {

    companion object {

        const val ACTION_COPY = "tech.summerly.action.copy"

        const val ACTION_UPDATE_REGEX = "tech.summerly.action.update_regex"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //收起 notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ID_NOTIFICATION_CODE)


        val message = intent.getSerializableExtra(NAME_MESSAGE) as Message?
        message?.let {
            when (intent.getStringExtra(MessageReceiver.NAME_ACTION)) {

                ACTION_COPY -> it.code.let { code: String ->
                    copyToClipboard(code)//复制验证码
                    toast(string(R.string.toast_format, code))//toast
                }

                ACTION_UPDATE_REGEX -> {//修改匹配规则
                    val intent = Intent(this, RegexModifyActivity::class.java)
                    val smsConfig = SmsConfigDataSource.dataSource.getConfigByNumber(number = it.number)
                    smsConfig.content = it.content
                    intent.putExtra(NAME_CONFIG, smsConfig)
                    startActivity(intent)
                }
            }
        }
        finish()
    }


}
