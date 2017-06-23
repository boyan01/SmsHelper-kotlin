package tech.summerly.smshelper.extention

import android.util.Base64
import java.io.*

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/20
 *     desc   :
 * </pre>
 */
fun Serializable.serialize(): String {

    var baops: ByteArrayOutputStream? = null
    var oos: ObjectOutputStream? = null

    try {
        baops = ByteArrayOutputStream()
        oos = ObjectOutputStream(baops)
        oos.writeObject(this)
        val bytes = baops.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: IOException) {

    } finally {
        try {
            oos?.close()
            baops?.close()
        } catch (e: IOException) {

        }
    }
    return ""
}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> String.getObjectFromString(): T? {
    var ois: ObjectInputStream? = null
    try {
        val bytes = Base64.decode(this, Base64.DEFAULT)
        ois = ObjectInputStream(ByteArrayInputStream(bytes))
        return ois.readObject() as T
    } catch (e: Exception) {
        return null
    } finally {
        try {
            ois?.close()
        } catch (e: Exception) {

        }
    }
}