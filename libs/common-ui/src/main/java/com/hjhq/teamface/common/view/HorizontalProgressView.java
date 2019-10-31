package com.hjhq.teamface.common.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalProgressView extends View {
    /**
     * 进度条最大值
     */
    private float maxCount;
    /**
     * 进度条当前值
     */
    private float currentCount1;
    private float currentCount2;
    private float currentCount3;
    private float currentCount4;

    private float startCurrentCount;

    private int currentColor =Color.TRANSPARENT;
    /**
     * 画笔
     */
    private Paint mPaint;

    private int mWidth, mHeight;

    public HorizontalProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public HorizontalProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public HorizontalProgressView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /***
     * 设置最大的进度值
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 得到最大进度值
     */
    public double getMaxCount() {
        return maxCount;
    }

    /***
     * 设置当前的进度值
     * @param currentCount1
     */
    public void setCurrentCount(float currentCount1,float currentCount2,float currentCount3,float currentCount4) {
        this.currentCount1 = currentCount1 > maxCount ? maxCount : currentCount1;
        this.currentCount2 = currentCount1 > maxCount ? maxCount : currentCount2;
        this.currentCount3 = currentCount3 > maxCount ? maxCount : currentCount3;
        this.currentCount4 = currentCount1 > maxCount ? maxCount : currentCount4;
        invalidate();
    }

    public void setColor(int color){
        currentColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        mPaint = new Paint();
        //设置抗锯齿效果
        mPaint.setAntiAlias(true);
        //设置画笔颜色
        mPaint.setColor(Color.rgb(255, 255, 255));
        int round = mHeight / 2;
        /**
         * RectF：绘制矩形，四个参数分别是left,top,right,bottom
         * 类型是单精度浮点数
         */
        RectF rf = new RectF(0, 0, mWidth, mHeight);
        /*绘制圆角矩形，背景色为画笔颜色*/
        canvas.drawRoundRect(rf, round, round, mPaint);
        /*设置progress内部是灰色*/
        mPaint.setColor(Color.rgb(216, 216, 216));
        RectF rectBlackBg = new RectF(2, 2, mWidth - 2, mHeight - 2);
        canvas.drawRoundRect(rectBlackBg, round, round, mPaint);
        //设置进度条进度及颜色
        float start1 = 0;
        float end1 = currentCount1/maxCount;
        drawProgress(canvas,start1,end1,Color.parseColor("#3CBB81"),0);
        float start2 = currentCount1/maxCount;
        float end2 = (currentCount1+currentCount2)/maxCount;
        if (currentCount1>0 && currentCount2>0){
            drawProgress(canvas,start2,end2,Color.parseColor("#008FE5"),3);
        }else {
            drawProgress(canvas,start2,end2,Color.parseColor("#008FE5"),0);
        }

        float start3 = (currentCount1+currentCount2)/maxCount;
        float end3 = (currentCount1+currentCount2+currentCount3)/maxCount;
        if ((currentCount1+currentCount2)>0 && currentCount3>0){
            drawProgress(canvas,start3,end3,Color.parseColor("#666666"),3);
        }else {
            drawProgress(canvas,start3,end3,Color.parseColor("#666666"),0);
        }
        float start4 = (currentCount1+currentCount2+currentCount3)/maxCount;
        float end4 = (currentCount1+currentCount2+currentCount3+currentCount4)/maxCount;
        if((currentCount1+currentCount2+currentCount3)>0 && currentCount4>0){
            drawProgress(canvas,start4,end4,Color.parseColor("#F5222D"),3);
        }else {
            drawProgress(canvas,start4,end4,Color.parseColor("#F5222D"),0);
        }

    }

    public void drawProgress(Canvas canvas,float start,float end,int color,int type){
        int round = mHeight / 2;
        RectF rectProgressBg = new RectF(mWidth * start-type, 3, mWidth * end, mHeight - 3);
        mPaint.setColor(color);
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
    }

    //dip * scale + 0.5f * (dip >= 0 ? 1 : -1)
    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));//加0.5是为了四舍五入
    }

    /**
     * 指定自定义控件在屏幕上的大小,onMeasure方法的两个参数是由上一层控件
     * 传入的大小，而且是模式和尺寸混合在一起的数值，需要MeasureSpec.getMode(widthMeasureSpec)
     * 得到模式，MeasureSpec.getSize(widthMeasureSpec)得到尺寸
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //MeasureSpec.EXACTLY，精确尺寸
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        //MeasureSpec.AT_MOST，最大尺寸，只要不超过父控件允许的最大尺寸即可，MeasureSpec.UNSPECIFIED未指定尺寸
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(12);
        } else {
            mHeight = heightSpecSize;
        }
        //设置控件实际大小
        setMeasuredDimension(mWidth, mHeight);


    }

}
