package com.anubis.module_view.views.web

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.anubis.kt_extends.eColor
import com.anubis.kt_extends.eColor.Companion.eIColor
import com.anubis.module_view.R
import java.util.*

/**
 * 水波浪容器视图
 */
class eWaveView : View {
    enum class ShapeType {
        //圆形
        CIRCLE,  //矩形
        SQUARE
    }

     /*是否波形*/
    var isShowWave = false

    // shader containing repeated waves
    private var mWaveShader: BitmapShader? = null

    // shader matrix
    private var mShaderMatrix: Matrix? = null

    // paint to draw wave
    private var mViewPaint: Paint? = null

    // paint to draw border
    private var mBorderPaint: Paint? = null
    private var mDefaultAmplitude = 0f
    private var mDefaultWaterLevel = 0f
    private var mDefaultWaveLength = 0f
    private var mDefaultAngularFrequency = 0.0
    private var mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO

    /**
     * Set horizontal size of wave according to `waveLengthRatio`
     *
     * @param waveLengthRatio Default to be 1.
     * Ratio of wave length to width of WaveView.
     */
   private var waveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO
    private var mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO
    private var mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
   private var behindWaveColor = DEFAULT_BEHIND_WAVE_COLOR
    private var frontWaveColor = DEFAULT_FRONT_WAVE_COLOR
    private    var mShapeType = DEFAULT_WAVE_SHAPE
    private var borderWidth = 10
    var borderColor = Color.parseColor("#4489CFF0")
    private var mAnimatorSet: AnimatorSet? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /**
     * Shift the wave horizontally according to `waveShiftRatio`.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     * Result of waveShiftRatio multiples width of WaveView is the length to shift.
     */
    var eWaveShiftRatio: Float
        get() = mWaveShiftRatio
        set(waveShiftRatio) {
            if (mWaveShiftRatio != waveShiftRatio) {
                mWaveShiftRatio = waveShiftRatio
                invalidate()
            }
        }

    /**
     *设置水平值
     */
    var eWaterLevelRatio: Float
        get() = mWaterLevelRatio
        set(waterLevelRatio) {
            if (mWaterLevelRatio != waterLevelRatio) {
                mWaterLevelRatio = waterLevelRatio
                invalidate()
            }
        }

    /**
     * Set vertical size of wave according to `amplitudeRatio`
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     * Ratio of amplitude to height of WaveView.
     */
    var eAmplitudeRatio: Float
        get() = mAmplitudeRatio
        set(amplitudeRatio) {
            if (mAmplitudeRatio != amplitudeRatio) {
                mAmplitudeRatio = amplitudeRatio
                invalidate()
            }
        }

