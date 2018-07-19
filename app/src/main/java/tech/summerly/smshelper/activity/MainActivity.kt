package tech.summerly.smshelper.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.R
import tech.summerly.smshelper.extention.log
import tech.summerly.smshelper.extention.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestToReceiveSms()
        supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, AppMainFragment())
                .commit()
    }

    class AppMainFragment : PreferenceFragmentCompat() {

        private var mHints = LongArray(3)//用于记录点击的事件

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.main_preference)
            setUpPreference()
        }

        private fun setUpPreference() {
            //配置 keyword
            val preferenceDefaultKeyword = preferenceScreen.findPreference(getString(R.string.key_setting_default_keyword))
            val keyword = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(getString(R.string.key_setting_default_keyword), getString(R.string.default_keyword))
            log("keyword = $keyword")
            preferenceDefaultKeyword.summary = getString(R.string.summary_setting_default_keyword_template, keyword)
            preferenceDefaultKeyword.setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = getString(R.string.summary_setting_default_keyword_template, newValue)
                true
            }

            //配置 default regex
            val preferenceDefaultRegex = preferenceScreen.findPreference(getString(R.string.key_setting_default_regex))
            val regex = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(getString(R.string.key_setting_default_regex), getString(R.string.default_regex))
            log("regex = $regex")
            preferenceDefaultRegex.summary = getString(R.string.summary_setting_default_regex_template, regex)
            preferenceDefaultRegex.setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = getString(R.string.summary_setting_default_keyword_template, newValue)
                true
            }


            val preferenceSimulate = preferenceScreen.findPreference(getString(R.string.key_setting_simulate))
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isPreferenceSimulateShow", false)) {
                (preferenceScreen.findPreference(getString(R.string.key_setting_category_about)) as PreferenceCategory).removePreference(preferenceSimulate)
            }

            //连续点击三次关于选项可进入开发者选项
            val preferenceAbout = preferenceScreen.findPreference(getString(R.string.key_setting_about))
            preferenceAbout.setOnPreferenceClickListener {
                System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1)
                mHints[mHints.size - 1] = SystemClock.uptimeMillis()
                if (SystemClock.uptimeMillis() - mHints[0] <= 500) {
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val isPreferenceSimulateShow = preferences.getBoolean("isPreferenceSimulateShow", false)
                    if (!isPreferenceSimulateShow) {
                        preferences.edit().putBoolean("isPreferenceSimulateShow", true).apply()
                        (preferenceScreen.findPreference(getString(R.string.key_setting_category_about)) as PreferenceCategory).addPreference(preferenceSimulate)
                    }
                }
                true
            }
        }


    }

    private fun requestToReceiveSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 100)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            toast(getString(R.string.toast_need_sms_permission_to_receive_sms))
            requestToReceiveSms()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        AppContext.instance.exit()
    }


}
