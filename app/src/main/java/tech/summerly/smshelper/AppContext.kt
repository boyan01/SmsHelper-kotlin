package tech.summerly.smshelper

import android.app.Activity
import android.app.Application
import tech.summerly.smshelper.utils.extention.DelegateExt
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
        var instance by Delegates.notNull<AppContext>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val activities = mutableListOf<Activity>()

    fun register(activity: Activity) {
        activities.add(activity)
    }

    fun unRegister(activity: Activity) {
        activities.remove(activity)
    }

    fun exit() {
        val activityList = activities.toList()
        activityList.forEach {
            it.finish()
        }
    }
}