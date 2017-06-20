package tech.summerly.smshelper.activity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.receiver.MessageReceiver
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.ID_NOTIFICATION_CODE
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.NAME_MESSAGE
import tech.summerly.smshelper.utils.extention.copyToClipboard
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

                ACTION_COPY -> it.code.let {
                    copyToClipboard(it)//复制验证码
                    toast(getString(tech.summerly.smshelper.R.string.toast_format, it))//toast
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
