package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 多级下拉
 *
 * @author lx
 * @date 2017/8/23
 */

public class MultiPickListSelectView extends BaseView implements ActivityPresenter.OnActivityResult {
    //多级选择类型：0：2级下拉、1:3级下拉
    private String selectType;
    //默认值
    private CustomBean.DefaultEntryBean defaultEntrys;
    private List<EntryBean> entrys1 = new ArrayList<>();
    private List<EntryBean> entrys2 = new ArrayList<>();
    private List<EntryBean> entrys3 = new ArrayList<>();

    private List<EntryBean> selectEntryBean = new ArrayList<>();
    private FlowLayout flowLayout1;
    private ImageView ivRight1;
    private TextView tvTitle;
    private TextView tvContent1;
    private View llContent1;

    public MultiPickListSelectView(CustomBean bean) {
        super(bean);
        this.entrys1 = bean.getEntrys();
        defaultEntrys = bean.getDefaultEntrys();
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            selectType = field.getSelectType();
            if ("0".equals(selectType)) {
                for (EntryBean entry : entrys1) {
                    List<EntryBean> subList = entry.getSubList();
                    for (EntryBean entry2 : subList) {
                        entry2.setSubList(null);
                    }
                }
            }
        }
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            //关联映射
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            //关联映射
            RxManager.$(aHashCode).on(keyName, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        if ("0".equals(structure)) {
            mView = View.inflate(activity, R.layout.custom_select_multi_widget_layout, null);
        } else {
            mView = View.inflate(activity, R.layout.custom_select_multi_widget_row_layout, null);
        }
        parent.addView(mView);

        tvTitle = mView.findViewById(R.id.tv_title);

        tvContent1 = mView.findViewById(R.id.tv_content1);

        llContent1 = mView.findViewById(R.id.ll_content1);

        ivRight1 = mView.findViewById(R.id.iv_right1);


        flowLayout1 = mView.findViewById(R.id.pick_flow_layout1);


        initOption();
        onListener();
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
    }

    public void initOption() {


        if (isDetailState()) {
            ivRight1.setVisibility(View.GONE);
            llContent1.setClickable(false);
            tvContent1.setVisibility(View.GONE);
        } else {
            if (state == CustomConstants.ADD_STATE) {
                setDefaultValue();
            }
            if (state == CustomConstants.ADD_STATE || state == CustomConstants.EDIT_STATE) {
                ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
            }
            if (!CustomConstants.FIELD_READ.equals(fieldControl)) {
                llContent1.setOnClickListener(view -> onClick1());
            } else {
                llContent1.setOnClickListener(v -> ToastUtils.showError(getContext(), "只读属性不可更改"));
            }
        }

        setTitle(tvTitle, title);
        Object value = bean.getValue();
        if (value != null && !"".equals(value)) {
            setData(value);
        }
        if (isDetailState() && ((value == null || "".equals(value)))) {
           /* EntryBean entryBean = new EntryBean("", "未选择", "");
            List<EntryBean> list = new ArrayList<>();
            list.add(entryBean);
            setPickValueV2(list);*/
            tvContent1.setVisibility(View.VISIBLE);
            tvContent1.setText("未选择");
            tvContent1.setTextColor(getContext().getResources().getColor(R.color._999999));
            flowLayout1.setVisibility(View.GONE);
        }

    }

