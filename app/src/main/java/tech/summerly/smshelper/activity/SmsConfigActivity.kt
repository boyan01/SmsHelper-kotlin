package tech.summerly.smshelper.activity

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import kotlinx.android.synthetic.main.activity_sms_config.*
import kotlinx.android.synthetic.main.item_sms_config.view.*
import org.jetbrains.anko.*
import tech.summerly.smshelper.R
import tech.summerly.smshelper.activity.RegexModifyActivity.Companion.NAME_CONFIG
import tech.summerly.smshelper.activity.base.BaseActivity
import tech.summerly.smshelper.data.SmsConfig
import tech.summerly.smshelper.data.datasource.SmsConfigDataSource
import tech.summerly.smshelper.extention.copyToClipboard
import tech.summerly.smshelper.extention.getObjectFromString
import tech.summerly.smshelper.extention.serialize
import tech.summerly.smshelper.extention.toast

class SmsConfigActivity : BaseActivity(), AnkoLogger {


    val smsConfigs: ArrayList<SmsConfig> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_config)
        initList()
    }

    private fun initList() {
        listSmsConfig.layoutManager = LinearLayoutManager(this)
        listSmsConfig.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        listSmsConfig.adapter = SmsConfigListAdapter(smsConfigs, {
            info(it.toString())
            startActivity<RegexModifyActivity>(NAME_CONFIG to it)
        })

        listSmsConfig.setSwipeAble({
            val smsConfig = smsConfigs.removeAt(it)
            SmsConfigDataSource.dataSource.deleteByNumber(smsConfig.number)
            smsConfig
        }) { (position, smsConfig) ->
            smsConfig?.let {
                smsConfigs.add(position, smsConfig)
                SmsConfigDataSource.dataSource.insert(smsConfig)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        info("onStart : 刷新列表")
        refreshList()
    }


    /**
     * 刷新列表
     */
    private fun refreshList() {
        smsConfigs.clear()
        smsConfigs.addAll(SmsConfigDataSource.dataSource.getAll())
        listSmsConfig.adapter.notifyDataSetChanged()
    }

    override fun hasBackArrow() = true

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sms_config_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sms_config_list_import -> importConfigFromClipboard()
            R.id.menu_sms_config_list_export -> exportConfigToClipboard()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportConfigToClipboard() {

        val serialization = smsConfigs.serialize()
        copyToClipboard(serialization)
        info { "复制了: $serialization" }
        toast("已成功复制到剪切板.")
    }

    private fun importConfigFromClipboard() {
        val clipText: String = getSystemService(Context.CLIPBOARD_SERVICE).run {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                (this as ClipboardManager).primaryClip.getItemAt(0).coerceToText(this@SmsConfigActivity).toString()
            else
                @Suppress("DEPRECATION")
                (this as android.text.ClipboardManager).text.toString()
        }

        info("clipText : $clipText")

        val arrayList = clipText.getObjectFromString<ArrayList<SmsConfig>>()
        if (arrayList == null) {
            toast("导入失败.")
            return
        }
        info("arrayList = $arrayList")
        doAsync {
            arrayList.forEach {
                SmsConfigDataSource.dataSource.deleteByNumber(it.number)
                SmsConfigDataSource.dataSource.insert(it.copy(id = -1))
            }
            uiThread {
                toast("导入成功")
                refreshList()
            }
        }
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


    /**
     * 让 RecyclerView 可以左滑删除
     * @param onSwiped 响应左滑删除时的调用
     * @param onRevoked 响应撤回删除时的调用
     */
    private inline fun <T> RecyclerView.setSwipeAble(noinline onSwiped: (Int) -> T, crossinline onRevoked: (Pair<Int, T?>) -> Unit) = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        /**
         * 用 int 和 T 键值对临时保存所擦除的那一项的 位置 和  数据
         */
        var removed: Pair<Int, T?> = 0 to null

        //不响应 move 事件
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            removed = viewHolder.adapterPosition to onSwiped(viewHolder.adapterPosition)//记录下最近移除的条目
            this@setSwipeAble.adapter.notifyItemRemoved(viewHolder.adapterPosition)
            Snackbar.make(this@setSwipeAble, "刚刚进行了删除操作,是否撤销?", Snackbar.LENGTH_LONG).setAction("撤销", {
                onRevoked(removed)
                this@setSwipeAble.adapter.notifyItemInserted(removed.first)
            }).show()
        }

        override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            viewHolder.itemView.scrollTo(-dX.toInt(), 0)
        }

        override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            //防止view的复用导致撤销删除错位的bug,移除view的时候,view回滚到原来的状态,
            viewHolder.itemView.scrollTo(0, 0)
        }

    }).attachToRecyclerView(this@setSwipeAble)

}

