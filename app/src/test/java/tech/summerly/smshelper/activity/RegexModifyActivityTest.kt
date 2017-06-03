package tech.summerly.smshelper.activity

import org.junit.Test
import java.util.regex.Pattern


/**
 * <pre>
 * author : YangBin
 * e-mail : yangbinyhbn@gmail.com
 * time   : 2017/6/2
 * desc   :
 * version: 1.0
</pre> *
 */

class RegexModifyActivityTest {


    val testmap = mapOf(
            "24jriojiojgajgop" to "jrio",
            "aaaaaaaaaaaaaaa" to "aa",
            "nihcao nide yjzhan ui 565466" to "565466",
            "656465 uis yuot  code" to "656465",
            "nihfihia is 65545 eor osf jiog" to "65545",
            "fa45646isadbvc" to "45646",
            "fa\\d45646is" to "45646"
    )

    @Test
    fun getRegexByIndexTest() {
        testmap.forEach { t, u ->
            val start = t.indexOf(u)
            val end = start + u.length

            val regex = getRegexByIndex(t, start, end)
            println("regex:$regex ")
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(t)
            matcher.find()
            println("$u  find: ${matcher.group()} \n $t\n\n")
        }
    }

    @Test
    fun escapeTest() {

        val strs = mapOf("(?<=adb).*?adb" to "(?<=adb)", "\\d{5}" to "\\d{")
        strs.forEach { t, u ->
            val pattern = Pattern.compile(u.escape())
            val matcher = pattern.matcher(t)
            print(matcher.find().toString() + "  ")
            println(matcher.group())
        }

    }

    private val REGEX_TEMPLATE = "%s.*?%s"

    private val REGEX_META_CHAR = "\\^$()*+?.[]{}|"

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

    private fun String.escape(): String {
        var result = this
        REGEX_META_CHAR.forEach {
            result = result.replace(it.toString(), "\\$it")
        }
        return result
    }

}