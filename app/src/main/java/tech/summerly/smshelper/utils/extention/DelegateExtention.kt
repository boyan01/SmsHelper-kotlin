package tech.summerly.smshelper.utils.extention

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import tech.summerly.smshelper.AppContext
import kotlin.reflect.KProperty

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object DelegateExt {
    fun <T> preference(name: String, default: T, context: Context = AppContext.instance) = Preference(context, name, default)
}

class Preference<T>(private val context: Context, private val name: String, private val default: T) {
    private val PREF_TITLE = "sms_helper"

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_TITLE, Context.MODE_PRIVATE)
    }


    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T): T = with(pref) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        res as T
    }

    private fun putPreference(name: String, value: T) = with(pref.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }
    }.apply()
}
