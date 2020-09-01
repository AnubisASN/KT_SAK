package com.anubis.module_dialog

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import kotlinx.android.synthetic.main.sample_dia.*
import kotlinx.android.synthetic.main.sample_dia.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import kotlin.properties.Delegates

/**
 * Author  ： AnubisASN   on 20-7-16 上午9:32.
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
open class eDiaAlert internal constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context
        private var mICallEdit: ICallEdit? = null
        private var mICallBack: ICallBack? = null
        private var mStyle by Delegates.notNull<Int>()
        private var mDismissTime: Long = 100L
        fun eInit(
                context: Context,
                style: Int = R.style.dialog,
                ICallBack: ICallBack? = null,
                dismissTime: Long = mDismissTime
        ): eDiaAlert {
            mContext = context
            mICallBack = ICallBack
            mStyle = style
            mDismissTime = dismissTime
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eDiaAlert() }
    }

    open fun eDefaultShow(
            title: String? = null,
            body: String? = null,
            foot: String? = null,
            isShowOK: Boolean = true,
            isShowCancel: Boolean = true,
            isShowClose: Boolean = false,
            isShowAVI: Boolean = false,
            isDisableBack: Boolean = true,
            isCanceledOnTouchOutside:Boolean=false,
            gravity: Int = Gravity.CENTER,
            x: Int = 0,
            y: Int = 0,
            alpha: Float = 0.9f,
            dismissTime: Long = 100L,
            ICallBack: ICallBack? = mICallBack,
            ICallEdit: ICallEdit? = null,
            block: (Dialog, View) -> Unit = { _, _ -> }
    ): Dialog {
        mICallBack = ICallBack
        mICallEdit = ICallEdit
        val dia = Dialog(mContext, mStyle)
        with(dia) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.sample_dia, null)
            setContentView(view)
            setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            if (ICallEdit == null)
                view.dia_etInput.visibility = View.GONE
            else
                view.dia_etInput.visibility = View.VISIBLE
            if (title == null)
                view.dia_tvTitle.visibility = View.GONE
            else
                view.dia_tvTitle.text = title
            if (body == null)
                view.dia_tvBody.visibility = View.GONE
            else
                view.dia_tvBody.text = body
            if (foot == null)
                view.dia_tvFoot.visibility = View.GONE
            else
                view.dia_tvFoot.text = foot
            if (isShowOK)
                view.dia_btOk.onClick {
                    onClickUI(it, dismissTime)
                    mICallEdit?.let { ic ->
                        ic.onEditInput(dia, dia_etInput, it)
                        return@onClick
                    }
                    Handler().postDelayed({
                        mICallBack?.onClickOK(this, it) ?: dismiss()
                    }, dismissTime)
                }
            else
                view.dia_btOk.visibility = View.GONE
            if (isShowCancel)
                view.dia_btCancel.onClick {
                    onClickUI(it, dismissTime)
                    mICallEdit?.let { ic ->
                        ic.onEditInput(dia, dia_etInput, it)
                        return@onClick
                    }
                    Handler().postDelayed({
                        mICallBack?.onClickCancel(this, it) ?: dismiss()
                    }, dismissTime)
                }
            else
                view.dia_btCancel.visibility = View.GONE
            if (isShowClose)
                view.dia_ivClose.onClick {
                    mICallEdit?.onEditInput(dia, dia_etInput, it)
                    mICallBack?.onClickClose(this, it) ?: dismiss()

                } else
                view.dia_ivClose.visibility = View.GONE
            if (isShowAVI)
                view.dia_avi.visibility = View.VISIBLE
            block(this, view)
            if (isDisableBack)
                setOnKeyListener { _, keyCode, _ ->
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> return@setOnKeyListener true
                        else -> return@setOnKeyListener false
                    }
                }
            show()
            val params = window.attributes
            params.alpha = alpha
            params.x = x
            params.y = y
            window.attributes = params
            window.setGravity(gravity)

        }
        return dia
    }

    open fun onClickDown(downClickView:View) {
        mICallEdit = null
        downClickView.callOnClick()
    }

    open fun eDIYShow(
            layout: Int,
            iCallBack: IDIYCallBack? = null,
            onClick: IDIYClickCallBack? = null,
            isDisableBack: Boolean = true
    ): Dialog {
        val dia = Dialog(mContext, mStyle)
        with(dia) {
            val view = LayoutInflater.from(mContext).inflate(layout, null)
            setCanceledOnTouchOutside(false)
            val params = window.attributes
            window.setGravity(Gravity.CENTER)
            params.alpha = 0.9f
            window.attributes = params
            if (isDisableBack)
                setOnKeyListener { _, keyCode, _ ->
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> return@setOnKeyListener true
                        else -> return@setOnKeyListener false
                    }
                }
            iCallBack?.DIY(this, view, onClick)
            setContentView(view)
            show()

        }
        return dia
    }

    open fun onClickUI(it: View?, dismissTime: Long = mDismissTime, block: () -> Unit = {}) {
        with(it as? Button) {
            this?.textColor = Color.parseColor("#ffffff")
            this?.background = mContext.getDrawable(R.drawable.dia_btbackground1)
            Handler().postDelayed({
                this?.textColor = Color.parseColor("#25CEFB")
                this?.background = mContext.getDrawable(R.drawable.dia_btbackground0)
                block()
            }, dismissTime)
        }
    }


    interface ICallBack {
        fun onClickOK(dia: Dialog, it: View?)
        fun onClickCancel(dia: Dialog, it: View?)
        fun onClickClose(dia: Dialog, it: View?)
    }

    interface ICallEdit {
        fun onEditInput(dia: Dialog, editText: EditText, iv: View? = null)
    }

    interface IDIYCallBack {
        fun DIY(dia: Dialog, view: View, onClick: IDIYClickCallBack?)
    }

    interface IDIYClickCallBack {
        fun onClick(view: View, it: View?)
    }
}
