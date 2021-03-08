package com.example.maeassignment.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.anubis.module_extends.R

/**
 * Author  ： AnubisASN   on 21-2-9 下午3:41.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * HomePage： www.anubisasn.me
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *类说明：
 */
open class  eFragment(val activity: AppCompatActivity, val fragmentId: Int, val fragments: ArrayList<Fragment>, currentFraId: Int? = null) {

     private var mCurrentFraId: Int= -1
    init {
        currentFraId?.let {
            eReplaceFragment(it,false, null)
        }
    }

    val eDefultBlock: ((FragmentTransaction, Int) -> Unit)
    get()  = { fragmentTransaction: FragmentTransaction, i: Int ->
        if (mCurrentFraId != i || i >-1){
            if (i > mCurrentFraId)
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out
                )

            if (i < mCurrentFraId)
                fragmentTransaction. setCustomAnimations(
                        R.anim.slide_left_in, R.anim.slide_right_out,
                        R.anim.slide_right_in, R.anim.slide_left_out
                )
        }



    }

    fun eReplaceFragment(index: Int,isToStack:Boolean=false, block: ((FragmentTransaction, Int) -> Unit)? = eDefultBlock) {
        val fragmentManager = activity.supportFragmentManager
        val mTransaction = fragmentManager.beginTransaction()  // 开启一个事务
        block?.invoke(mTransaction, index)
        mTransaction.replace(fragmentId, fragments[index])
        if (isToStack)
        mTransaction.addToBackStack(null)
        mTransaction.commit()
        mCurrentFraId = index
    }

    fun eReplaceFragment(fragment: Fragment,isToStack:Boolean=false, block: ((FragmentTransaction, Int) -> Unit)? = eDefultBlock) {
        eReplaceFragment(fragments.indexOf(fragment),isToStack, block)

    }

}
