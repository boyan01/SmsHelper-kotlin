package tech.summerly.smshelper.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_send_message.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.base.BaseActivity
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.extention.showContentInfo
import tech.summerly.smshelper.extention.toast

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/2
 *     desc   : used to simulate send a message broadcast to test
 *     version: 1.0
 * </pre>
 */
class SendMessageActivity : BaseActivity() {

    val messages by lazy {
        listOf("5665456" to "this is a test message, code is 545356",
                "12306" to "564656 is your code")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        buttonSend.setOnClickListener {
            if (textNumber.text.isEmpty()) {
                textNumber.error = "can not be empty"
            }
            if (textContent.text.isEmpty()) {
                textContent.error = "can not be empty"
            }

            val message = Message(textNumber.text.toString(), textContent.text.toString())
//            parse(message)
            if (message.code.isEmpty()) {
                toast("没解析出来")
            } else {
                showContentInfo(message)
            }
        }

        buttonRandom.setOnClickListener {
            val (number, content) = messages[(Math.random() * messages.size).toInt()]
            textContent.setText(content)
            textNumber.setText(number)
        }
    }
}