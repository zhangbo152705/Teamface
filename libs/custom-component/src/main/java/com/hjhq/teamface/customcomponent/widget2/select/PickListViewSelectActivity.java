package com.hjhq.teamface.customcomponent.widget2.select;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.customcomponent.adapter.SelectAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/10.
 */

public class PickListViewSelectActivity extends ActivityPresenter<MultiGradeSelectDelegate, CommonModel> {
    private SelectAdapter mSelectAdapter1;
    private ArrayList<EntryBean> entryList1 = new ArrayList<>();
    private boolean isMulti = false;
    private int fromType = 0;//选项类型 1:任务状态
    private String defValue;
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            entryList1 = (ArrayList<EntryBean>) getIntent().getSerializableExtra(Constants.DATA_TAG1);
            isMulti = getIntent().getBooleanExtra(Constants.DATA_TAG2, false);
            fromType =  getIntent().getIntExtra(Constants.DATA_TAG3,0);
        }

    }

    @Override
    public void init() {
        setDefState();
        if (isMulti) {
            viewDelegate.showMenu(0);
        } else {
            viewDelegate.showMenu();
        }
        initAdapter();
    }

    /**
     * 获取默认传过来的状态值
     */
    public void setDefState(){
        if (!CollectionUtils.isEmpty(entryList1)){
            for (EntryBean item :  entryList1){
                if (item != null && item.isCheck() && !TextUtil.isEmpty(item.getValue())){
                    defValue = item.getValue();
                }
            }
        }
    }
    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView1.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int fromType = entryList1.get(position).getFromType();//zzh->ad:
                    if (isMulti) {
                        if (fromType == Constants.STATE_FROM_PROJECR){//zzh->ad:任务状态改为单选
                            for (int i = 0; i < entryList1.size(); i++) {
                                entryList1.get(i).setCheck(false);
                            }
                        }
                        entryList1.get(position).setCheck(!entryList1.get(position).isCheck());
                        mSelectAdapter1.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < entryList1.size(); i++) {
                            entryList1.get(i).setCheck(false);
                        }
                        entryList1.get(position).setCheck(true);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.DATA_TAG1, entryList1);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

            }
        });
    }

    private void initAdapter() {
        mSelectAdapter1 = new SelectAdapter(entryList1);
        viewDelegate.setAdapter1(mSelectAdapter1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (fromType == 1){
            if(isSelectStopStatus() ==1){//暂停原因
                showStopStatus(1);
            }else if (isSelectStopStatus() == 2){//激活原因
                showStopStatus(2);
            }else {
                onclickSure("");
            }
        }else {
            onclickSure("");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击确定按钮
     * @param mark
     */
    public void onclickSure(String mark){
        Intent intent = new Intent();
        if (!TextUtil.isEmpty(mark)){
            intent.putExtra(Constants.DATA_TAG3, mark);
        }
        intent.putExtra(Constants.DATA_TAG1, entryList1);
        setResult(RESULT_OK, intent);
        finish();
    }

    public int isSelectStopStatus(){
        int flag = 0;
        if (!CollectionUtils.isEmpty(entryList1)){
            for (EntryBean item :  entryList1){
                if (item != null && item.isCheck() && !TextUtil.isEmpty(item.getValue())){
                    if (item.getValue().equals("2")){
                        flag = 1;
                    }else if (defValue.equals("3") && !item.getValue().equals("3")){
                        flag =2;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 弹框输入暂停原因
     */
    public void  showStopStatus(int type){
        String reason = "";
        if (type == 1){
            reason = "暂停原因";
        }else if (type ==2){
            reason = "激活原因";
        }
        DialogUtils.getInstance().inputDialog(mContext,reason , null, "必填", viewDelegate.getRootView(), content -> {
            if (TextUtil.isEmpty(content)) {
                ToastUtils.showError(mContext, "请输入原因");
                return false;
            }else {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onclickSure(content);
                    }
                });

            }
            return true;
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
