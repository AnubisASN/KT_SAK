package com.anubis.module_extends

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

/**
 * Author  ： AnubisASN   on 20-10-20 下午3:13.
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
 */

/**
 *说明：ePagerAdapter 适配器
 * @param viewPager: ViewPager; viewPager 控件
 * @param layoutViewList:  ArrayList<View>; layoutView 布局控件
 * @param tDatas: T：ArrayList<Any> ; 布局View  datas
 * @param onPageChangeListener: ViewPager.OnPageChangeListener? = null; 页面更改监听
 * @param itemEditBlock:((itemView: View, data: T?, position: Int) -> Unit)?=null; 布局编辑块
 * @param longClickBlock: ((itemView: View, data: T?,position: Int) -> Unit)? = null; 布局控件长按块
 * @param itemClickBlock: ((itemView: View, data: T?, position: Int) -> Unit)? = null; 布局控件点击块
 */
class ePagerAdapter<T : ArrayList<Any>>(
        viewPager: ViewPager,
        val layoutViewList: ArrayList<View>,
        val tDatas: T? = null,
        onPageChangeListener: ViewPager.OnPageChangeListener? = null,
        val itemEditBlock: ((itemView: View, data: T?, position: Int) -> Unit)? = null,
        val longClickBlock: ((itemView: View, data: T?, position: Int) -> Unit)? = null,
        val itemClickBlock: ((itemView: View, data: T?, position: Int) -> Unit)? = null
) : PagerAdapter() {
    init {
        viewPager.adapter = this
        onPageChangeListener?.let {
            //ViewPager滑动Pager监听
            viewPager.setOnPageChangeListener(it)
        }
    }

    //item的个数
    override fun getCount() = tDatas?.size ?: layoutViewList.size

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    //初始化item布局
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutViewList[position]
        itemEditBlock?.let { it(itemView, tDatas, position) }
        longClickBlock?.let { lcb ->
            itemView.onLongClick {
                lcb(itemView, tDatas, position)
                return@onLongClick true
            }
        }
        itemClickBlock?.let { itemView.onClick { it(itemView, tDatas, position) } }
        container.addView(itemView)
        return itemView
    }

    //销毁item
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}

