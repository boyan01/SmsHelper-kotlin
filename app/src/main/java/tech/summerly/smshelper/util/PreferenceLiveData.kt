package tech.summerly.smshelper.util

import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import tech.summerly.smshelper.extention.string

/**
 * preference util class
 *
 * @param key the key binding with data
 * @param defaultValue if data is not exist, a default value will be return by [getValue]
 */
class PreferenceLiveData<T>(
        private val key: String,
        private val defaultValue: T,
        private val preference: SharedPreferences
) : MutableLiveData<T>() {

    init {
        val v: Any? = when (defaultValue) {
            is Long -> preference.getLong(key, defaultValue)
            is String -> preference.getString(key, defaultValue)
            is Int -> preference.getInt(key, defaultValue)
            is Boolean -> preference.getBoolean(key, defaultValue)
            is Float -> preference.getFloat(key, defaultValue)
            else -> {
                defaultValue
            }
        }
        @Suppress(names = ["UNCHECKED_CAST"])
        setValue(v as T)
    }

    constructor(@StringRes stringId: Int, defaultValue: T, preference: SharedPreferences)
            : this(string(stringId), defaultValue, preference)

    override fun setValue(value: T) {
        super.setValue(value)
        val editor = preference.edit()
        with(editor) {
            when (value) {
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                else -> throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }
        editor.apply()
    }

}