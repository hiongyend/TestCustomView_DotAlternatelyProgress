package com.kincai.testcustomview_dotalternatelyprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Copyright (C) 2015 The KINCAI Open Source Project
 * .
 * Create By KINCAI
 * .
 * Time 2017-06-16 21:44
 * .
 * Desc 两个源点来回移动
 */

public class DotAlternatelyView extends View {
    private final String TAG = this.getClass().getSimpleName();
    private Paint mPaint = new Paint();
    /**
     * 可视为左边圆点颜色值
     */
    private int mLeftColor;
    /**
     * 可视为右边圆点颜色值
     */
    private int mRightColor;
    /**
     * 圆点半径
     */
    private int mDotRadius;
    /**
     * 圆点间距
     */
    private int mDotSpacing;
    /**
     * 圆点位移量
     */
    private float mMoveDistance;
    /**
     * 圆点移动率
     */
    private float mMoveRate;
    /**
     * 以刚开始左边圆点为准 向右移
     */
    private final int DOT_STATUS_RIGHT = 0X101;
    /**
     * 以刚开始左边圆点为准 圆点移动方向-向左移
     */
    private final int DOT_STATUS_LEFT = 0X102;
    /**
     * 以刚开始左边圆点为准，圆点移动方向
     */
    private int mDotChangeStatus = DOT_STATUS_RIGHT;
    /**
     * 圆点透明度变化最大(也就是透明度在255-mAlphaChangeTotal到255之间)
     */
    private int mAlphaChangeTotal = 100;
    /**
     * 透明度变化率
     */
    private float mAlphaChangeRate;
    /**
     * 透明度改变量
     */
    private float mAlphaChange;

    public DotAlternatelyView(Context context) {
        this(context, null);
    }

    public DotAlternatelyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotAlternatelyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotAlternatelyView, defStyleAttr, 0);
        initAttributes(typedArray);
        typedArray.recycle();
        init();
    }

    private void initAttributes(TypedArray Attributes) {
        mLeftColor = Attributes.getColor(R.styleable.DotAlternatelyView_dot_dark_color, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mRightColor = Attributes.getColor(R.styleable.DotAlternatelyView_dot_light_color, ContextCompat.getColor(getContext(), R.color.colorAccent));
        mDotRadius = Attributes.getDimensionPixelSize(R.styleable.DotAlternatelyView_dot_radius, DensityUtils.dp2px(getContext(), 3));
        mDotSpacing = Attributes.getDimensionPixelSize(R.styleable.DotAlternatelyView_dot_spacing, DensityUtils.dp2px(getContext(), 6));
        mMoveRate = Attributes.getFloat(R.styleable.DotAlternatelyView_dot_move_rate, 1.2f);
    }

    /**
     * 初始化
     */
    private void init() {
        //移动总距离/移动率 = alpha总变化/x
        //x = 移动率 * alpha总变化 / 移动总距离
        mAlphaChangeRate = mMoveRate * mAlphaChangeTotal / (mDotRadius * 2 + mDotSpacing);
        mPaint.setColor(mLeftColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        Log.e(TAG, " aaaa " + mAlphaChangeRate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量宽高
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY widthSize=" + widthSize);
        } else {
            //指定最小宽度所有圆点加上间距的宽度, 以最小半径加上间距算总和再加上最左边和最右边变大后的距离
            width = (mDotRadius * 2) * 2 + mDotSpacing;
            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY widthSize=" + widthSize + " width=" + width);
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST width=" + width);
            }

        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY heightSize=" + heightSize);
        } else {
            height = mDotRadius * 2;
            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY heightSize=" + heightSize + " height=" + height);
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST height=" + height);
            }

        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //左边圆点起点x轴
        int startPointX = getWidth() / 2 - (2 * mDotRadius * 2 + mDotSpacing) / 2 + mDotRadius;
        //左边圆点起点y轴
        int startPointY = getHeight() / 2;
        //向右移 位移要增加对应透明度变化量也需要增加 反之都需要减小
        if (mDotChangeStatus == DOT_STATUS_RIGHT) {
            mMoveDistance += mMoveRate;
            mAlphaChange += mAlphaChangeRate;
        } else {
            mAlphaChange -= mAlphaChangeRate;
            mMoveDistance -= mMoveRate;
        }
        Log.e(TAG, "mAlphaChange " + mAlphaChange);
        //当移动到最右 那么需要改变方向 反过来
        if (mMoveDistance >= mDotRadius * 2 + mDotSpacing && mDotChangeStatus == DOT_STATUS_RIGHT) {
            mDotChangeStatus = DOT_STATUS_LEFT;
            mMoveDistance = mDotRadius * 2 + mDotSpacing;
            mAlphaChange = mAlphaChangeTotal;
        } else if (mMoveDistance <= 0 && mDotChangeStatus == DOT_STATUS_LEFT) { //当移动到最座 那么需要改变方向 反过来
            mDotChangeStatus = DOT_STATUS_RIGHT;
            mMoveDistance = 0f;
            mAlphaChange = 0f;
        }

        //因为两个圆点可能会给定不同的颜色来显示 所以提供两种颜色设置mLeftColor和mRightColor
        mPaint.setColor(mLeftColor);
        mPaint.setAlpha((int) (255 - mAlphaChange));
        canvas.drawCircle(startPointX + mMoveDistance, startPointY, mDotRadius, mPaint);
        mPaint.setColor(mRightColor);
        mPaint.setAlpha((int) (255 - mAlphaChange));
        canvas.drawCircle(startPointX + mDotRadius * 2 - mMoveDistance + mDotSpacing, startPointY, mDotRadius, mPaint);

        invalidate();
    }
}
