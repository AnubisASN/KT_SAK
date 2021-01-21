package com.anubis.app_skin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anubis.module_extends.eRvAdapter
import com.anubis.selfServicePayment.Utils.eDefaultItemAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*
import org.jetbrains.anko.onClick

class MainActivity : AppCompatActivity() {
val dataArray= arrayListOf<Int>()
    lateinit var mAdapter:eRvAdapter<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    fun initView() {
           mAdapter= eRvAdapter(this,rv,R.layout.item,dataArray,{ view: View, i: Int, i1: Int ->
view.item_tv.text= "$i"
        },eDefaultItemAnimator())


        add.onClick {
            mAdapter.eAddData((mAdapter.mDatas?.size?:0)+1)
        }
        adds.onClick {
            mAdapter.eAddData(listOf(11,22,33,44,55))
        }
        set.onClick {
            mAdapter.eSetData(arrayListOf(10,20,30,40,50))
        }
        del.onClick {
            mAdapter.eDelData(0)
        }
        dels.onClick {
            mAdapter.eDelData(arrayListOf(11,22,33,44))
        }
    }
}
