package com.anubis.module_base.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anubis.kt_extends.eLogE
import com.anubis.module_extends.DataItemInfo
import com.anubis.module_extends.R
import kotlinx.android.synthetic.main.adapter_default_item.view.*
import org.jetbrains.anko.*

/**
 * Author  ： AnubisASN   on 20-9-12 下午3:18.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
class eAdapter<T>(
        val mActivity: Context,
        val recyclerView: RecyclerView,
        val itemLayoutId: Int,
        tDataArrayList:List<T>?=null,
        val itemEditBlock: ((mAdapter:eAdapter<T>,itemView: View, data: T, position: Int) -> Unit)?=null,
        val longClickBlock: ((itemView: View, position: Int) -> Unit)? = null,
        orientation: Int = LinearLayoutManager.VERTICAL,
        layoutManager: LinearLayoutManager = LinearLayoutManager(mActivity),
        val clickBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<eAdapter<T>.MyHolder>() {
    private var mDatas: MutableList<T>? = arrayListOf()

  init {
        tDataArrayList?.let {
            mDatas=it.toMutableList()
        }
        layoutManager.orientation = orientation
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(mActivity).inflate(itemLayoutId, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return mDatas?.size ?: 0
    }

    fun eAddData(data: T, index: Int? = null) {
        if (index == null)
            mDatas?.add(data)
        else
            mDatas?.add(index, data)
        eUpdateData()
    }

    fun eDelData(index: Int) {
        mDatas?.removeAt(index)
        eUpdateData()
    }

    fun eSetData(data: ArrayList<T>) {
        mDatas?.clear()
        mDatas = data.clone() as ArrayList<T>
        eUpdateData()
    }

    fun eGetData(index: Int) = mDatas?.get(index)

    fun eUpdateData() {
        notifyDataSetChanged()
        recyclerView.setItemViewCacheSize(mDatas?.size ?: 0)
    }

    //方法\界面绑定
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        mDatas?.get(position)?.let { holder.setData(it, position) }
    }

    //界面设置
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(data: T, position: Int) {
            try {
                itemEditBlock?.invoke(this@eAdapter, itemView, data, position)?:if (data is DataItemInfo){
                    defaultItemEdit(itemView,data)
                }
                longClickBlock?.let { lcb ->
                    itemView.onLongClick {
                        lcb(itemView, position)
                        return@onLongClick true
                    }
                }
                clickBlock?.let { cb ->
                    itemView.onClick {
                        cb(itemView, data, position)
                    }
                }
            } catch (e: Exception) {
                e.eLogE("MyHolder")
            }
        }
    }

    open fun defaultItemEdit(itemView: View, data: DataItemInfo, itemWidth: Float? = null, itemHeight: Float? = null, itemWeight: FloatArray = floatArrayOf(0f, 0f, 0f, 0f)) {
        try {

            with(itemView.table_item_ll) {
                val param = layoutParams
                itemWidth?.let {
                    param.width = it.toInt()
                }
                itemHeight?.let {
                    param.height = it.toInt()
                }
                layoutParams = param
            }
            with(itemView.table_item_cb) {
                if (data.isCb == null) {
                    visibility = View.GONE
                } else {
                    isChecked = data.isCb!!
                    onCheckedChange { compoundButton, b ->
                        data.isCb = b
                    }
                    visibility = View.VISIBLE
                    data.copy(isCb = data.isCb ?: false)
                }
            }
            with(itemView.table_item_tvStr1) {
                if (data.str1 == null) {
                    visibility = View.GONE
                } else {
                    if (itemWeight[0] > 0f) {
                        val param = LinearLayout.LayoutParams(layoutParams)
                        param.weight = itemWeight[0]
                        layoutParams = param
                    }
                    visibility = View.VISIBLE
                    text = data.str1
                    if (data.color != null)
                        textColor = data.color!!
                }
            }
            with(itemView.table_item_tvStr2) {
                if (data.str2 == null) {
                    visibility = View.GONE
                } else {
                    if (itemWeight[1] > 0f) {
                        val param = LinearLayout.LayoutParams(layoutParams)
                        param.weight = itemWeight[1]
                        layoutParams = param
                    }
                    visibility = View.VISIBLE
                    text = data.str2
                    if (data.color != null)
                        textColor = data.color!!
                }
            }
            with(itemView.table_item_tvIco) {
                if (data.ico == null) {
                    visibility = View.GONE
                } else {
                    if (itemWeight[2] > 0f) {
                        val param = LinearLayout.LayoutParams(layoutParams)
                        param.weight = itemWeight[2]
                        layoutParams = param
                    }
                    visibility = View.VISIBLE
                    val icoData = data.ico!!
                    when (icoData) {
                        is Drawable -> image = icoData
                        is String -> com.bumptech.glide.Glide.with(this).load(icoData).placeholder(
                                R.mipmap.ico_default
                        ).into(
                                this
                        )
                        else -> visibility = View.GONE
                    }
                }
            }
            with(itemView.table_item_tvStr3) {
                if (data.str3 == null) {
                    visibility = View.GONE
                } else {
                    if (itemWeight[2] > 0f) {
                        val param = LinearLayout.LayoutParams(layoutParams)
                        param.weight = itemWeight[2]
                        layoutParams = param
                    }
                    visibility = View.VISIBLE
                    text = data.str3
                    if (data.color != null)
                        textColor = data.color!!
                }
            }
            if (data.ico == null && data.str3 == null)
                itemView.table_item_tlTV_IV.visibility = View.GONE
            with(itemView.table_item_tvStr4) {
                if (data.str4 == null) {
                    visibility = View.GONE
                } else {
                    if (itemWeight[3] > 0f) {
                        val param = LinearLayout.LayoutParams(layoutParams)
                        param.weight = itemWeight[3]
                        layoutParams = param
                    }
                    visibility = View.VISIBLE
                    text = data.str4
                    if (data.color != null)
                        textColor = data.color!!
                }
            }
            with(itemView.table_item_tvLine) {
                visibility = if (data.isShowLine) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        } catch (e: Exception) {
            e.eLogE("MyHolder")
        }

    }
}

