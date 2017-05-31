package tech.summerly.smshelper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_sms_config.*
import kotlinx.android.synthetic.main.item_sms_config.view.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.data.dao.SmsConfigDao
import tech.summerly.smshelper.data.entity.SmsConfig
import tech.summerly.smshelper.utils.extention.log

class SmsConfigActivity : AppCompatActivity() {


    val smsConfigs: List<SmsConfig> by lazy {
        return@lazy SmsConfigDao.getAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_config)
        listSmsConfig.layoutManager = LinearLayoutManager(this)
        listSmsConfig.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        listSmsConfig.adapter = SmsConfigListAdapter(smsConfigs, {
            log(it.toString())
            val intent = Intent(this, RegexModifyActivity::class.java)
            intent.putExtra(NAME_CONFIG, it)
            startActivity(intent)
        })
    }

    class SmsConfigListAdapter(val configs: List<SmsConfig>, val itemClick: ((SmsConfig) -> Unit)? = null) : RecyclerView.Adapter<SmsConfigListAdapter.Holder>() {
        override fun onBindViewHolder(holder: Holder, position: Int) {
            with(configs[position]) {
                holder.itemView.textNumber.text = number
                holder.itemView.textContent.text = content
                holder.itemView.textRegex.text = regex
                holder.itemView.setOnClickListener {
                    itemClick?.invoke(this)
                }
            }
        }

        override fun getItemCount() = configs.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sms_config, parent, false)
            return Holder(view)
        }


        class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    }

}

