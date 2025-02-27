package com.hjhq.teamface.common.view.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hjhq.teamface.common.R;


/**
 * 页码显示类
 *
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class PageShowView extends View {

    int colorCurrent = 0;

    int colorOther = 0;

    int total = 0;

    int current = 0;

    private Paint mPaint = null;

    public PageShowView(Context context) {
        this(context, null);
    }

    public PageShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColor();
    }

    protected void initColor() {
        colorCurrent = getResources().getColor(R.color.white);

        colorOther = getResources().getColor(R.color.gray_b2);

        mPaint = new Paint();
    }

    public void setCurrentView(int current, int total) {
        this.current = current;
        this.total = total;
        invalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

        int view_height = getHeight() - getPaddingBottom() - getPaddingBottom();

        int view_width = getWidth() - getPaddingLeft() - getPaddingRight();

        int height = view_height / 2;

        int width = height * 3;

        if (total > 1) {

            if (width * total + height * (total - 1) > view_width) {
                width = (view_width - (height * (total - 1))) / total;
            }

            int posX = view_width / 2 - (width * total + height * (total - 1) * 3) / 2;

            mPaint.setStrokeWidth(height);

            for (int i = 0; i < total; i++) {
                if (i != current) {
                    mPaint.setColor(colorOther);
                } else {
                    mPaint.setColor(colorCurrent);
                }

                // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.state_unselect);
                canvas.drawLine(posX, view_height / 2, posX + width, view_height / 2, mPaint);
                // canvas.drawBitmap(bitmap, posX, view_height / 2, mPaint);
                // canvas.drawRect(getResources().getDrawable(R.drawable.state_selected).getBounds(), mPaint);
                canvas.drawCircle(posX, view_height / 2, height / 2, mPaint);
                canvas.drawCircle(posX + width, view_height / 2, height / 2, mPaint);


                posX += height * 3 + width;
            }
        }

    }

    /**
     * 获取当前显示的位置
     */
    public int getCurrent() {
        return this.current;
    }

}
