package com.hjhq.teamface.customcomponent.widget2.base;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;


public abstract class SelectCommonView extends BaseView {

    protected TextView tvTitle;
    protected LinearLayout llRoot;
    protected TextView tvContent;
    protected ImageView ivRight;
    private View llSelect;
    protected ImageView tv_icon;

    public SelectCommonView(CustomBean bean) {
        super(bean);
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            RxManager.$(aHashCode).on(keyName, this::setContent);
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
    }

    public void setContent(Object o) {
    }


    public abstract int getLayout();

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        mView = View.inflate(activity, getLayout(), null);
        parent.addView(mView);

        llSelect = mView.findViewById(R.id.ll_select);
        tvTitle = mView.findViewById(R.id.tv_title);
        llRoot = mView.findViewById(R.id.ll_content);
        tvContent = mView.findViewById(R.id.tv_content);
        ivRight = mView.findViewById(R.id.iv_right);
        tv_icon = mView.findViewById(R.id.tv_icon);
        initView();
        initOption();
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
    }


    public void initView() {
        if (CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvTitle.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
        }
        if (isDetailState()) {
            pointOut = "";
            ivRight.setVisibility(View.GONE);
            llRoot.setClickable(false);
        } else {
            if (state == CustomConstants.ADD_STATE) {
                setDefaultValue();
            }
            if (!CustomConstants.FIELD_READ.equals(fieldControl)) {
                llRoot.setOnClickListener(view -> onClick());
            } else {
                llRoot.setOnClickListener(v -> ToastUtils.showError(getContext(), "只读属性不可更改"));
            }
        }
        TextUtil.setHint(tvContent, pointOut);
        setTitle(tvTitle, title);

        Object value = bean.getValue();
        if (value != null && !"".equals(value)) {
            setData(value);
        }
    }


    public abstract void initOption();


    public void onClick() {
        SoftKeyboardUtils.hide(mView);
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
    }


    protected abstract Object getValue();


    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, getValue());
    }


    @Override
    public boolean checkNull() {
        return "".equals(getValue());
    }

    @Override
    protected abstract void setData(Object value);


    public void setDefaultValue() {
        TextUtil.setText(tvContent, defaultValue);
    }


}
