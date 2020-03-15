package com.anubis.module_webRTC.demo.miniclass

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.serverAPI.InterfaceUrls
import com.anubis.module_webRTC.ui.CircularCoverView
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.ColorUtils
import com.anubis.module_webRTC.utils.DensityUtils
import com.anubis.module_webRTC.utils.StarListUtil
import com.anubis.module_webRTC.R
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import demo.miniclass.MiniClassActivity

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.ArrayList

class MiniClassListActivity : BaseActivity(), AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var vList: ListView? = null
    private var myListAdapter: MyListAdapter? = null
    private var mDatas: ArrayList<MiniClassInfo>? = null
    private var mInflater: LayoutInflater? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.anubis.module_webRTC.R.layout.activity_mini_class_list)
        (findViewById<View>(com.anubis.module_webRTC.R.id.title_text) as TextView).text = "小班课列表"
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener { finish() }
        findViewById<View>(com.anubis.module_webRTC.R.id.create_btn).setOnClickListener { startActivity(Intent(this@MiniClassListActivity, MiniClassCreateActivity::class.java)) }
        refreshLayout = findViewById<View>(com.anubis.module_webRTC.R.id.refresh_layout) as SwipeRefreshLayout
        //设置刷新时动画的颜色，可以设置4个
        refreshLayout!!.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light)
        refreshLayout!!.setOnRefreshListener(this)

        mDatas = ArrayList()
        mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        myListAdapter = MyListAdapter()
        vList = findViewById<View>(com.anubis.module_webRTC.R.id.list) as ListView
        vList!!.adapter = myListAdapter
        vList!!.onItemClickListener = this
        vList!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                when (i) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> if (StarListUtil.isListViewReachTopEdge(absListView)) {
                        refreshLayout!!.isEnabled = true
                    } else {
                        refreshLayout!!.isEnabled = false
                    }
                }
            }

            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
    }


    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_GOT_LIST -> {
                refreshLayout!!.isRefreshing = false
                mDatas!!.clear()
                if (success) {
                    val datas = eventObj as JSONArray
                    for (i in 0 until datas.length()) {
                        try {
                            val json = datas.getJSONObject(i)
                            val tmp = json.getString("data")
                            val tmpObj = JSONObject(URLDecoder.decode(tmp, "utf-8"))
                            val item = MiniClassInfo()
                            item.creator = tmpObj.getString("creator")
                            item.id = tmpObj.getString("id")
                            item.name = tmpObj.getString("name")
                            mDatas!!.add(item)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }

                    }
                    myListAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val clickMeetingInfo = mDatas!![position]
        val intent = Intent(this@MiniClassListActivity, MiniClassActivity::class.java)
        intent.putExtra(MiniClassActivity.CLASS_ID, clickMeetingInfo.id)
        intent.putExtra(MiniClassActivity.CLASS_NAME, clickMeetingInfo.name)
        intent.putExtra(MiniClassActivity.CLASS_CREATOR, clickMeetingInfo.creator)
        startActivity(intent)
    }

    public override fun onResume() {
        super.onResume()
        AEvent.addListener(AEvent.AEVENT_GOT_LIST, this)
        onRefresh()
    }

    public override fun onPause() {
        super.onPause()
        AEvent.removeListener(AEvent.AEVENT_GOT_LIST, this)
        super.onStop()
    }

    override fun onRefresh() {
        queryAllList()
    }

    private fun queryAllList() {
        if (MLOC.AEventCenterEnable!!) {
            InterfaceUrls.demoQueryList(MLOC.LIST_TYPE_CLASS_ALL)
        } else {
            XHClient.getInstance().liveManager.queryList("", MLOC.LIST_TYPE_CLASS_ALL, object : IXHResultCallback {
                override fun success(data: Any) {
                    val res = data as Array<String>
                    val array = JSONArray()
                    for (i in res.indices) {
                        var info = res[i]
                        try {
                            info = URLDecoder.decode(info, "utf-8")
                            val jsonObject = JSONObject(info)
                            array.put(jsonObject)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }

                    }

                    refreshLayout!!.isRefreshing = false
                    mDatas!!.clear()
                    try {
                        //                    JSONArray array = (JSONArray) data;
                        for (i in array.length() - 1 downTo 0) {
                            val info = MiniClassInfo()
                            val obj = array.getJSONObject(i)
                            info.creator = obj.getString("creator")
                            info.id = obj.getString("id")
                            info.name = obj.getString("name")
                            mDatas!!.add(info)
                        }
                        myListAdapter!!.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun failed(errMsg: String) {
                    MLOC.d("VideoMettingListActivity", errMsg)
                    refreshLayout!!.isRefreshing = false
                    mDatas!!.clear()
                    myListAdapter!!.notifyDataSetChanged()
                }
            })
        }

    }

    internal inner class MyListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mDatas!!.size
        }

        override fun getItem(position: Int): Any {
            return mDatas!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val viewIconImg: ViewHolder
            if (convertView == null) {
                viewIconImg = ViewHolder()
                convertView = mInflater!!.inflate(com.anubis.module_webRTC.R.layout.item_all_list, null)
                viewIconImg.vRoomName = convertView!!.findViewById<View>(com.anubis.module_webRTC.R.id.item_id) as TextView
                viewIconImg.vCreaterId = convertView.findViewById<View>(com.anubis.module_webRTC.R.id.item_creater_id) as TextView
                viewIconImg.vHeadBg = convertView.findViewById(com.anubis.module_webRTC.R.id.head_bg)
                viewIconImg.vHeadImage = convertView.findViewById<View>(com.anubis.module_webRTC.R.id.head_img) as ImageView
                viewIconImg.vHeadCover = convertView.findViewById<View>(com.anubis.module_webRTC.R.id.head_cover) as CircularCoverView
                convertView.tag = viewIconImg
            } else {
                viewIconImg = convertView.tag as ViewHolder
            }
            viewIconImg.vRoomName!!.text = mDatas!![position].name
            viewIconImg.vCreaterId!!.text = mDatas!![position].creator
            viewIconImg.vHeadBg!!.setBackgroundColor(ColorUtils.getColor(this@MiniClassListActivity, mDatas!![position].id))
            viewIconImg.vHeadCover!!.setCoverColor(Color.parseColor("#FFFFFF"))
            val cint = DensityUtils.dip2px(this@MiniClassListActivity, 28f)
            viewIconImg.vHeadCover!!.setRadians(cint, cint, cint, cint, 0)
            viewIconImg.vHeadImage!!.setImageResource(com.anubis.module_webRTC.R.drawable.icon_main_class)
            return convertView
        }

        internal inner class ViewHolder {
            var vRoomName: TextView? = null
            var vCreaterId: TextView? = null
            var vHeadBg: View? = null
            var vHeadCover: CircularCoverView? = null
            var vHeadImage: ImageView? = null
        }
    }


}
