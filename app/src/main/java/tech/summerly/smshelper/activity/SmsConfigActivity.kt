package tech.summerly.smshelper.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_sms_config.*
import kotlinx.android.synthetic.main.item_sms_config.view.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.activity.base.BaseActivity
import tech.summerly.smshelper.data.dao.SmsConfigDao
import tech.summerly.smshelper.data.entity.SmsConfig
import tech.summerly.smshelper.utils.extention.log

class SmsConfigActivity : BaseActivity() {


    val smsConfigs: MutableList<SmsConfig> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_config)
        initList()

    }

    private fun initList() {
        listSmsConfig.layoutManager = LinearLayoutManager(this)
        listSmsConfig.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        listSmsConfig.adapter = SmsConfigListAdapter(smsConfigs, {
            log(it.toString())
            val intent = Intent(this, RegexModifyActivity::class.java)
            intent.putExtra(NAME_CONFIG, it)
            startActivity(intent)
        }, {
            log("撤销删除 ${smsConfigs[it]}")
            listSmsConfig.post {
                listSmsConfig.adapter.notifyItemChanged(it)
            }
        })

        //增加滑动删除
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            //不响应 move 事件
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("删除")
            }

        })
        itemTouchHelper.attachToRecyclerView(listSmsConfig)

    }

    override fun onStart() {
        super.onStart()
        log("onStart : 刷新列表")
        refreshList()
    }


    /**
     * 刷新列表
     */
    private fun refreshList() {
        smsConfigs.clear()
        smsConfigs.addAll(SmsConfigDao.getAll().toMutableList())
        listSmsConfig.adapter.notifyDataSetChanged()
    }

    override fun hasBackArrow() = true

    class SmsConfigListAdapter(val configs: List<SmsConfig>, val itemClick: ((SmsConfig) -> Unit)? = null, val delete: ((Int) -> Unit)? = null) : RecyclerView.Adapter<SmsConfigListAdapter.Holder>() {
        override fun onBindViewHolder(holder: Holder, position: Int) {
            with(configs[position]) {
                holder.itemView.textNumber.text = number
                holder.itemView.textContent.text = content
                holder.itemView.textRegex.text = regex
                holder.itemView.setOnClickListener {
                    itemClick?.invoke(this)
                }
                holder.itemView.textDelete.setOnClickListener {
                    delete?.invoke(position)
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

