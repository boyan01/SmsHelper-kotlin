package tech.summerly.smshelper.data.entity

import java.io.Serializable

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class SmsConfig(val id: Int = -1, val number: String, var content: String = "", var regex: String = "") : Serializable