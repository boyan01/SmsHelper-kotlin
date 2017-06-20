package tech.summerly.smshelper.data

import java.io.Serializable

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/18
 *     desc   : 数据实体
 * </pre>
 */

//短信
data class Message(val number: String, val content: String) : Serializable {
    companion object {
        val serialVersionUID = 3L
    }

    var code: String = ""
}

//短信配置
data class SmsConfig(val id: Int = -1, val number: String, var content: String = "", var regex: String = "") : Serializable {
    companion object {
        val serialVersionUID = 3L shl 2
    }

}