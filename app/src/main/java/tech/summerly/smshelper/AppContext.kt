package tech.summerly.smshelper

import android.app.Activity
import android.app.Application
import kotlin.properties.Delegates

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class AppContext : Application() {

    companion object {

        @Suppress("ObjectPropertyName")
        private var _instance: AppContext? = null

        val instance get() = _instance!!
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }


    @Deprecated("...")
    fun register(activity: Activity) {
        //do nothing
    }

    @Deprecated("...")
    fun unRegister(activity: Activity) {
        //do nothing
    }

    fun exit() {

    }
}