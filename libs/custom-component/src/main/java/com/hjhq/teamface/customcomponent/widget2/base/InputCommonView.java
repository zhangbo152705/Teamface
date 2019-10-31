package com.hjhq.teamface.customcomponent.widget2.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.InputFocusInterface;
import com.hjhq.teamface.customcomponent.widget2.input.LocationInputView;

import java.util.LinkedHashMap;


public abstract class InputCommonView extends BaseView implements InputFocusInterface {
    protected TextView tvTitle;
    protected LinearLayout llRoot;
    protected EditText etInput;
    private View llInput;
    protected ImageView ivLeft;
    protected ImageView ivRight;
    public Activity mContext;
    private InputMethodManager imm;

    public InputCommonView(CustomBean bean) {
        super(bean);
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
                        RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
                        RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
                        RxManager.$(aHashCode).on(keyName, this::setContent);
                        RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
    }


    public abstract int getLayout();

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        mView = View.inflate(activity, getLayout(), null);
        mContext = (Activity) mView.getContext();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        parent.addView(mView);
        tvTitle = mView.findViewById(R.id.tv_title);
        llRoot = mView.findViewById(R.id.ll_content);

        llInput = mView.findViewById(R.id.ll_input);
        etInput = mView.findViewById(R.id.et_input);
        ivLeft = mView.findViewById(R.id.iv_left);
        ivRight = mView.findViewById(R.id.iv_right);

        initView();
        initOption();
        RxManager.$(aHashCode).on(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, o -> {
            clearFocus();
        });
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
                        RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
                        RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
                        RxManager.$(aHashCode).on(keyName, this::setContent);
                        RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
        if (!isDetailState() && etInput != null) {
            etInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && isLinkage) {
                        linkageData();
                    }
                }
            });

            etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        etInput.setFocusable(false);
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    private void initView() {
        if (isDetailState()) {
            pointOut = "";
            etInput.setFocusable(false);
                    } else {
            if (state == CustomConstants.ADD_STATE) {
                setContent(defaultValue);
            }
            if (state == CustomConstants.DETAIL_STATE) {
                etInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
            }
            if (CustomConstants.FIELD_READ.equals(fieldControl)) {
                etInput.setFocusable(false);
                etInput.setOnClickListener(v -> ToastUtils.showError(getContext(), "只读属性不可更改"));
                tvTitle.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
                etInput.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
            } else {
                etInput.setOnFocusChangeListener((v, hasFocus) -> {

                });
                etInput.setOnClickListener(v -> {
                    etInput.setFocusable(true);                    etInput.setFocusableInTouchMode(true);                    etInput.requestFocus();                    etInput.findFocus();                    SoftKeyboardUtils.show(etInput);
                });
            }
        }
        TextUtil.setHint(etInput, pointOut);
        setTitle(tvTitle, title);


        Object value = bean.getValue();
        setData(value);
    }


    public void setContent(String content) {
        if (isDetailState() && needConceal) {
            switch (type) {
                case CustomConstants.PHONE:
                    TextUtil.setText(etInput, CustomUtil.getConcealPhone(content));
                    break;
                case CustomConstants.LOCATION:
                    TextUtil.setText(etInput, CustomUtil.getConcealLocation(content));
                    break;
                case CustomConstants.EMAIL:
                    TextUtil.setText(etInput, CustomUtil.getConcealEmailAddress(content));
                    break;
                default:
                    if (!TextUtils.isEmpty(content)) {
                        TextUtil.setText(etInput, content + "");
                    }
                    break;
            }

        } else {
            if (!TextUtils.isEmpty(content)) {
                TextUtil.setText(etInput, content + "");
            }
        }
        if (isDetailState() && TextUtils.isEmpty(content)) {
            etInput.setHint("未填写");
            etInput.setHintTextColor(getContext().getResources().getColor(R.color._999999));
            if (ivRight != null) {
                ivRight.setVisibility(View.GONE);
            }
        }

    }

    public void setContent(Object content) {
        if (content instanceof String) {

            TextUtil.setText(etInput, content + "");
            return;
        }
        if (this instanceof LocationInputView && content instanceof LinkedHashMap) {
            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) content;
            final LocationInputView locationInputView = (LocationInputView) this;
            if (map != null && map.size() > 0) {
                TextUtil.setText(etInput, map.get("value"));
                locationInputView.setLocationValue(map);
            } else {
                map = new LinkedHashMap<String, String>();
                map.put("value", "");
                map.put("lat", "");
                map.put("lng", "");
                map.put("area", "");
                TextUtil.setText(etInput, "");
                locationInputView.setLocationValue(map);
            }
        }
    }

    @Override
    protected void setData(Object value) {
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            return;
        }
        String content = value.toString();
        setContent(content);
    }


    public abstract void initOption();

    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, getValue());
    }


    @Override
    public boolean checkNull() {
        return "".equals(getValue());
    }

    public Object getValue() {
        return getContent();
    }

    public String getContent() {
        if (etInput == null) {
            return "";
        }
        return etInput.getText().toString().trim();
    }


    protected void setRepeatCheckIcon() {
        ImageView ivRight = mView.findViewById(R.id.iv_right);
        if (bean.getField() != null) {
            String repeatCheck = bean.getField().getRepeatCheck();
            boolean hasIcon = "1".equals(repeatCheck) || "2".equals(repeatCheck);
            if (hasIcon && !isDetailState()) {
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setOnClickListener(view -> repeatCheck());
            }
        }
    }


    private void repeatCheck() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, bean.getModuleBean());
        bundle.putString(Constants.DATA_TAG1, getContent());
        bundle.putString(Constants.DATA_TAG2, keyName);
        bundle.putString(Constants.DATA_TAG3, title);
        bundle.putString(Constants.DATA_TAG4, bean.getLabel());
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_REPEAT_CHECK_CODE, bundle);
    }

    protected void setLeftImage(int res) {
        if (ivLeft == null) {
            return;
        }
                ivLeft.setVisibility(View.GONE);
        ivLeft.setImageResource(res);
    }

    protected void setRightImage(int res) {
        if (ivRight == null) {
            return;
        }
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(res);
    }

    public void clearFocus() {
        if (etInput != null) {
            etInput.setFocusable(false);
        }
    }
}
