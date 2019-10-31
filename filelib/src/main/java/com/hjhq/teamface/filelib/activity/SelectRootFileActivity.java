package com.hjhq.teamface.filelib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.AttachmentBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.filelib.FilelibModel;
import com.hjhq.teamface.filelib.R;
import com.hjhq.teamface.filelib.adapter.RootFolderAdapter;
import com.hjhq.teamface.filelib.bean.RootFolderResBean;
import com.hjhq.teamface.filelib.view.SelectRootFileDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

@RouteNode(path = "/select_file", desc = "从文件库选择文件")
public class SelectRootFileActivity extends ActivityPresenter<SelectRootFileDelegate, FilelibModel> {


    private int folderLevel = 0;
    private int tempFolderLevel = 0;
    private RecyclerView mRvNavi;
    private RecyclerView mRvFileList;
    private RootFolderAdapter mAdapter;
    private ArrayList<RootFolderResBean.DataBean> dataList = new ArrayList<>();

    @Override
    public void init() {
        initView();
        initData();
    }


    protected void initView() {
        viewDelegate.setTitle("选择文件");
        mRvNavi = (RecyclerView) findViewById(R.id.rv_navi);
        mRvFileList = (RecyclerView) findViewById(R.id.rv_department);
        mAdapter = new RootFolderAdapter(dataList);
        mRvFileList.setLayoutManager(new LinearLayoutManager(SelectRootFileActivity.this));
        mRvFileList.setAdapter(mAdapter);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        mRvFileList.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                if ("6".equals(dataList.get(position).getId())) {
                    bundle.putBoolean(Constants.DATA_TAG1, true);
                    CommonUtil.startActivtiyForResult(mContext, ProjectListActivity.class, Constants.REQUEST_CODE1, bundle);

                } else {
                    bundle.putInt(FileConstants.FOLDER_STYLE, TextUtil.parseInt(dataList.get(position).getId()));
                    bundle.putInt(FileConstants.FOLDER_LEVEL, 0);
                    bundle.putString(FileConstants.FOLDER_NAME, getRootFolderName(position));
                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/select_filelibrary_file", bundle, Constants.REQUEST_CODE1);
                }


            }
        });
    }


    protected void initData() {
        loadCacheData();
        getRootFolder();
    }


    /**
     * 根目录文件夹列表
     */
    private void getRootFolder() {

        model.queryfileCatalog(SelectRootFileActivity.this,
                new ProgressSubscriber<RootFolderResBean>(SelectRootFileActivity.this, false) {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RootFolderResBean rootFolderResBean) {
                        dataList.clear();

                        List<RootFolderResBean.DataBean> data = rootFolderResBean.getData();
                        for (int i = 0; i < data.size(); i++) {
                            //去除我共享的和与我共享
                            if ("4".equals(data.get(i).getId()) || "5".equals(data.get(i).getId())) {
                                continue;
                            } else {
                                dataList.add(data.get(i));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.FILE_LIB_CACHE_DATA, FileConstants.ROOT_LIB);
        if (!TextUtils.isEmpty(cacheData)) {
            List<RootFolderResBean.DataBean> list =
                    new Gson().fromJson(cacheData, new TypeToken<List<RootFolderResBean.DataBean>>() {
                    }.getType());
            dataList.clear();
            if (list != null && list.size() > 0) {
                if (list.size() > 3) {
                    for (int i = 0; i < 3; i++) {
                        dataList.add(list.get(i));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void jumpFolder(MessageBean bean) {
        if (bean.getTag().equals(Constants.MOVE_FILE_JUMP_FOLDER) && bean.getCode() < folderLevel) {
            finish();
        }
        if (bean.getTag().equals(FileConstants.MOVE_OR_COPY_OK)) {
            setResult(Activity.RESULT_OK);
            finish();
        }

    }

    /**
     * 获取根目录文件夹名字
     *
     * @param folderStyle
     * @return
     */
    private String getRootFolderName(int folderStyle) {
        switch (folderStyle) {
            case 1:
                return "公司文件";
            case 2:
                return "应用文件";
            case 3:
                return "个人文件";
            case 4:
                return "我的共享";
            case 5:
                return "与我共享";
            default:
                break;

        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_CODE1) {
            AttachmentBean bean = null;
            if (data != null) {
                bean = (AttachmentBean) data.getSerializableExtra(Constants.DATA_TAG1);
            }
            Intent i = new Intent();
            i.putExtra(Constants.DATA_TAG1, bean);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (0 == folderLevel) {
                finish();
            } else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
