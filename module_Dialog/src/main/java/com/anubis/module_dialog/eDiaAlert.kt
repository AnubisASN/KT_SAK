package com.anubis.module_dialog

import android .annotation.SuppressLint
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
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.anubis.module_dialog.View.eArrowDownloadButton
import com.anubis.module_extends.eRvAdapter
import kotlinx.android.synthetic.main.dia_default.*
import kotlinx.android.synthetic.main.dia_default.view.*
import kotlinx.android.synthetic.main.dia_gradeselect.view.*
import kotlinx.android.synthetic.main.sample_gradeselect.view.*
import org.jetbrains.anko.*
import kotlin.collections.ArrayList
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
        private var mStyle by Delegates.notNull<Int>()
        private var mDismissTime: Long = 100L
        fun eInit(
                context: Context,
                style: Int = R.style.dialog,
                dismissTime: Long = mDismissTime
        ): eDiaAlert {
            mContext = context
            mStyle = style
            mDismissTime = dismissTime
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eDiaAlert() }
    }

    /**
     * @param title: String? = null,
     * @param body: String? = null,
     * @param foot: String? = null,
     * @param btOK: String? = null,
     * @param btCancel: String? = null,
     * @param isShowClose: Boolean = false,
     * @param isShowAVI: Boolean = false, 加载动画
     * @param isDisableBack: Boolean = true, 是否禁用返回
     * @param isCanceledOnTouchOutside: Boolean = false, 是否外部取消
     * @param gravity: Int = Gravity.CENTER, 弹窗位置
     * @param x: Int = 0, 弹窗移动 X
     * @param y: Int = 0, 弹窗移动 Y
     * @param alpha: Float = 0.9f, 弹窗透明度
     * @param buttonReplyTime: Long = 100L,
     * @param onClickAnimation:Block=,
     * @param ICallBackEdit: ICallBackEdit? = null,
     * @param ICallBackClick: ICallBackClick? = null,
     * @param adbEditBlock:((Dialog, View, eArrowDownloadButton) -> Unit )?=null,
     * @param itemEditBlock: (Dialog, View) -> Unit = { _, _ -> }
     * */
    open fun eDefaultShow(
            title: String? = null,
            body: String? = null,
            foot: String? = null,
            btOK: String? = null,
            btCancel: String? = null,
            isShowClose: Boolean = false,
            isShowAVI: Boolean = false,
            isDisableBack: Boolean = true,
            isCanceledOnTouchOutside: Boolean = false,
            gravity: Int = Gravity.CENTER,
            x: Int = 0,
            y: Int = 0,
            alpha: Float = 1f,
            buttonReplyTime: Long = 100L,
            onClickAnimation:((View?,Long)->Unit)?=null,
            ICallBackEdit: ICallBackEdit? = null,
            ICallBackClick: ICallBackClick? = null,
            adbEditBlock: ((Dialog, View, eArrowDownloadButton) -> Unit)? = null,
            itemEditBlock: (Dialog, View) -> Unit = { _, _ -> }
    ): Dialog {
        val dia = Dialog(mContext, mStyle)
        with(dia) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.dia_default, null)
            setContentView(view)
            setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            if (ICallBackEdit == null)
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
            if (btOK == null)
                view.dia_btOk.visibility = View.GONE
            else {
                view.dia_btOk.text = btOK
                view.dia_btOk.onClick {
                    if (buttonReplyTime>0L)
                        onClickAnimation?.invoke(it, buttonReplyTime)?:onDefaultClickAnimation(it, buttonReplyTime)
                    ICallBackEdit?.let { ic ->
                        ic.onEditInput(dia, dia_etInput, it)
                        return@onClick
                    }
                    Handler().postDelayed({
                        ICallBackClick?.onClickOK(this, it) ?: dismiss()
                    }, buttonReplyTime)
                }
            }
            if (btCancel == null)
                view.dia_btCancel.visibility = View.GONE
            else {
                view.dia_btCancel.text = btCancel
                view.dia_btCancel.onClick {
                    if (buttonReplyTime>0L)
                        onClickAnimation?.invoke(it, buttonReplyTime)?:onDefaultClickAnimation(it, buttonReplyTime)
                    ICallBackEdit?.let { ic ->
                        ic.onEditInput(dia, dia_etInput, it)
                        return@onClick
                    }
                    Handler().postDelayed({
                        ICallBackClick?.onClickCancel(this, it) ?: dismiss()
                    }, buttonReplyTime)
                }
            }
            if (isShowClose)
                view.dia_ivClose.onClick {
                    ICallBackEdit?.onEditInput(dia, dia_etInput, it)
                    ICallBackClick?.onClickClose(this, it) ?: dismiss()
                } else
                view.dia_ivClose.visibility = View.GONE
            if (isShowAVI)
                view.dia_avi.visibility = View.VISIBLE
            adbEditBlock?.let { it(dia, view, view.dia_adb) }
            itemEditBlock(dia, view)
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


    open fun eDIYShow(
            layout: Int,
            ICallBackClick: IDIYCallBackClick? = null,
            isDisableBack: Boolean = true,
            isCanceledOnTouchOutside:Boolean=false,
            gravity: Int = Gravity.CENTER,
            x: Int = 0,
            y: Int = 0,
            alpha: Float =1f,
            itemEditBlock: (Dialog, View, IDIYCallBackClick?) -> Unit
            ): Dialog {
        val dia = Dialog(mContext, mStyle)
        with(dia) {
            val view = LayoutInflater.from(mContext).inflate(layout, null)
            setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            val params = window.attributes
            params.alpha = alpha
            params.x=x
            params.y=y
            window.setGravity(gravity)
            window.attributes = params
            if (isDisableBack)
                setOnKeyListener { _, keyCode, _ ->
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> return@setOnKeyListener true
                        else -> return@setOnKeyListener false
                    }
                }
            itemEditBlock(this,view,ICallBackClick)
            setContentView(view)
            show()
        }
        return dia
    }

    open fun onDefaultClickAnimation(it: View?, dismissTime: Long = mDismissTime) {
        with(it as? Button) {
            this?.textColor = Color.parseColor("#ffffff")
            this?.background = mContext.getDrawable(R.drawable.dia_btbackground1)
            Handler().postDelayed({
                this?.textColor = Color.parseColor("#25CEFB")
                this?.background = mContext.getDrawable(R.drawable.dia_btbackground0)
            }, dismissTime)
        }
    }


    /**
     * 说明：分级选择器
     * @param gradeTitleArray: Array<String> = arrayOf("请选择小区"), //级别1  标题
     * @param itemDataList: ArrayList<ArrayList<T>> = arrayListOf(), //级别2  子项
     * @param adapterBlock: (ArrayList<eRvAdapter<T>>) -> Unit, //级别1  返回级别2 适配器
     * @param itemClickBlock: ((itemView1: View, i1: Int, itemView2: View, T, i2: Int) -> Unit)? = null,//级别2  点击项
     * @param orientation: Int = LinearLayoutManager.HORIZONTAL,  //方向
     * @param returnBlock: (ArrayList<T?>) -> Unit  //返回结果组
     * */
    open fun <T> eDiaGradeSelect(
            gradeTitleArray: Array<String> = arrayOf("请选择小区"), //级别1  标题
            itemDataList: ArrayList<ArrayList<T>> = arrayListOf(), //级别2  子项
            adapterBlock: (ArrayList<eRvAdapter<T>>) -> Unit, //级别1  返回级别2 适配器
            itemClickBlock: ((itemView1: View, i1: Int, itemView2: View, T, i2: Int) -> Unit)? = null,//级别2  点击项
            orientation: Int = LinearLayoutManager.HORIZONTAL,  //方向
            isCanceledOnTouchOutside: Boolean = true,
            returnBlock: (ArrayList<T?>) -> Unit  //返回结果组
    ) {
        val gradeReturnData = arrayListOf<T?>()
        val adapterList = arrayListOf<eRvAdapter<T>>()

        eDIYShow(R.layout.dia_gradeselect){ dia: Dialog, view: View, idiyCallBackClick: IDIYCallBackClick? ->
            dia.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            //分级布局
            eRvAdapter(mContext, view.gradedia_ll, R.layout.sample_gradeselect, gradeTitleArray.toMutableList() as ArrayList<String>, { itemview1: View, s: String, i1: Int ->
                itemview1.gradedia_tvTitle.text = s
                itemview1.gradedia_rvBody.backgroundColor = Color.parseColor("#525655")
                itemview1.gradedia_csLayout.onClick {
                    itemview1.onDiaGradeSeClickUI(Pair(itemview1.gradedia_tvTitle, itemview1.gradedia_tvDynamic))
                }
                gradeReturnData.add(null)
                adapterList.add(eRvAdapter(mContext, itemview1.gradedia_rvBody, R.layout.adapter_default_item, itemDataList.getOrNull(i1)) { itemview2: View, t: T, i2: Int ->
                    itemClickBlock?.let { it(itemview1, i1, itemview2, t, i2) }
                    gradeReturnData[i1] = t
                    with(gradeReturnData.filter {
                        it != null
                    }) {
                        if (this.size == gradeReturnData.size) {
                            returnBlock(gradeReturnData)
                            dia.dismiss()
                        }
                    }
                })
            }, orientation = orientation)
            adapterBlock(adapterList)
        }
    }


    private var colorViewPair: Pair<TextView, TextView>? = null
    internal open fun View.onDiaGradeSeClickUI(viewPair: Pair<TextView, TextView>? = null) {
        colorViewPair?.first?.textColor = Color.parseColor("#ffffff")
        colorViewPair?.second?.backgroundColor = Color.parseColor("#ffffff")
        viewPair?.let {
            it.first.textColor = Color.parseColor("#25CEFB")
            it.second.backgroundColor = Color.parseColor("#25CEFB")
            colorViewPair = viewPair
        }

    }

    /*-----*/
    interface ICallBackClick {
        fun onClickOK(dia: Dialog, it: View?)
        fun onClickCancel(dia: Dialog, it: View?)
        fun onClickClose(dia: Dialog, it: View?)
    }

    interface ICallBackEdit {
        fun onEditInput(dia: Dialog, editText: EditText, iv: View? = null)
    }

    interface IDIYCallBackClick {
        fun onClick(view: View, it: View?)
    }
}
