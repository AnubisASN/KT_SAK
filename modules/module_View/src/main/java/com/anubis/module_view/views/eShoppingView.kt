package com.anubis.module_view.views

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.anubis.module_view.R
import java.util.*

/** 购物车式按钮
 */
class eShoppingView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var mPaintBg: Paint? = null
    private var mPaintText: Paint? = null
    private var mPaintNum: Paint? = null
    private var mPaintMinus: Paint? = null

    //是否是向前状态（= = 名字不好取，意思就是区分向前和回退状态）
    private var mIsForward = true

    //动画时长
    private var mDuration = 0

    //购买数量
    private var mNum = 0

    //展示文案
    private var mShoppingText: String? = null

    //当前状态
    private var mState = STATE_NONE

    //属性值
    private var mWidth = 0
    private var mAngle = 0
    private var mTextPosition = 0
    private var mMinusBtnPosition = 0
    private var mAlpha = 0
    private var MAX_WIDTH = 0
    private var MAX_HEIGHT = 0
    private var mShoppingClickListener: ShoppingClickListener? = null
    private fun init(attrs: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.eShoppingView)
        mDuration = typeArray.getInt(R.styleable.eShoppingView_sv_duration, DEFAULT_DURATION)
        mShoppingText = if (TextUtils.isEmpty(typeArray.getString(R.styleable.eShoppingView_sv_text))) DEFAULT_SHOPPING_TEXT else typeArray.getString(R.styleable.eShoppingView_sv_text)
        //展示文案大小
        val textSize = typeArray.getDimension(R.styleable.eShoppingView_sv_text_size, sp2px(16f).toFloat()).toInt()
        //背景色
        val bgColor = typeArray.getColor(R.styleable.eShoppingView_sv_bg_color, Color.parseColor("#6A5ACD"))
        typeArray.recycle()
        mPaintBg = Paint()
        mPaintBg!!.color = bgColor
        mPaintBg!!.style = Paint.Style.FILL
        mPaintBg!!.isAntiAlias = true
        mPaintMinus = Paint()
        mPaintMinus!!.color = bgColor
        mPaintMinus!!.style = Paint.Style.STROKE
        mPaintMinus!!.isAntiAlias = true
        mPaintMinus!!.strokeWidth = textSize / 6.toFloat()
        mPaintText = Paint()
        mPaintText!!.color = Color.WHITE
        mPaintText!!.strokeWidth = textSize / 6.toFloat()
        mPaintText!!.textSize = textSize.toFloat()
        mPaintText!!.isAntiAlias = true
        mPaintNum = Paint()
        mPaintNum!!.color = Color.BLACK
        mPaintNum!!.textSize = textSize / 3 * 4.toFloat()
        mPaintNum!!.strokeWidth = textSize / 6.toFloat()
        mPaintNum!!.isAntiAlias = true
        MAX_WIDTH = getTextWidth(mPaintText, mShoppingText) / 5 * 8
        MAX_HEIGHT = textSize * 2
        if (MAX_WIDTH / MAX_HEIGHT < 3.5) {
            MAX_WIDTH = (MAX_HEIGHT * 3.5).toInt()
        }
        mTextPosition = MAX_WIDTH / 2
        mMinusBtnPosition = MAX_HEIGHT / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MAX_WIDTH, MAX_HEIGHT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mState == STATE_NONE) {
            drawBgMove(canvas)
            drawShoppingText(canvas)
        } else if (mState == STATE_MOVE) {
            drawBgMove(canvas)
        } else if (mState == STATE_MOVE_OVER) {
            mState = STATE_ROTATE
            if (mIsForward) {
                drawAddBtn(canvas)
                startRotateAnim()
            } else {
                drawBgMove(canvas)
                drawShoppingText(canvas)
                mState = STATE_NONE
                mIsForward = true
                mNum = 0
            }
        } else if (mState == STATE_ROTATE) {
            mPaintMinus!!.alpha = mAlpha
            mPaintNum!!.alpha = mAlpha
            drawMinusBtn(canvas, mAngle.toFloat())
            drawNumText(canvas)
            drawAddBtn(canvas)
        } else if (mState == STATE_ROTATE_OVER) {
            drawMinusBtn(canvas, mAngle.toFloat())
            drawNumText(canvas)
            drawAddBtn(canvas)
            if (!mIsForward) {
                startMoveAnim()
            }
        }
    }

    /**
     * 绘制移动的背景
     *
     * @param canvas 画板
     */
    private fun drawBgMove(canvas: Canvas) {
        canvas.drawArc(RectF(mWidth.toFloat(), 0f, (mWidth + MAX_HEIGHT).toFloat(), MAX_HEIGHT.toFloat()), 90f, 180f, false, mPaintBg)
        canvas.drawRect(RectF((mWidth + MAX_HEIGHT / 2).toFloat(), 0f, (MAX_WIDTH - MAX_HEIGHT / 2).toFloat(), MAX_HEIGHT.toFloat()), mPaintBg)
        canvas.drawArc(RectF((MAX_WIDTH - MAX_HEIGHT).toFloat(), 0f, MAX_WIDTH.toFloat(), MAX_HEIGHT.toFloat()), 180f, 270f, false, mPaintBg)
    }

    /**
     * 绘制购物车文案
     *
     * @param canvas 画板
     */
    private fun drawShoppingText(canvas: Canvas) {
        canvas.drawText(mShoppingText, MAX_WIDTH / 2 - getTextWidth(mPaintText, mShoppingText) / 2f, MAX_HEIGHT / 2 + getTextHeight(mShoppingText, mPaintText) / 2f, mPaintText)
    }

    /**
     * 绘制加号按钮
     *
     * @param canvas 画板
     */
    private fun drawAddBtn(canvas: Canvas) {
        canvas.drawCircle(MAX_WIDTH - MAX_HEIGHT / 2.toFloat(), MAX_HEIGHT / 2.toFloat(), MAX_HEIGHT / 2.toFloat(), mPaintBg)
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2.toFloat(), MAX_HEIGHT / 4.toFloat(), MAX_WIDTH - MAX_HEIGHT / 2.toFloat(), MAX_HEIGHT / 4 * 3.toFloat(), mPaintText)
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2 - (MAX_HEIGHT / 4).toFloat(), MAX_HEIGHT / 2.toFloat(), MAX_WIDTH - MAX_HEIGHT / 4.toFloat(), MAX_HEIGHT / 2.toFloat(), mPaintText)
    }

    /**
     * 绘制减号按钮
     *
     * @param canvas 画板
     * @param angle  旋转角度
     */
    private fun drawMinusBtn(canvas: Canvas, angle: Float) {
        if (angle != 0f) {
            canvas.rotate(angle, mMinusBtnPosition.toFloat(), MAX_HEIGHT / 2.toFloat())
        }
        canvas.drawCircle(mMinusBtnPosition.toFloat(), MAX_HEIGHT / 2.toFloat(), MAX_HEIGHT / 2 - MAX_HEIGHT / 20.toFloat(), mPaintMinus)
        canvas.drawLine(mMinusBtnPosition - MAX_HEIGHT / 4.toFloat(), MAX_HEIGHT / 2.toFloat(), mMinusBtnPosition + MAX_HEIGHT / 4.toFloat(), MAX_HEIGHT / 2.toFloat(), mPaintMinus)
        if (angle != 0f) {
            canvas.rotate(-angle, mMinusBtnPosition.toFloat(), MAX_HEIGHT / 2.toFloat())
        }
    }

    /**
     * 绘制购买数量
     *
     * @param canvas 画板
     */
    private fun drawNumText(canvas: Canvas) {
        drawText(canvas, mNum.toString(), mTextPosition - getTextWidth(mPaintNum, mNum.toString()) / 2f, MAX_HEIGHT / 2 + getTextHeight(mNum.toString(), mPaintNum) / 2f, mPaintNum, mAngle.toFloat())
    }

    /**
     * 绘制Text带角度
     *
     * @param canvas 画板
     * @param text   文案
     * @param x      x坐标
     * @param y      y坐标
     * @param paint  画笔
     * @param angle  旋转角度
     */
    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint?, angle: Float) {
        if (angle != 0f) {
            canvas.rotate(angle, x, y)
        }
        canvas.drawText(text, x, y, paint)
        if (angle != 0f) {
            canvas.rotate(-angle, x, y)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mState == STATE_NONE) {
                    mNum++
                    startMoveAnim()
                    if (mShoppingClickListener != null) {
                        mShoppingClickListener!!.onAddClick(mNum)
                    }
                } else if (mState == STATE_ROTATE_OVER) {
                    if (isPointInCircle(PointF(event.x, event.y), PointF((MAX_WIDTH - MAX_HEIGHT / 2).toFloat(), (MAX_HEIGHT / 2).toFloat()), MAX_HEIGHT / 2.toFloat())) {
                        if (mNum > 0) {
                            mNum++
                            mIsForward = true
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener!!.onAddClick(mNum)
                            }
                        }
                        invalidate()
                    } else if (isPointInCircle(PointF(event.x, event.y), PointF((MAX_HEIGHT / 2).toFloat(), (MAX_HEIGHT / 2).toFloat()), MAX_HEIGHT / 2.toFloat())) {
                        if (mNum > 1) {
                            mNum--
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener!!.onMinusClick(mNum)
                            }
                            invalidate()
                        } else {
                            if (mShoppingClickListener != null) {
                                mShoppingClickListener!!.onMinusClick(0)
                            }
                            mState = STATE_ROTATE
                            mIsForward = false
                            startRotateAnim()
                        }
                    }
                }
                return true
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 开始移动动画
     */
    private fun startMoveAnim() {
        mState = STATE_MOVE
        val valueAnimator: ValueAnimator
        valueAnimator = if (mIsForward) {
            ValueAnimator.ofInt(0, MAX_WIDTH - MAX_HEIGHT)
        } else {
            ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT, 0)
        }
        valueAnimator.duration = mDuration.toLong()
        valueAnimator.addUpdateListener { valueAnimator ->
            mWidth = valueAnimator.animatedValue as Int
            if (mIsForward) {
                if (mWidth == MAX_WIDTH - MAX_HEIGHT) {
                    mState = STATE_MOVE_OVER
                }
            } else {
                if (mWidth == 0) {
                    mState = STATE_MOVE_OVER
                }
            }
            invalidate()
        }
        valueAnimator.start()
    }

    /**
     * 开始旋转动画
     */
    private fun startRotateAnim() {
        val animatorList: MutableCollection<Animator> = ArrayList()
        val animatorTextRotate: ValueAnimator
        animatorTextRotate = if (mIsForward) {
            ValueAnimator.ofInt(0, 360)
        } else {
            ValueAnimator.ofInt(360, 0)
        }
        animatorTextRotate.duration = mDuration.toLong()
        animatorTextRotate.addUpdateListener { valueAnimator ->
            mAngle = valueAnimator.animatedValue as Int
            if (mIsForward) {
                if (mAngle == 360) {
                    mState = STATE_ROTATE_OVER
                }
            } else {
                if (mAngle == 0) {
                    mState = STATE_ROTATE_OVER
                }
            }
        }
        animatorList.add(animatorTextRotate)
        val animatorAlpha: ValueAnimator
        animatorAlpha = if (mIsForward) {
            ValueAnimator.ofInt(0, 255)
        } else {
            ValueAnimator.ofInt(255, 0)
        }
        animatorAlpha.duration = mDuration.toLong()
        animatorAlpha.addUpdateListener { valueAnimator ->
            mAlpha = valueAnimator.animatedValue as Int
            if (mIsForward) {
                if (mAlpha == 255) {
                    mState = STATE_ROTATE_OVER
                }
            } else {
                if (mAlpha == 0) {
                    mState = STATE_ROTATE_OVER
                }
            }
        }
        animatorList.add(animatorAlpha)
        val animatorTextMove: ValueAnimator
        animatorTextMove = if (mIsForward) {
            ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_WIDTH / 2)
        } else {
            ValueAnimator.ofInt(MAX_WIDTH / 2, MAX_WIDTH - MAX_HEIGHT / 2)
        }
        animatorTextMove.duration = mDuration.toLong()
        animatorTextMove.addUpdateListener { valueAnimator ->
            mTextPosition = valueAnimator.animatedValue as Int
            if (mIsForward) {
                if (mTextPosition == MAX_WIDTH / 2) {
                    mState = STATE_ROTATE_OVER
                }
            } else {
                if (mTextPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                    mState = STATE_ROTATE_OVER
                }
            }
        }
        animatorList.add(animatorTextMove)
        val animatorBtnMove: ValueAnimator
        animatorBtnMove = if (mIsForward) {
            ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2)
        } else {
            ValueAnimator.ofInt(MAX_HEIGHT / 2, MAX_WIDTH - MAX_HEIGHT / 2)
        }
        animatorBtnMove.duration = mDuration.toLong()
        animatorBtnMove.addUpdateListener { valueAnimator ->
            mMinusBtnPosition = valueAnimator.animatedValue as Int
            if (mIsForward) {
                if (mMinusBtnPosition == MAX_HEIGHT / 2) {
                    mState = STATE_ROTATE_OVER
                }
            } else {
                if (mMinusBtnPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                    mState = STATE_ROTATE_OVER
                }
            }
            invalidate()
        }
        animatorList.add(animatorBtnMove)
        val animatorSet = AnimatorSet()
        animatorSet.duration = mDuration.toLong()
        animatorSet.playTogether(animatorList)
        animatorSet.start()
    }

    /**
     * 购买数量
     * @param num 购买数量
     */
    var textNum
        get() = mNum
        set(value) {
            mNum = value
            mState = STATE_ROTATE_OVER
            invalidate()
        }

    fun setOnShoppingClickListener(shoppingClickListener: ShoppingClickListener?) {
        mShoppingClickListener = shoppingClickListener
    }

    interface ShoppingClickListener {
        fun onAddClick(num: Int)
        fun onMinusClick(num: Int)
    }

    /**
     * 判断点是否在圆内
     *
     * @param pointF 待确定点
     * @param circle 圆心
     * @param radius 半径
     * @return true在圆内
     */
    private fun isPointInCircle(pointF: PointF, circle: PointF, radius: Float): Boolean {
        return Math.pow((pointF.x - circle.x).toDouble(), 2.0) + Math.pow((pointF.y - circle.y).toDouble(), 2.0) <= Math.pow(radius.toDouble(), 2.0)
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    //获取Text高度
    private fun getTextHeight(str: String?, paint: Paint?): Int {
        val rect = Rect()
        paint!!.getTextBounds(str, 0, str!!.length, rect)
        return (rect.height() / 33f * 29).toInt()
    }

    //获取Text宽度
    private fun getTextWidth(paint: Paint?, str: String?): Int {
        var iRet = 0
        if (str != null && str.length > 0) {
            val len = str.length
            val widths = FloatArray(len)
            paint!!.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    companion object {
        private const val STATE_NONE = 0
        private const val STATE_MOVE = 1
        private const val STATE_MOVE_OVER = 2
        private const val STATE_ROTATE = 3
        private const val STATE_ROTATE_OVER = 4
        private const val DEFAULT_DURATION = 250
        private const val DEFAULT_SHOPPING_TEXT = "加入购物车"
    }

    init {
        init(attrs)
    }
}
