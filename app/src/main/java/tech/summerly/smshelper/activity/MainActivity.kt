package tech.summerly.smshelper.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.base.AppCompatPreferenceActivity
import tech.summerly.smshelper.utils.extention.DelegateExt
import tech.summerly.smshelper.utils.extention.log
import tech.summerly.smshelper.utils.extention.toast

@Suppress("DEPRECATION")
class MainActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.main_preference)
        requestToReceiveSms()
        setUpPreference()
    }


    private fun setUpPreference() {
        //配置 keyword
        val preferenceDefaultKeyword = preferenceScreen.findPreference(getString(R.string.key_setting_default_keyword))
        val keyword = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_setting_default_keyword), getString(R.string.default_keyword))
        log("keyword = $keyword")
        preferenceDefaultKeyword.summary = getString(R.string.summary_setting_default_keyword_template, keyword)
        preferenceDefaultKeyword.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = getString(R.string.summary_setting_default_keyword_template, newValue)
            true
        }

        //配置 default regex
        val preferenceDefaultRegex = preferenceScreen.findPreference(getString(R.string.key_setting_default_regex))
        val regex = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_setting_default_regex), getString(R.string.default_regex))
        log("regex = $regex")
        preferenceDefaultRegex.summary = getString(R.string.summary_setting_default_regex_template, regex)
        preferenceDefaultRegex.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = getString(R.string.summary_setting_default_keyword_template, newValue)
            true
        }


        val preferenceSimulate = preferenceScreen.findPreference(getString(R.string.key_setting_simulate))
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isPreferenceSimulateShow", false)) {
            (preferenceScreen.findPreference(getString(R.string.key_setting_category_about)) as PreferenceCategory).removePreference(preferenceSimulate)
        }

        val preferenceAbout = preferenceScreen.findPreference(getString(R.string.key_setting_about))
        preferenceAbout.setOnPreferenceClickListener {
            System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1)
            mHints[mHints.size - 1] = SystemClock.uptimeMillis()
            if (SystemClock.uptimeMillis() - mHints[0] <= 500) {
                var isPreferenceSimulateShow by DelegateExt.preference("isPreferenceSimulateShow", false)
                if (!isPreferenceSimulateShow) {
                    isPreferenceSimulateShow = true
                    (preferenceScreen.findPreference(getString(R.string.key_setting_category_about)) as PreferenceCategory).addPreference(preferenceSimulate)
                }
            }
            true
        }
    }

    private var mHints = LongArray(3)//初始全部为0

    private fun requestToReceiveSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 100)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