    /**
     * 监听
     */
    private void onListener() {
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            ivRight1.setOnClickListener(v -> {
                if (flowLayout1.getChildCount() != 0) {
                    clearEntry1();
                }
            });

        }
    }

    private void onClick1() {
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
        if (CollectionUtils.isEmpty(entrys1)) {
            ToastUtils.showError(getContext(), "未获取到选项值");
            return;
        }
        //>>>>>>>>>>>
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_TAG1, (Serializable) entrys1);
        bundle.putSerializable(Constants.DATA_TAG2, (Serializable) selectEntryBean);
        bundle.putInt(Constants.DATA_TAG3, TextUtil.parseInt(selectType, -1));
        CommonUtil.startActivtiyForResult(getContext(), MultiGradeSelectActivity.class, code, bundle);


    }


    private void clearEntry1() {
        Observable.from(entrys1).subscribe(entry -> entry.setCheck(false));
        ivRight1.setImageResource(R.drawable.icon_to_next);
        flowLayout1.removeAllViews();
        tvContent1.setVisibility(View.VISIBLE);
        selectEntryBean.clear();

    }


    @Override
    protected void setData(Object value) {
        if (value instanceof String){
            JSONArray arr = JSONArray.parseArray(value+"");
            selectEntryBean = new JsonParser<EntryBean>().jsonFromList(arr, EntryBean.class);
        }else {
            selectEntryBean = new JsonParser<EntryBean>().jsonFromList(value, EntryBean.class);
        }
        setPickValue(selectEntryBean);
        setOtherEntryBean();
    }


    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, getValue());
    }

    @Override
    public boolean checkNull() {
        return "".equals(getValue());
    }

    @Override
    public boolean formatCheck() {
        return true;
    }


    public void setDefaultValue() {
        if (defaultEntrys == null) {
            return;
        }
        if (TextUtil.isEmpty(defaultEntrys.getOneDefaultValueId())) {
            return;
        }
        EntryBean entryBean = new EntryBean(defaultEntrys.getOneDefaultValueId(), defaultEntrys.getOneDefaultValue(), defaultEntrys.getOneDefaultValueColor());
        selectEntryBean.add(entryBean);
        if (!TextUtil.isEmpty(defaultEntrys.getTwoDefaultValueId())) {
            entryBean = new EntryBean(defaultEntrys.getTwoDefaultValueId(), defaultEntrys.getTwoDefaultValue(), defaultEntrys.getTwoDefaultValueColor());
            selectEntryBean.add(entryBean);
            if (!TextUtil.isEmpty(defaultEntrys.getThreeDefaultValueId())) {
                entryBean = new EntryBean(defaultEntrys.getThreeDefaultValueId(), defaultEntrys.getThreeDefaultValue(), defaultEntrys.getThreeDefaultValueColor());
                selectEntryBean.add(entryBean);
            }
        }
        setPickValue(selectEntryBean);
        setOtherEntryBean();
    }

    /**
     * 设置二三级的选项
     */
    private void setOtherEntryBean() {
        if (selectEntryBean.size() > 0) {
            Observable.from(entrys1)
                    .filter(entry1 -> entry1.getLabel().equals(selectEntryBean.get(0).getLabel()))//zzh:value比对改为lable比对
                    .subscribe(entry1 -> {
                        entry1.setCheck(true);
                        selectEntryBean.get(0).setColor(entry1.getColor());
                        selectEntryBean.get(0).setValue(entry1.getValue());
                        entrys2.clear();
                        CollectionUtils.addAll(entrys2, entry1.getSubList());
                    });
        }
        if (selectEntryBean.size() > 1) {
            Observable.from(entrys2)
                    .filter(entry2 -> entry2.getLabel().equals(selectEntryBean.get(1).getLabel()))//zzh:value比对改为lable比对
                    .subscribe(entry2 -> {
                        entry2.setCheck(true);
                        selectEntryBean.get(1).setColor(entry2.getColor());
                        selectEntryBean.get(1).setValue(entry2.getValue());
                        entrys3.clear();
                        CollectionUtils.addAll(entrys3, entry2.getSubList());
                    });
        }
        if (selectEntryBean.size() > 2) {
            Observable.from(entrys3)
                    .filter(entry3 -> entry3.getLabel().equals(selectEntryBean.get(2).getLabel()))//zzh:value比对改为lable比对
                    .subscribe(entry3 -> {
                        entry3.setCheck(true);
                        selectEntryBean.get(2).setColor(entry3.getColor());
                        selectEntryBean.get(2).setValue(entry3.getValue());
                    });
        }
    }

    public Object getValue() {
        if (selectEntryBean.size() == 0) {
            return "";
        } else {
            JSONArray ja = new JSONArray();
            for (int i = 0; i < selectEntryBean.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("color", selectEntryBean.get(i).getColor());
                jo.put("label", selectEntryBean.get(i).getLabel());
                jo.put("value", selectEntryBean.get(i).getValue());
                ja.add(jo);
            }
            return ja;
        }
    }

    public void setContent(Object o) {
        setData(o);
    }

    /**
     * 设置选项值
     *
     * @param labels 集合
     */
    protected void setPickValue(List<EntryBean> labels) {
        setPickValueV2(labels);
        /*flowLayout1.removeAllViews();
        tvContent1.setVisibility(View.VISIBLE);
        flowLayout2.removeAllViews();
        tvContent1.setVisibility(View.VISIBLE);
        flowLayout3.removeAllViews();
        tvContent1.setVisibility(View.VISIBLE);
        for (int i = 0; i < labels.size(); i++) {
            EntryBean entryBean = labels.get(i);
            String color = entryBean.getColor();
            TextView view = new TextView(mView.getContext());
            view.setText(entryBean.getLabel());
            view.setTextSize(14);
            view.setGravity(Gravity.CENTER);
            if (TextUtil.isEmpty(color)) {
                view.setTextColor(ColorUtils.resToColor(getContext(), R.color.custom_content_color));
            } else {
                view.setTextColor(Color.WHITE);
                view.setPadding(12, 4, 12, 4);
                view.setBackgroundResource(R.drawable.custom_flow_label);
                GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                if ("#FFFFFF".equals(color)) {
                    view.setTextColor(ColorUtils.resToColor(getContext(), R.color.black_4a));
                }
                myGrad.setColor(ColorUtils.hexToColor(entryBean.getColor(), "#000000"));
            }
            switch (i) {
                case 0:
                    tvContent1.setVisibility(View.GONE);
                    flowLayout1.addView(view);
                    break;
                case 1:
                    tvContent2.setVisibility(View.GONE);
                    flowLayout2.addView(view);
                    break;
                case 2:
                    tvContent3.setVisibility(View.GONE);
                    flowLayout3.addView(view);
                    break;
            }
        }

        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            if (CollectionUtils.isEmpty(labels)) {
                ivRight1.setImageResource(R.drawable.icon_to_next);
                ivRight2.setImageResource(R.drawable.icon_to_next);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else if (labels.size() == 1) {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.icon_to_next);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else if (labels.size() == 2) {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.clear_button);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.clear_button);
                ivRight3.setImageResource(R.drawable.clear_button);
            }
        }*/
    }

    protected void setPickValueV2(List<EntryBean> labels) {
        flowLayout1.removeAllViews();
        tvContent1.setVisibility(View.VISIBLE);
        for (int i = 0; i < labels.size(); i++) {
            EntryBean entryBean = labels.get(i);
            String color = entryBean.getColor();
            RelativeLayout relativeLayout = new RelativeLayout(mView.getContext());
            TextView view = new TextView(mView.getContext());
            view.setText(entryBean.getLabel());
            view.setTextSize(14);
            view.setGravity(Gravity.CENTER);
            if (TextUtil.isEmpty(color)) {
                view.setTextColor(ColorUtils.resToColor(getContext(), R.color.custom_content_color));
            } else {
                view.setTextColor(Color.WHITE);
                view.setPadding(12, 4, 12, 4);
                view.setBackgroundResource(R.drawable.custom_flow_label);
                GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                if ("#FFFFFF".equals(color)) {
                    view.setTextColor(ColorUtils.resToColor(getContext(), R.color.black_4a));
                }
                myGrad.setColor(ColorUtils.hexToColor(entryBean.getColor(), "#000000"));
            }
            tvContent1.setVisibility(View.GONE);
            relativeLayout.addView(view);
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = 10;
            flowLayout1.addView(relativeLayout);
        }

        /*if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            if (CollectionUtils.isEmpty(labels)) {
                ivRight1.setImageResource(R.drawable.icon_to_next);
                ivRight2.setImageResource(R.drawable.icon_to_next);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else if (labels.size() == 1) {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.icon_to_next);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else if (labels.size() == 2) {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.clear_button);
                ivRight3.setImageResource(R.drawable.icon_to_next);
            } else {
                ivRight1.setImageResource(R.drawable.clear_button);
                ivRight2.setImageResource(R.drawable.clear_button);
                ivRight3.setImageResource(R.drawable.clear_button);
            }
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code && resultCode == Activity.RESULT_OK && data != null) {
            selectEntryBean = (List<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            setPickValueV2(selectEntryBean);
        }
    }
}
