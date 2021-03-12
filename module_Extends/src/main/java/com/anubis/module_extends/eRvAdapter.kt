package com.anubis.module_extends

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.selfServicePayment.Utils.eDefaultItemAnimator
import kotlinx.android.synthetic.main.adapter_default_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
@SuppressLint("WrongConstant")
/**
 *说明：RecyclerView 适配器
 * @param mContext: Context; 上下文
 * @param recyclerView: RecyclerView; 控件ID
 * @param itemLayoutId: Int; item布局ID
 * @param tDatas: ArrayList<T>? = null; 数据组
 * @param itemEditBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null; item编辑回调代码块
 * @param positionForBlock: ((recyclerView: RecyclerView, recyclerBottomCoordinate: Int, lastItemBottomCoordinate: Int, itemTotal: Int, lastItemCount: Int) -> Unit)? = null; 滑动位置监听回调代码块
 * @param longClickBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null; item长按回调代码块
 * @param  layoutManagerBlock:((Context)->LinearLayoutManager)?=null; 布局管理扩展模块
 * @param orientation: Int = LinearLayoutManager.VERTICAL； RecyclerView方向
 * @param clickBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null； item点击回调代码块
 */

class eRvAdapter<T>(
        val mContext: Context,
        val recyclerView: RecyclerView,
        val itemLayoutId: Int,
        tDatas: ArrayList<T>? = null,
        val itemEditBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null,
        val itemAnim: eDefaultItemAnimator? = null,
        positionForBlock: ((recyclerView: RecyclerView, recyclerBottomCoordinate: Int, lastItemBottomCoordinate: Int, itemTotal: Int, lastItemCount: Int) -> Unit)? = null,
        val longClickBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null,
        layoutManagerBlock:((Context)->LinearLayoutManager)?=null,
        orientation: Int = GridLayoutManager.VERTICAL,
        val clickBlock: ((itemView: View, data: T, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<eRvAdapter<T>.MyHolder>() {
    //    companion object{
    var mDatas: ArrayList<T>? = arrayListOf()
    init {
        var layoutManager=LinearLayoutManager(mContext)
        layoutManagerBlock?.let { layoutManager=it(mContext) }
        layoutManager.orientation=orientation
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
        recyclerView.itemAnimator = itemAnim
        //ViewPager滑动Pager监听
        positionForBlock?.let {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //得到当前显示的最后一个item的view
                    val lastChildView =
                            recyclerView.layoutManager!!.getChildAt(recyclerView.layoutManager!!.childCount - 1)
                    //得到lastItemView的bottom坐标值
                    val lastItemBottomCoordinate = lastChildView!!.bottom
                    //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                    val recyclerBottomCoordinate = recyclerView.bottom - recyclerView.paddingBottom
                    //通过这个lastChildView得到这个view当前的position值
                    val lastItemCount = recyclerView.layoutManager!!.getPosition(lastChildView)
                    //项目总数
                    val itemTotal = recyclerView.layoutManager!!.itemCount - 1
                    //判断lastChildView的bottom值跟recyclerBottom
                    //判断lastPosition是不是最后一个position
                    //如果两个条件都满足则说明是真正的滑动到了底部
                    it(
                            recyclerView,
                            recyclerBottomCoordinate,
                            lastItemBottomCoordinate,
                            itemTotal,
                            lastItemCount
                    )
                }
            })
        }

        tDatas?.let {
            mDatas = tDatas.clone() as ArrayList<T>
        }
    }

    private fun eMoveShow(i:Int,b:Boolean){
        if (b && i in 0 until (mDatas?.size?:0))
        recyclerView.scrollToPosition(i)
    }
    /**
     * 说明：滑动底部监听方法
     * @param              recyclerBottomCoordinate: Int； 内容最底部的坐标
     * @param    lastItemBottomCoordinate: Int；lastItemView的bottom坐标值
     * @param   itemTotal: Int； item总数
     * @param    lastItemCount: Int；最后item 位置
     * @param   ReachBottomBlock: () -> Unit； 底部回调
     * */
    open fun eOnScrolledBottom(
            recyclerBottomCoordinate: Int,
            lastItemBottomCoordinate: Int,
            itemTotal: Int,
            lastItemCount: Int,
            ReachBottomBlock: () -> Unit
    ) {
        if (recyclerBottomCoordinate == lastItemBottomCoordinate && itemTotal == lastItemCount)
            ReachBottomBlock()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(mContext).inflate(itemLayoutId, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return mDatas?.size ?: 0
    }


    fun eAddData(datas: List<T>?, index: Int? = null, animExBlock: ((eRvAdapter<T>) -> Unit)? = null, changeStatus: ((Int, Boolean) -> Unit)?={ i: Int, b: Boolean -> eMoveShow(i-1,b)}) {
        datas ?: return
        itemAnim ?: index?.let {
            mDatas?.addAll(it, datas)
        } ?: mDatas?.addAll(datas)
 itemAnim?.let { anim ->
            GlobalScope.launch {
                datas.forEachIndexed { i, t ->
                    mContext.runOnUiThread {
                        index?.let { it ->
                            eAddData(t, it + i,animExBlock,changeStatus)
                        } ?: eAddData(t,index,animExBlock,changeStatus)
                    }
                    delay(itemAnim.time / 2)
                }
            }
        } ?: eDefaultNotify()
    }
    fun eAddData(data: T, index: Int? = null, animExBlock: ((eRvAdapter<T>) -> Unit)? = null, changeStatus: ((Int, Boolean) -> Unit)?={ i: Int, b: Boolean -> eMoveShow(i-1,b)}) {
        if (index == null)
            mDatas?.add(data)
        else {
            if (index > mDatas?.size ?: 0)
                return Unit.apply { eLogE("index越界：$index>${mDatas?.size ?: 0}")
                    changeStatus?.invoke(index, false)}
            mDatas?.add(index, data)
        }
        animExBlock?.let { it(this) } ?: itemAnim?.let {
            notifyItemInserted(index ?: mDatas?.size ?: 0)
        } ?: eDefaultNotify()
        changeStatus?.invoke(index ?: mDatas?.size ?: 0, true)
    }

    fun eDelData(indexs: List<T>, animExBlock: ((eRvAdapter<T>) -> Unit)? = null, changeStatus: ((Int, Boolean) -> Unit)?={ i: Int, b: Boolean -> eMoveShow(i,b)}) {
        GlobalScope.launch {
            indexs.forEach {
                mContext.runOnUiThread {
                    eDelData(mDatas?.indexOf(it) ?: -1, animExBlock, changeStatus)
                }
                itemAnim?.let {
                    delay(itemAnim.time / 3)
                }

            }
        }
    }

    fun eDelData(index: Int=0, animExBlock: ((eRvAdapter<T>) -> Unit)? = null, changeStatus: ((Int, Boolean) -> Unit)?={ i: Int, b: Boolean -> eMoveShow(i,b)}) {
        if (index < 0 || index >= mDatas?.size ?: 0)
            return Unit.apply { changeStatus?.invoke(index, false) }
        mDatas?.removeAt(index)
        animExBlock?.invoke(this) ?: itemAnim?.let { notifyItemRemoved(index) } ?: eDefaultNotify()
        changeStatus?.invoke(index, true)
    }


    fun eSetData(datas: ArrayList<T>?, animExBlock: ((eRvAdapter<T>) -> Unit)? = null) {
        animExBlock?.invoke(this) ?: itemAnim?.let {
            val cleanSize=mDatas?.size?:0
            mDatas?.clear()
            notifyItemRangeRemoved(0,cleanSize)
            eAddData(datas)
        } ?:(mDatas?.clear().apply {
            eDefaultNotify()
            mDatas = datas?.clone() as ArrayList<T>
            eDefaultNotify()
        })


    }

    fun eGetItemData(index: Int) = mDatas?.get(index)

    fun eDefaultNotify() {
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
                itemEditBlock?.invoke(itemView, data, position) ?: if (data is DataItemInfo) {
                    defaultItemEdit(itemView, data)
                }
                longClickBlock?.let { lcb ->
                    itemView.onLongClick {
                        lcb(itemView, data, position)
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

    /**
     * 说明：默认item编辑
     * @param itemView: View； itemView
     *  @param data: DataItemInfo, 数据
     *   @param itemWidth: Float? = null, item宽度
     *  @param  itemHeight: Float? = null, item高度
     *   @param itemWeight: FloatArray = floatArrayOf(0f, 0f, 0f, 0f) ，item比重
     * */
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

data class DataItemInfo(var id: Long? = null, var isCb: Boolean? = null, var str1: String? = null, var str2: String? = null, var ico: Any? = null, var str3: String? = null, var str4: String? = null, var isShowLine: Boolean = false, var color: Int? = null)
