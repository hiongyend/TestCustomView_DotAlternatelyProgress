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
 * Desc TODO
 */

public class DotAlternatelyView extends View {
    private final String TAG = this.getClass().getSimpleName();
    private Paint mDarkPaint = new Paint();
    private Paint mLightPaint = new Paint();
    private int mDarkColor;
    private int mLightColor;
    private int mDotRadius;
    private int mDotSpacing;
    private float mChange;
    private final int DOT_STATUS_RIGHT = 0X101;
    private final int DOT_STATUS_LEFT = 0X102;
    private int mDotChangeStatus = DOT_STATUS_RIGHT;
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
        mDarkColor = Attributes.getColor(R.styleable.DotAlternatelyView_dot_dark_color, ContextCompat.getColor(getContext(),R.color.colorPrimary));
        mLightColor = Attributes.getColor(R.styleable.DotAlternatelyView_dot_light_color, ContextCompat.getColor(getContext(),R.color.colorAccent));
        mDotRadius = Attributes.getDimensionPixelSize(R.styleable.DotAlternatelyView_dot_radius,DensityUtils.dp2px(getContext(),3));
        mDotSpacing = Attributes.getDimensionPixelSize(R.styleable.DotAlternatelyView_dot_spacing,DensityUtils.dp2px(getContext(),6));
    }
    /**
     * 初始化
     */
    private void init() {
        mDarkPaint.setColor(mDarkColor);
        mDarkPaint.setAntiAlias(true);
        mDarkPaint.setStyle(Paint.Style.FILL);
        mLightPaint.setColor(mLightColor);
        mLightPaint.setAntiAlias(true);
        mLightPaint.setStyle(Paint.Style.FILL);
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

        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY widthSize="+widthSize);
        } else {
            //指定最小宽度所有圆点加上间距的宽度, 以最小半径加上间距算总和再加上最左边和最右边变大后的距离
            width = (mDotRadius * 2) * 2 + mDotSpacing;
            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY widthSize="+widthSize+" width="+width);
            if(widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST width="+width);
            }

        }

        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
            Log.e(TAG, "onMeasure MeasureSpec.EXACTLY heightSize="+heightSize);
        } else {
            height = mDotRadius * 2;
            Log.e(TAG, "onMeasure no MeasureSpec.EXACTLY heightSize="+heightSize+" height="+height);
            if(heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
                Log.e(TAG, "onMeasure MeasureSpec.AT_MOST height="+height);
            }

        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startPointX = getWidth() / 2 - (2 * mDotRadius * 2 + mDotSpacing) / 2 + mDotRadius;
        int startPointY = getHeight() / 2;
        if(mDotChangeStatus == DOT_STATUS_RIGHT) {
            mChange += 1.1f;
        } else {
            mChange -= 1.1f;
        }
        if(mChange >= mDotRadius * 2 + mDotSpacing && mDotChangeStatus == DOT_STATUS_RIGHT) {
            mDotChangeStatus = DOT_STATUS_LEFT;
            mChange = mDotRadius * 2 + mDotSpacing;
        } else if(mChange <= 0 && mDotChangeStatus == DOT_STATUS_LEFT) {
            mDotChangeStatus = DOT_STATUS_RIGHT;
            mChange = 0;
        }
        canvas.drawCircle(startPointX + mChange, startPointY, mDotRadius, mDarkPaint);
        canvas.drawCircle(startPointX + mDotRadius * 2 - mChange + mDotSpacing, startPointY, mDotRadius, mLightPaint);

        invalidate();
    }
}
