package com.hjhq.teamface.attendance.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;

import java.util.List;


public class AttendanceApprovalAdapter extends BaseQuickAdapter<AttendanceApproveListBean.DataBean.DataListBean, BaseViewHolder> {

    public AttendanceApprovalAdapter(List<AttendanceApproveListBean.DataBean.DataListBean> data) {
        super(R.layout.attendance_approval_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttendanceApproveListBean.DataBean.DataListBean item) {
        //helper.addOnClickListener(R.id.edit);
        helper.addOnClickListener(R.id.delete);
        helper.setText(R.id.name, item.getChinese_name());
        String status = "";
        if (!TextUtils.isEmpty(item.getRelevance_status())) {
            switch (item.getRelevance_status()) {
                case "0":
                    status = "缺卡";
                    break;
                case "1":
                    status = "请假";//zzh->ad:新增 下面五种状态

                    break;
                case "2":
                    status = "加班";
                    break;
                case "3":
                    status = "出差";
                    break;
                case "4":
                    status = "销假";
                    break;
                case "5":
                    status = "外出";
                    break;


            }
        }
        helper.setText(R.id.desc, "修正状态:" + status);

    }
}