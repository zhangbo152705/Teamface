package com.hjhq.teamface.customcomponent.widget2;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.widget2.reference.ReferenceView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseView {
    protected int code = this.hashCode() % 10000;
    protected View mView;
    protected String whoIsYouFather;

    protected String type;

    protected boolean needConceal = false;

    protected String title;

    protected String parentName = "";

    protected String defaultValue;

    protected String pointOut;

    protected String fieldControl;

    protected String keyName;

    private String addView;

    private String editView;

    private String detailView;

    protected String terminalApp;

    List<SubfieldView> mViewList = new ArrayList<>();
    protected CustomBean bean;


    protected boolean isLinkage;

    protected String subFormName;

    protected int subFormIndex = -1;//zzh->ad:默认值由0改为-1

    protected String referenceDataName;

    protected String referenceValue;

    protected String structure = "0";

    protected String numberType;


    protected int state;

    protected int stateEnv;
    protected boolean isHidenField;
    //被下拉组件控制显示/隐藏
    public boolean controlHide = false;
    protected int aHashCode;

    public BaseView(CustomBean bean) {
        if (bean == null) {
            return;
        }
        this.bean = bean;
        this.type = bean.getType();
        this.title = bean.getLabel();
        this.keyName = bean.getName();
        this.needConceal = bean.isNeedConceal();
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            pointOut = field.getPointOut();
            defaultValue = field.getDefaultValue();
            fieldControl = field.getFieldControl();
            addView = field.getAddView();
            editView = field.getEditView();
            detailView = field.getDetailView();
            terminalApp = field.getTerminalApp();
            structure = field.getStructure();
            numberType = field.getNumberType();
        }
    }

    public void setState(int state) {
        this.state = state;

    }

    public int getStateEnv() {
        return stateEnv;
    }

    public void setStateEnv(int stateEnv) {
        this.stateEnv = stateEnv;
    }

    public String getWhoIsYouFather() {
        return whoIsYouFather;
    }

    public void setWhoIsYouFather(String whoIsYouFather) {
        this.whoIsYouFather = whoIsYouFather;
    }


    protected abstract void setData(Object value);


    public void addView(LinearLayout parent, Activity activity) {
        aHashCode = activity.hashCode();
        RxManager.$(aHashCode).on(keyName, this::setData);
        //联动
        if (keyName.startsWith(CustomConstants.SUBFORM + "_")) {
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, o -> {
                if (BaseView.this instanceof PickListView) {
                    ((PickListView) this).setValue(o);
                } else {
                    Log.e("baseview","setdata"+JSONObject.toJSONString(o));
                    setData(o);
                }
            });
        } else {
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, o -> {
                if (BaseView.this instanceof PickListView) {
                    ((PickListView) this).setValue(o);
                } else {
                    Log.e("baseview","setdata"+JSONObject.toJSONString(o));
                    setData(o);
                }
            });
        }

        int childCount = parent.getChildCount();
        addView(parent, activity, childCount);
    }


    public abstract void addView(LinearLayout parent, Activity activity, int index);

    public void delView(LinearLayout parent) {
        parent.removeView(mView);
        mView = null;
    }

    public void setVisibility(int visibility) {
        if (mView == null) {
            return;
        }
        mView.setVisibility(visibility);
    }


    protected void setTitle(TextView textView, String title) {
        if (title == null) {
            title = "";
        }
        if (CustomConstants.FIELD_MUST.equals(fieldControl) && (CustomConstants.ADD_STATE == state || CustomConstants.EDIT_STATE == state)) {
            title = String.format("<font color='#F94C4A'>*</font>%s", title);
            textView.setText(Html.fromHtml(title));
            return;
        }
        TextUtil.setText(textView, title);

    }


    public View getView() {
        return mView;
    }

    public int getCode() {
        return code;
    }


    public String getTitle() {
        return title;
    }


    public abstract void put(JSONObject jsonObj);


    public abstract boolean checkNull();


    public abstract boolean formatCheck();

    public String getFieldControl() {
        return fieldControl;
    }

    public String getKeyName() {
        return keyName;
    }

    public CustomBean getBean() {
        return bean;
    }

    protected RxAppCompatActivity getContext() {
        return (RxAppCompatActivity) mView.getContext();
    }


    public void setStateVisible() {
        if (CustomConstants.ADD_STATE == state) {
            setVisibility("1".equals(addView) ? View.VISIBLE : View.GONE);
        } else if (CustomConstants.EDIT_STATE == state) {
            setVisibility("1".equals(editView) ? View.VISIBLE : View.GONE);
        }else if (CustomConstants.DETAIL_STATE == state){//zzh:增加详情状态显示判断 detailView:为时不显示,否则显示
            setVisibility("0".equals(detailView) ? View.GONE : View.VISIBLE);
        }
        if (stateEnv == CustomConstants.DETAIL_STATE || stateEnv == CustomConstants.APPROVE_DETAIL_STATE) {
            setVisibility(View.VISIBLE);
        }
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
        isHidenField = View.VISIBLE == mView.getVisibility();
        // Log.e("setStateVisible", keyName + ">>>>>" + title + ">>>>>" + (mView.getVisibility() == View.VISIBLE) + ">>>>>Value=" + JSONObject.toJSONString(bean.getValue()));
    }

    public boolean getVisibility() {
        return mView.getVisibility() == View.VISIBLE;
    }


    public void setHidenFieldVisible(boolean visible) {
        mView.setVisibility(visible ? View.VISIBLE : View.GONE);
        controlHide = !visible;
        //Log.e("setHidenFieldVisible", keyName + ">>>>>" + title + ">>>>>" + (mView.getVisibility() == View.VISIBLE) + "");
        // setStateVisible();
    }

    public boolean isHidenField() {
        return isHidenField;
    }


    protected boolean isDetailState() {
        return state == CustomConstants.DETAIL_STATE
                || (state == CustomConstants.APPROVE_DETAIL_STATE && CustomConstants.FIELD_READ.equals(fieldControl));
    }


    public void setSubFormInfo(String keyName, int index) {
        this.subFormName = keyName;
        this.subFormIndex = index;
        RxManager.$(aHashCode).on(this.keyName + subFormIndex, this::setData);
        RxManager.$(aHashCode).on(this.keyName + CustomConstants.LINKAGE_TAG + subFormIndex, o -> {
            if (this instanceof PickListView) {
                ((PickListView) this).setValue(o);
            } else {
                setData(o);
            }
        });
        RxManager.$(aHashCode).on(this.keyName + CustomConstants.SRT_REFERENCE_DATA + subFormIndex, tag->{
            ReferDataTempResultBean.DataListBean checkItem = (ReferDataTempResultBean.DataListBean) tag;
            if (this instanceof ReferenceView) {
                ((ReferenceView) this).setReferenceData(checkItem);
            }
        });
    }

    public String getDefaultValue() {
        return defaultValue;
    }


    public void setLinkage() {
        isLinkage = true;
    }


    protected void linkageData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bean", bean.getModuleBean());
        

        JSONObject valueJson = new JSONObject();
        if (BaseView.this instanceof ReferenceView) {
            valueJson.put("name", referenceDataName);
            valueJson.put("id", referenceValue);
            
            jsonObject.put(keyName, valueJson);
        } else {
            put(valueJson);
            
            jsonObject.put(keyName, valueJson.get(keyName));
        }
        if (!TextUtil.isEmpty(subFormName)) {
            jsonObject.put("subform", subFormName);
            jsonObject.put("currentSubIndex", subFormIndex);
        }
        jsonObject.put("key", keyName);
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_MULTI_LINKAGE_CODE, jsonObject);


    }


    public void setParentName(String title) {
        parentName = title;
    }
}

