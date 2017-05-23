package tech.summerly.smshelper.utils.extention

import android.content.Context
import android.os.Build
import android.support.annotation.IdRes
import android.util.Log
import android.widget.Toast

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
fun Any.log(message: String?, tag: String = this.javaClass.name.replace("tech.summerly.smshelper", "")) {
    Log.i(tag, message)
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.color(@IdRes id: Int): Int {
    if (Build.VERSION.SDK_INT >= 23) {
        return this.getColor(id)
    } else {
        return this.resources.getColor(id)
    }
}