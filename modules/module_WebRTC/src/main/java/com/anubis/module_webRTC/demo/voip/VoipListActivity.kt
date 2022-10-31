package com.anubis.module_webRTC.demo.voip

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.database.CoreDB
import com.anubis.module_webRTC.database.HistoryBean
import com.anubis.module_webRTC.ui.CircularCoverView
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.ColorUtils
import com.anubis.module_webRTC.utils.DensityUtils

import java.util.ArrayList

class VoipListActivity : BaseActivity() {

    private var mTargetId: String? = null
    private var mHistoryList: MutableList<HistoryBean>? = null
    private var vHistoryList: ListView? = null
    private var myListAdapter: MyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voip_list)

        (findViewById<View>(R.id.title_text) as TextView).text = "VOIP会话列表"
        findViewById<View>(R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(R.id.title_left_btn).setOnClickListener { finish() }

        findViewById<View>(R.id.create_btn).setOnClickListener { startActivity(Intent(this@VoipListActivity, VoipCreateActivity::class.java)) }

        mHistoryList = ArrayList()
        myListAdapter = MyListAdapter()
        vHistoryList = findViewById<View>(R.id.list) as ListView
        vHistoryList!!.adapter = myListAdapter
        vHistoryList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mTargetId = mHistoryList!![position].conversationId as String
            MLOC.saveVoipUserId(this@VoipListActivity, mTargetId!!)

            val builder = AlertDialog.Builder(this@VoipListActivity)
            builder.setItems(arrayOf("视频通话", "音频通话")) { dialogInterface, i ->
                if (i == 0) {
                    val intent = Intent(this@VoipListActivity, VoipActivity::class.java)
                    intent.putExtra("targetId", mTargetId)
                    intent.putExtra(VoipActivity.ACTION, VoipActivity.CALLING)
                    startActivity(intent)
                } else if (i == 1) {
                    val intent = Intent(this@VoipListActivity, VoipAudioActivity::class.java)
                    intent.putExtra("targetId", mTargetId)
                    intent.putExtra(VoipAudioActivity.ACTION, VoipAudioActivity.CALLING)
                    startActivity(intent)
                }
            }
            builder.setCancelable(true)
            val dialog = builder.create()
            dialog.show()
        }
    }

    public override fun onResume() {
        super.onResume()
        MLOC.hasNewVoipMsg = false
        mHistoryList!!.clear()
        val list = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_VOIP)
        if (list != null && list.size > 0) {
            mHistoryList!!.addAll(list)
        }
        myListAdapter!!.notifyDataSetChanged()
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        if (aEventID == AEvent.AEVENT_GOT_ONLINE_USER_LIST) {
            if (success) {
                val list = eventObj as ArrayList<HistoryBean>
                //删除自己的ID
                for (i in list.indices.reversed()) {
                    if (list[i].conversationId == MLOC.userId) {
                        list.removeAt(i)
                        continue
                    }
                }
                //                //删除两个列表中重复的和 历史列表中不在线的
                //                for(int i = mHistoryList.size()-1;i>=0;i--){
                //                    boolean b = true;
                //                    for(int j = list.size()-1;j>=0;j--){
                //                        if(mHistoryList.get(i).getConversationId().equals(list.get(j).getConversationId())){
                //                            list.remove(j);
                //                            b = false;
                //                            break;
                //                        }
                //                    }
                //                    if(b){
                //                        mHistoryList.remove(i);
                //                    }
                //                }

                mHistoryList!!.addAll(list)
                myListAdapter!!.notifyDataSetChanged()
            }
        }
    }

    inner class MyListAdapter : BaseAdapter() {
        private val mInflater: LayoutInflater

        init {
            mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return if (mHistoryList != null) mHistoryList!!.size else 0
        }

        override fun getItem(position: Int): Any? {
            return if (mHistoryList == null) null else mHistoryList!![position]
        }

        override fun getItemId(position: Int): Long {
            return if (mHistoryList == null) 0 else position.toLong()
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val itemSelfHolder: ViewHolder
            if (convertView == null) {
                itemSelfHolder = ViewHolder()
                convertView = mInflater.inflate(R.layout.item_voip_list, null)
                itemSelfHolder.vUserId = convertView!!.findViewById<View>(R.id.item_id) as TextView
                itemSelfHolder.vTime = convertView.findViewById<View>(R.id.item_time) as TextView
                itemSelfHolder.vCount = convertView.findViewById<View>(R.id.item_count) as TextView
                itemSelfHolder.vHeadBg = convertView.findViewById(R.id.head_bg)
                itemSelfHolder.vHeadImage = convertView.findViewById<View>(R.id.head_img) as ImageView
                itemSelfHolder.vHeadCover = convertView.findViewById<View>(R.id.head_cover) as CircularCoverView
                convertView.tag = itemSelfHolder
            } else {
                itemSelfHolder = convertView.tag as ViewHolder
            }
            val userId = mHistoryList!![position].conversationId
            itemSelfHolder.vUserId!!.text = userId
            itemSelfHolder.vTime!!.text = mHistoryList!![position].lastTime
            itemSelfHolder.vHeadBg!!.setBackgroundColor(ColorUtils.getColor(this@VoipListActivity, userId))
            itemSelfHolder.vHeadCover!!.setCoverColor(Color.parseColor("#FFFFFF"))
            val cint = DensityUtils.dip2px(this@VoipListActivity, 28f)
            itemSelfHolder.vHeadCover!!.setRadians(cint, cint, cint, cint, 0)
            itemSelfHolder.vHeadImage!!.setImageResource(MLOC.getHeadImage(this@VoipListActivity, userId))

            if (mHistoryList!![position].newMsgCount == 0) {
                itemSelfHolder.vCount!!.visibility = View.INVISIBLE
            } else {
                itemSelfHolder.vCount!!.text = "" + mHistoryList!![position].newMsgCount
                itemSelfHolder.vCount!!.visibility = View.VISIBLE
            }

            return convertView
        }
    }

    inner class ViewHolder {
        var vUserId: TextView? = null
        var vTime: TextView? = null
        var vCount: TextView? = null
        var vHeadBg: View? = null
        var vHeadCover: CircularCoverView? = null
        var vHeadImage: ImageView? = null
    }

}
