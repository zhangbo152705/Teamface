package com.hjhq.teamface.custom.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.bean.LayoutData;
import com.hjhq.teamface.basis.bean.MappingDataBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
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
import com.hjhq.teamface.custom.bean.SaveCustomDataRequestBean;
import com.hjhq.teamface.custom.ui.template.LinkDataActivity;
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
import java.util.function.LongFunction;

import rx.Observable;
import rx.functions.Func1;


@RouteNode(path = "/add", desc = "新增 自定义")
public class AddCustomActivity extends ActivityPresenter<AddCustomDelegate, AddCustomModel> implements ReferenceViewInterface, AMapLocationListener {
    private String moduleBean;
    private String sourceBean;
    private String moduleId;
    private String processId;
    private boolean fromApprove = false;
    private HashMap<String, Object> detailMap;
    protected HashMap<String, Object> detailMap2;
    protected HashMap<String, Object> dataMap = new HashMap<>();
    private String moduleTitle;
    //公海池菜单Id
    private String seasPoolId;
    private String relevanceField;
    private String relevanceValue;
    private CustomLayoutResultBean.DataBean customLayoutResultBeanData;
    private JSONObject defaultValue;
    //声明mlocationClient对象
    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    private String lng;
    private String lat;
    private String address;
    private String area;

    private boolean mappingDataReady = false;
    private boolean layoutDataReady = false;
    private CustomLayoutResultBean mCustomLayoutResultBean;

