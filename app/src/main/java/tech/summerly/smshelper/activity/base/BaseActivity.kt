package tech.summerly.smshelper.activity.base

import android.os.Bundle
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import tech.summerly.smshelper.AppContext
import tech.summerly.smshelper.R
import tech.summerly.smshelper.extention.color

/**
 * <pre>
 *     author : YangBin
 *     e-mail : yangbinyhbn@gmail.com
 *     time   : 2017/6/1
 *     desc   :
 *     version: 1.0
 * </pre>
 */
open class BaseActivity : AppCompatActivity() {


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContext.instance.register(this)

        if (hasBackArrow()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val arrowBack = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            DrawableCompat.setTint(arrowBack, color(R.color.colorWhite))
            supportActionBar?.setHomeAsUpIndicator(arrowBack)
        }
    }

    protected open fun hasBackArrow() = false


    override fun onDestroy() {
        super.onDestroy()
        AppContext.instance.unRegister(this)
    }

    override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}