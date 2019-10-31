package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.SelectCommonView;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间组件
 *
 * @author lx
 * @date 2017/8/23
 */

public class TimeSelectView extends SelectCommonView implements ActivityPresenter.OnActivityResult {
    private Calendar calendar = Calendar.getInstance();

    private String datetimeType;
    private boolean flag = false;
    private View bottom_line;
    private String defaultTitle = "";

    /**
     * 0 没有默认值 1 当前时间  2 指定时间
     */
    private String defaultValueId;

    public TimeSelectView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            defaultValueId = TextUtil.getNonNull(field.getDefaultValueId(), "0");
            datetimeType = TextUtil.getNonNull(field.getFormatType(), "yyyy-MM-dd");
        }
    }

    public TimeSelectView(CustomBean bean,boolean flag,String defaultTitle) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            defaultValueId = TextUtil.getNonNull(field.getDefaultValueId(), "0");
            datetimeType = TextUtil.getNonNull(field.getFormatType(), "yyyy-MM-dd");
        }
        this.flag = flag;
        this.defaultTitle = defaultTitle;
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
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvContent.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    if (TextUtil.isEmpty(s.toString())) {
                        ivRight.setImageResource(R.drawable.custom_icon_date);
                    } else {
                        ivRight.setImageResource(R.drawable.clear_button);
                    }
                }
            });
            ivRight.setOnClickListener(v -> {
                String content = tvContent.getText().toString();
                if (!TextUtil.isEmpty(content)) {
                    calendar.setTime(new Date());
                    TextUtil.setText(tvContent, "");
                }
            });
        }
        bottom_line = mView.findViewById(R.id.bottom_line);
        if (flag){
            tvTitle.setVisibility(View.GONE);
            bottom_line.setVisibility(View.GONE);
        }
        super.initView();
    }

    @Override
    public void setContent(Object o) {
        super.setContent(o);
        setData(o);
    }

    @Override
    public void initOption() {
        ivRight.setImageResource(R.drawable.custom_icon_date);
        if (isDetailState() && checkNull()) {
            if (flag){
                tvContent.setText(defaultTitle);
            }else{
                tvContent.setHint("未选择");
            }
            tvContent.setHintTextColor(getContext().getResources().getColor(R.color._999999));
            ivRight.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setData(Object value) {
        if (isDetailState() && (value == null || TextUtils.isEmpty(value + ""))) {
            tvContent.setHint("未选择");
            tvContent.setHintTextColor(getContext().getResources().getColor(R.color._999999));
            return;
        }
        if (value == null || TextUtils.isEmpty(value + "")) {
            TextUtil.setText(tvContent, "");
            return;
        }
        if ("".equals(value)) {
            value = "0";
        }
        try {
            long l = new BigDecimal(value + "").longValue();
//        long l = TextUtil.parseLong(value + "");
            if (l != 0) {
                calendar.setTimeInMillis(l);
                setDateTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick() {
        super.onClick();
        showDialogPick();
    }

    @Override
    public boolean formatCheck() {
        return true;
    }


    /**
     * 将两个选择时间的dialog放在该函数中
     */
    private void showDialogPick() {
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, calendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, datetimeType);
        CommonUtil.startActivtiyForResult(getContext(), DateTimeSelectPresenter.class, code, bundle);
    }

    /**
     * 设置时间
     */
    private void setDateTime() {
        Log.e("TimeSelectView","time:"+calendar.getTime().getTime());
        String date = DateTimeUtil.longToStr(calendar.getTime().getTime(), datetimeType);
        TextUtil.setText(tvContent, date);
    }

    public long getTimeByTimeType(String type,Calendar mcalendar){
        long time = 0;
        if (type != null && mcalendar != null){
            String date = DateTimeUtil.longToStr(calendar.getTime().getTime(), type);
            time = DateTimeUtil.strToLong(date,type);
        }
        return time;
    }

    @Override
    public void setDefaultValue() {
        if ("1".equals(defaultValueId)) {
            calendar.setTime(new Date());
            setDateTime();
        } else if ("2".equals(defaultValueId)) {
            long l = TextUtil.parseLong(defaultValue);
            if (l != 0) {
                calendar.setTime(new Date(l));
                setDateTime();
            }
        }
    }

    @Override
    public Object getValue() {
        if (TextUtil.isEmpty(tvContent.getText().toString())) {
            return "";
        }

        if (!TextUtil.isEmpty(tvContent.getText().toString()) && (tvContent.getText().toString().equals("开始时间")
                || tvContent.getText().toString().equals("截止时间"))) {
            return "";
        }
        //zzh->ad:增加根据datetimeType 返回时间戳
        if (datetimeType != null && getTimeByTimeType(datetimeType,calendar)>0){
            return  getTimeByTimeType(datetimeType,calendar);
        }
        return calendar.getTime().getTime();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == Activity.RESULT_OK) {
                this.calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
                setDateTime();
            } else if (resultCode == Constants.CLEAR_RESULT_CODE) {
                calendar.setTime(new Date());
                TextUtil.setText(tvContent, "");
            }
            if (!checkNull() && isLinkage) {
                linkageData();
            }
        }
    }
}
