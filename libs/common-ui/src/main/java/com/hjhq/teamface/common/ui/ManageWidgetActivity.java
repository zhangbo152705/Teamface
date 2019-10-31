package com.hjhq.teamface.common.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.WidgetListBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.adapter.ManageWidgetAdapter;
import com.hjhq.teamface.common.ui.member.ManageWidgetDelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 选人、选部门、选角色、动态选择
 *
 * @author Administrator
 * @date 2018/1/5
 */
public class ManageWidgetActivity extends ActivityPresenter<ManageWidgetDelegate, CommonModel> {
    ManageWidgetAdapter mWidgetAdapter1;
    ManageWidgetAdapter mWidgetAdapter2;
    private List<AppModuleBean> dataList = new ArrayList<>();
    private List<AppModuleBean> dataList1 = new ArrayList<>();
    private List<AppModuleBean> dataList2 = new ArrayList<>();
    private List<AppModuleBean> resultList1 = new ArrayList<>();
    private List<AppModuleBean> resultList2 = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;
    private String keyword = "";
    private boolean searchState = false;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            dataList = (List<AppModuleBean>) getIntent().getSerializableExtra(Constants.DATA_TAG1);
        }
    }

    @Override
    public void init() {
        initAdapter();
        initItemTouchHelper();
        dataList1.clear();
        dataList1.addAll(dataList);
        mWidgetAdapter1.getData().addAll(dataList);
        mWidgetAdapter1.notifyDataSetChanged();
        getNetData();
    }

    private void getNetData() {
        /*model.getCommonModules(mContext, new ProgressSubscriber<WidgetListBean>(mContext) {
            @Override
            public void onNext(WidgetListBean widgetListBean) {
                super.onNext(widgetListBean);


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });*/
        model.findAllModule(mContext, new ProgressSubscriber<WidgetListBean>(mContext) {
            @Override
            public void onNext(WidgetListBean widgetListBean) {
                super.onNext(widgetListBean);
                ArrayList<AppModuleBean> data = widgetListBean.getData();
                Iterator<AppModuleBean> iterator = data.iterator();
                while (iterator.hasNext()) {
                    final AppModuleBean next = iterator.next();
                    if ("workbench".equals(next.getEnglish_name())) {
                        iterator.remove();
                    }
                }
                if (dataList.size() > 0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        AppModuleBean appModuleBean = dataList.get(i);
                        Iterator<AppModuleBean> iterator2 = data.iterator();
                        while (iterator2.hasNext()) {
                            final AppModuleBean next = iterator2.next();
                            if (appModuleBean.getEnglish_name().equals(next.getEnglish_name())) {
                                iterator2.remove();
                            }
                        }
                    }
                }
                dataList2.addAll(data);
                mWidgetAdapter2.getData().clear();
                mWidgetAdapter2.getData().addAll(data);
                mWidgetAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

    }

    private void initItemTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (searchState) {
                    return true;
                }
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mWidgetAdapter1.getData(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mWidgetAdapter1.getData(), i, i - 1);
                    }
                }
                mWidgetAdapter1.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        mItemTouchHelper.attachToRecyclerView(viewDelegate.mRecyclerView1);
    }

    private void initAdapter() {
        mWidgetAdapter1 = new ManageWidgetAdapter(new ArrayList<>());
        mWidgetAdapter1.setType(1);
        mWidgetAdapter1.getData().addAll(dataList1);
        mWidgetAdapter2 = new ManageWidgetAdapter(new ArrayList<>());
        mWidgetAdapter2.setType(2);
        mWidgetAdapter2.getData().addAll(dataList2);
        viewDelegate.mRecyclerView1.setAdapter(mWidgetAdapter1);
        viewDelegate.mRecyclerView2.setAdapter(mWidgetAdapter2);
        viewDelegate.mRecyclerView1.addItemDecoration(new MyLinearDeviderDecoration(mContext));
        viewDelegate.mRecyclerView2.addItemDecoration(new MyLinearDeviderDecoration(mContext));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveData();
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        Map<String, Object> map = new HashMap<>();
        //final List<AppModuleBean> data = dataList1;
        for (int i = 0; i < dataList1.size(); i++) {//把从适配器获取改为从dataList1数组中获取数据
            dataList1.get(i).setData_type(1);
        }
        map.put("data", dataList1);
        model.saveWidgetList(mContext, map, new ProgressSubscriber<WidgetListBean>(mContext) {
            @Override
            public void onNext(WidgetListBean widgetListBean) {
                super.onNext(widgetListBean);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, ((ArrayList) dataList1));
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(ManageWidgetActivity.this,ManageWidgetActivity.this.getResources().getString(R.string.save_data_error));
            }
        });


    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView1.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                  AppModuleBean widgetBean = mWidgetAdapter1.getData().get(position);
               // if (searchState) {
                    mWidgetAdapter1.getData().remove(position);
                    Iterator<AppModuleBean> iterator = dataList1.iterator();
                    while (iterator.hasNext()) {
                         AppModuleBean next = iterator.next();
                        if (next.getEnglish_name().equals(widgetBean.getEnglish_name())) {
                            iterator.remove();
                            break;
                        }
                    }
                    mWidgetAdapter1.notifyDataSetChanged();
                    mWidgetAdapter2.getData().add(widgetBean);
                    dataList2.add(widgetBean);
                    mWidgetAdapter2.notifyDataSetChanged();

                /*} else {
                    mWidgetAdapter1.getData().remove(position);
                    mWidgetAdapter1.notifyDataSetChanged();
                    mWidgetAdapter2.getData().add(widgetBean);
                    mWidgetAdapter2.notifyDataSetChanged();
                }*/

            }
        });
        viewDelegate.mRecyclerView2.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final AppModuleBean widgetBean = mWidgetAdapter2.getData().get(position);
               // if (searchState) {
                    mWidgetAdapter2.getData().remove(position);
                    Iterator<AppModuleBean> iterator = dataList2.iterator();
                    while (iterator.hasNext()) {
                        final AppModuleBean next = iterator.next();
                        if (next.getEnglish_name().equals(widgetBean.getEnglish_name())) {
                            iterator.remove();
                            break;
                        }
                    }
                    mWidgetAdapter2.notifyDataSetChanged();
                    mWidgetAdapter1.getData().add(widgetBean);
                    dataList1.add(widgetBean);
                    mWidgetAdapter1.notifyDataSetChanged();
               /* } else {
                    mWidgetAdapter2.getData().remove(position);
                    mWidgetAdapter2.notifyDataSetChanged();
                    mWidgetAdapter1.getData().add(widgetBean);
                    mWidgetAdapter1.notifyDataSetChanged();
                }*/
            }
        });
        viewDelegate.get(R.id.iv_clear).setOnClickListener(v -> {
            viewDelegate.mEditText.setText("");
        });
        viewDelegate.mEditText.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                keyword = s.toString();
                if (!TextUtils.isEmpty(keyword)) {
                    viewDelegate.setVisibility(R.id.iv_clear, true);
                    searchData();
                } else {
                    viewDelegate.setVisibility(R.id.iv_clear, false);
                    clearResult();
                }
            }
        });
    }

    private void clearResult() {
        searchState = false;
        mWidgetAdapter1.getData().clear();
        mWidgetAdapter1.getData().addAll(dataList1);
        mWidgetAdapter1.notifyDataSetChanged();
        mWidgetAdapter2.getData().clear();
        mWidgetAdapter2.getData().addAll(dataList2);
        mWidgetAdapter2.notifyDataSetChanged();
    }

    private void searchData() {
        searchState = true;
        //zzh:dataList1和dataList2为原始数据不做任何清空操作

        //dataList1.clear();
       // dataList2.clear();
        resultList1.clear();
        resultList2.clear();
        //dataList1.addAll(mWidgetAdapter1.getData());
        //dataList2.addAll(mWidgetAdapter2.getData());
        for (int i = 0; i < dataList1.size(); i++) {
            if (!TextUtils.isEmpty(dataList1.get(i).getChinese_name()) && dataList1.get(i).getChinese_name().contains(keyword)) {
                resultList1.add(dataList1.get(i));
            }
        }
        for (int i = 0; i < dataList2.size(); i++) {
            if (!TextUtils.isEmpty(dataList2.get(i).getChinese_name()) && dataList2.get(i).getChinese_name().contains(keyword)) {
                resultList2.add(dataList2.get(i));
            }
        }
        mWidgetAdapter1.getData().clear();
        mWidgetAdapter1.getData().addAll(resultList1);
        mWidgetAdapter1.notifyDataSetChanged();
        mWidgetAdapter2.getData().clear();
        mWidgetAdapter2.getData().addAll(resultList2);
        mWidgetAdapter2.notifyDataSetChanged();


    }
}
