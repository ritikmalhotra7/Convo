package com.zuck.swipe.sunbaby.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

class SunBabyLoadingView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {
    /**
     * 地平线起点坐标(lineStartX, lineStartY)，地平线长度lineLength
     */
    private var lineStartX = 0f
    private var lineStartY = 0f
    private var lineLength = 0f

    /**
     * 文字坐标点x,y值
     */
    private var textX = 0f
    private var textY = 0f

    /**
     * 太阳圆圈的半径
     */
    private var sunRadius = 0f

    /**
     * 阳光的起始坐标x,y值
     */
    private var sunshineStartX = 0.0
    private var sunshineStartY = 0.0
    private var sunshineStopX = 0.0
    private var sunshineStopY = 0.0

    /**
     * 眼睛转动的最大距离
     */
    private var maxEyesTurn = 0f

    /**
     * 眼睛转动的偏移值
     */
    private var turnOffsetX = 0f

    /**
     * 用于缓存太阳圆弧的外轮廓矩形区域顶点坐标值
     */
    private var orectLeft = 0f
    private var orectTop = 0f
    private var orectRight = 0f
    private var orectBottom = 0f
    private var once = true
    private var isDrawEyes = true
    private var offsetY = DEFAULT_OFFSET_Y.toFloat()
    private var offsetSpin = 0f
    private var offsetAngle = 0f
    private var tempOffsetY = offsetY
    private var mPaint: Paint? = null
    private var sunPaint: Paint? = null
    private var eyePaint: Paint? = null
    private var bgPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private var rectF: RectF? = null
    private fun initRes() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPaint!!.strokeWidth = 5f
        mPaint!!.color = Color.parseColor(PAINT_COLOR)
        sunPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        sunPaint!!.style = Paint.Style.STROKE
        sunPaint!!.strokeWidth = 10f
        sunPaint!!.color = Color.parseColor(PAINT_COLOR)
        eyePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        eyePaint!!.style = Paint.Style.FILL
        eyePaint!!.strokeCap = Paint.Cap.ROUND
        eyePaint!!.strokeJoin = Paint.Join.ROUND
        eyePaint!!.strokeWidth = 1f
        eyePaint!!.color = Color.parseColor(PAINT_COLOR)
        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.style = Paint.Style.FILL_AND_STROKE
        mTextPaint!!.strokeWidth = 1f
        mTextPaint!!.textSize = 20f
        mTextPaint!!.color = Color.parseColor(PAINT_COLOR)
        mTextPaint!!.textAlign = Paint.Align.CENTER
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.style = Paint.Style.FILL
        bgPaint!!.strokeCap = Paint.Cap.ROUND
        bgPaint!!.strokeJoin = Paint.Join.ROUND
        bgPaint!!.strokeWidth = 1f
        bgPaint!!.color = Color.parseColor(BG_COLOR)
        rectF = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize: Int
        val heightSize: Int
        val r = Resources.getSystem()
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_DIAMETER_SIZE.toFloat(),
                r.displayMetrics
            )
                .toInt()
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_DIAMETER_SIZE.toFloat(),
                r.displayMetrics
            )
                .toInt()
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = width
        val height = height

        // 初始化地平线长度
        lineLength = width * RATIO_LINE_START_X

        // 初始化地平线起始坐标X,Y值
        lineStartX = (width - lineLength) * .5f
        lineStartY = height * RATIO_LINE_START_Y

        // 计算文字的坐标X,Y值
        textX = width * .5f
        textY =
            lineStartY + (height - lineStartY) * .5f + Math.abs(mTextPaint!!.descent() + mTextPaint!!.ascent()) * .5f

        // 计算太阳圆圈的半径
        sunRadius = (lineLength - lineLength * RATIO_ARC_START_X) * .5f

        // 计算两眼之间的距离，也是眼睛平移的最大距离
        maxEyesTurn = (sunRadius + sunPaint!!.strokeWidth * .5f) * .5f
        calcAndSetRectPoint()
        calcOffsetAngle()
        initAnimaDriver()
    }

    /**
     * 计算由于太阳升起或者落下偏移Y值所对应对的角度
     */
    private fun calcOffsetAngle() {
        offsetAngle = (Math.asin((offsetY / sunRadius).toDouble()) * 180 / Math.PI).toFloat()
    }

    /**
     * 计算太阳圆弧的外轮廓矩形区域顶点坐标值, 并设置给rectF
     */
    private fun calcAndSetRectPoint() {
        val rectLeft = lineStartX + lineLength * .5f - sunRadius
        val rectTop = lineStartY - sunRadius + offsetY
        val rectRight = lineLength - rectLeft + 2 * lineStartX
        val rectBottom = rectTop + 2 * sunRadius
        rectF!![rectLeft, rectTop, rectRight] = rectBottom
    }

    /**
     * 初始化动画驱动
     */
    private fun initAnimaDriver() {
        startSpinAnima()
        val rise1SlowAnima = initRise1Animator()
        rise1SlowAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val riseFastAnima = initRiseFastAnimator()
                riseFastAnima.start()
                riseFastAnima.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        playRisedEyesAnimator()
                        playRisedCyclingAnimator(rise1SlowAnima)
                    }
                })
            }
        })
        rise1SlowAnima.start()
    }

    /**
     * 当太阳快速升起完毕后启动第二次缓慢升起动画
     * @param rise1SlowAnima
     */
    private fun playRisedCyclingAnimator(rise1SlowAnima: ValueAnimator) {
        val rise2SlowAnima = initRise2SlowAnimator()
        rise2SlowAnima.start()
        rise2SlowAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val sinkAnima = initSinkAnimator()
                sinkAnima.start()
                sinkAnima.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        rise1SlowAnima.start()
                    }
                })
            }
        })
    }

    /**
     * 当太阳快速升起完毕后启动眨动眼睛两次动画
     */
    private fun playRisedEyesAnimator() {
        val blink2Anima = initBlink2Animator()
        blink2Anima.start()
        blink2Anima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val turnEyesRightAnima = initTurnEyesRightAnimator()
                turnEyesRightAnima.start()
                turnEyesRightAnima.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        val blink1Anima = initBlink1Animator()
                        blink1Anima.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                val turnEyesLeftAnima = initTurnEyesLeftAnimator()
                                turnEyesLeftAnima.start()
                            }
                        })
                        blink1Anima.start()
                    }
                })
            }
        })
    }

    /**
     * 初始化太阳迅速落下的动画
     * @return
     */
    private fun initSinkAnimator(): ValueAnimator {
        val endValue = DEFAULT_OFFSET_Y - tempOffsetY
        val middleValue = endValue * .5f
        orectLeft = rectF!!.left
        orectTop = rectF!!.top
        orectRight = rectF!!.right
        orectBottom = rectF!!.bottom
        val sinkAnima = ValueAnimator.ofFloat(0f, endValue)
        sinkAnima.duration = 200
        sinkAnima.interpolator = AccelerateDecelerateInterpolator()
        sinkAnima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toFloat()
            val ratioValue: Float
            if (animaValue < middleValue) {
                ratioValue = animaValue * .5f
                rectF!![orectLeft + ratioValue, orectTop + animaValue, orectRight - ratioValue] =
                    orectBottom + animaValue
            } else {
                if (once) {
                    orectLeft = rectF!!.left
                    orectTop = rectF!!.top
                    orectRight = rectF!!.right
                    orectBottom = rectF!!.bottom
                    once = false
                }
                ratioValue = (animaValue - middleValue) * .5f
                rectF!![orectLeft - ratioValue, orectTop + animaValue, orectRight + ratioValue] =
                    orectBottom + animaValue
            }
            offsetY = tempOffsetY + animaValue
            calcOffsetAngle()
            postInvalidate()
        }
        sinkAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                once = true
            }
        })
        return sinkAnima
    }

    /**
     * 初始化第二次缓慢升起的动画
     * @return
     */
    private fun initRise2SlowAnimator(): ValueAnimator {
        val rise2SlowAnima = ValueAnimator.ofFloat(0f, SUNSHINE_RISE_HEIGHT * 1.5f)
        rise2SlowAnima.duration = 3000
        rise2SlowAnima.interpolator = LinearInterpolator()
        rise2SlowAnima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toFloat()
            offsetY = tempOffsetY - animaValue
            calcAndSetRectPoint()
            calcOffsetAngle()
            postInvalidate()
        }
        rise2SlowAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                tempOffsetY = offsetY
            }
        })
        return rise2SlowAnima
    }

    /**
     * 初始化眼睛向左边转动的动画
     * @return
     */
    private fun initTurnEyesLeftAnimator(): ValueAnimator {
        val turnEyesLeftAnima = ValueAnimator.ofFloat(maxEyesTurn, 0f)
        turnEyesLeftAnima.startDelay = 800
        turnEyesLeftAnima.duration = 150
        turnEyesLeftAnima.addUpdateListener { animation ->
            turnOffsetX = animation.animatedValue.toString().toFloat()
            postInvalidate()
        }
        return turnEyesLeftAnima
    }

    /**
     * 初始化眨动一次眼睛动画
     * @return
     */
    private fun initBlink1Animator(): ValueAnimator {
        val blink1Anima = ValueAnimator.ofInt(0, 1)
        blink1Anima.interpolator = LinearInterpolator()
        blink1Anima.startDelay = 600
        blink1Anima.duration = 100
        blink1Anima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toInt()
            if (animaValue == 0) {
                isDrawEyes = false
            } else if (animaValue == 1) {
                isDrawEyes = true
            }
            postInvalidate()
        }
        return blink1Anima
    }

    /**
     * 初始化眼睛向右转动的动画
     * @return
     */
    private fun initTurnEyesRightAnimator(): ValueAnimator {
        val turnEyesRightAnima = ValueAnimator.ofFloat(0f, maxEyesTurn)
        turnEyesRightAnima.startDelay = 200
        turnEyesRightAnima.duration = 150
        turnEyesRightAnima.addUpdateListener { animation ->
            turnOffsetX = animation.animatedValue.toString().toFloat()
            postInvalidate()
        }
        return turnEyesRightAnima
    }

    /**
     * 初始化眨动两次眼睛动画
     * @return
     */
    private fun initBlink2Animator(): ValueAnimator {
        val blink2Anima = ValueAnimator.ofInt(0, 1, 0, 1)
        blink2Anima.interpolator = LinearInterpolator()
        blink2Anima.startDelay = 400
        blink2Anima.duration = 500
        blink2Anima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toInt()
            if (animaValue == 0) {
                isDrawEyes = false
            } else if (animaValue == 1) {
                isDrawEyes = true
            }
            postInvalidate()
        }
        return blink2Anima
    }

    /**
     * 初始化太阳快速升起动画
     * @return
     */
    private fun initRiseFastAnimator(): ValueAnimator {
        orectLeft = rectF!!.left
        orectTop = rectF!!.top
        orectRight = rectF!!.right
        orectBottom = rectF!!.bottom
        val endValue = SUNSHINE_RISE_HEIGHT * 2.5f
        val middleValue = endValue * .5f
        val riseFastAnima = ValueAnimator.ofFloat(0f, endValue)
        riseFastAnima.duration = 200
        riseFastAnima.interpolator = AccelerateDecelerateInterpolator()
        riseFastAnima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toFloat()
            if (animaValue < middleValue) {
                rectF!![orectLeft - animaValue, orectTop + animaValue, orectRight + animaValue] =
                    orectBottom - animaValue
            } else {
                if (once) {
                    orectLeft = rectF!!.left
                    orectTop = rectF!!.top
                    orectRight = rectF!!.right
                    orectBottom = rectF!!.bottom
                    once = false
                }
                rectF!![orectLeft + (animaValue - middleValue), orectTop - (animaValue - middleValue), orectRight - (animaValue - middleValue)] =
                    orectBottom + (animaValue - middleValue)
            }
            offsetY = tempOffsetY - animaValue
            calcOffsetAngle()
            postInvalidate()
        }
        riseFastAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                tempOffsetY = offsetY
                once = true
            }
        })
        return riseFastAnima
    }

    /**
     * 初始化太阳第一次缓慢升起动画
     * @return
     */
    private fun initRise1Animator(): ValueAnimator {
        val rise1SlowAnima = ValueAnimator.ofFloat(0f, SUNSHINE_RISE_HEIGHT)
        rise1SlowAnima.duration = 2500
        rise1SlowAnima.interpolator = LinearInterpolator()
        rise1SlowAnima.addUpdateListener { animation ->
            val animaValue = animation.animatedValue.toString().toFloat()
            offsetY = DEFAULT_OFFSET_Y - animaValue
            calcAndSetRectPoint()
            calcOffsetAngle()
            postInvalidate()
        }
        rise1SlowAnima.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                tempOffsetY = offsetY
            }
        })
        return rise1SlowAnima
    }

    /**
     * 启动阳光旋转动画
     */
    private fun startSpinAnima() {
        val spinAnima = ValueAnimator.ofFloat(0f, 360f)
        spinAnima.repeatCount = -1
        spinAnima.duration = (24 * 1000).toLong()
        spinAnima.interpolator = LinearInterpolator()
        spinAnima.addUpdateListener { animation ->
            offsetSpin = animation.animatedValue.toString().toFloat()
            postInvalidate()
        }
        spinAnima.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(lineStartX, lineStartY, lineStartX + lineLength, lineStartY, mPaint!!)
        canvas.drawArc(rectF!!, -180 + offsetAngle, 180 - offsetAngle * 2, false, sunPaint!!)
        if (isDrawEyes) drawSunEyes(canvas)
        drawSunshine(canvas)
        drawUnderLineView(canvas)
    }

    private fun drawUnderLineView(canvas: Canvas) {
        canvas.save()
        canvas.drawRect(
            0f, lineStartY + mPaint!!.strokeWidth * .5f, width.toFloat(), height.toFloat(),
            bgPaint!!
        )
        canvas.drawText("Happy Chatting", textX, textY, mTextPaint!!)
        canvas.restore()
    }

    private fun drawSunshine(canvas: Canvas) {
        var a = 0
        while (a <= 360) {
            sunshineStartX =
                Math.cos(Math.toRadians((a + offsetSpin).toDouble())) * (sunRadius + SPACE_SUNSHINE + sunPaint!!.strokeWidth) + width * .5f
            sunshineStartY =
                Math.sin(Math.toRadians((a + offsetSpin).toDouble())) * (sunRadius + SPACE_SUNSHINE + sunPaint!!.strokeWidth) + offsetY + lineStartY
            sunshineStopX =
                Math.cos(Math.toRadians((a + offsetSpin).toDouble())) * (sunRadius + SPACE_SUNSHINE + SUNSHINE_LINE_LENGTH + sunPaint!!.strokeWidth) + width * .5f
            sunshineStopY =
                Math.sin(Math.toRadians((a + offsetSpin).toDouble())) * (sunRadius + SPACE_SUNSHINE + SUNSHINE_LINE_LENGTH + sunPaint!!.strokeWidth) + offsetY + lineStartY
            if (sunshineStartY <= lineStartY && sunshineStopY <= lineStartY) {
                canvas.drawLine(
                    sunshineStartX.toFloat(),
                    sunshineStartY.toFloat(),
                    sunshineStopX.toFloat(), sunshineStopY.toFloat(), mPaint!!
                )
            }
            a += SUNSHINE_SEPARATIO_ANGLE.toInt()
        }
    }

    private fun drawSunEyes(canvas: Canvas) {
        val lcx = width * .5f - (sunRadius + sunPaint!!.strokeWidth * .5f) * .5f + turnOffsetX
        val lcy = lineStartY + offsetY - SUN_EYES_RADIUS
        if (lcy + SUN_EYES_RADIUS >= lineStartY) return
        val rcx = width * .5f + turnOffsetX
        canvas.drawCircle(lcx, lcy, SUN_EYES_RADIUS, eyePaint!!)
        canvas.drawCircle(rcx, lcy, SUN_EYES_RADIUS, eyePaint!!)
    }

    companion object {
        private const val TAG = "SunBaby"

        /**
         * 默认宽高
         */
        private const val DEFAULT_DIAMETER_SIZE = 120

        /**
         * 直线起始位置的比率，以View的宽为参照
         */
        private const val RATIO_LINE_START_X = 5 / 6f

        /**
         * 直线起始位置的比率，以View的高为参照
         */
        private const val RATIO_LINE_START_Y = 3 / 4f

        /**
         * 太阳圆弧起始位置的比率，以地平线的宽为参照
         */
        private const val RATIO_ARC_START_X = 2 / 5f

        /**
         * 太阳光芒之间间隔的角度
         */
        private const val SUNSHINE_SEPARATIO_ANGLE = 45f

        /**
         * 画笔的颜色
         */
        private const val PAINT_COLOR = "#7A6021"

        /**
         * 背景色
         */
        private const val BG_COLOR = "#F4C042"

        /**
         * 太阳圆弧与光芒的空隙间距
         */
        private const val SPACE_SUNSHINE = 12f

        /**
         * 太阳光芒的长度
         */
        private const val SUNSHINE_LINE_LENGTH = 15f

        /**
         * 太阳升起高度基准值
         */
        private const val SUNSHINE_RISE_HEIGHT = 12f

        /**
         * 太阳眼睛的半径
         */
        private const val SUN_EYES_RADIUS = 6f

        /**
         * 默认偏移量
         */
        private const val DEFAULT_OFFSET_Y = 20
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.parseColor(BG_COLOR))
        initRes()
    }
}