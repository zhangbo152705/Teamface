package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.HidenFieldBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.SelectCommonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.regionselect.XmlUtils;
import kankan.wheel.widget.regionselect.model.CityWheelModel;
import kankan.wheel.widget.regionselect.model.DistrictWheelModel;
import kankan.wheel.widget.regionselect.model.ProvinceWheelModel;
import rx.Observable;

import static rx.Observable.from;


public class PickListView extends SelectCommonView implements ActivityPresenter.OnActivityResult {

    private FlowLayout flowLayout;
    private View rlFlowLayout;
    public View bottom_line;
    private List<EntryBean> defaultEntrys;

    private List<EntryBean> entrys;

    private List<EntryBean> unChangedEntrys;
    protected List<EntryBean> checkEntrys = new ArrayList<>();
    protected List<EntryBean> areaEntrys = new ArrayList<>();
    boolean needSetDefault = true;

    private boolean isFlow;


    private boolean isMulti;

    private boolean isFirstControl = true;
    private long linkageTime = 0L;

    private String commonlyArea;

    public PickListView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        this.entrys = bean.getEntrys();
        this.unChangedEntrys = this.entrys;
        if (field != null) {
            this.commonlyArea = bean.getField().getCommonlyArea();
            defaultEntrys = field.getDefaultEntrys();
            isMulti = "1".equals(field.getChooseType());
        }
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_select_single_widget_layout;
        } else {
            return R.layout.custom_select_single_widget_row_layout;
        }
    }

    @Override
    public void initView() {
        flowLayout = mView.findViewById(R.id.pick_flow_layout);
        rlFlowLayout = mView.findViewById(R.id.rl_flow_layout);
        bottom_line = mView.findViewById(R.id.bottom_line);
        initAreaData();
        setDefaultView();
        super.initView();
    }

    public void setDefaultView(){

    }

    @Override
    public void initOption() {
        RxManager.$(aHashCode).onSticky(CustomConstants.CONTROL_FIELD_TAG + keyName, value -> {
            Log.e("PickListView","CONTROL_FIELD_TAG:"+CustomConstants.CONTROL_FIELD_TAG + keyName);
            ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) checkEntrys));
            if (state == CustomConstants.DETAIL_STATE) {
                setValue(bean.getValue());
            } else {
                controlField(value);
                setHidenFieldVisible(false);
                ArrayList<EntryBean> newList = new ArrayList<EntryBean>();
                if (clone.size() > 0 && entrys.size() > 0) {
                    for (int i = 0; i < clone.size(); i++) {
                        for (int j = 0; j < entrys.size(); j++) {
                            if (clone.get(i).getLabel().equals(entrys.get(j).getLabel())) {
                                entrys.get(j).setCheck(true);
                                newList.add(entrys.get(j));
                            }
                        }
                    }
                }
                if (newList.size() > 0) {
                    checkEntrys.clear();
                    ((ArrayList<EntryBean>) checkEntrys).addAll(newList);
                    setPickValue(checkEntrys, 144);
                } else {
                    Log.e("initOption","135");
                    setDefaultValue();
                }
            }
        });
        RxManager.$(aHashCode).onSticky(CustomConstants.CONTROL_FIELD_CLEAR_TAG + keyName, value -> {
            Log.e("PickListView","CONTROL_FIELD_CLEAR_TAG:"+CustomConstants.CONTROL_FIELD_CLEAR_TAG + keyName);
            ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) checkEntrys));
            controlField(value);
            if (state == CustomConstants.DETAIL_STATE) {
                setValue(bean.getValue());
            } else {
                clear(true);
                controlAndHide(true);
                ArrayList<EntryBean> newList = new ArrayList<EntryBean>();
                if (clone.size() > 0 && entrys.size() > 0) {
                    for (int i = 0; i < clone.size(); i++) {
                        for (int j = 0; j < entrys.size(); j++) {
                            if (clone.get(i).getLabel().equals(entrys.get(j).getLabel())) {
                                entrys.get(j).setCheck(true);
                                newList.add(entrys.get(j));
                            }
                        }
                    }
                }
                if (newList.size() > 0) {
                    needSetDefault = false;
                    checkEntrys.clear();
                    ((ArrayList<EntryBean>) checkEntrys).addAll(newList);
                    setPickValue(checkEntrys, 172);
                } else {
                    Log.e("CONTROL_FIELD_CLEAR_TAG","165");
                    setDefaultValue();
                }
            }
        });
        RxManager.$(aHashCode).on(CustomConstants.CLEAR_FIELD_CONTROL_TAG + keyName, bean -> {
            Log.e("PickListView","CLEAR_FIELD_CONTROL_TAG:"+CustomConstants.CLEAR_FIELD_CONTROL_TAG + keyName);
            this.entrys = this.unChangedEntrys;
        });


        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvContent.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    if (TextUtil.isEmpty(s.toString())) {
                        ivRight.setImageResource(R.drawable.icon_to_next);
                    } else {
                        ivRight.setImageResource(R.drawable.clear_button);
                    }
                }
            });
            ivRight.setOnClickListener(v -> {
                SoftKeyboardUtils.hide(((Activity) mView.getContext()));
                String content = tvContent.getText().toString();
                if (!TextUtil.isEmpty(content) || flowLayout.getChildCount() != 0) {
                    Observable.from(checkEntrys).subscribe(checkEntrys -> {
                        String controlField = checkEntrys.getControlField();
                        if (!TextUtil.isEmpty(controlField)) {
                            RxManager.$(aHashCode).post(CustomConstants.CLEAR_FIELD_CONTROL_TAG + controlField, null);
                        }
                        List<HidenFieldBean> hidenFields = checkEntrys.getHidenFields();
                        if (hidenFields != null) {
                            RxManager.$(aHashCode).post(CustomConstants.CLEAR_FIELD_HIDE_TAG + getContext().toString(), hidenFields);
                        }
                    });
                    needSetDefault = false;
                    clear();
                    clearDefault();
                    RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
                    RxManager.$(aHashCode).post(CustomConstants.MESSAGE_TASK_DETAIL_CLEAR_LABLE_CODE, "");//zzh->ad:任务详情清空标签
                }
            });


        }
        if (state == CustomConstants.ADD_STATE || state == CustomConstants.EDIT_STATE) {
            ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        }
        if (isDetailState() && checkNull()) {
            setPickValue(new ArrayList<>(), 222);
        }
    }

    @Override
    public void setContent(Object o) {
        super.setContent(o);
        setValue(o);
    }


    private void controlField(Object values) {

        List<EntryBean> clone = CloneUtils.clone(((ArrayList) checkEntrys));

        this.entrys = (List<EntryBean>) values;
        for (EntryBean entry : this.entrys) {//zzh:value比对改为lable比对
            Observable.from(this.unChangedEntrys)
                    .filter(unChangeEntry -> entry.getLabel().equals(unChangeEntry.getLabel()))
                    .subscribe(unChangeEntry -> {
                        entry.setControlField(unChangeEntry.getControlField());
                        entry.setHidenFields(unChangeEntry.getHidenFields());
                        entry.setRelyonList(unChangeEntry.getRelyonList());
                        entry.setSubList(unChangeEntry.getSubList());
                        entry.setCheck(unChangeEntry.isCheck());
                        entry.setLabel(unChangeEntry.getLabel());
                        entry.setColor(unChangeEntry.getColor());
                    });
        }
        checkEntrys.clear();

        for (int i = 0; i < clone.size(); i++) {
            final EntryBean outer = clone.get(i);
            for (int j = 0; j < this.entrys.size(); j++) {
                final EntryBean inner = this.entrys.get(j);
                if (outer.getLabel().equals(inner.getLabel())) {//zzh:value比对改为lable比对
                    checkEntrys.add(inner);
                    inner.setCheck(true);
                    needSetDefault = false;
                }
            }
        }
        if (clone.size() > 0) {
            ArrayList<EntryBean> newList = new ArrayList<EntryBean>();
            if (clone.size() > 0 && clone.size() > 0) {
                for (int i = 0; i < clone.size(); i++) {
                    for (int j = 0; j < clone.size(); j++) {
                        if (clone.get(i).getLabel().equals(clone.get(j).getLabel())) {//zzh:value比对改为lable比对
                            clone.get(j).setCheck(true);
                            newList.add(clone.get(j));
                        }
                    }
                }
            }
            if (newList.size() > 0) {
                checkEntrys.clear();
                ((ArrayList<EntryBean>) checkEntrys).addAll(newList);
                setPickValue(checkEntrys, 283);
            }
        }
        if (needSetDefault && defaultEntrys != null && defaultEntrys.size() > 0 && this.entrys != null & this.entrys.size() > 0) {
            for (int i = 0; i < defaultEntrys.size(); i++) {
                final EntryBean outer = defaultEntrys.get(i);
                for (int j = 0; j < this.entrys.size(); j++) {
                    final EntryBean inner = this.entrys.get(j);
                    if (outer.getLabel().equals(inner.getLabel())) {//zzh:value比对改为lable比对
                        checkEntrys.add(inner);
                        inner.setCheck(true);
                    }
                }
            }

        }
        setPickValue(checkEntrys, 299);
    }

    public void setEntrys(List<EntryBean> entrys) {
        this.entrys = entrys;
    }


    public void clear(boolean setDefaultValue) {
        if (state == CustomConstants.DETAIL_STATE) {
            return;
        }
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) checkEntrys));

        checkEntrys.clear();
        tvContent.setText("");
        flowLayout.removeAllViews();
        for (EntryBean entry : entrys) {
            entry.setCheck(false);
        }
        if (clone.size() > 0) {
            ArrayList<EntryBean> newList = new ArrayList<EntryBean>();
            if (clone.size() > 0 && clone.size() > 0) {
                for (int i = 0; i < clone.size(); i++) {
                    for (int j = 0; j < entrys.size(); j++) {
                        if (clone.get(i).getLabel().equals(entrys.get(j).getLabel())) {//zzh->ad: 第二层for循环clone数组改为entrys
                            clone.get(j).setCheck(true);
                            newList.add(clone.get(j));
                        }
                    }
                }
            }
            if (newList.size() > 0) {
                checkEntrys.clear();
                ((ArrayList<EntryBean>) checkEntrys).addAll(newList);
                setPickValue(checkEntrys, 335);
            } else {
                needSetDefault = true;
            }
        } else {
            if (setDefaultValue) {
                Log.e("clear","329");
                setDefaultValue();
            }
        }


    }

    public void clear() {
        if (state == CustomConstants.DETAIL_STATE) {
            return;
        }
        checkEntrys.clear();
        tvContent.setText("");
        flowLayout.removeAllViews();
        for (EntryBean entry : entrys) {
            entry.setCheck(false);
        }
    }


    private void clearDefault() {
        try {
            defaultEntrys.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setData(Object value) {
        setValue(value);
        controlAndHide(false);
    }


    private void initAreaData() {
        if (!TextUtils.isEmpty(commonlyArea)) {
            areaEntrys.clear();
            switch (commonlyArea) {
                case "1":
                    List<ProvinceWheelModel> provinceData = XmlUtils.getInstance().getProvinceData(mView.getContext(), "");
                    for (ProvinceWheelModel data : provinceData) {
                        EntryBean bean = new EntryBean();
                        bean.setLabel(data.getName());
                        bean.setValue(data.getId());
                        entrys.add(bean);
                    }
                    break;
                case "2":
                    List<CityWheelModel> cityData = XmlUtils.getInstance().getCityData(mView.getContext(), "");
                    for (CityWheelModel data : cityData) {
                        EntryBean bean = new EntryBean();
                        bean.setLabel(data.getName());
                        bean.setValue(data.getId());
                        entrys.add(bean);
                    }
                    break;
                case "3":
                    List<DistrictWheelModel> districtData = XmlUtils.getInstance().getDistrictData(mView.getContext(), "");
                    for (DistrictWheelModel data : districtData) {
                        EntryBean bean = new EntryBean();
                        bean.setLabel(data.getName());
                        bean.setValue(data.getId());
                        entrys.add(bean);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void setValue(Object value) {
        for (EntryBean entry : entrys) {
            entry.setCheck(false);
        }

        if (value instanceof String){
            JSONArray arr = JSONArray.parseArray(value+"");
            checkEntrys = new JsonParser<EntryBean>().jsonFromList(arr, EntryBean.class);
        }else {
            checkEntrys = new JsonParser<EntryBean>().jsonFromList(value, EntryBean.class);
        }

        for (EntryBean entry : entrys) {
            for (EntryBean checkEntry : checkEntrys) {
                checkEntry.setCheck(true);
                if (entry.getLabel().equals(checkEntry.getLabel())) {//zzh:value比对改为lable比对
                    entry.setCheck(true);
                    checkEntry.setHidenFields(entry.getHidenFields());
                    checkEntry.setRelyonList(entry.getRelyonList());
                    checkEntry.setControlField(entry.getControlField());
                    checkEntry.setColor(entry.getColor());
                    checkEntry.setValue(entry.getValue());
                }
            }
        }
        setPickValue(checkEntrys, 406);
    }

    @Override
    public void onClick() {
        super.onClick();
        if (entrys == null) {
            entrys = new ArrayList<>();
        }
        SoftKeyboardUtils.hide(llRoot);
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) entrys));
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_TAG1, clone);
        bundle.putBoolean(Constants.DATA_TAG2, isMulti);
        CommonUtil.startActivtiyForResult(getContext(), PickListViewSelectActivity.class, code, bundle);
    }

    @Override
    public void setHidenFieldVisible(boolean visible) {
        super.setHidenFieldVisible(visible);
        if (visible) {
            Log.e("setHidenFieldVisible","442");
            setDefaultValue();
        }
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    @Override
    public boolean checkNull() {
        if (entrys == null || entrys.size() == 0) {
            return true;
        } else {
            for (EntryBean en : entrys) {
                if (en.isCheck()) {
                    return false;
                }
            }
            return true;
        }


    }

    @Override
    public void setDefaultValue() {
        if (state == CustomConstants.DETAIL_STATE) {
            return;
        }
        if (!needSetDefault) {
            return;
        }

        if (state == CustomConstants.EDIT_STATE) {//zzh:当时编辑状态时 传进来的值不为空时 不设置默认值
            boolean flag = false;
            for (EntryBean entry : entrys) {
                for (EntryBean entry2 : checkEntrys) {
                    if (entry.getLabel().equals(entry2.getLabel())) {//zzh:value比对改为lable比对
                        flag = true;
                        break;
                    }
                }
                if (flag){
                    break;
                }
            }
            if (flag){
                return;
            }
        }


        checkEntrys.clear();
        if (CollectionUtils.isEmpty(entrys) || CollectionUtils.isEmpty(defaultEntrys)) {
            defaultValue = "";
            return;
        }
        for (EntryBean entry : entrys) {
            for (EntryBean entry2 : defaultEntrys) {
                if (entry.getLabel().equals(entry2.getLabel())) {
                    entry.setCheck(true);
                    checkEntrys.add(entry);
                }
            }
        }
        defaultValue = JSONObject.toJSONString(defaultEntrys);
        setPickValue(checkEntrys, 489);
        controlAndHide(false);

    }


    private void controlAndHide(boolean isAuto) {
        if (isMulti || CollectionUtils.isEmpty(checkEntrys)) {
            return;
        }
        EntryBean entryBean = checkEntrys.get(0);
        String controlField = entryBean.getControlField();
        if (!TextUtil.isEmpty(controlField)) {
            List<EntryBean> relyonList = entryBean.getRelyonList();
            if (isAuto) {
                RxManager.$(aHashCode).post(CustomConstants.CONTROL_FIELD_TAG + controlField, relyonList);
            } else {
                RxManager.$(aHashCode).post(CustomConstants.CONTROL_FIELD_CLEAR_TAG + controlField, relyonList);
            }
        }
        ArrayList<HidenFieldBean> hidenFields = (ArrayList<HidenFieldBean>) entryBean.getHidenFields();
        if (hidenFields != null) {
            ArrayList<HidenFieldBean> clone = (ArrayList<HidenFieldBean>) hidenFields.clone();
            clone.add(0, new HidenFieldBean(keyName, title));
            if (isAuto) {
                RxManager.$(aHashCode).post(getContext().toString(), clone);
            } else {
                RxManager.$(aHashCode).post(CustomConstants.CLEAR_FIELD_VALUE_TAG + getContext().toString(), clone);
            }
        }
    }

    @Override
    public Object getValue() {
        List<Map<String, Object>> entryList = new ArrayList();
        for (int i = 0; i < entrys.size(); i++) {
            if (entrys.get(i).isCheck()) {
                JSONObject map = new JSONObject();
                map.put("value", entrys.get(i).getValue());
                map.put("label", entrys.get(i).getLabel());
                map.put("color", entrys.get(i).getColor());
                entryList.add(map);
            }

        }
        if (entryList.size() == 0) {
            return "";
        }
        return entryList;
    }


    protected void setPickValue(List<EntryBean> list, int line) {
        Log.e("setPickValue" + line, "keyName>>>" + keyName + ">>>" + bean.getLabel() + ">>>code>>>" + code + ">>check>>" + JSON.toJSONString(list)+"num:"+list.size());
        if (isDetailState() && (list == null || list.size() == 0)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("未选择");
            tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            rlFlowLayout.setVisibility(View.GONE);
            return;
        }
        isFlow = false;
        from(list)
                .filter(entrysBean -> !TextUtils.isEmpty(entrysBean.getColor()))
                .subscribe(entry -> isFlow = true);

        tvContent.setVisibility(isFlow ? View.GONE : View.VISIBLE);
        rlFlowLayout.setVisibility(isFlow ? View.VISIBLE : View.GONE);

        if (isFlow) {
            initFlowLayout(flowLayout, list);
        } else {
            StringBuilder label = new StringBuilder();
            for (EntryBean entry : list) {
                label.append(entry.getLabel() + "/");
            }
            if (!TextUtils.isEmpty(label.toString())) {
                String substring = label.substring(0, label.length() - 1);
                TextUtil.setText(tvContent, substring);
            }
            if (isDetailState() && TextUtils.isEmpty(label)) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText("未填写");
                tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
                rlFlowLayout.setVisibility(View.GONE);
            }
        }
        if (isLinkage && System.currentTimeMillis() - linkageTime > 1000) {
            linkageTime = System.currentTimeMillis();
            setLinkage();
        }
    }


    private void initFlowLayout(FlowLayout mFragmentFileDetailFlowLayout,
                                List<EntryBean> labels) {
        if (isDetailState() && (labels == null || labels.size() == 0)) {
            labels = new ArrayList<>();
            EntryBean fake = new EntryBean("", "未选择", "#FFFFFF");
            labels.add(fake);
        }

        mFragmentFileDetailFlowLayout.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;
        if (labels != null) {
            for (EntryBean entryBean : labels) {
                TextView view = new TextView(mView.getContext());
                view.setPadding(12, 4, 12, 4);
                view.setText(entryBean.getLabel());
                Log.e("initFlowLayout:",entryBean.getLabel());
                view.setTextColor(Color.WHITE);
                if (TextUtils.isEmpty(entryBean.getValue())) {
                    view.setTextColor(ColorUtils.hexToColor("#999999"));
                }
                view.setTextSize(14);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.custom_flow_label);
                GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                if ("#FFFFFF".equals(entryBean.getColor())) {
                    view.setTextColor(ColorUtils.resToColor(getContext(), R.color.black_4a));
                }
                myGrad.setColor(ColorUtils.hexToColor(entryBean.getColor(), "#000000"));
                mFragmentFileDetailFlowLayout.addView(view, lp);
            }
        }

        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            if (CollectionUtils.isEmpty(labels)) {
                ivRight.setImageResource(R.drawable.icon_to_next);
            } else {
                ivRight.setImageResource(R.drawable.clear_button);
            }
        }
    }


    public boolean isMulti() {
        return isMulti;
    }

    public List<EntryBean> getCheckEntrys() {
        return checkEntrys;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (code == requestCode && Activity.RESULT_OK == resultCode && data != null) {
            ArrayList<EntryBean> list = (ArrayList<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            entrys = list;
            if (!CollectionUtils.isEmpty(checkEntrys)) {
                List<HidenFieldBean> hidenFields = checkEntrys.get(0).getHidenFields();
                if (!CollectionUtils.isEmpty(hidenFields)) {
                    RxManager.$(aHashCode).post(CustomConstants.CLEAR_FIELD_HIDE_TAG + getContext().toString(), hidenFields);
                }
            }
            needSetDefault = false;
            checkEntrys.clear();
            Observable.from(entrys).filter(entrysBean -> entrysBean.isCheck()).subscribe(entrysBean -> checkEntrys.add(entrysBean));
            setPickValue(checkEntrys, 662);
            controlAndHide(false);
            if (!isMulti && !checkNull() && isLinkage) {
                linkageData();
            }

        }
    }
}
