package com.hjhq.teamface.customcomponent.widget2.reference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.CaptureActivity;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.ReferenceViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ReferenceView extends BaseView implements ActivityPresenter.OnActivityResult, View.OnClickListener {
    private CustomBean.RelevanceModule relevanceModule;
    public TextView tvTitle;
    private LinearLayout llRoot;
    protected TextView tvContent;
    private ImageView ivRight;
    private LinearLayout llContent;
    protected String value;
    private String referenceFieldName;
    private ReferenceViewInterface referenceViewInterface;
    private ReferDataTempResultBean.DataListBean checkItem;
    private ImageView iv_scan;//zzh:新增扫一扫控件
    private String allowScan;//是否可以扫码

    public ReferenceView(CustomBean bean) {
        super(bean);
        CustomBean.RelevanceField relevanceField = bean.getRelevanceField();
        relevanceModule = bean.getRelevanceModule();
        allowScan = bean.getField().getAllowScan();
        if (relevanceField != null) {
            referenceFieldName = relevanceField.getFieldName();
        }
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            Log.e("ReferenceView","keyName:"+keyName+subFormIndex);
            RxManager.$(aHashCode).onSticky(keyName + subFormIndex, this::setContent);
            RxManager.$(aHashCode).onSticky(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);

        } else {
            RxManager.$(aHashCode).onSticky(keyName, this::setContent);
            RxManager.$(aHashCode).onSticky(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
        //RxManager.$(aHashCode).onSticky(keyName + "clear_data", this::clearData);
        RxManager.$(aHashCode).onSticky(keyName + "clear_data",tag->{//zzh->ad:增加tag判断和当前的sunFromIndex是否相等
            if (keyName.startsWith(CustomConstants.SUBFORM)) {
                if (subFormIndex == (Integer) tag){
                    clearData(null);
                }
            }else {
                clearData(null);
            }

        });
        RxManager.$(aHashCode).onSticky("referenceclear_data",tag->{//zzh->ad:增加主表单清空子表单
            if (keyName.startsWith(CustomConstants.SUBFORM)) {
                if (tag != null && keyName.equals(tag.toString()))
                    clearData(null);
            }
        });
    }


    public void setReferenceViewInterface(ReferenceViewInterface referenceViewInterface) {
        this.referenceViewInterface = referenceViewInterface;
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int code) {
        if ("0".equals(structure)) {
            mView = View.inflate(activity, R.layout.custom_select_single_widget_layout, null);
        } else {
            mView = View.inflate(activity, R.layout.custom_select_single_widget_row_layout, null);
        }

        tvTitle = mView.findViewById(R.id.tv_title);
        ivRight = mView.findViewById(R.id.iv_right);

        llRoot = mView.findViewById(R.id.ll_content);
        tvContent = mView.findViewById(R.id.tv_content);
        llContent = mView.findViewById(R.id.ll_select);
        iv_scan = mView.findViewById(R.id.iv_scan);
        onListener();
        initView();
        parent.addView(mView);
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
    }


    protected void initView() {
        ivRight.setImageResource(R.drawable.custom_icon_reference);
        setTitle(tvTitle, title);
        setData(bean.getValue());
        llRoot.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        iv_scan.setVisibility(View.VISIBLE);
        if (isDetailState()) {
            TextUtil.setHint(tvContent, "");
            ivRight.setVisibility(View.GONE);
            if (TextUtils.isEmpty(value)) {
                tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            } else {
                tvContent.setTextColor(ColorUtils.resToColor(getContext(), R.color.app_blue));
            }
            if (TextUtil.isEmpty(value)) {
                llRoot.setClickable(false);
            }
            iv_scan.setVisibility(View.GONE);

        } else {
            if (CustomConstants.FIELD_READ.equals(fieldControl)) {
                TextUtil.setHint(tvContent, "");
                ivRight.setVisibility(View.GONE);
                llRoot.setOnClickListener(v -> ToastUtils.showError(getContext(), "只读属性不可更改"));
                iv_scan.setVisibility(View.GONE);
            } else {

                TextUtil.setHint(tvContent, pointOut);
            }
        }

        //zzh:子表单不显示扫一扫
        if (keyName.startsWith(CustomConstants.SUBFORM) || TextUtil.isEmpty(allowScan) || !allowScan.equals("1")) {
            iv_scan.setVisibility(View.GONE);
        }

    }

    private void onListener() {
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvContent.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    if (TextUtil.isEmpty(s.toString())) {
                        ivRight.setImageResource(R.drawable.custom_icon_reference);
                    } else {
                        ivRight.setImageResource(R.drawable.clear_button_red);
                    }
                }
            });

            ivRight.setOnClickListener(v -> {
                clearData(null);
                String content = tvContent.getText().toString();
                if (!TextUtil.isEmpty(content)) {
                    TextUtil.setText(tvContent, "");
                    value = "";
                    if (checkItem != null) {
                        Map<String, Object> relationField = checkItem.getRelationField();
                        if (relationField != null && relationField.size() > 0) {
                            Iterator<String> iterator = relationField.keySet().iterator();
                            while (iterator.hasNext()) {
                                String next = iterator.next();
                                RxManager.$(aHashCode).post(next, "");
                            }
                        }
                    }
                }
            });
            if (keyName.startsWith(CustomConstants.SUBFORM)) {
                RxManager.$(aHashCode).onSticky(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
            } else {
                RxManager.$(aHashCode).onSticky(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
            }
        }

    }

    public void clearData(Object object) {
        value = "";
        tvContent.setText("");
        String relyonFields = bean.getRelyonFields();
        if (!TextUtils.isEmpty(relyonFields)) {
            final String[] split = relyonFields.split(",");
            if (split != null && split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    Log.e("clearData:","keyName:"+keyName);
                    if (keyName.startsWith(CustomConstants.SUBFORM)) {
                        RxManager.$(aHashCode).post(split[i] + "clear_data", subFormIndex);//zzh->ad:tag null->tag
                    } else {
                        RxManager.$(aHashCode).post(split[i] + "clear_data", null);
                        RxManager.$(aHashCode).post("referenceclear_data", split[i]);//zzh->ad:主表单切换时其他子表单清空
                    }
                }
            }
        }
    }

    private void setContent(Object object) {
        Log.e("setContentdd", JSON.toJSONString(object));
        if (object instanceof List) {
            List<EntryBean> list = new ArrayList<>();
            if (((List) object).size() > 0) {
                final JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(((List) object).get(0)));
                value = jsonObject.getString("id");
                setContent(jsonObject.getString("name"));
            } else {
                value = "";
                setContent("");
            }
        }
        if (object == null || TextUtils.isEmpty(object + "")) {
            value = "";
            setContent("");
        }
    }


    protected void setContent(String content) {
        TextUtil.setText(tvContent, content);
    }

    @Override
    protected void setData(Object value) {
        if (isDetailState() && (value == null || TextUtil.isEmpty(value + ""))) {
            tvContent.setText("未选择");
            tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            return;
        }
        if (value instanceof String && (TextUtil.isDouble(value.toString()) ||TextUtil.isInteger(value.toString()) )) {
            if (TextUtils.isEmpty(value.toString())) {
                value = "";
            } else {
                int v = (int) TextUtil.parseDouble(value.toString());
                if (v == 0 || ((String) value).startsWith("0")) {
                    value = value+"";
                } else {
                    value = v + "";
                }
            }
            TextUtil.setText(tvContent, value.toString());
            return;
        }else if (value instanceof String){
            if (TextUtils.isEmpty(value.toString())) {
                value = "";
            } else {
                value = value + "";
            }
            TextUtil.setText(tvContent, value.toString());
            return;
        }
        try {
            Map<String, Object> map = new HashMap<>();
            if (TextUtil.isJsonArray(value.toString()) ) {//&& ((JSONArray) value).size() > 0
                JSONArray jsonArr =  JSONArray.parseArray(value.toString());
                if (jsonArr.size()>0){
                    String mapstr = jsonArr.get(0).toString();
                    if (mapstr != null){
                        map = JSON.parseObject(mapstr);
                    }
                }
               // map = (Map<String, Object>) ((JSONArray) value).get(0);
            } else if (value instanceof List && ((List) value).size() > 0) {
                map = (Map<String, Object>) ((List) value).get(0);
            }
            if (value instanceof Map) {
                map = (Map<String, Object>) value;
            }
            if (map != null && map.get("name") != null) {
                this.value = map.get("id") + "";
                TextUtil.setText(tvContent, map.get("name") + "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, value == null ? "" : value);
    }

    @Override
    public boolean checkNull() {
        return TextUtil.isEmpty(value);
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            checkItem = (ReferDataTempResultBean.DataListBean) data.getSerializableExtra(Constants.DATA_TAG1);
            if (checkItem != null) {
                setReferenceData();
            } else {
                ArrayList<ReferDataTempResultBean.DataListBean> listData = (ArrayList<ReferDataTempResultBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG2);

                Log.e("listData", JSONObject.toJSONString(listData));
                if (listData != null) {
                    if (listData.size() == 1) {
                        checkItem = listData.get(0);
                        setReferenceData();
                    } else if (listData.size() > 1) {
                        checkItem = listData.get(0);
                        setReferenceData();
                        listData.remove(0);
                        for (int i = 0; i < listData.size(); i++) {
                            JSONObject jo = new JSONObject();
                            jo.put("id", listData.get(i).getId().getValue());
                            jo.put("name", listData.get(i).getRow().get(0).getValue());
                            listData.get(i).getRelationField().put(keyName, jo);
                        }
                        Log.e("处理后数据", JSONObject.toJSONString(listData));
                        RxManager.$(aHashCode).post(subFormName + "index", subFormIndex);
                        RxManager.$(aHashCode).post(subFormName + "key", keyName);
                        RxManager.$(aHashCode).post(subFormName + "more", listData);
                    }

                }
            }

        }
    }

    public void setReferenceData(ReferDataTempResultBean.DataListBean checkItem) {
        this.checkItem = checkItem;
        Log.e("xxxsetReferenceData","checkItem");
        setReferenceData();
    }
    private void setReferenceData() {
        clearData(null);
        try {
            final String id = checkItem.getId().getValue();
            this.value = id;
            referenceValue = id;
            referenceDataName = checkItem.getRow().get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            value = "";
        }

        List<RowBean> rows = checkItem.getRow();
        for (RowBean rowBean : rows) {
            String name = rowBean.getName();
            if (referenceFieldName.equals(name)) {
                CustomUtil.setReferenceTempValue(tvContent, rowBean);
                break;
            }
        }


        Map<String, Object> relationField = checkItem.getRelationField();
        if (relationField != null && relationField.size() > 0) {
            Iterator<String> iterator = relationField.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                Object relationValue = relationField.get(next);
                if (relationValue != null) {
                    if (next.startsWith(CustomConstants.SUBFORM)) {
                        if (next.lastIndexOf("_") == 7) {
                            RxManager.$(aHashCode).post(next + "data_list", relationValue);
                        } else {
                            Log.e("子表单内的组件", next);

                            RxManager.$(aHashCode).post(next + subFormIndex, relationValue);
                        }

                    } else {
                        RxManager.$(aHashCode).post(next, relationValue);
                    }
                }
            }
        }
        if (!checkNull() && isLinkage) {
            linkageData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_content){
            clickContent("");
        }else if (v.getId() == R.id.iv_scan){
            clickContent("scan");
        }

    }

    public void clickContent(String scan){
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
        Bundle bundle = new Bundle();
        if (isDetailState() && !TextUtils.isEmpty(value)) {
            String relevanceModuleBean = relevanceModule.getModuleName();
            AuthHelper.getInstance().queryDataAuth(getContext(), relevanceModuleBean, value,
                    new ProgressSubscriber<ViewDataAuthResBean>(getContext()) {
                        @Override
                        public void onNext(ViewDataAuthResBean moduleAuthBean) {
                            super.onNext(moduleAuthBean);
                            String readAuth = moduleAuthBean.getData().getReadAuth();
                            if (!("1".equals(readAuth) || "3".equals(readAuth)|| "4".equals(readAuth)|| "5".equals(readAuth))){
                                ToastUtils.showError(getContext(), "没有权限！");
                                return;
                            }
                            bundle.putString(Constants.DATA_ID, value);
                            bundle.putString(Constants.MODULE_BEAN, relevanceModuleBean);
                            RxManager.$(aHashCode).post(CustomConstants.MESSAGE_DATA_DETAIL_CODE, bundle);
                        }
                    });

            return;
        }
        //zzh:设置通过扫码得到值
        if (!TextUtil.isEmpty(scan)){
            bundle.putString(Constants.REFERENCE_SCAN,scan);
        }

        bundle.putString(Constants.MODULE_BEAN, bean.getModuleBean());
        bundle.putString(Constants.DATA_TAG2, pointOut);
        bundle.putString(Constants.REFERENCE_FIELD, keyName);
        if (!TextUtil.isEmpty(subFormName)) {
            bundle.putString(Constants.SUBFORM_NAME, subFormName);
        }
        if (referenceViewInterface != null) {
            JSONObject moduleValue = referenceViewInterface.getReferenceValue();
            bundle.putSerializable(Constants.DATA_TAG1, moduleValue);
        }
        bundle.putBoolean(Constants.DATA_TAG7, checkNull() && keyName.startsWith(CustomConstants.SUBFORM));
        bundle.putInt(Constants.DATA_TAG666, subFormIndex);
        bundle.putString(Constants.DATA_TAG777, subFormName);
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE,
                new MessageBean(code, null, bundle));
    }
}
