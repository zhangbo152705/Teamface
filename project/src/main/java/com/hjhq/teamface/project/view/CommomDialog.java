package com.hjhq.teamface.project.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hjhq.teamface.project.R;

/**
 * Created by Administrator on 2019/5/13.
 */

public class CommomDialog extends Dialog implements View.OnClickListener{
    public EditText contentTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private AppCompatActivity mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private int type;

    public CommomDialog(AppCompatActivity context) {
        super(context);
        this.mContext = context;
    }

    public CommomDialog(AppCompatActivity context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommomDialog(AppCompatActivity context, int themeResId, String content,int type, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
        this.type = type;
    }

    protected CommomDialog(AppCompatActivity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommomDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CommomDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommomDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_commom_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentTxt = (EditText) findViewById(R.id.content);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        if (!TextUtils.isEmpty(content)){
            contentTxt.setText(content);
        }

        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = (int)d.getWidth();
        this.getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
            if (listener != null) {
                listener.onClick(this, false,type);
            }
            this.dismiss();
        }else if(v.getId() == R.id.submit){
                if(listener != null){
                    listener.onClick(this, true,type);
                }
        }
    }

    public interface OnCloseListener{
        void onClick(CommomDialog dialog, boolean confirm,int type);
    }
}
