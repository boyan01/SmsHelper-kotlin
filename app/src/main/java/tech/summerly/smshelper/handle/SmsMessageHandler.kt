package tech.summerly.smshelper.handle

import android.preference.PreferenceManager
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.R
import tech.summerly.smshelper.extention.string
import tech.summerly.smshelper.model.SmsMessage

/**
 * util class to parse message
 */
object SmsMessageHandler {

    /**
     */
    fun handleMessage(message: SmsMessage) {
        //step 1: check is code message
        if (!isCodeMessage(message.content)) {
            return
        }
        //step 2: parse code
        //TODO
    }


    private fun isCodeMessage(content: String): Boolean {
        val keyword = PreferenceManager.getDefaultSharedPreferences(AppContext.instance)
                .getString(string(R.string.key_setting_default_keyword),
                        string(R.string.default_keyword))
        keyword.split("\\").forEach {
            if (content.contains(it)) {
                return true
            }
        }
        return false
    }


}