package com.anubis.module_extends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anubis.kt_extends.eLogE
import org.jetbrains.anko.onClick

/**
 * Author  ： AnubisASN   on 20-7-30 上午9:26.
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
class eMyAdapter<T>(
        val mActivity: Context,
        val layoutId: Int,
        val mDatas: ArrayList<T>,
        val itemBlock: (data: T, position: Int) -> Unit = { _, _ -> },
        val block: (itemView: View, position: Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<eMyAdapter<T>.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(mActivity).inflate(layoutId, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    //方法\界面绑定
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(mDatas[position], position)
    }

    var v: View? = null
    var i: Int = -1

    //界面设置
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(data: T, position: Int) {
            try {
                itemBlock(data,position)
                itemView.onClick {
                    block(itemView, position)
                }
            } catch (e: Exception) {
                e.eLogE("MyHolder")
            }
        }
    }
}
