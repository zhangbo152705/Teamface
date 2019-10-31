package com.hjhq.teamface.customcomponent.widget2.subforms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.hjhq.teamface.basis.bean.InsertSubformBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.SubformRelationBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.CaptureActivity;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.base.InputCommonView;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;


/**
 * 通用子表单
 *
 * @author lx
 * @date 2017/10/9
 */

public class CommonSubFormsView extends BaseView implements ActivityPresenter.OnActivityResult {
    protected int scan_code =code+101;
    protected LinearLayout llContent;
    protected TextView tvTitle;
    protected TextView tvDescription;
    protected TextView tvInsert;
    protected LinearLayout llRoot;
    protected ImageView ivRight;
    protected ImageView iv_scan;
    protected List<JSONObject> subFormsValue = new ArrayList<>();
    private List<List<BaseView>> mViewList = new ArrayList<>();
    private List<SubFormsSubfieldView> subfieldList = new ArrayList<>();
    private int childIndex = 0;
    private String fromWho = "";
    private String subRenferenceName;
    private String allowScan;

    public CommonSubFormsView(CustomBean bean) {
        super(bean);
        allowScan = bean.getField().getAllowScan();
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        subFormName = keyName;
        mView = View.inflate(activity, R.layout.custom_item_subforms_view, null);
        llContent = mView.findViewById(R.id.ll_layout);
        tvTitle = mView.findViewById(R.id.tv_title);
        tvDescription = mView.findViewById(R.id.tv_description);
        tvInsert = mView.findViewById(R.id.tv_insert);

        llRoot = mView.findViewById(R.id.ll_content);
        ivRight = mView.findViewById(R.id.iv_right);
        iv_scan = mView.findViewById(R.id.iv_scan);
        initView();
        parent.addView(mView);
        if (CustomConstants.FIELD_READ.equals(fieldControl)) {
            mView.setEnabled(false);
            tvInsert.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        }
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
    }

