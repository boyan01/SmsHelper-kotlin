package tech.summerly.smshelper.data.entity

import java.io.Serializable

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class Message(val number: String, val content: String) : Serializable {
    var code: String = ""
}

