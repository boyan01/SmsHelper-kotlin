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
data class SmsConfig(var id: Int = -1, val number: String, val content: String, var regex: String? = null) : Serializable