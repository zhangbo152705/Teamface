package com.hjhq.teamface.customcomponent.widget2.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.customcomponent.adapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class MultiGradeSelectActivity extends ActivityPresenter<MultiGradeSelectDelegate, CommonModel> {
    private SelectAdapter mSelectAdapter1;
    private SelectAdapter mSelectAdapter2;
    private SelectAdapter mSelectAdapter3;
    private ArrayList<EntryBean> entryList1 = new ArrayList<>();
    private ArrayList<EntryBean> entryList2 = new ArrayList<>();
    private ArrayList<EntryBean> entryList3 = new ArrayList<>();
    private ArrayList<EntryBean> selected = new ArrayList<>();
    private EntryBean entry1;
    private EntryBean entry2;
    private EntryBean entry3;
    private int level = 1;
    private int selectType;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            selectType = getIntent().getIntExtra(Constants.DATA_TAG3, -1);
            entryList1 = (ArrayList<EntryBean>) getIntent().getSerializableExtra(Constants.DATA_TAG1);
            selected = (ArrayList<EntryBean>) getIntent().getSerializableExtra(Constants.DATA_TAG2);
        }
        if (selectType == -1) {
            ToastUtils.showError(mContext, "数据错误");
            finish();
            return;
        }
    }

    @Override
    public void init() {
        viewDelegate.showMenu();
        initAdapter();
    }

    private void showMenu() {
        viewDelegate.showMenu();
        if (selectType == 0 && level == 2) {
            viewDelegate.showMenu(0);
        } else if (selectType == 1 && level == 3) {
            viewDelegate.showMenu(0);
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView1.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                level = 2;
                showMenu();

                List<EntryBean> data = mSelectAdapter1.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (position == i) {
                        data.get(position).setCheck(true);
                        entry1 = data.get(position);
                    } else {
                        data.get(position).setCheck(false);
                    }
                }
                mSelectAdapter1.notifyDataSetChanged();
                entryList2.clear();
                entryList2.addAll(entry1.getSubList());
                for (int i = 0; i < entryList2.size(); i++) {
                    if (selected.size() >= 2) {
                        final EntryBean entryBean = selected.get(1);
                        entryList2.get(i).setCheck(entryBean.getValue().equals(entryList2.get(i).getValue()));
                    }
                }
                mSelectAdapter2.setNewData(entryList2);
                viewDelegate.levelChange(level);
            }
        });
        viewDelegate.mRecyclerView2.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                List<EntryBean> data = mSelectAdapter2.getData();
                if (selectType == 0) {
                    boolean check = data.get(position).isCheck();
                    for (int i = 0; i < mSelectAdapter2.getData().size(); i++) {
                        mSelectAdapter2.getData().get(i).setCheck(false);

                    }
                    entry2 = mSelectAdapter2.getItem(position);
                    mSelectAdapter2.getData().get(position).setCheck(!check);
                    mSelectAdapter2.notifyDataSetChanged();
                } else if (selectType == 1) {
                    level = 3;
                    showMenu();

                    boolean check = data.get(position).isCheck();
                    for (int i = 0; i < mSelectAdapter2.getData().size(); i++) {
                        mSelectAdapter2.getData().get(i).setCheck(false);

                    }
                    entry2 = mSelectAdapter2.getItem(position);
                    mSelectAdapter2.getData().get(position).setCheck(!check);
                    mSelectAdapter2.notifyDataSetChanged();
                    entryList3.clear();
                    entryList3.addAll(entry2.getSubList());
                    for (int i = 0; i < entryList3.size(); i++) {
                        if (selected.size() >= 3) {
                            final EntryBean entryBean = selected.get(2);
                            entryList3.get(i).setCheck(entryBean.getValue().equals(entryList3.get(i).getValue()));
                        }
                    }
                    mSelectAdapter3.setNewData(entryList3);
                    viewDelegate.levelChange(level);
                }


            }
        });
        viewDelegate.mRecyclerView3.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                List<EntryBean> data = mSelectAdapter3.getData();
                boolean check = data.get(position).isCheck();
                for (int i = 0; i < mSelectAdapter3.getData().size(); i++) {
                    mSelectAdapter3.getData().get(i).setCheck(false);

                }
                entry3 = mSelectAdapter3.getItem(position);
                mSelectAdapter3.getData().get(position).setCheck(!check);
                /*for (int i = 0; i < mSelectAdapter3.getData().size(); i++) {
                    mSelectAdapter3.getData().get(position).setCheck(false);
                }
                mSelectAdapter3.getData().get(position).setCheck(true);
                entry3 = mSelectAdapter3.getData().get(position);*/
                // mSelectAdapter3.setNewData(data);
                mSelectAdapter3.notifyDataSetChanged();
            }
        });

    }

    private void initAdapter() {
        if (selected != null && selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                final EntryBean entryBean = selected.get(i);

                if (i == 0 && entryList1 != null) {
                    for (int j = 0; j < entryList1.size(); j++) {
                        final EntryBean entryBean1 = entryList1.get(j);
                        if (entryBean.getValue() != null && entryBean.getValue().equals(entryBean1.getValue())) {
                            entryList1.get(j).setCheck(true);
                        }
                    }
                } /*else if (i == 1) {
                    for (int j = 0; j < entryList2.size(); j++) {
                        final EntryBean entryBean1 = entryList2.get(j);
                        if (entryBean.getValue() != null && entryBean.getValue().equals(entryBean1.getValue())) {
                            entryList2.get(j).setCheck(true);
                        }
                    }
                } else if (i == 2) {
                    for (int j = 0; j < entryList3.size(); j++) {
                        final EntryBean entryBean1 = entryList3.get(j);
                        if (entryBean.getValue() != null && entryBean.getValue().equals(entryBean1.getValue())) {
                            entryList3.get(j).setCheck(true);
                        }
                    }
                }*/
            }

        }
        mSelectAdapter1 = new SelectAdapter(null);
        mSelectAdapter2 = new SelectAdapter(null);
        mSelectAdapter3 = new SelectAdapter(null);
        viewDelegate.setAdapter1(mSelectAdapter1);
        viewDelegate.setAdapter2(mSelectAdapter2);
        viewDelegate.setAdapter3(mSelectAdapter3);
        mSelectAdapter1.setNewData(entryList1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<EntryBean> list = new ArrayList<>();
        list.add(entry1);
        if (selectType == 0) {
            boolean flag = false;
            for (int i = 0; i < entryList2.size(); i++) {
                if (entryList2.get(i).isCheck()) {
                    flag = true;
                }
            }
            if (!flag) {
                ToastUtils.showToast(mContext, "请选择");
                return true;
            }
            list.add(entry2);

        } else if (selectType == 1) {
            boolean flag = false;
            for (int i = 0; i < entryList3.size(); i++) {
                if (entryList3.get(i).isCheck()) {
                    flag = true;
                }
            }
            if (!flag) {
                ToastUtils.showToast(mContext, "请选择");
                return true;
            }
            list.add(entry2);
            list.add(entry3);
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.DATA_TAG1, list);
        setResult(RESULT_OK, intent);
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (level == 1) {
            finish();
        } else if (level == 2) {
            level = 1;
            viewDelegate.levelChange(level);
            entryList2.clear();
            mSelectAdapter2.setNewData(new ArrayList<>());
            entry2 = null;
        } else if (level == 3) {
            level = 2;
            viewDelegate.levelChange(level);
            entryList3.clear();
            mSelectAdapter3.setNewData(new ArrayList<>());
            entry3 = null;
        }
    }
}