    public void clickContent(String scan){
        Bundle bundle = new Bundle();

        //zzh:设置通过扫码得到值
        if (!TextUtil.isEmpty(scan)){
            bundle.putString(Constants.REFERENCE_SCAN,scan);
        }
        List<CustomBean> componentList = bean.getComponentList();
        JSONObject moduleValue = new JSONObject();

        if (componentList == null) {
            return;
        }
        for (CustomBean bean : componentList){
            Log.e("clickContent",bean.getName());
            if (bean != null && bean.getName() != null && bean.getName().indexOf("reference") != -1){
                moduleValue.put(bean.getName(),"");
                Log.e("clickContent",bean.getName());
                subRenferenceName = bean.getName();
                bundle.putString(Constants.REFERENCE_FIELD, bean.getName());
                break;
            }
        }
        //bundle.putSerializable(Constants.DATA_TAG1, moduleValue);

        bundle.putString(Constants.MODULE_BEAN, bean.getModuleBean());
        bundle.putString(Constants.DATA_TAG2, pointOut);


        if (!TextUtil.isEmpty(subFormName)) {
            bundle.putString(Constants.SUBFORM_NAME, subFormName);
        }

        bundle.putBoolean(Constants.DATA_TAG7, checkNull() && keyName.startsWith(CustomConstants.SUBFORM));
        bundle.putInt(Constants.DATA_TAG666, subFormIndex);
        bundle.putString(Constants.DATA_TAG777, subFormName);
        ((ActivityPresenter) getContext()).setOnActivityResult(scan_code, this);
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE,
                new MessageBean(scan_code, null, bundle));
    }

    public void checkReference(){
        List<CustomBean> componentList = bean.getComponentList();
        JSONObject moduleValue = new JSONObject();

        if (componentList == null) {
            return;
        }
        for (CustomBean bean : componentList){
            Log.e("clickContent",bean.getName());
            if (bean != null && bean.getName() != null && bean.getName().indexOf("reference") != -1){
                moduleValue.put(bean.getName(),"");
                Log.e("clickContent",bean.getName());
                allowScan = bean.getField().getAllowScan();
                break;
            }
        }
    }

    private void initView() {
        if (CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvTitle.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
        }
        setTitle(tvTitle, title);
        setData(bean.getValue());
        setDefaultValue(bean.getDefaultSubform());//zzh:新增时显示子表单默认值
        if (isDetailState()) {
            tvDescription.setVisibility(View.GONE);
            tvInsert.setVisibility(View.GONE);
            iv_scan.setVisibility(View.GONE);
        } else {
            if (!CustomConstants.FIELD_READ.equals(fieldControl)) {
                tvDescription.setOnClickListener(v -> addSubForms(true));
                final SubformRelationBean subformRelation = bean.getSubformRelation();
                if (subformRelation != null && !TextUtils.isEmpty(subformRelation.getControlField())) {
                    tvInsert.setVisibility(View.VISIBLE);
                    TextUtil.setText(tvInsert, subformRelation.getTitle());
                    tvInsert.setOnClickListener(v -> chooseData());
                }
            } else {
                tvDescription.setVisibility(View.GONE);
            }
            iv_scan.setVisibility(View.VISIBLE);
            iv_scan.setOnClickListener(v -> {
                clickContent("scan");
            });
            checkReference();
            //zzh:子表单不显示扫一扫
            if ( TextUtil.isEmpty(allowScan) || !allowScan.equals("1")) {
                iv_scan.setVisibility(View.GONE);
            }
        }


        RxManager.$(aHashCode).on(keyName + "index", o -> {
            childIndex = (int) o;
        });
        RxManager.$(aHashCode).on(keyName + "key", o -> {
            fromWho = (String) o;
        });
        //zzh:从主表关联关系获取子表数据,自动添加
        RxManager.$(aHashCode).on(keyName + "data_list", o -> {
            Log.e("从主表关联关系选择数据", JSONObject.toJSONString(o));
            if (o instanceof JSONArray) {
                JSONArray arr = ((JSONArray) o);
                int size = subfieldList.size();
                for (int i = 0; i < arr.size(); i++) {
                    addSubForms(null, true);
                    int index = i;
                    final JSONObject jsonObject = arr.getJSONObject(i);
                    final Set<String> strings = jsonObject.keySet();
                    final Iterator<String> iterator = strings.iterator();

                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        Object relationValue = jsonObject.get(next);
                        if (next.startsWith(CustomConstants.SUBFORM)) {
                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                        } else {
                            RxManager.$(aHashCode).post(next, relationValue);
                        }
                    }
                }
            }
        });

        //zzh:从简历解析中获取子表数据,自动添加
        RxManager.$(aHashCode).on(keyName + "resumeanlusis_list", o -> {
           try {
               String jsonStr = JSONObject.toJSONString(o);
               Log.e("从简历解析中获取子表数据",jsonStr);
               JSONArray arr = JSON.parseArray(jsonStr);
               if (arr != null && arr.size()>0) {
                   int size = subfieldList.size();
                   for (int i=0;i<size;i++){
                       delSubFroms(i);
                   }
                   for (int i = 0; i < arr.size(); i++) {
                       addSubForms(null, true);
                       int index = i;
                       final JSONObject jsonObject = arr.getJSONObject(i);
                       final Set<String> strings = jsonObject.keySet();
                       final Iterator<String> iterator = strings.iterator();

                       while (iterator.hasNext()) {
                           String next = iterator.next();
                           Object relationValue = jsonObject.get(next);
                           if (next.startsWith(CustomConstants.SUBFORM)) {
                               RxManager.$(aHashCode).post(next + (size + index), relationValue);
                           } else {
                               RxManager.$(aHashCode).post(next, relationValue);
                           }
                       }
                   }
               }
           }catch (Exception e){
               Log.e("resumeanlusis_list",e.toString());
           }
        });
        //zzh->ad:从主表联动获取数据
        RxManager.$(aHashCode).on(keyName + "linkage_data_list", o -> {
            Log.e("LinkageData", "从主表联动获取数据"+JSONObject.toJSONString(o));
            if (o instanceof JSONArray) {
                JSONArray arr = ((JSONArray) o);
                int size = subfieldList.size();
                for (int i = 0; i < arr.size(); i++) {
                    addSubForms(null, true);
                    int index = i;
                    final JSONObject jsonObject = arr.getJSONObject(i);
                    final Set<String> strings = jsonObject.keySet();
                    final Iterator<String> iterator = strings.iterator();

                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        Object relationValue = jsonObject.get(next);
                        if (next.startsWith(CustomConstants.SUBFORM)) {
                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                        } else {
                            RxManager.$(aHashCode).post(next, relationValue);
                        }
                    }
                }
            }
        });
        RxManager.$(aHashCode).onSticky(keyName + "more原来的逻辑", o -> {
            if (o instanceof List) {
                ArrayList<ReferDataTempResultBean.DataListBean> list = (ArrayList<ReferDataTempResultBean.DataListBean>) o;
                int size = subfieldList.size();
                for (int i = 0; i < list.size(); i++) {
                    int index = i;
                    ReferDataTempResultBean.DataListBean checkItem = list.get(i);
                    addSubForms(null, true);
                    Map<String, Object> relationField = checkItem.getRelationField();
                    if (relationField != null && relationField.size() > 0) {
                        Iterator<String> iterator = relationField.keySet().iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            Object relationValue = relationField.get(next);
                            if (relationValue != null) {
                                tvTitle.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (next.startsWith(CustomConstants.SUBFORM)) {
                                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                                        } else {
                                            RxManager.$(aHashCode).post(next, relationValue);
                                        }
                                    }
                                }, i * 50);
                            }
                        }
                    }
                }
            }
        });
        RxManager.$(aHashCode).onSticky(keyName + "more", o -> {
            if (o instanceof List) {
                ArrayList<ReferDataTempResultBean.DataListBean> list = (ArrayList<ReferDataTempResultBean.DataListBean>) o;
                for (int i = childIndex + 1; i < mViewList.size(); i++) {
                    final List<BaseView> baseViews = mViewList.get(i);
                    int index = i;
                    for (int j = 0; j < baseViews.size(); j++) {
                        final BaseView baseView = baseViews.get(j);
                        if (fromWho.equals(baseView.getKeyName()) && baseView.checkNull() && list.size() > 0) {
                            ReferDataTempResultBean.DataListBean checkItem = list.get(0);
                            list.remove(0);
                            Map<String, Object> relationField = checkItem.getRelationField();
                            if (relationField != null && relationField.size() > 0) {
                                Iterator<String> iterator = relationField.keySet().iterator();
                                while (iterator.hasNext()) {
                                    String next = iterator.next();
                                    Object relationValue = relationField.get(next);
                                    if (relationValue != null) {
                                        tvTitle.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (next.startsWith(CustomConstants.SUBFORM)) {
                                                    RxManager.$(aHashCode).post(next + index, relationValue);
                                                } else {
                                                    RxManager.$(aHashCode).post(next, relationValue);
                                                }
                                            }
                                        }, i * 50);
                                    }
                                }
                            }

                        }


                    }

                }
                if (list.size() > 0) {
                    int size = subfieldList.size();
                    for (int i = 0; i < list.size(); i++) {
                        int index = i;
                        ReferDataTempResultBean.DataListBean checkItem = list.get(i);
                        addSubForms(null, true);
                        Map<String, Object> relationField = checkItem.getRelationField();
                        if (relationField != null && relationField.size() > 0) {
                            Iterator<String> iterator = relationField.keySet().iterator();
                            while (iterator.hasNext()) {
                                String next = iterator.next();
                                Object relationValue = relationField.get(next);
                                if (relationValue != null) {
                                    tvTitle.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (next.startsWith(CustomConstants.SUBFORM)) {
                                                RxManager.$(aHashCode).post(next + (size + index), relationValue);
                                            } else {
                                                RxManager.$(aHashCode).post(next, relationValue);
                                            }
                                        }
                                    }, i * 50);
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    private void chooseData() {
        Bundle bundle = new Bundle();
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("subformName", subFormName);
        map.put("controlField", bean.getSubformRelation().getControlField());
        map.put("fuzzySearch", "");
        bundle.putSerializable(Constants.DATA_TAG1, map);
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_SUBFORM_INSERT_CODE,
                new MessageBean(code, null, bundle));
    }

    @Override
    protected void setData(Object value) {
        if (value == null || "".equals(value)) {
            llContent.setVisibility(View.GONE);
            return;
        }
        try {
            subFormsValue.clear();
            if (value instanceof List) {
                subFormsValue = JSONArray.parseArray(JSON.toJSONString(value), JSONObject.class);
            }
            for (JSONObject jsonObject : subFormsValue) {
                addSubForms(jsonObject, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增时设置子表单默认值
     */
    public void setDefaultValue(Object value){
        if (value == null || "".equals(value)  || CustomConstants.ADD_STATE != state
                || CustomConstants.EDIT_STATE != state|| bean.getValue() != null) {
            return;
        }
        try{
            if (value instanceof List) {
                subFormsValue = JSONArray.parseArray(JSON.toJSONString(value), JSONObject.class);
            }
            for (JSONObject jsonObject : subFormsValue) {
                addSubForms(jsonObject, false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, getValue(true));
    }

    public void put(JSONObject jsonObj, boolean checkNull) {
        jsonObj.put(keyName, getValue(checkNull));
    }

    /**
     * zzh->ad:增加获取子表单根据栏目获取值
     * @param jsonObj
     * @param checkNull
     * @param subFormIndex
     */
    public void put(JSONObject jsonObj, boolean checkNull,int subFormIndex) {
        jsonObj.put(keyName, getValue(checkNull,subFormIndex));
    }

    public Object getValue(boolean checkNull) {
        if (subfieldList.size() == 0) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        getSubFormsValue(jsonArray, checkNull);
        if (jsonArray.size() == 0) {
            return "";
        }
        return jsonArray;
    }
    /**
     * zzh->ad:增加获取子表单根据栏目获取值
     * @param checkNull
     * @param subFromIndex
     */
    public Object getValue(boolean checkNull,int subFromIndex) {
        if (subfieldList.size() == 0) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        getSubFormsValueByIndex(jsonArray, checkNull,subFromIndex);
        if (jsonArray.size() == 0) {
            return "";
        }
        return jsonArray;
    }

    @Override
    public boolean checkNull() {
        return subfieldList.size() == 0;
    }

    @Override
    public boolean formatCheck() {
        if (subfieldList.size() == 0) {
            return true;
        }
        return getSubFormsValue(new JSONArray(), true);
    }

    public boolean getSubFormsValue(JSONArray jsonArray, boolean showNotify) {
        try {
            Map<String, Set<String>> map = new HashMap<>();
            for (List<BaseView> listView : mViewList) {
                JSONObject json = new JSONObject();
                for (BaseView view : listView) {
                    CustomBean bean = view.getBean();
                    if ("0".equals(bean.getField().getAddView())) {
                        if (view.controlHide) {
                            json.put(view.getKeyName(), "");
                            continue;
                        } else {
                            json.put(view.getKeyName(), view.getDefaultValue());
                        }
                        continue;
                    }

                    if (view.controlHide) {
                        json.put(view.getKeyName(), "");
                        continue;
                    }
                    boolean must = CustomConstants.FIELD_MUST.equals(view.getFieldControl());

                    if (view.checkNull() && must) {
                        if (showNotify) {
                            ToastUtils.showError(getContext(), title + "-" + bean.getLabel() + "必填项");
                        }
                        return false;
                    } else if (view.formatCheck()) {
                        view.put(json);
                    } else {
                        return true;
                    }

                    String repeatCheck = bean.getField().getRepeatCheck();
                    if ("2".equals(repeatCheck)) {
                        if (!"".equals(((InputCommonView) view).getValue())) {
                            Set<String> stringSet = map.get(bean.getName());
                            if (stringSet == null) {
                                stringSet = new HashSet<>();
                                map.put(bean.getName(), stringSet);
                            }
                            boolean add = stringSet.add(((InputCommonView) view).getValue() + "");
                            if (!add) {
                                ToastUtils.showError(getContext(), getTitle() + "的" + view.getTitle() + "组件值重复");
                                return false;
                            }
                        }
                    }
                }
                jsonArray.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showError(getContext(), "数据异常");
            return false;
        }
        return true;
    }


    /**
     * zzh->ad:获取子表单某个栏目的值
     * @param jsonArray
     * @param showNotify
     * @param subFromIndex
     * @return
     */
    public boolean getSubFormsValueByIndex(JSONArray jsonArray, boolean showNotify,int subFromIndex) {
        try {
            Map<String, Set<String>> map = new HashMap<>();
            if (subFromIndex>=0 && mViewList.size()>0 && mViewList.size()>subFromIndex) {
                List<BaseView> listView = mViewList.get(subFromIndex);
                JSONObject json = new JSONObject();
                for (BaseView view : listView) {
                    CustomBean bean = view.getBean();
                    if ("0".equals(bean.getField().getAddView())) {
                        if (view.controlHide) {
                            json.put(view.getKeyName(), "");
                            continue;
                        } else {
                            json.put(view.getKeyName(), view.getDefaultValue());
                        }
                        continue;
                    }

                    if (view.controlHide) {
                        json.put(view.getKeyName(), "");
                        continue;
                    }
                    boolean must = CustomConstants.FIELD_MUST.equals(view.getFieldControl());

                    if (view.checkNull() && must) {
                        if (showNotify) {
                            ToastUtils.showError(getContext(), title + "-" + bean.getLabel() + "必填项");
                        }
                        view.put(json);
                        // return false;
                    } else if (view.formatCheck()) {
                        view.put(json);
                    } else {
                        return true;
                    }

                    String repeatCheck = bean.getField().getRepeatCheck();
                    if ("2".equals(repeatCheck)) {
                        if (!"".equals(((InputCommonView) view).getValue())) {
                            Set<String> stringSet = map.get(bean.getName());
                            if (stringSet == null) {
                                stringSet = new HashSet<>();
                                map.put(bean.getName(), stringSet);
                            }
                            boolean add = stringSet.add(((InputCommonView) view).getValue() + "");
                            if (!add) {
                                ToastUtils.showError(getContext(), getTitle() + "的" + view.getTitle() + "组件值重复");
                                return false;
                            }
                        }
                    }
                }
                jsonArray.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showError(getContext(), "数据异常");
            return false;
        }
        return true;
    }

    public void addSubForms(boolean isAdd) {
        addSubForms(null, isAdd);
        mView.setEnabled(false);
    }


    public void addSubForms(JSONObject jsonObject, boolean isAdd) {
        List<CustomBean> componentList = bean.getComponentList();
        if (componentList == null) {
            return;
        }
        SubFormsSubfieldView subFormsSubfieldView = new SubFormsSubfieldView(getContext(), mViewList.size() + 1, state, fieldControl, position -> {
            /*if ("2".equals(fieldControl) && subfieldList.size() == 1) {
                ToastUtils.showToast(getContext(), "必填子表单,内容不能全部移除");
                return;
            }*/
            List<BaseView> baseViews = mViewList.get(position);
            for (BaseView view : baseViews) {
                view.delView(llContent);
            }
            mViewList.remove(position);
            subfieldList.get(position).delView(llContent);
            subfieldList.remove(position);
            for (int i = 0; i < subfieldList.size(); i++) {
                SubFormsSubfieldView subfieldView = subfieldList.get(i);
                subfieldView.setTitle(i + 1);
            }
        });
        subFormsSubfieldView.addView(llContent);
        List<BaseView> list = new ArrayList<>();
        for (CustomBean component : componentList) {
            component.setModuleBean(bean.getModuleBean());
            component.setValue("");
            if (CustomConstants.FIELD_READ.equals(fieldControl)) {
                //component.getField().setFieldControl(fieldControl); //ZZH:子表单只读属性不针对子表单内所有字段生效
            }
            if (jsonObject != null) {
                Object o = jsonObject.get(component.getName());
                component.setValue(o);
            }
            BaseView baseView;
            if (stateEnv == CustomConstants.DETAIL_STATE || stateEnv == CustomConstants.APPROVE_DETAIL_STATE) {
                component.setState(stateEnv);
                baseView = CustomUtil.drawLayout(stateEnv, llContent, component);
                baseView.setStateEnv(stateEnv);
            } else {
                component.setState(state);
                if (isAdd) {
                    baseView = CustomUtil.drawLayout(llContent, component, CustomConstants.ADD_STATE);
                    //RxManager.$(hashCode()).postDelayed(CustomConstants.MESSAGE_SUBFORM_LINKAGE_CODE, baseView,50);
                } else {
                    baseView = CustomUtil.drawLayout(llContent, component, state);
                }

            }
            if (baseView != null) {
                baseView.setSubFormInfo(keyName, subfieldList.size());
                list.add(baseView);

            }

        }
        mViewList.add(list);
        subfieldList.add(subFormsSubfieldView);
        llContent.setVisibility(View.VISIBLE);
        if (isAdd) {
            RxManager.$(hashCode()).post(CustomConstants.MESSAGE_SUBFORM_LINKAGE_CODE,list);
        }
    }

    public void delSubFroms(int position){
        List<BaseView> baseViews = mViewList.get(position);
        for (BaseView view : baseViews) {
            view.delView(llContent);
        }
        mViewList.remove(position);
        subfieldList.get(position).delView(llContent);
        subfieldList.remove(position);
        for (int i = 0; i < subfieldList.size(); i++) {
            SubFormsSubfieldView subfieldView = subfieldList.get(i);
            subfieldView.setTitle(i + 1);
        }
    }

    public List<List<BaseView>> getViewList() {
        return mViewList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra(Constants.DATA_TAG2) != null) {
                ArrayList<ReferDataTempResultBean.DataListBean> list = (ArrayList<ReferDataTempResultBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG2);
                int size = subfieldList.size();
                for (int i = 0; i < list.size(); i++) {
                    int index = i;
                    ReferDataTempResultBean.DataListBean checkItem = list.get(i);
                    addSubForms(null, true);
                    Map<String, Object> relationField = checkItem.getRelationField();
                    if (relationField != null && relationField.size() > 0) {
                        Iterator<String> iterator = relationField.keySet().iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            Object relationValue = relationField.get(next);
                            if (relationValue != null) {
                                tvTitle.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (next.startsWith(CustomConstants.SUBFORM)) {
                                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                                        } else {
                                        }
                                    }
                                }, i * 50);
                            }
                        }
                    }
                }
            }
            if (data.getSerializableExtra(Constants.DATA_TAG3) != null) {
                ArrayList<InsertSubformBean.DataBean.DataListBean> list =
                        (ArrayList<InsertSubformBean.DataBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG3);
                int size = subfieldList.size();
                for (int i = 0; i < list.size(); i++) {
                    int index = i;
                    InsertSubformBean.DataBean.DataListBean checkItem = list.get(i);
                    addSubForms(null, true);
                    Map<String, Object> relationField = new HashMap<>();
                    ArrayList<RowBean> row = checkItem.getRow();
                    for (int r = 0; r < row.size(); r++) {
                        relationField.put(row.get(r).getName(), row.get(r).getValue());
                    }
                    if (relationField != null && relationField.size() > 0) {
                        Iterator<String> iterator = relationField.keySet().iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            Object relationValue = relationField.get(next);
                            if (relationValue != null) {
                                tvTitle.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (next.startsWith(CustomConstants.SUBFORM)) {
                                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                                        } else {
                                        }
                                    }
                                }, i * 50);
                            }
                        }
                    }
                }
            }



        }else if(requestCode == scan_code && resultCode == Activity.RESULT_OK){
            if (data.getSerializableExtra(Constants.DATA_TAG1) != null) {
                ReferDataTempResultBean.DataListBean scandata = (ReferDataTempResultBean.DataListBean) data.getSerializableExtra(Constants.DATA_TAG1);
                ArrayList<ReferDataTempResultBean.DataListBean> list = new ArrayList<>();
                list.add(scandata);
                int size = subfieldList.size();
                for (int i = 0; i < list.size(); i++) {
                    int index = i;
                    ReferDataTempResultBean.DataListBean checkItem = list.get(i);
                    addSubForms(null, true);
                    setReferenceData(checkItem,size,index);
                    /*Map<String, Object> relationField = checkItem.getRelationField();
                    if (relationField != null && relationField.size() > 0) {
                        Iterator<String> iterator = relationField.keySet().iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            Object relationValue = relationField.get(next);
                            if (relationValue != null) {
                                tvTitle.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (next.startsWith(CustomConstants.SUBFORM)) {
                                            Log.e("setReferenceData22:","value:"+next + (size + index));
                                            RxManager.$(aHashCode).post(next + (size + index), relationValue);
                                        } else {
                                        }
                                    }
                                }, i * 50);
                            }
                        }
                    }*/
                }
            }
        }
    }

    private void setReferenceData(ReferDataTempResultBean.DataListBean checkItem,int size,int index) {
        try {
            String data = JSON.toJSONString(checkItem);
            RxManager.$(aHashCode).post(subRenferenceName+ CustomConstants.SRT_REFERENCE_DATA + (size + index),checkItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setReferenceData() {

    }
}
