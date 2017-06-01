package tech.summerly.smshelper.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatDrawableManager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_regex_modify.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.base.BaseActivity
import tech.summerly.smshelper.data.dao.SmsConfigDao
import tech.summerly.smshelper.data.entity.SmsConfig
import tech.summerly.smshelper.utils.extention.clear
import tech.summerly.smshelper.utils.extention.color
import tech.summerly.smshelper.utils.extention.log
import tech.summerly.smshelper.utils.extention.toast
import java.util.regex.Pattern

class RegexModifyActivity : BaseActivity() {

    companion object {
        val NAME_CONFIG = "smsConfig"
    }

    var smsConfig: SmsConfig? = null

    val stringConsole = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regex_modify)
        smsConfig = intent.getSerializableExtra(NAME_CONFIG) as SmsConfig?
        smsConfig?.let {
            editRegex.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrBlank()) {
                        return
                    }
                    //使用 stringBuilder 来作提醒的容器
                    stringConsole.clear()
                    showInputMatchedInfo(s.toString(), it.content)

                    textConsole.text = stringConsole
                    if (stringConsole.startsWith(getString(R.string.regex_modify_activity_console_error))) {
                        log("匹配出错")
                        textConsole.setTextColor(color(R.color.colorError))
                    } else {
                        textConsole.setTextColor(color(R.color.colorPrimary))
                    }

                }
            })
            textContent.text = it.content
            textMessageTitle.text = getString(R.string.regex_modify_activity_title_message, it.number)
            editRegex.setText(it.regex)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_modify_regex, menu)
        with(menu.findItem(R.id.menu_modify_regex_save)) {
            DrawableCompat.setTint(icon, color(R.color.colorWhite))
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_modify_regex_save -> saveRegexToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 将正则表达式和对应的 smsConfig 一起存入 SmsConfigDb 中
     */
    private fun saveRegexToDb() {
        if (editRegex.text.isEmpty()) {
            toast("您还什么都没输入哦!!")
            return
        }
        smsConfig?.let {
            it.regex = editRegex.text.toString()
            SmsConfigDao.save(it)
        }
    }

    /**
     * 根据输入的正则表达式 , 将匹配的结果以高亮的形式显示在 textContent 中
     */
    private fun showInputMatchedInfo(input: String, content: String) = try {
        textContent.text = content
        val pattern = Pattern.compile(input)
        val matcher = pattern.matcher(content)

        val ss = SpannableString.valueOf(content)
        stringConsole.append(getString(R.string.regex_modify_activity_console_regex))
        while (matcher.find()) {//遍历正则查找结果
            val result = matcher.group()
            if (result.isEmpty()) {
                stringConsole.append("NULL")
                break
            }
            stringConsole.append(result).append(" ")

            //遍历字符串, 高亮显示结果
            var index = 0
            while (index < content.length - 1) {
                index = ss.indexOf(result, index)
                if (index == -1) {//当前字符的查找已经结束
                    break
                }
                val span = ForegroundColorSpan(color(R.color.colorPrimary))
                ss.setSpan(span, index, index + result.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                index += result.length
            }
        }
        textContent.text = ss
    } catch (e: Exception) {
        stringConsole.clear()
        stringConsole.append(getString(R.string.regex_modify_activity_console_error))
                .append(e.message)
    }

    override fun onBackPressed() {
        //如果没做改变
        if (smsConfig?.regex?.equals((editRegex.text.toString())) ?: false) {
            finish()
            return
        }

        @Suppress("DEPRECATION")
        val icon = resources.getDrawable(R.drawable.ic_warning_black_24dp)
        DrawableCompat.setTint(icon, color(R.color.colorError))
        val dialog = AlertDialog.Builder(this)
                .setIcon(icon)
                .setTitle("修改未保存")
                .setMessage("是否完成保存再退出?")
                .setPositiveButton("保存", { _, _ ->
                    saveRegexToDb()
                    finish()
                })
                .setNegativeButton("否", { _, _ ->
                    finish()
                }).create()
        dialog.show()
    }

    override fun hasBackArrow(): Boolean = true

}
