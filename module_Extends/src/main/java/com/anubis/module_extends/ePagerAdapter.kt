package com.anubis.module_extends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 *说明：
 */
class   ePagerAdapter<T:ArrayList<Any>>(
    viewPager: ViewPager,
    val layoutViewList: ArrayList<View>,
   val tDatas: T? = null,
    val itemEditBlock: ((itemView: View, data: T?, position: Int) -> Unit)?=null,
    onPageChangeListener: ViewPager.OnPageChangeListener? = null,
    val longClickBlock: ((itemView: View, data: T?,position: Int) -> Unit)? = null,
    val itemClickBlock: ((itemView: View, data: T?, position: Int) -> Unit)? = null
) : PagerAdapter() {
    init {
        viewPager.adapter=this
        onPageChangeListener?.let {
            //ViewPager滑动Pager监听
            viewPager.setOnPageChangeListener(it)
        }
    }
        //item的个数
        override fun getCount()=tDatas?.size?:layoutViewList.size


        override fun isViewFromObject(view: View, `object`: Any)=view === `object`

        //初始化item布局
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView =layoutViewList[position]
            itemEditBlock?.let { it(itemView, tDatas,position) }
            longClickBlock?.let { lcb ->
                itemView.onLongClick {
                    lcb(itemView,tDatas, position)
                    return@onLongClick true
                }
            }
            itemClickBlock?.let {itemView.onClick { it(itemView,tDatas,position) }}
            container.addView(itemView)
            return itemView
        }

        //销毁item
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

