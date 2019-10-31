package com.hjhq.teamface.customcomponent.widget2.base;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class FileView extends BaseView {
    protected RecyclerView mRecyclerView;
    protected Activity mActivity;
    protected TextView tvTitle;
    protected RelativeLayout rlAdd;
    protected RelativeLayout rlEmpty;
    protected LinearLayout llContent;
    protected View bottom_line;

    protected int countLimit;
    protected int maxCount;
    protected String maxSize;

    protected List<UploadFileBean> uploadFileBeanList = new ArrayList<>();

    public FileView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            countLimit = TextUtil.parseInt(field.getCountLimit());
            maxCount = TextUtil.parseInt(field.getMaxCount());
            maxSize = field.getMaxSize();
        }
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        this.mActivity = activity;
        if ("0".equals(structure)) {
            mView = View.inflate(mActivity, R.layout.custom_item_attachment_vertical_view, null);
        } else {
            mView = View.inflate(mActivity, R.layout.custom_item_attachment_vertical_view_row, null);
        }
        llContent = mView.findViewById(R.id.ll_content);
        mRecyclerView = mView.findViewById(R.id.recyler_upload_file);
        tvTitle = mView.findViewById(R.id.tv_title);
        rlAdd = mView.findViewById(R.id.rl_add);
        rlEmpty = mView.findViewById(R.id.rl_empty);
        bottom_line = mView.findViewById(R.id.bottom_line);
        initView();

        parent.addView(mView, index);
        initOption();
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
    }

    private void initView() {
        Object value = bean.getValue();
        if (value != null && !"".equals(value)) {
            setData(bean.getValue());
        }
        if (state != CustomConstants.DETAIL_STATE && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            rlAdd.setVisibility(View.VISIBLE);
        } else {
            rlAdd.setVisibility(View.GONE);
            if (uploadFileBeanList.size() <= 0) {
                rlEmpty.setVisibility(View.VISIBLE);
            } else {
                rlEmpty.setVisibility(View.GONE);
            }
        }
        if (CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvTitle.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
        }
    }

    @Override
    protected void setData(Object value) {
        try {
            if (value instanceof JSONArray) {
                uploadFileBeanList = JSONArray.parseArray(value + "", UploadFileBean.class);
            } else if (value instanceof List) {
                List<Map<String, Object>> value1 = (List<Map<String, Object>>) value;
                for (Map<String, Object> map : value1) {
                    UploadFileBean uploadFileBean = JSONObject.parseObject(JSON.toJSONString(map), UploadFileBean.class);
                    uploadFileBeanList.add(uploadFileBean);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public abstract void initOption();

    public abstract void uploadFile();

    public abstract Object getValue();


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


    protected boolean checkFileSize(File file) {
        long maxSizeLong = TextUtil.parseLong(maxSize) * 1024 * 1024;
        if (countLimit == 0) {
            return true;
        } else if (file.length() < maxSizeLong) {
            return true;
        } else {
            return false;
        }
    }
}
