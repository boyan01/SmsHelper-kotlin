package tech.summerly.smshelper.utils.extention

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.IdRes
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.NotificationHandleActivity
import tech.summerly.smshelper.data.entity.Message
import tech.summerly.smshelper.receiver.MessageReceiver

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
    if (false) {// close log output
        Log.i(if (tag.isEmpty()) "empty" else tag, message)
    }
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

/**
 * 弹出验证码解析结果的 notification
 */
fun Context.showContentInfo(message: Message) {


    //添加通知处理操作
    val intent = Intent(this, NotificationHandleActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra(MessageReceiver.NAME_MESSAGE, message)

    //action : 复制验证码
    val copy = Intent(intent)
    copy.putExtra(MessageReceiver.NAME_ACTION, NotificationHandleActivity.ACTION_COPY)
    val copyIntent = PendingIntent.getActivity(this, 100,
            copy, PendingIntent.FLAG_UPDATE_CURRENT)

    //如果打开了自动复制选项
    val isAutoCopy by DelegateExt.preference(getString(R.string.key_setting_auto_copy), false)
    if (isAutoCopy) {
        log("自动复制...")
        startActivity(copy)
        return
    }

    //action : 修改当前号码对应的正则表达式
    val update = Intent(intent)
    update.putExtra(MessageReceiver.NAME_ACTION, NotificationHandleActivity.ACTION_UPDATE_REGEX)
    val updateIntent = PendingIntent.getActivity(this, 99,
            update, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon)
            .setAutoCancel(true)
            .setContentTitle(message.number)
            .setContentIntent(copyIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(R.drawable.ic_content_copy_24dp, getString(R.string.notification_action_copy), copyIntent)
            .addAction(R.drawable.ic_edit_black_24dp, getString(R.string.notification_action_update_regex), updateIntent)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("验证码:" + if (message.code.isEmpty()) "解析失败" else message.code)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// heads-up notification
        builder.setFullScreenIntent(copyIntent, true)
    }
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(MessageReceiver.ID_NOTIFICATION_CODE, builder.build())
}

@Suppress("DEPRECATION")
fun color(@IdRes id: Int, context: Context = AppContext.instance): Int {
    if (Build.VERSION.SDK_INT >= 23) {
        return context.getColor(id)
    } else {
        return context.resources.getColor(id)
    }
}

fun string(@IdRes stringId: Int) = AppContext.instance.getString(stringId)!!

fun string(@IdRes stringId: Int, vararg formatArgs: Any) = AppContext.instance.getString(stringId, formatArgs)!!


fun StringBuilder.clear() {
    if (isEmpty()) {
        return
    }
    delete(0, length)
}