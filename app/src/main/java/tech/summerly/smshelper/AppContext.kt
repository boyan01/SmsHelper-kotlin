package tech.summerly.smshelper

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
        var keyword by DelegateExt.preference("keyword", "")
        if (keyword.isEmpty()) {
            keyword = "\\码\\code\\碼" //这个操作将保存默认 keyword 到相应的 preference 中
        }
    }
}