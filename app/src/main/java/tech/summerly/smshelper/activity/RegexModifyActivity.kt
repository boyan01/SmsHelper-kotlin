package tech.summerly.smshelper.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_regex_modify.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.data.dao.SmsConfigDao
import tech.summerly.smshelper.data.entity.Message
import tech.summerly.smshelper.receiver.MessageReceiver
import tech.summerly.smshelper.utils.extention.color
import tech.summerly.smshelper.utils.extention.log
import tech.summerly.smshelper.utils.extention.toast
import java.util.regex.Pattern

class RegexModifyActivity : Activity() {

    var message: Message? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regex_modify)
        message = intent.getSerializableExtra(MessageReceiver.NAME_MESSAGE) as Message?
        message?.let {
            textContent.text = it.content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                actionBar?.title = it.number
            } else {
                title = it.number
            }
            editRegex.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrBlank()) {
                        return
                    }
                    showInputMatchedInfo(s.toString(), it.content)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MenuInflater(this).inflate(R.menu.menu_modify_regex, menu)
        with(menu.findItem(R.id.menu_modify_regex_save)) {
            DrawableCompat.setTint(icon, color(R.color.colorContent))
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.menu_modify_regex_save -> saveRegexToDb()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 将正则表达式和对应的 message 一起存入 SmsConfigDb 中
     */
    private fun saveRegexToDb() {
        if (editRegex.text.isEmpty()) {
            toast("您还什么都没输入哦!!")
            return
        }
        message?.let {
            SmsConfigDao.save(it.number, it.content, editRegex.text.toString())
        }
    }

    /**
     * 根据输入的正则表达式 , 将匹配的结果以高亮的形式显示在 textContent 中
     */
    private fun showInputMatchedInfo(input: String, content: String) = try {
        textContent.text = content
        val pattern = Pattern.compile(input)
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            val result = matcher.group()
            if (result.isEmpty()) {
                break
            }
            log("找到正则的结果 : $result")
            val spannableString = SpannableString.valueOf(content)
            var index = 0
            while (index < content.length - 1) {
                index = spannableString.indexOf(result, index)
                if (index == -1) {
                    break
                }
                val span = ForegroundColorSpan(color(R.color.colorPrimary))
                spannableString.setSpan(span, index, index + result.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                index += result.length
                log("index : " + index)
            }
            textContent.text = spannableString
        }
    } catch (e: Exception) {
        log(e.message)
    }


}
