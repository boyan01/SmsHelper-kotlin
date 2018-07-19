package tech.summerly.smshelper

import android.app.Activity
import android.app.Application
import tech.summerly.smshelper.extention.log

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

        private var sInstance: AppContext? = null

        val instance get() = sInstance!!
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
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