package tech.summerly.smshelper

import android.text.TextUtils
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        print(1 shl 2)
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun test() {
        val regex = Regex("\\d{5}")
        val text = "12306用户注册或既有用户手机核验专用验证码：060973。如非本人直接访问12306，请停止操作，切勿将验证码提供给第三方。"

        val result = text.replace(regex, {
            println("range : ${it.range}")
            "<red>${it.value}"
        })
        println(result)
    }

    @Test
    fun formatTest() {
        println(string(1))

        println(string(1, "world"))
    }

    val stringMap = mapOf(1 to "hello%s", 2 to "nihao %s")

    private fun string(id: Int): String = stringMap[id]!!

    private fun string(id: Int, vararg formatArgs: Any): String = string(id).format(*formatArgs)

}