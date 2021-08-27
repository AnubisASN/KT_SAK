package com.anubis.module_view

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SlidingDrawer
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.kt_extends.eTime
import com.anubis.kt_extends.eTime.Companion.eITime
import com.anubis.module_view.views.bubble.eBubbleLayout
import com.anubis.module_view.views.eCaptchaView
import com.anubis.module_view.views.eSwipeCaptcha
import com.anubis.module_view.views.web.eWaveView
import com.anubis.module_view.views.like.eShineButtonView
import com.tamsiree.rxkit.eWeb
import kotlinx.android.synthetic.main.activity_test_view.*
import kotlinx.android.synthetic.main.sample_drawer_view.*
import kotlinx.android.synthetic.main.sample_web_view.*
import java.util.*

/**
 * Author  ： AnubisASN   on 21-3-31 下午2:31.
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
class eView {
    companion object {
        val eIView by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eView() }
    }

    /**随机生成验证码图片
     * @param imageView: ImageView, 验证图显示控件
     * @param codeLength: Int = 5,验证码长度
     * @param  block: ((eCaptcha) -> Unit)? = null,扩展
     * @return String,验证码返回
     */
    fun eInitCaptchaCode(imageView: ImageView, codeLength: Int = 5, block: ((eCaptchaView) -> Unit)? = null): String {
        val captcha = eCaptchaView.build()!!
                .backColor(0xffffff)!!
                .codeLength(codeLength)!!
                .fontSize(60)!!
                .lineNumber(0)!!
                .size(200, 70)!!
                .type(eCaptchaView.TYPE.CHARS)!!
        block?.let { it(captcha) }
        captcha.into(imageView)
        return captcha.getCode()
    }

    /**滑块验证
     * @param context: Context, 上下文
     * @param  resourceId: Int,滑块图片资源
     * @param  swipeCaptchaView: eSwipeCaptcha,滑块验证控件
     * @param seekBarView: SeekBar,滑块控件
     * @param noBlock: (() -> Unit)? = null,验证失败
     * @param okBlock: (() -> Unit)? = null，验证失败
     */
    fun eInitSwipeCaptcha(activity: Activity, imgResourceId: Int, swipeCaptchaViewId: Int=R.id.sample_swipe_scv  , seekBarViewId: Int = R.id.sample_swipe_bar, noBlock: (() -> Unit)? = null, okBlock: ((String) -> Unit)? = null) {
        with(activity) {
            var time: Date? = Date()
            val swipeCaptchaView = activity.findViewById<eSwipeCaptcha>(swipeCaptchaViewId)
            swipeCaptchaView.setImageResource(imgResourceId)
            val seekBarView = activity.findViewById<SeekBar>(seekBarViewId)
            swipeCaptchaView.onCaptchaMatchCallback = object : eSwipeCaptcha.OnCaptchaMatchCallback {
                override fun matchSuccess(swipeCaptcha: eSwipeCaptcha) {
                    okBlock?.let {
                        it("${eITime.eGetTimeDifference(time, type = Calendar.SECOND)}s")
                        return
                    }
                    activity.eShowTip("验证通过")
                    seekBarView.isEnabled = false
                }

                override fun matchFailed(swipeCaptcha: eSwipeCaptcha) {
                    noBlock?.let {
                        it()
                        return
                    }
                    activity.eShowTip("验证失败:拖动滑块将悬浮头像正确拼合")
                    swipeCaptcha.eResetCaptcha()
                    seekBarView.progress = 0
                }
            }
            seekBarView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    swipeCaptchaView.eSetCurrentSwipeValue(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    time = eITime.eGetDataStrToDate()
                    //随便放这里是因为控件
                    seekBar.max = swipeCaptchaView.eMaxSwipeValue
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    swipeCaptchaView.eMatchCaptcha()
                }
            })
            seekBarView.isEnabled = true
            seekBarView.progress = 0
            swipeCaptchaView.eCreateCaptcha()
        }

    }

    /**抽屉详情
     * @param activity: Activity, 界面意图
     * @param contentLayoutId: Int,内容布局ID
     * @param  swipeCaptchaView: eSwipeCaptcha,滑块验证控件
     * @param  addBlock: ((View) -> Unit)? = null，添加成功扩展
     */
    fun eInitDrawer(activity: Activity, contentLayoutId: Int, addBlock: ((View?) -> Unit)? = null) {
        val view = View.inflate(activity, R.layout.sample_drawer_view, null)
        activity.addContentView(view, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ))
        activity.sample_sd.setOnDrawerOpenListener {
            with(activity.sample_ivSlide) {
                tag = "1"
                setImageResource(R.drawable.slibe_down)
            }
        }
        activity.sample_sd.setOnDrawerCloseListener {
            with(activity.sample_ivSlide) {
                tag = "0"
                setImageResource(R.drawable.slibe_up)
            }
        }
        activity.sample_sd.setOnDrawerScrollListener(object : SlidingDrawer.OnDrawerScrollListener {
            override fun onScrollEnded() {}
            override fun onScrollStarted() {}
        })
        activity.sample_vs.layoutResource = contentLayoutId
        addBlock?.let { it(activity.sample_vs?.inflate()) }
    }

    /**气泡控件
     * @param view:View,触摸生效控件
     * @param heartLayout: eBubbleLayout, 气泡布局控件
    * */
    fun eInitBubble(view:View,heartLayout: eBubbleLayout) {
        view.setOnClickListener {
            val rgb = Color.rgb(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255))
            heartLayout.eAddBubble(rgb)
        }
    }
    /** 点赞效果控件
     * @param activity: Activity,意图
     * @param sbView: eShineButtonView， 发光按钮控件
    * */
    fun eInitShineButtonView(activity: Activity,sbView: eShineButtonView){
        sbView.eInit(activity)
    }

    /**水波浪容器视图
     *  @param wave:eWaveView, 水波容器控件
     *  @param startValue: Float = 0f, 水平启始值
     *  @param endValue: Float = 0f,水平结束值
     *  @param  time: Long = 5000，动画时间
     * */
    fun eInitWave(wave: eWaveView, startValue: Float = 0f, endValue: Float = 0f, time: Long = 5000){
        wave.eInit(startValue, endValue, time)
    }

    /**WebView
     * @param
     * */
    fun eInitWeb(activity: Activity,webView: WebView,webViewLoad:eWeb.OnWebViewLoad,url:String){
        eWeb.initWebView(activity,webView,webViewLoad,url)
    }
}
