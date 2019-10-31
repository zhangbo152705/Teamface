package com.hjhq.teamface.attendance.presenter;

import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.PlugManageListAdapter;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.PlugManageDelegate;
import com.hjhq.teamface.basis.bean.PlugBean;
import com.hjhq.teamface.basis.bean.PlugListBean;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/plug_manage", desc = "插件管理")
public class PlugManageActivity extends ActivityPresenter<PlugManageDelegate, AttendanceModel>{

    PlugManageListAdapter mAdapter;
    List<PlugBean> dataList = new ArrayList<>();
    public long changeId;
    private int status;

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        viewDelegate.setTitle(R.string.attendance_clock_manger);
        setAdapter();
        getNetData(true);
    }

    private void setAdapter() {
        mAdapter = new PlugManageListAdapter(dataList,lisenter);
        viewDelegate.setAdapter(mAdapter);

    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("onItemChildClick","position:"+position);
                if (view.getId() == R.id.rl_check) {

                }
                super.onItemChildClick(adapter, view, position);
            }
        });

        super.bindEvenListener();
    }


    PlugManageListAdapter.OnItemClickLisenter lisenter = new PlugManageListAdapter.OnItemClickLisenter(){

        @Override
        public void onItemClick(PlugBean item) {
            if (item != null){
                status = TextUtil.parseInt(item.getPlugin_status());
                changeId = TextUtil.parseLong(item.getId());
                if (status == 0){
                    status = 1;
                }else {
                    status = 0;
                }
                changePlugStatus(changeId,status);
            }
        }
    };


    /**
     * 获取班次列表
     */
    private void getNetData(boolean flag) {
        model.getAttendancePlugList(mContext, new ProgressSubscriber<PlugListBean>(mContext, flag) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(PlugListBean plugListBean) {
                super.onNext(plugListBean);
                dataList.clear();
                dataList.addAll(plugListBean.getData().getDataList());
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 切换插件状态
     */
   public void changePlugStatus(long id,int pluginStatus){
       JSONObject obj = new JSONObject();
       obj.put("id",id);
       obj.put("pluginStatus",pluginStatus);
       model.closeOrOpenPlug(mContext,obj,new ProgressSubscriber<PlugListBean>(mContext, true) {
           @Override
           public void onError(Throwable e) {
               super.onError(e);
               mAdapter.notifyDataSetChanged();
           }

           @Override
           public void onNext(PlugListBean plugListBean) {
               super.onNext(plugListBean);
               handleStatusData();
           }
       });
   }

   public void handleStatusData(){
       if (!CollectionUtils.isEmpty(dataList)){
           for (PlugBean item : dataList){
                   if (changeId == TextUtil.parseLong(item.getId())){
                       item.setPlugin_status(status+"");
                   }
           }
           mAdapter.notifyDataSetChanged();
       }

   }


}