    private List<String> linkData;
    private String layoutString = "";
    private List<LayoutBean> layoutData;
    private Serializable layoutObject;
    private String originData = "";


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            moduleId = intent.getStringExtra(Constants.MODULE_ID);
            detailMap = (HashMap<String, Object>) intent.getSerializableExtra(Constants.DATA_TAG1);
            detailMap2 = (HashMap<String, Object>) intent.getSerializableExtra(Constants.DATA_TAG2);
            sourceBean = intent.getStringExtra(Constants.DATA_TAG3);
            fromApprove = intent.getBooleanExtra(Constants.DATA_TAG4, false);
            seasPoolId = intent.getStringExtra(Constants.POOL);
            if (detailMap != null && detailMap.keySet().size() > 0) {
                Iterator<String> iterator = detailMap.keySet().iterator();
                while (iterator.hasNext()) {
                    final String next = iterator.next();
                    dataMap.put(next, detailMap.get(next));
                }
            }
            if (detailMap2 != null && detailMap2.keySet().size() > 0) {
                Iterator<String> iterator = detailMap2.keySet().iterator();
                while (iterator.hasNext()) {
                    final String next = iterator.next();
                    dataMap.put(next, detailMap2.get(next));
                }
            }


        }
    }


    @Override
    public void init() {
        if (fromApprove) {
            //新增审批数据不显示链接
            viewDelegate.showMenu(1);
        }
        //审批不需要判断权限
        getCustomLayout();
        getLinkageFields();
        getMappingData();

        CustomUtil.handleHidenFields(hashCode(), toString(), viewDelegate.mViewList);

    }


    private void getLinkageFields() {
        new CommonModel().getLinkageFields(mContext, moduleBean, new ProgressSubscriber<LinkageFieldsResultBean>(mContext) {
            @Override
            public void onNext(LinkageFieldsResultBean linkageFieldsResultBean) {
                super.onNext(linkageFieldsResultBean);
                linkData = linkageFieldsResultBean.getData();
                setLinkage();
            }
        });

    }

    //zzh->ad:子表单增加时设置link值
    public void setCommonSubLinkage(Object o){
                if (o != null){
                    List<BaseView> mViewList = (List<BaseView>) o;
                    Observable.from(mViewList).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link -> {
                        subView.setLinkage();
                        Log.e("setCommonSubLinkage","KeyName"+subView.getKeyName());
                      }
                    ));
                }
    }


    private void getMappingData() {
        if (TextUtils.isEmpty(sourceBean)) {
            mappingDataReady = true;
            return;
        }
        model.findReferenceMapping(this, sourceBean, moduleBean,
                JSONObject.toJSONString(detailMap2), new ProgressSubscriber<MappingDataBean>(this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MappingDataBean baseBean) {
                        super.onNext(baseBean);
                        mappingDataReady = true;
                        Map<String, Object> data = baseBean.getData();
                        Set<String> set = data.keySet();
                        Iterator<String> iterator = set.iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            if (data.get(next) == null) {
                                dataMap.put(next, "");
                            } else {
                                dataMap.put(next, data.get(next));
                            }
                        }
                        if (layoutDataReady) {
                            setData(mCustomLayoutResultBean);
                        }
                    }
                });
    }


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
                }).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link ->subView.setLinkage()));
            } else {
                Observable.from(linkData).filter(link -> link.equals(baseView.getKeyName())).subscribe(link -> baseView.setLinkage());
            }
        });
    }


    private void getCustomLayout() {
        Map<String, Object> map = new HashMap<>();
        map.put("bean", moduleBean);
        map.put("operationType", CustomConstants.ADD_STATE);
        map.put("isSeasPool", TextUtil.isEmpty(seasPoolId) ? "" : "1");
        model.getCustomLayout(this, map, new ProgressSubscriber<CustomLayoutResultBean>(this) {
            @Override
            public void onNext(CustomLayoutResultBean customLayoutResultBean) {
                super.onNext(customLayoutResultBean);
                mCustomLayoutResultBean = customLayoutResultBean;
                layoutString = JSONObject.toJSONString(mCustomLayoutResultBean.getData());
                layoutData = mCustomLayoutResultBean.getData().getLayout();
                final JSONObject jsonObject = JSONObject.parseObject(mCustomLayoutResultBean.getRaw() + "");
                final JSONObject data = jsonObject.getJSONObject("data");
                layoutObject = data.getJSONArray("layout");
                layoutDataReady = true;
                if (mappingDataReady) {
                    setData(customLayoutResultBean);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    private void setData(CustomLayoutResultBean customLayoutResultBean) {
        moduleTitle = customLayoutResultBean.getData().getTitle();
        customLayoutResultBeanData = customLayoutResultBean.getData();
        processId = customLayoutResultBean.getData().getProcessId();
        viewDelegate.drawLayout(customLayoutResultBean.getData(), dataMap, CustomConstants.ADD_STATE, moduleBean);
        JSONObject origin = new JSONObject();
        CustomUtil.getValue(viewDelegate.mViewList, mContext, origin);
        originData = JSONObject.toJSONString(origin);
        setLinkage();
        defaultValue = new JSONObject();
        CustomUtil.putDefaultValue(customLayoutResultBeanData.getLayout(), defaultValue);
        needLocation();
    }

    private void needLocation() {
        Set<String> set = defaultValue.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (!TextUtils.isEmpty(next) && next.startsWith(CustomConstants.LOCATION)) {
                if ("1".equals(defaultValue.get(next))) {
                    location();
                    break;
                }
            }
            if (!TextUtils.isEmpty(next) && next.startsWith(CustomConstants.SUBFORM)) {
                JSONArray jsonArray = defaultValue.getJSONArray(next);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject o = jsonArray.getJSONObject(i);
                    Set<String> keySet = o.keySet();
                    Iterator<String> iterator1 = keySet.iterator();
                    while (iterator1.hasNext()) {
                        String next1 = iterator1.next();
                        if (!TextUtils.isEmpty(next1) && next1.startsWith(CustomConstants.SUBFORM + "_" + CustomConstants.LOCATION)) {
                            if ("1".equals(o.get(next1))) {
                                location();
                                break;
                            }
                        }
                    }


                }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                viewLinkData();
                break;
            case 1:
               // saveCustomData();
                handleSaveCustomData();
                break;
            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleSaveCustomData(){
        SaveCustomDataRequestBean bean = packageData();
        //zzh:审批流增加接口
        if (bean != null && bean.getLayout_data() != null && !TextUtil.isEmpty(bean.getLayout_data().getProcessId())
                && !bean.getLayout_data().getProcessId().equals("0")){ //判断是否有审批流
            JSONObject data = (JSONObject) bean.getData();
            data.put("bean",bean.getBean());
            checkAttendanceRelevanceTime(data,bean);
        }else {
            saveCustomData();
        }
    }

    private void saveCustomData() {
        SaveCustomDataRequestBean bean = packageData();
        model.saveCustomData(this, bean, new ProgressSubscriber<ModuleBean>(this, "正在保存信息") {
            @Override
            public void onNext(ModuleBean baseBean) {
                super.onNext(baseBean);
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, baseBean.getData());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(AddCustomActivity.this,"保存失败");
            }
        });
    }

    /**
     * 检测是否可以掉审批保存
     */
    public void checkAttendanceRelevanceTime(JSONObject bean,SaveCustomDataRequestBean savebean){
        model.checkAttendanceRelevanceTime(this,bean,new ProgressSubscriber<BaseBean>(this, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                saveCustomData();

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(AddCustomActivity.this,"保存失败");
            }
        });
    }

    private void viewLinkData() {
        //1、后台获取发布链接，2、非公海池，3公海池，4关联模块
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, moduleBean);
        if (TextUtils.isEmpty(sourceBean)) {
            String seapooId = "";
            if (!TextUtils.isEmpty(seasPoolId) && !"0".equals(seasPoolId)) {
                seapooId = seasPoolId;
            }
            int source = 2;
            if (!TextUtils.isEmpty(seapooId)) {
                source = 3;
            }
            bundle.putString(Constants.DATA_TAG2, seapooId);
            bundle.putInt(Constants.DATA_TAG3, source);
            bundle.putString(Constants.DATA_TAG4, "");
            bundle.putString(Constants.DATA_TAG5, "");
            bundle.putString(Constants.DATA_TAG6, "");
        } else {
            bundle.putString(Constants.DATA_TAG2, "");
            bundle.putInt(Constants.DATA_TAG3, 4);
            bundle.putString(Constants.DATA_TAG4, sourceBean);
            bundle.putString(Constants.DATA_TAG5, relevanceField);
            bundle.putString(Constants.DATA_TAG6, relevanceValue);
        }
        CommonUtil.startActivtiy(mContext, LinkDataActivity.class, bundle);
    }


    private SaveCustomDataRequestBean packageData() {
        SaveCustomDataRequestBean bean = new SaveCustomDataRequestBean();
        LayoutData layout = new LayoutData();
        layout.setLayout(layoutObject);
        layout.setModuleId(moduleId);
        layout.setTitle(moduleTitle);
        layout.setProcessId(processId);
        bean.setLayout_data(layout);
        bean.setBean(moduleBean);
        JSONObject json = new JSONObject();
        //赋默认值
        List<LayoutBean> layoutBeanList = customLayoutResultBeanData.getLayout();
        //     CustomUtil.putDefaultValue(layoutBeanList, json);
        if (!CustomUtil.addPut(viewDelegate.mViewList, this, json)) {
            return null;
        }
        if (!TextUtils.isEmpty(seasPoolId) && !"0".equals(seasPoolId)) {
            json.put("seas_pool_id", TextUtil.parseLong(seasPoolId, 0L));
        }
        List<String> list = new ArrayList<>();
        final Iterator<String> iterator = json.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith(CustomConstants.IDENTIFIER) || next.startsWith(CustomConstants.SERIAL_NUMBER)) {
                //自动编号不取值
                list.add(next);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            json.remove(list.get(i));

        }
        bean.setData(json);
        return bean;
    }

    private void location() {
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(this);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                lat = amapLocation.getLatitude() + "";
                lng = amapLocation.getLongitude() + "";
                String provinceName = amapLocation.getProvince();
                String cityName = amapLocation.getCity();
                String adName = amapLocation.getDistrict();
                area = provinceName + "#" + cityName + "#" + adName;
                address = amapLocation.getAddress();
                // address = provinceName + cityName + adName + poiInfo.getBusinessArea() + poiInfo.getSnippet();
                JSONObject jo = new JSONObject();
                jo.put("lng", lng);
                jo.put("value", address);
                jo.put("lat", lat);
                jo.put("area", area);

                Set<String> set = defaultValue.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (!TextUtils.isEmpty(next) && next.startsWith(CustomConstants.LOCATION)) {
                        if ("1".equals(defaultValue.get(next))) {
                            defaultValue.put(next, jo);
                        }
                    }
                    if (!TextUtils.isEmpty(next) && next.startsWith(CustomConstants.SUBFORM)) {
                        JSONArray jsonArray = defaultValue.getJSONArray(next);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);
                            Set<String> keySet = o.keySet();
                            Iterator<String> iterator1 = keySet.iterator();
                            while (iterator1.hasNext()) {
                                String next1 = iterator1.next();
                                if (!TextUtils.isEmpty(next1) && next1.startsWith(CustomConstants.SUBFORM + "_" + CustomConstants.LOCATION)) {
                                    o.put(next1, jo);
                                }
                            }
                        }
                    }
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public JSONObject getReferenceValue() {
        JSONObject json = new JSONObject();
        CustomUtil.putReference(viewDelegate.mViewList, json);
        return json;
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
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SUBFORM_LINKAGE_CODE, tag -> {
            if (!CollectionUtils.isEmpty(linkData)) {
                setCommonSubLinkage(tag);
               // Observable.from(linkData).filter(link -> link.equals(((BaseView) tag).getKeyName())).subscribe(link -> ((BaseView) tag).setLinkage());
            }
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REPEAT_CHECK_CODE, tag -> CommonUtil.startActivtiy(mContext, RepeatCheckActivity.class, (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE, tag -> {
            SoftKeyboardUtils.hide(AddCustomActivity.this);
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
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SUBFORM_INSERT_CODE, tag -> {
            SoftKeyboardUtils.hide(mContext);
            MessageBean messageBean = (MessageBean) tag;
            Bundle bundle = (Bundle) messageBean.getObject();
            HashMap<String, Serializable> map = (HashMap<String, Serializable>) bundle.getSerializable(Constants.DATA_TAG1);
            JSONObject valueJson = new JSONObject();
            CustomUtil.getValue(viewDelegate.mViewList, mContext, valueJson);
            String ref = (String) map.get("controlField");
            String valueString = valueJson.getString(ref);
            if (TextUtils.isEmpty(valueString)) {
                valueString = "0";
            }
            JSONObject controlField = new JSONObject();
            controlField.put("controlName", ref);
            controlField.put("controlValue", valueString);
            map.put("controlField", controlField);
            map.put("beanName", moduleBean);
            UIRouter.getInstance().openUri(mContext, "DDComp://custom/subform_insert", bundle, messageBean.getCode());
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_MULTI_LINKAGE_CODE, tag -> {
            SoftKeyboardUtils.hide(mContext);
            JSONObject jsonObject = (JSONObject) tag;
            Log.e("LINKAGE_CODE","jsonObject:"+jsonObject.toJSONString());
            JSONObject origin = new JSONObject();
            String keyName = jsonObject.getString("key");
            jsonObject.remove("key");
            String subFormName = jsonObject.getString("subform");
            String subFormIndex = jsonObject.getString("currentSubIndex");
            int subFormIndexInt = TextUtil.parseInt(subFormIndex, 0);
            CustomUtil.getValue(viewDelegate.mViewList, mContext, origin,subFormIndexInt);
            Log.e("LINKAGE_CODE","origin:"+origin.toJSONString());
            List<JSONArray> list = new ArrayList<JSONArray>();
            Set<String> set = origin.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                final String next = iterator.next();
                if (!TextUtil.isEmpty(subFormName)){//zzh->ad:判断是否为子表单联动
                    if (next.startsWith(CustomConstants.SUBFORM) && next.equals(subFormName)) {//zzh->ad:判断是否为d当前子表单
                        list.add(origin.getJSONArray(next));
                    }
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
                            //subFormIndexInt +  //zzh每个子表单都是取第一个栏目
                            if (arr != null && arr.size() >= 1 && arr.getJSONObject(0) != null && arr.getJSONObject(0).containsKey(keyI)) {
                                jsonObject.put(keyI, arr.getJSONObject(0).get(keyI));
                            }
                        }
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            JSONArray arr = list.get(j);
                            if (arr != null && arr.size() > 0 && arr.getJSONObject(0).containsKey(keyI)) {
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
                                Log.e("LinkageData:","Map");
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
        viewDelegate.svRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int height = viewDelegate.svRoot.getHeight();
                if (height > viewDelegate.height) {
                    int[] num = new int[2];
                    viewDelegate.svRoot.getLocationOnScreen(num);
                    viewDelegate.svRoot.scrollTo(0, viewDelegate.top);
                }
                viewDelegate.height = height;


            }
        });
        viewDelegate.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (viewDelegate.getRootView().getRootView().getHeight() - viewDelegate.getRootView().getHeight() > 100) {
                    if (!SoftKeyboardUtils.isShown(mContext)) {
                        RxManager.$(hashCode()).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, null);
                    }
                }
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setRelevaneData(String key) {
        relevanceField = key;
        if (detailMap2 != null && detailMap2.keySet().contains("id")) {
            relevanceValue = detailMap2.get("id") + "";
        }
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
