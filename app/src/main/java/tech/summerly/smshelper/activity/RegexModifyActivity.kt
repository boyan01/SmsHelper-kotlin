package tech.summerly.smshelper.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_regex_modify.*
import kotlinx.android.synthetic.main.window_pop_regex.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.base.BaseActivity
import tech.summerly.smshelper.data.SmsConfig
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.utils.extention.clear
import tech.summerly.smshelper.utils.extention.color
import tech.summerly.smshelper.utils.extention.log
import tech.summerly.smshelper.utils.extention.toast
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 * 此 activity 启动模式为 single task
 */
class RegexModifyActivity : BaseActivity() {

    companion object {
        val NAME_CONFIG = "smsConfig"
    }

    var smsConfig: SmsConfig? = null

    val stringConsole = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regex_modify)
        init(intent)

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_mode_comment_black_24dp)
        DrawableCompat.setTint(drawable, color(R.color.colorAccent))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textAttention.background = drawable
        } else {
            @Suppress("DEPRECATION")
            textAttention.setBackgroundDrawable(drawable)
        }
        textAttention.setOnClickListener {
            editRegex.setText(textAttention.text)
            val animation = AnimationUtils.loadAnimation(this, R.anim.anim_window_pop_regex_gone)
            textAttention.startAnimation(animation)
            textAttention.visibility = View.GONE
        }
    }

    /**
     * 用于监听用户所选择的短信内容部分
     */
    var contentSelectionWatcher: Runnable by Delegates.notNull<Runnable>()


    private val REGEX_TEMPLATE = "%s.*?%s"

    /**
     * 自动生成正则表达式,根据字符串start -> end 间的内容自动选择正则表达式
     *
     */
    private fun getRegexByIndex(text: CharSequence, start: Int, end: Int): String {
        val first = Math.min(start, end)
        val second = Math.max(start, end)
        val regex1 = when {
            first == 0 -> "^"
            first in 1..4 -> "(?<=^${text.substring(0, first).escape()})"
            first >= 5 && first < text.length - 1 -> "(?<=${text.substring(first - 5, first).escape()})"//零宽度正回顾后发断言
            else -> throw ArrayIndexOutOfBoundsException(start)
        }

        val regex2 = when {
            second == text.length -> "$"
            second < text.length && second >= text.length - 4 -> "(?=${text.substring(second, text.length).escape()}$)"
            second < text.length - 4 && second >= 0 -> "(?=${text.substring(second, second + 5).escape()})"//零宽度正预测先行断言
            else -> throw ArrayIndexOutOfBoundsException(start)
        }
        return REGEX_TEMPLATE.format(regex1, regex2)
    }

    private val REGEX_META_CHAR = "\\^$()*+?.[]{}|"

    /**
     * 将字符串进行正则转义,防止其被识别为正则表达式
     */
    private fun String.escape(): String {
        var result = this
        REGEX_META_CHAR.forEach {
            result = result.replace(it.toString(), "\\$it")
        }
        return result
    }


    override fun onResume() {
        super.onResume()
        contentSelectionWatcher = Runnable {
            if (textContent.selectionStart != textContent.selectionEnd) {
                val regex = getRegexByIndex(textContent.text, textContent.selectionStart, textContent.selectionEnd)
                log("自动生成的正则为 : $regex")
                showRecommendRegex(regex)
            }
            textContent.postDelayed(contentSelectionWatcher, 500)
        }
        textContent.post(contentSelectionWatcher)
    }


    private fun showRecommendRegex(regex: String) {
        if (textAttention.visibility != View.VISIBLE) {
            textAttention.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_widow_pop_regex_show))
            textAttention.visibility = View.VISIBLE
        }
        textAttention.text = regex
    }

    override fun onPause() {
        super.onPause()
        textContent.removeCallbacks(contentSelectionWatcher)
    }


    private fun init(intent: Intent) {
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
            if (it.id == -1) {
                SmsConfigDataSource.dataSource.insert(it)
            } else {
                SmsConfigDataSource.dataSource.update(it)
            }
            toast("保存成功!")
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
            stringConsole.append(result.replace(' ', '_')).append(" ")//使用 _ 代替结果中的空格

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
        if (smsConfig == null || smsConfig?.regex == null ||
                smsConfig?.regex?.equals(editRegex.text.toString()) ?: false) {
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        init(intent)
    }

    override fun hasBackArrow(): Boolean = true

}
