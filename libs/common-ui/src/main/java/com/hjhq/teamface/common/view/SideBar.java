package com.hjhq.teamface.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hjhq.teamface.common.R;


public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "G", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private int choose = -1;// 选中
    private Paint paint = new Paint();
    private int singleHeight = 0;

    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        singleHeight = dp2px(17f);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        singleHeight = dp2px(17f);
    }

    public SideBar(Context context) {
        super(context);
        singleHeight = dp2px(17f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int computedWidth = dp2px(14f);
        int computedHeight = singleHeight * b.length;

        setMeasuredDimension(computedWidth, computedHeight);
    }

    /**
     * 重写这个方法
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = singleHeight * b.length;// 获取对应高度
        int width = getWidth(); // 获取对应宽度
//		int singleHeight = height /26;// 获取每一个字母的高度
//		int halfwidth = (26 - b.length)/2*singleHeight;//获取剩余的一半的高度

        for (int i = 0; i < b.length; i++) {
//			右侧字体颜色
            paint.setColor(ContextCompat.getColor(getContext(), R.color.gray_69));
            // paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(dp2px(12));
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();// 重置画笔
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                // 设置右侧字母列表[A,B,C,D,E....]的背景颜色
                setBackgroundResource(R.drawable.sortlistview_sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *Selcet
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void refresh(String[] firstLetters) {
        if (firstLetters != null && firstLetters.length > 0 && "#".equals(firstLetters[0])) {
            String[] newLetters = new String[firstLetters.length];
            for (int i = 0; i < firstLetters.length; i++) {
                if (i == 0) {
                    continue;
                }
                newLetters[i - 1] = firstLetters[i];
            }
            newLetters[firstLetters.length - 1] = "#";
            this.b = newLetters;
        } else {

            this.b = firstLetters;
        }
        invalidate();
    }

}