    private fun init(context: Context, attrs: AttributeSet?) {

        //获得这个控件对应的属性。
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.eWaveView)
        try {
            //获得属性值

            //标题颜色
            frontWaveColor = a.getColor(R.styleable.eWaveView_eWaveColor, Color.parseColor("#89CFF0"))
            behindWaveColor = eIColor.eGetChangeColorAlpha(frontWaveColor, 40)
            borderColor = eIColor.eGetChangeColorAlpha(frontWaveColor, 68)
            borderWidth = a.getDimension(R.styleable.eWaveView_eWaveBorder, borderWidth * 1f).toInt()
            val type = a.getInt(R.styleable.eWaveView_eWaveShapeType, 0)
            when (type) {
                1 -> mShapeType = ShapeType.SQUARE
                0 -> mShapeType = ShapeType.CIRCLE
                else -> {
                }
            }
        } finally {
            //回收这个对象
            a.recycle()
        }
        mShaderMatrix = Matrix()
        mViewPaint = Paint()
        mViewPaint!!.isAntiAlias = true
        eSetWaveColor(behindWaveColor, frontWaveColor)
        eSetBorder(borderWidth, borderColor)

    }

    /** 开始初始化
     * @param startValue: Float = 0f,开始水平值
     * @param endValue: Float = 0f,结束水平值
     * @param  time: Long = 5000,动画时长
    * */
    fun eInit(startValue: Float = 0f, endValue: Float = 0f, time: Long = 5000) {
        initAnimation(startValue,endValue,time)
        eStart()
    }

    fun eSetWaveColor(behindWaveColor: Int, frontWaveColor: Int) {
        this.behindWaveColor = behindWaveColor
        this.frontWaveColor = frontWaveColor
        if (width > 0 && height > 0) {
            // need to recreate shader when color changed
            mWaveShader = null
            createShader()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createShader()
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private fun createShader() {
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO
        mDefaultWaveLength = width.toFloat()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val wavePaint = Paint()
        wavePaint.strokeWidth = 2f
        wavePaint.isAntiAlias = true

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        val endX = width + 1
        val endY = height + 1
        val waveY = FloatArray(endX)
        wavePaint.color = behindWaveColor
        for (beginX in 0 until endX) {
            val wx = beginX * mDefaultAngularFrequency
            val beginY = (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx)).toFloat()
            canvas.drawLine(beginX.toFloat(), beginY, beginX.toFloat(), endY.toFloat(), wavePaint)
            waveY[beginX] = beginY
        }
        wavePaint.color = frontWaveColor
        val wave2Shift = (mDefaultWaveLength / 4).toInt()
        for (beginX in 0 until endX) {
            canvas.drawLine(beginX.toFloat(), waveY[(beginX + wave2Shift) % endX], beginX.toFloat(), endY.toFloat(), wavePaint)
        }

        // use the bitamp to create the shader
        mWaveShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        mViewPaint!!.shader = mWaveShader
    }

    override fun onDraw(canvas: Canvas) {
        // modify paint shader according to mShowWave state
        if (isShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint!!.shader == null) {
                mViewPaint!!.shader = mWaveShader
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix!!.setScale(
                    waveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0f,
                    mDefaultWaterLevel)
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix!!.postTranslate(
                    mWaveShiftRatio * width,
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * height)

            // assign matrix to invalidate the shader
            mWaveShader!!.setLocalMatrix(mShaderMatrix)
            val borderWidth = if (mBorderPaint == null) 0f else mBorderPaint!!.strokeWidth
            when (mShapeType) {
                ShapeType.CIRCLE -> {
                    if (borderWidth > 0) {
                        canvas.drawCircle(width / 2f, height / 2f,
                                (width - borderWidth) / 2f - 1f, mBorderPaint)
                    }
                    val radius = width / 2f - borderWidth
                    canvas.drawCircle(width / 2f, height / 2f, radius, mViewPaint)
                }
                ShapeType.SQUARE -> {
                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2f,
                                borderWidth / 2f,
                                width - borderWidth / 2f - 0.5f,
                                height - borderWidth / 2f - 0.5f,
                                mBorderPaint)
                    }
                    canvas.drawRect(borderWidth, borderWidth, width - borderWidth,
                            height - borderWidth, mViewPaint)
                }
                else -> {
                }
            }
        } else {
            mViewPaint!!.shader = null
        }
    }

    fun eSetBorder(width: Int, color: Int) {
        if (mBorderPaint == null) {
            mBorderPaint = Paint()
            mBorderPaint!!.isAntiAlias = true
            mBorderPaint!!.style = Paint.Style.STROKE
        }
        borderColor = color
        borderWidth = width
        mBorderPaint!!.color = borderColor
        mBorderPaint!!.strokeWidth = borderWidth.toFloat()
        invalidate()
    }

    var eShapeType: ShapeType
        get() = mShapeType
        set(shapeType) {
            mShapeType = shapeType
            invalidate()
        }

    fun eStart() {
        isShowWave = true
        if (mAnimatorSet != null) {
            mAnimatorSet!!.start()
        }
    }

    private fun initAnimation(startValue: Float , endValue: Float  , time: Long = 10000) {
        val animators: MutableList<Animator> = ArrayList()

        // horizontal animation.
        // wave waves infinitely.
        val waveShiftAnim = ObjectAnimator.ofFloat(
                this, "eWaveShiftRatio", 0f, 1f)
        waveShiftAnim.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim.duration = 1000
        waveShiftAnim.interpolator = LinearInterpolator()
        animators.add(waveShiftAnim)

        // vertical animation.
        // water level increases from 0 to center of eWaveView
        /*水位动画*/
        val waterLevelAnim = ObjectAnimator.ofFloat(
                this, "eWaterLevelRatio", startValue, endValue)
        waterLevelAnim.duration = time
        waterLevelAnim.interpolator = DecelerateInterpolator()
        animators.add(waterLevelAnim)

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        val amplitudeAnim = ObjectAnimator.ofFloat(
                this, "eAmplitudeRatio", 0.0001f, 0.05f)
        amplitudeAnim.repeatCount = ValueAnimator.INFINITE
        amplitudeAnim.repeatMode = ValueAnimator.REVERSE
        amplitudeAnim.duration = 5000
        amplitudeAnim.interpolator = LinearInterpolator()
        animators.add(amplitudeAnim)
        mAnimatorSet = AnimatorSet()
        mAnimatorSet!!.playTogether(animators)
    }

    fun eCancel() {
        if (mAnimatorSet != null) {
//            mAnimatorSet.cancel();
            mAnimatorSet!!.end()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    companion object {
        /**
         * +------------------------+
         * |<--wave length->        |______
         * |   /\          |   /\   |  |
         * |  /  \         |  /  \  | amplitude
         * | /    \        | /    \ |  |
         * |/      \       |/      \|__|____
         * |        \      /        |  |
         * |         \    /         |  |
         * |          \  /          |  |
         * |           \/           | water level
         * |                        |  |
         * |                        |  |
         * +------------------------+__|____
         */
        private const val DEFAULT_AMPLITUDE_RATIO = 0.05f
        var DEFAULT_WATER_LEVEL_RATIO =0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1.0f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        val DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#2889CFF0")
        val DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3C89CFF0")
        val DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE
    }
}
