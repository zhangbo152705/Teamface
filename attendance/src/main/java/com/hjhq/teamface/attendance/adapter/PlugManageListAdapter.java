package com.hjhq.teamface.attendance.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.bean.PlugBean;
import com.hjhq.teamface.basis.util.TextUtil;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;


public class PlugManageListAdapter extends BaseQuickAdapter<PlugBean, BaseViewHolder> {

    OnItemClickLisenter mLisenter;
    public PlugManageListAdapter(List<PlugBean> data,OnItemClickLisenter lisenter) {
        super(R.layout.attendance_plug_manage_item, data);
        this.mLisenter = lisenter;

    }


    @Override
    protected void convert(BaseViewHolder helper, PlugBean item) {
        helper.setText(R.id.tv_name, item.getPlugin_name());
        int status = TextUtil.parseInt(item.getPlugin_status());
        SwitchButton sBtnTaskCheck  = helper.getView(R.id.clock_check);
        if (status == 0){
            sBtnTaskCheck.setChecked(false);
        }else {
            sBtnTaskCheck.setChecked(true);
        }
        sBtnTaskCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLisenter.onItemClick(item);
            }
        });

        helper.addOnClickListener(R.id.rl_check);
    }

    public interface  OnItemClickLisenter{
        void onItemClick(PlugBean item);
    }

}