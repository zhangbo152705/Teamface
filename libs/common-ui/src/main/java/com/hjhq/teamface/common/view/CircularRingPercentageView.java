package com.hjhq.teamface.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hjhq.teamface.common.R;


public class CircularRingPercentageView extends View {
    private Paint paint;
    private int circleWidth;
    private int roundBackgroundColor;
    private int roundProgressColor;
    private int completeProgressColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private float progress = 0;
    private int[] colors = {0xffff4639, 0xffCDD513, 0xff3CDF5F};
    private int radius;
    private RectF oval;
    private Paint mPaintText;
    private int maxColorNumber = 100;
    private float singlPoint = 9;
    private float lineWidth = 0.3f;
    private int circleCenter;
    private SweepGradient sweepGradient;
    private boolean isLine;
    private boolean drawCenterText;
    private static final float GAP_DEGREE = 3f;//分割线大小（角度）

    /**
     * 分割的数量
     *
     * @param maxColorNumber 数量
     */
    public void setMaxColorNumber(int maxColorNumber) {
        this.maxColorNumber = maxColorNumber;
        singlPoint = (float) 360 / (float) maxColorNumber;
        invalidate();
    }

    /**
     * 是否是线条
     *
     * @param line true 是 false否
     */
    public void setLine(boolean line) {
        isLine = line;
        invalidate();
    }

    public int getCircleWidth() {
        return circleWidth;
    }

    public CircularRingPercentageView(Context context) {
        this(context, null);
    }

    public CircularRingPercentageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularRingPercentageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularRing);
        maxColorNumber = mTypedArray.getInt(R.styleable.CircularRing_circleNumber, 40);
        progress = 100;
        circleWidth = mTypedArray.getDimensionPixelOffset(R.styleable.CircularRing_circleWidth, getDpValue(180));
        roundBackgroundColor = mTypedArray.getColor(R.styleable.CircularRing_roundColor, 0xffdddddd);
        roundProgressColor = mTypedArray.getColor(R.styleable.CircularRing_roundProgressColor, 0xffdddddd);
        textColor = mTypedArray.getColor(R.styleable.CircularRing_circleTextColor, 0xff999999);
        roundWidth = mTypedArray.getDimension(R.styleable.CircularRing_circleRoundWidth, 40);
        completeProgressColor = mTypedArray.getColor(R.styleable.CircularRing_completeProgressColor, 0xffffec00);
        textSize = mTypedArray.getDimension(R.styleable.CircularRing_circleTextSize, getDpValue(8));
        colors[0] = mTypedArray.getColor(R.styleable.CircularRing_circleColor1, 0xffff4639);
        colors[1] = mTypedArray.getColor(R.styleable.CircularRing_circleColor2, 0xffcdd513);
        colors[2] = mTypedArray.getColor(R.styleable.CircularRing_circleColor3, 0xff3cdf5f);
        initView();
        mTypedArray.recycle();
    }


    /**
     * 空白出颜色背景
     *
     * @param roundBackgroundColor
     */
    public void setRoundBackgroundColor(int roundBackgroundColor) {
        this.roundBackgroundColor = roundBackgroundColor;
        paint.setColor(roundBackgroundColor);
        invalidate();
    }

    /**
     * 刻度字体颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaintText.setColor(textColor);
        invalidate();
    }

    /**
     * 刻度字体大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mPaintText.setTextSize(textSize);
        invalidate();
    }

    /**
     * 渐变颜色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("colors length < 2");
        }
        this.colors = colors;
        sweepGradientInit();
        invalidate();
    }


    /**
     * 间隔角度大小
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }


    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 圆环宽度
     *
     * @param roundWidth 宽度
     */
    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
        if (roundWidth > circleCenter) {
            this.roundWidth = circleCenter;
        }
        radius = (int) (circleCenter - this.roundWidth / 2); // 圆环的半径
        oval.left = circleCenter - radius;
        oval.right = circleCenter + radius;
        oval.bottom = circleCenter + radius;
        oval.top = circleCenter - radius;
        paint.setStrokeWidth(this.roundWidth);
        invalidate();
    }

    /**
     * 圆环的直径
     *
     * @param circleWidth 直径
     */
    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        circleCenter = circleWidth / 2;

        if (roundWidth > circleCenter) {
            roundWidth = circleCenter;
        }
        setRoundWidth(roundWidth);
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    /**
     * 渐变初始化
     */
    public void sweepGradientInit() {
        //渐变颜色
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    public void initView() {

        circleCenter = circleWidth / 2;//半径
        singlPoint = (float) 360 / (float) maxColorNumber;
        radius = (int) (circleCenter - roundWidth / 2); // 圆环的半径
        sweepGradientInit();
        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(roundBackgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);

        // 用于定义的圆弧的形状和大小的界限
        oval = new RectF(circleCenter - radius, circleCenter - radius, circleCenter + radius, circleCenter + radius);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (progress == 100){
            paint.setColor(completeProgressColor);//如果进度完成，用特别颜色，不画间隔线
            canvas.drawArc(oval, -90, (float) (progress * 3.6), false, paint);
            return;
        }
        
        if (progress == 0){
            paint.setColor(roundBackgroundColor);//如果进度为0，不画间隔线
            canvas.drawArc(oval, -90, (float) ((100-progress) * 3.6), false, paint);
            return;
        }
        
        //背景颜色
        paint.setColor(roundProgressColor);
        canvas.drawArc(oval, -90, (float) (progress * 3.6), false, paint);

        //绘制剩下的空白区域
        paint.setColor(roundBackgroundColor);
        canvas.drawArc(oval, -90 - GAP_DEGREE, (float) (-(100 - progress) * 3.6)+(GAP_DEGREE*2), false, paint);


        //canvas.drawText(""+progress+"%", circleCenter, circleCenter - mSearchRadius + roundWidth / 2 + getDpValue(4) + textSize, mPaintText);
//        canvas.restore();//
        //绘制文字刻度
//        for (int i = 1; i <= 10; i++) {
//            canvas.save();// 保存当前画布
//            canvas.rotate(360 / 10 * i, circleCenter, circleCenter);
//            canvas.drawText(i * 10 + "", circleCenter, circleCenter - mSearchRadius + roundWidth / 2 + getDpValue(4) + textSize, mPaintText);
//            canvas.restore();//
//        }
    }


    OnProgressScore onProgressScore;

    public interface OnProgressScore {
        void setProgressScore(float score);

    }

    public synchronized void setProgress(final float p) {
        progress = p;
        postInvalidate();
    }

    /**
     * @param p
     */
    public synchronized void setProgress(final float p, OnProgressScore onProgressScore) {
        this.onProgressScore = onProgressScore;
        progress = p;
        postInvalidate();
    }

}
