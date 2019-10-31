package com.hjhq.teamface.custom.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.LayoutData;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.bean.LinkageFieldsResultBean;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.bean.SaveCustomDataRequestBean;
import com.hjhq.teamface.custom.ui.template.ReferenceTempActivity;
import com.hjhq.teamface.custom.ui.template.RepeatCheckActivity;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.ReferenceViewInterface;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;
import com.hjhq.teamface.customcomponent.widget2.subforms.CommonSubFormsView;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

/**
 * 修改业务信息
 *
 * @author xj
 * @date 2017/9/4
 */

@RouteNode(path = "/edit", desc = "修改 自定义")
public class EditCustomActivity extends ActivityPresenter<AddCustomDelegate, AddCustomModel> implements ReferenceViewInterface {
    private HashMap detailMap;
    private String moduleBean;
    private String objectId;
    //公海池菜单ID
    private String seasPoolId;
    private String processFieldV;
    private String taskKey;
    private int operationType;
    private boolean isEdit = false;
    private Serializable layoutObject;
    private String moduleTitle;
    private String moduleId;
    private String processId;
    private CustomLayoutResultBean mCustomLayoutResultBean;
    private boolean isEditApprove;
    private int mPostDelay = 0;
    private String originData = "";
    /**
     * 联动组件key
     */
    private List<String> linkData;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            detailMap = (HashMap) intent.getSerializableExtra(Constants.DATA_TAG1);
            moduleId = detailMap.get("module_id") + "";
            isEdit = intent.getBooleanExtra(Constants.DATA_TAG2, false);
            isEditApprove = intent.getBooleanExtra(Constants.DATA_TAG3, false);
            objectId = intent.getStringExtra(Constants.DATA_ID);
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            seasPoolId = intent.getStringExtra(Constants.POOL);
            taskKey = intent.getStringExtra(ApproveConstants.TASK_KEY);
            processFieldV = intent.getStringExtra(ApproveConstants.PROCESS_FIELD_V);
            operationType = intent.getIntExtra(ApproveConstants.OPERATION_TYPE, CustomConstants.EDIT_STATE);
        }
    }

    @Override
    public void init() {
        getCustomLayout();
        getLinkageFields();
        CustomUtil.handleHidenFields(hashCode(), toString(), viewDelegate.mViewList);
    }

    /**
     * 获取联动字段
     */
    private void getLinkageFields() {
        new CommonModel().getLinkageFields(mContext, moduleBean, new ProgressSubscriber<LinkageFieldsResultBean>(mContext) {
            @Override
            public void onNext(LinkageFieldsResultBean linkageFieldsResultBean) {
                super.onNext(linkageFieldsResultBean);
                linkData = linkageFieldsResultBean.getData();
                setLinkage();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 设置驱动
     */
    private void setLinkage() {
        if (linkData == null || CollectionUtils.isEmpty(viewDelegate.mViewList)) {
            return;
        }
        Observable.from(viewDelegate.mViewList).flatMap(new Func1<SubfieldView, Observable<BaseView>>() {
            @Override
            public Observable<BaseView> call(SubfieldView subfieldView) {
                return Observable.from(subfieldView.getViewList());
            }
        }).subscribe(baseView -> {
            if (baseView instanceof CommonSubFormsView) {
                Observable.from(((CommonSubFormsView) baseView).getViewList()).flatMap(new Func1<List<BaseView>, Observable<BaseView>>() {
                    @Override
                    public Observable<BaseView> call(List<BaseView> baseViews) {
                        return Observable.from(baseViews);
                    }
                }).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link -> subView.setLinkage()));
            } else {
                Observable.from(linkData).filter(link -> link.equals(baseView.getKeyName())).subscribe(link -> baseView.setLinkage());
            }
            RxManager.$(hashCode()).postDelayed(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, null, mPostDelay);
            mPostDelay += 100;
        });
    }

    /**
     * 得到自定义布局
     */
    private void getCustomLayout() {
        Map<String, Object> map = new HashMap<>();
        map.put("bean", moduleBean);
        map.put("operationType", operationType);
        map.put("dataId", objectId);
        map.put("taskKey", taskKey);
        map.put("isSeasPool", TextUtil.isEmpty(seasPoolId) ? null : "1");
        map.put("processFieldV", processFieldV);
        model.getCustomLayout(this, map, new ProgressSubscriber<CustomLayoutResultBean>(this) {
            @Override
            public void onNext(CustomLayoutResultBean customLayoutResultBean) {
                super.onNext(customLayoutResultBean);
                mCustomLayoutResultBean = customLayoutResultBean;
                final JSONObject jsonObject = JSONObject.parseObject(mCustomLayoutResultBean.getRaw() + "");
                final JSONObject data = jsonObject.getJSONObject("data");
                layoutObject = data.getJSONArray("layout");
                processId = customLayoutResultBean.getData().getProcessId();
                moduleTitle = customLayoutResultBean.getData().getTitle();
                if (!isEdit){
                    viewDelegate.setIsCopyData(1);
                }
                viewDelegate.drawLayout(customLayoutResultBean.getData(), detailMap, CustomConstants.EDIT_STATE, moduleBean);
                JSONObject origin = new JSONObject();
                CustomUtil.getValue(viewDelegate.mViewList, mContext, origin);
                originData = JSONObject.toJSONString(origin);
                setLinkage();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoftKeyboardUtils.hide(this);
        if (isEdit) {
            editCustomData();
        } else {
            //copyCustomData();
            handleCopyCustomData();
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleCopyCustomData(){
        if (!TextUtil.isEmpty(processId) && !processId.equals("0")){
            JSONObject data = new JSONObject();
            if (!CustomUtil.editPut(viewDelegate.mViewList, this, data)) {
                return;
            }
            if (isEditApprove) {
                data.put("data_type", 1);
            }
            data.put("bean",moduleBean);
            checkAttendanceRelevanceTime(data,2);
        }else {
            copyCustomData();
        }
    }

    /**
     * 复制
     */
    private void copyCustomData() {

        SaveCustomDataRequestBean bean = new SaveCustomDataRequestBean();
        //bean.setId(dataId);
        bean.setBean(moduleBean);
        JSONObject json = new JSONObject();
        if (!CustomUtil.editPut(viewDelegate.mViewList, this, json)) {
            ToastUtils.showError(mContext, getResources().getString(R.string.data_error2));
            return;
        }
        bean.setData(json);
        LayoutData layout = new LayoutData();
        layout.setTitle(moduleTitle);
        layout.setModuleId(moduleId);
        layout.setLayout(layoutObject);
        layout.setProcessId(processId);
        bean.setLayout_data(layout);
        model.saveCustomData(this, bean, new ProgressSubscriber<ModuleBean>(this, "正在保存信息") {
            @Override
            public void onNext(ModuleBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 保存编辑
     */
    private void editCustomData() {

        if (!TextUtil.isEmpty(processId) && !processId.equals("0")){
            JSONObject data = new JSONObject();
            if (!CustomUtil.editPut(viewDelegate.mViewList, this, data)) {
                return;
            }
            if (isEditApprove) {
                data.put("data_type", 1);
            }
            data.put("bean",moduleBean);
            checkAttendanceRelevanceTime(data,1);
        }else {
            update();
        }

    }

    public void update(){
        SaveCustomDataRequestBean bean = new SaveCustomDataRequestBean();
        bean.setId(objectId);
        bean.setBean(moduleBean);
        LayoutData layout = new LayoutData();
        layout.setTitle(moduleTitle);
        layout.setModuleId(moduleId);
        layout.setLayout(layoutObject);
        layout.setProcessId(processId);
        bean.setLayout_data(layout);
        JSONObject json = new JSONObject();
        if (!CustomUtil.editPut(viewDelegate.mViewList, this, json)) {
            return;
        }
        if (isEditApprove) {
            json.put("data_type", 1);
        }
        bean.setData(json);
        bean.setOldData(detailMap);
        model.updateCustomData(this, bean, new ProgressSubscriber<BaseBean>(this, "正在保存信息") {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * 检测是否可以掉审批保存
     */
    public void checkAttendanceRelevanceTime(JSONObject bean,int mtype){
        model.checkAttendanceRelevanceTime(this,bean,new ProgressSubscriber<BaseBean>(this, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                if(mtype == 1){
                    update();
                }else if (mtype == 2){
                    copyCustomData();
                }


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(EditCustomActivity.this,"保存失败");
            }
        });
    }

    //zzh->ad:子表单增加时设置link值
    public void setCommonSubLinkage(Object o){
        if (o != null){
            List<BaseView> mViewList = (List<BaseView>) o;
            Observable.from(mViewList).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link -> {
                        subView.setLinkage();
                    }
            ));
        }

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //子表单数据联动联动
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SUBFORM_LINKAGE_CODE, tag -> {
            if (!CollectionUtils.isEmpty(linkData)) {
                setCommonSubLinkage(tag);//zzh->修改子表单设置link设置
                //Observable.from(linkData).filter(link -> link.equals(((BaseView) tag).getKeyName())).subscribe(link -> ((BaseView) tag).setLinkage());
            }
        });
        //查重
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REPEAT_CHECK_CODE, tag -> CommonUtil.startActivtiy(mContext, RepeatCheckActivity.class, (Bundle) tag));
        //关联列表
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE, tag -> {
            SoftKeyboardUtils.hide(mContext);
            MessageBean messageBean = (MessageBean) tag;
            Bundle bundle = (Bundle) messageBean.getObject();
            int index = bundle.getInt(Constants.DATA_TAG666);
            String subformName = bundle.getString(Constants.DATA_TAG777);
            HashMap<String, Object> relyMap = new HashMap<String, Object>();
            JSONObject relyForm = new JSONObject();
            CustomUtil.getValue(viewDelegate.mViewList, this, relyForm);
            // Log.e("JSONArray", "index=" + index + ">>>" + JSONObject.toJSONString(relyForm));
            Set<String> keySet = relyForm.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.startsWith(CustomConstants.REFERENCE)) {
                    relyMap.put(next, relyForm.get(next));
                } else if (next.startsWith(CustomConstants.SUBFORM)) {
                    if (next.equals(subformName)) {
                        JSONArray jsonArray = relyForm.getJSONArray(next);
                        if (jsonArray != null && jsonArray.size() >= index + 1 && index>0) {
                            JSONObject jsonObj = jsonArray.getJSONObject(index);
                            if (jsonObj != null && jsonObj.size() > 0) {
                                Set<String> sub = jsonObj.keySet();
                                Iterator<String> subIt = sub.iterator();
                                while (subIt.hasNext()) {
                                    String next1 = subIt.next();
                                    if (next1.startsWith(CustomConstants.SUBFORM + "_" + CustomConstants.REFERENCE)) {
                                        relyMap.put(next1, jsonObj.get(next1));
                                    }
                                }
                            }
                        }
                    } else {
                        JSONArray jsonArray = relyForm.getJSONArray(next);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            if (jsonObj != null && jsonObj.size() > 0) {
                                Set<String> sub = jsonObj.keySet();
                                Iterator<String> subIt = sub.iterator();
                                while (subIt.hasNext()) {
                                    String next1 = subIt.next();
                                    if (next1.startsWith(CustomConstants.SUBFORM + "_" + CustomConstants.REFERENCE)) {
                                        relyMap.put(next1, jsonObj.get(next1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            bundle.putSerializable(Constants.DATA_TAG8, relyMap);
            CommonUtil.startActivtiyForResult(mContext, ReferenceTempActivity.class, messageBean.getCode(), bundle);
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_MULTI_LINKAGE_CODE, tag -> {
            SoftKeyboardUtils.hide(mContext);
             // MessageBean messageBean = (MessageBean) tag;
            //JSONObject jsonObject = (JSONObject) messageBean.getObject();//zzh->ad:tag直接改为object类型
            JSONObject jsonObject = (JSONObject) tag;
            JSONObject origin = new JSONObject();
            //CustomUtil.getValue(viewDelegate.mViewList, mContext, origin);
            String keyName = jsonObject.getString("key");
            jsonObject.remove("key");
            String subFormName = jsonObject.getString("subform");
            String subFormIndex = jsonObject.getString("currentSubIndex");
            int subFormIndexInt = TextUtil.parseInt(subFormIndex, 0);
            CustomUtil.getValue(viewDelegate.mViewList, mContext, origin,subFormIndexInt);
            List<JSONArray> list = new ArrayList<JSONArray>();
            Set<String> set = origin.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                final String next = iterator.next();
               /* if (next.startsWith(CustomConstants.SUBFORM)) {
                    list.add(origin.getJSONArray(next));
                }*/
                if (!TextUtil.isEmpty(subFormName)){//zzh->ad:判断是否为子表单联动
                    if (next.startsWith(CustomConstants.SUBFORM) && next.equals(subFormName)) {//zzh->ad:判断是否为d当前子表单
                        list.add(origin.getJSONArray(next));
                    }
                }else{
                    list.add(origin.getJSONArray(next));
                }
            }

            for (int i = 0; i < linkData.size(); i++) {
                String keyI = linkData.get(i);
                if (origin.containsKey(keyI)) {
                    jsonObject.put(keyI, origin.get(keyI));
                }
                if (keyI.startsWith(CustomConstants.SUBFORM)) {
                    if (keyI.equals(keyName)) {
                        for (int j = 0; j < list.size(); j++) {
                            JSONArray arr = list.get(j);
                            /*if (arr.size() >= subFormIndexInt + 1 && arr.getJSONObject(subFormIndexInt).containsKey(keyI)) {
                                jsonObject.put(keyI, arr.getJSONObject(subFormIndexInt).get(keyI));
                            }*/
                            //subFormIndexInt +  //zzh每个子表单都是取第一个栏目
                            if (arr != null && arr.size() >= 1 && arr.getJSONObject(0) != null && arr.getJSONObject(0).containsKey(keyI)) {
                                jsonObject.put(keyI, arr.getJSONObject(0).get(keyI));
                            }
                        }
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            JSONArray arr = list.get(j);
                            if (arr.size() > 0 && arr.getJSONObject(0).containsKey(keyI)) {
                                jsonObject.put(keyI, arr.getJSONObject(0).get(keyI));
                            }
                        }
                    }
                }
            }


            new CommonModel().getLinkageData(mContext, jsonObject, new ProgressSubscriber<DetailResultBean>(mContext) {
                @Override
                public void onNext(DetailResultBean baseBean) {
                    super.onNext(baseBean);
                    Map<String, Object> map = baseBean.getData();
                    Integer[] indext = {0};
                    Observable.from(map.keySet()).subscribe(key -> {
                        Object o = map.get(key);
                        indext[0] = indext[0] +1;
                        if (key.startsWith(CustomConstants.SUBFORM)) {
                            if (o instanceof Map) {
                                Map<String, Object> subFormMap = (Map<String, Object>) o;
                                Observable.from(subFormMap.keySet())
                                        .subscribe(name -> {
                                            if (keyName.startsWith(CustomConstants.SUBFORM) && !TextUtil.isEmpty(subFormName)) {
                                                RxManager.$(hashCode()).post(name + CustomConstants.LINKAGE_TAG + subFormIndex, subFormMap.get(name));
                                            } else {
                                                RxManager.$(hashCode()).post(name + CustomConstants.LINKAGE_TAG, subFormMap.get(name));
                                            }
                                        });
                            }else{//zzh->ad:联动数据增加子表单数组逻辑
                                if (indext[0] ==  map.size()){
                                    Log.e("LinkageData:","isArray");
                                    if (map != null && map.size() > 0) {
                                        Iterator<String> iterator = map.keySet().iterator();
                                        while (iterator.hasNext()) {
                                            String next = iterator.next();
                                            Object relationValue = map.get(next);
                                            if (relationValue != null) {
                                                if (next.startsWith(CustomConstants.SUBFORM)) {
                                                    if (next.lastIndexOf("_") == 7) {
                                                        RxManager.$(hashCode()).post(next + "linkage_data_list", relationValue);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        } else {
                            RxManager.$(hashCode()).post(key + CustomConstants.LINKAGE_TAG, o);
                        }
                    });
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SUBFORM_INSERT_CODE, tag -> {
            SoftKeyboardUtils.hide(mContext);
            MessageBean messageBean = (MessageBean) tag;
            Bundle bundle = (Bundle) messageBean.getObject();
            HashMap<String, String> map = (HashMap<String, String>) bundle.getSerializable(Constants.DATA_TAG1);
            map.put("beanName", moduleBean);
            UIRouter.getInstance().openUri(mContext, "DDComp://custom/subform_insert", bundle, messageBean.getCode());
        });
    }

    @Override
    protected void onDestroy() {
        SoftKeyboardUtils.hide(this);
        super.onDestroy();
    }

    @Override
    public JSONObject getReferenceValue() {
        JSONObject json = new JSONObject();
        CustomUtil.putReference(viewDelegate.mViewList, json);
        return json;
    }

    @Override
    public void onBackPressed() {
        JSONObject origin = new JSONObject();
        CustomUtil.getValue(viewDelegate.mViewList, mContext, origin);
        if (TextUtils.isEmpty(originData)) {
            finish();
        } else {
            if (originData.equals(JSONObject.toJSONString(origin))) {
                finish();
            } else {
                DialogUtils.getInstance().sureOrCancel(mContext, "确定放弃此次编辑吗?", "", viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
                    @Override
                    public void clickSure() {
                        finish();
                    }
                });
            }
        }
    }
}
