package tech.summerly.smshelper.extention

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.NotificationHandleActivity
import tech.summerly.smshelper.activity.NotificationHandleActivity.Companion.ACTION_COPY
import tech.summerly.smshelper.activity.NotificationHandleActivity.Companion.ACTION_UPDATE_REGEX
import tech.summerly.smshelper.data.Message
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.ID_NOTIFICATION_CODE
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.NAME_ACTION
import tech.summerly.smshelper.receiver.MessageReceiver.Companion.NAME_MESSAGE

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/20
 *     desc   :
 * </pre>
 */

fun Context.copyToClipboard(code: String) = with(getSystemService(Context.CLIPBOARD_SERVICE)) {
    (this as ClipboardManager).primaryClip = android.content.ClipData.newPlainText("code", code)
}

private const val CHANNEL_ID = "test"

/**
 * 弹出验证码解析结果的 notification
 */
fun Context.showContentInfo(message: Message) {


    //添加通知处理操作
    val intent = Intent(this, NotificationHandleActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra(NAME_MESSAGE, message)

    //action : 复制验证码
    val copy = Intent(intent)
    copy.putExtra(NAME_ACTION, ACTION_COPY)
    val copyIntent = PendingIntent.getActivity(this, 100,
            copy, PendingIntent.FLAG_UPDATE_CURRENT)

    //action : 修改当前号码对应的正则表达式
    val update = Intent(intent)
    update.putExtra(NAME_ACTION, ACTION_UPDATE_REGEX)
    val updateIntent = PendingIntent.getActivity(this, 99,
            update, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
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
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(ID_NOTIFICATION_CODE, builder.build())
}