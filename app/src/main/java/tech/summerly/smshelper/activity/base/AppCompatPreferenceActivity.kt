package tech.summerly.smshelper.activity.base

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import tech.summerly.smshelper.AppContext


/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/5/31
 *     version: 1.0
 * </pre>
 */
open class AppCompatPreferenceActivity : PreferenceActivity() {

    private val delegate by lazy {
        AppCompatDelegate.create(this, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        AppContext.instance.register(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    var supportActionBar: ActionBar? = null
        get() = delegate.supportActionBar

    fun setSupportActionBar(@Nullable toolbar: Toolbar) =
            delegate.setSupportActionBar(toolbar)

    override fun getMenuInflater(): MenuInflater = delegate.menuInflater

    override fun setContentView(@LayoutRes layoutResID: Int) = delegate.setContentView(layoutResID)

    override fun setContentView(view: View) = delegate.setContentView(view)

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) = delegate.setContentView(view, params)

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) = delegate.addContentView(view, params)

    override fun onPostResume() {
        super.onPostResume()
        delegate.onPostResume()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        delegate.setTitle(title)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        delegate.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
        AppContext.instance.unRegister(this)
    }

    override fun invalidateOptionsMenu() = delegate.invalidateOptionsMenu()

}