package com.hjhq.teamface.filelib.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.adapter.FileNaviAdapter;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.FolderNaviData;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.UriUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.filelib.FilelibModel;
import com.hjhq.teamface.filelib.R;
import com.hjhq.teamface.filelib.adapter.MoveFileListAdapter;
import com.hjhq.teamface.filelib.bean.FileListResBean;
import com.hjhq.teamface.filelib.view.MoveFileDelegate;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoveFileActivity extends ActivityPresenter<MoveFileDelegate, FilelibModel> {
    //移动文件
    public static final int MOVE_FILE = 100;
    //复制文件
    public static final int COPY_FILE = 200;

    private int flag = -1;
    private int folderLevel = 1;
    private RecyclerView mRvNavi;
    private RecyclerView mRvFileList;
    private MoveFileListAdapter mFileAdapter;
    private FileNaviAdapter mNaviAdapter;
    private Bundle mBundle;
    private String folderName = "";
    private String folderId;
    private int folderStyle;
    private ArrayList<FolderNaviData> naviData = new ArrayList<>();

    private String fileId;
    private String chooseFolderId;

    //分页
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    //总数
    private int totalNum = 0;
    private int state = Constants.NORMAL_STATE;
    private int pageSize = 20;
    private ArrayList<FileListResBean.DataBean.DataListBean> mDataList = new ArrayList<>();

    @Override
    public void init() {
        initView();
        setListener();
    }

    protected void initView() {
        mBundle = getIntent().getExtras();

        if (mBundle != null) {
            folderLevel = mBundle.getInt(FileConstants.FOLDER_LEVEL);
            folderName = mBundle.getString(FileConstants.FOLDER_NAME);
            folderId = mBundle.getString(FileConstants.FOLDER_ID);
            folderStyle = mBundle.getInt(FileConstants.FOLDER_STYLE);
            fileId = mBundle.getString(FileConstants.FILE_ID);
            flag = mBundle.getInt(FileConstants.MOVE_OR_COPY, -1);
            naviData = (ArrayList<FolderNaviData>) mBundle.getSerializable(FileConstants.FOLDER_NAVI_DATA);
            if (naviData == null) {
                naviData = new ArrayList<>();
            }
            //文件夹名字
            if (flag == FileConstants.COPY) {
                viewDelegate.setTitle("复制到");
            }
            if (flag == FileConstants.MOVE) {
                viewDelegate.setTitle("移动到");
            }
            if (flag == FileConstants.CONVERT) {
                viewDelegate.setTitle("转为公司文件");
            }
        }


        mRvNavi = (RecyclerView) findViewById(R.id.rv_navi);
        mRvFileList = (RecyclerView) findViewById(R.id.rv_department);
        mFileAdapter = new MoveFileListAdapter(folderLevel, mDataList);
        mRvFileList.setLayoutManager(new LinearLayoutManager(MoveFileActivity.this));
        mRvFileList.setAdapter(mFileAdapter);
        LinearLayoutManager naviLm = new LinearLayoutManager(MoveFileActivity.this);
        naviLm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvNavi.setLayoutManager(naviLm);
        mNaviAdapter = new FileNaviAdapter(naviData);
        mRvNavi.setAdapter(mNaviAdapter);
        loadCacheData();
        getNetData();

    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.FILE_LIB_FOLDER_CACHE_DATA, folderStyle + "_" + folderId);
        if (!TextUtils.isEmpty(cacheData)) {
            List<FileListResBean.DataBean.DataListBean> cacheDataList = new Gson().fromJson(cacheData, new TypeToken<List<FileListResBean.DataBean.DataListBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                mDataList.clear();
                mDataList.addAll(cacheDataList);
                mFileAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void setListener() {
        mRvFileList.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                System.gc();
                for (FileListResBean.DataBean.DataListBean data : mDataList) {
                    data.setChecked(false);
                }
                mFileAdapter.notifyDataSetChanged();
                Bundle bundle = new Bundle();
                bundle.putInt(FileConstants.FOLDER_TYPE, folderStyle);
                bundle.putInt(FileConstants.FOLDER_STYLE, folderStyle);
                bundle.putString(FileConstants.FOLDER_ID, mDataList.get(position).getId());
                bundle.putString(FileConstants.FOLDER_NAME, mDataList.get(position).getName());
                bundle.putInt(FileConstants.FOLDER_LEVEL, folderLevel + 1);
                bundle.putInt(FileConstants.MOVE_OR_COPY, flag);
                bundle.putString(FileConstants.FILE_ID, fileId);
                ArrayList<FolderNaviData> list = new ArrayList<>();
                list.addAll(naviData);
                FolderNaviData c = new FolderNaviData();
                c.setFolderName(mFileAdapter.getData().get(position).getName());
                c.setFloderType(folderStyle);
                c.setFolderLevel(folderLevel + 1);
                c.setFolderId(mDataList.get(position).getId());
                list.add(c);
                bundle.putSerializable(FileConstants.FOLDER_NAVI_DATA, list);
                CommonUtil.startActivtiy(MoveFileActivity.this, MoveFileActivity.class, bundle);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.bg) {

                    for (FileListResBean.DataBean.DataListBean data : mDataList) {
                        data.setChecked(false);
                    }
                    mDataList.get(position).setChecked(true);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        mRvNavi.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                System.gc();
                EventBusUtils.sendEvent(new MessageBean(position, Constants.MOVE_FILE_JUMP_FOLDER, null));

            }
        });
        //监听下拉刷新
        viewDelegate.mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                /*viewDelegate.mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        viewDelegate.mRefreshLayout.finishRefresh();
                    }
                }, 500);*/
            }
        });
        //上拉加载
        viewDelegate.mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mFileAdapter.getData().size() >= totalNum) {
                    viewDelegate.mRefreshLayout.finishLoadMore();
                    return;
                }
                state = Constants.LOAD_STATE;
                getNetData();
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        state = Constants.REFRESH_STATE;
        getNetData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).isChecked()) {
                chooseFolderId = mDataList.get(i).getId();
            }
        }
        //进入空文件夹
        if (mDataList.size() <= 0 && folderLevel >= 1) {
            chooseFolderId = folderId;
        }
        if (TextUtils.isEmpty(chooseFolderId)) {
            ToastUtils.showToast(mContext, "请选择文件夹!");
            return true;
        }

        if (flag == FileConstants.MOVE || flag == FileConstants.CONVERT) {
            if (fileId.equals(chooseFolderId)) {
                ToastUtils.showError(mContext, "");
                return true;
            }
            model.shiftFileLibrary(MoveFileActivity.this, chooseFolderId, fileId,
                    folderStyle + "", new ProgressSubscriber<BaseBean>(MoveFileActivity.this, true) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);

                            e.printStackTrace();
                            //ToastUtils.showError(MoveFileActivity.this, "没有权限复制或移动此目录");
                            //ToastUtils.showToast(MoveFileActivity.this, "操作失败", ToastUtils.FAILED);

                        }

                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);

                            if (baseBean.getResponse().getCode() == 1001) {
                                ToastUtils.showSuccess(MoveFileActivity.this, "操作成功");
                                EventBusUtils.sendEvent(new MessageBean(FileConstants.MOVE, FileConstants.MOVE_OR_COPY_OK, fileId));
                                EventBusUtils.sendEvent(new MessageBean(1, FileConstants.MOVE_FILE_SUCCESS, fileId));
                            } else {
                                ToastUtils.showError(mContext, baseBean.getResponse().getDescribe());
                            }

                        }
                    });
        } else if (flag == FileConstants.COPY) {
            model.copyFileLibrary(MoveFileActivity.this, chooseFolderId, fileId,
                    folderStyle + "", new ProgressSubscriber<BaseBean>(MoveFileActivity.this, true) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);

                            //ToastUtils.showToast(MoveFileActivity.this, "复制失败", ToastUtils.FAILED);
                        }

                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);

                            if (baseBean.getResponse().getCode() == 1001) {
                                ToastUtils.showSuccess(MoveFileActivity.this, "操作成功");
                                EventBusUtils.sendEvent(new MessageBean(1, FileConstants.MOVE_OR_COPY_OK, ""));

                            } else {
                                ToastUtils.showError(mContext, baseBean.getResponse().getDescribe());
                            }

                        }
                    });
        } else {
            ToastUtils.showToast(mContext, "数据错误!");
        }
        return super.onOptionsItemSelected(item);
    }


    private void getNetData() {
        currentPageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        pageSize = Constants.PAGESIZE;
        if (folderLevel >= 1) {
            model.queryFilePartList(MoveFileActivity.this, folderStyle + "", folderId, currentPageNo, pageSize,
                    FileConstants.FOLDER, new ProgressSubscriber<FileListResBean>(this, false) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);

                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(FileListResBean fileListResBean) {
                            super.onNext(fileListResBean);

                            showData(fileListResBean);
                        }
                    });
        } else {
            model.queryFileList(MoveFileActivity.this, folderStyle + "", currentPageNo, pageSize,
                    FileConstants.FOLDER, new ProgressSubscriber<FileListResBean>(this, false) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);

                            e.printStackTrace();

                        }

                        @Override
                        public void onNext(FileListResBean fileListResBean) {
                            super.onNext(fileListResBean);

                            showData(fileListResBean);
                        }
                    });
        }
    }

    /**
     * 显示数据
     *
     * @param fileListResBean
     */
    private void showData(FileListResBean fileListResBean) {
        viewDelegate.mRefreshLayout.finishLoadMore(true);
        viewDelegate.mRefreshLayout.finishRefresh(true);
        List<FileListResBean.DataBean.DataListBean> newDataList = new ArrayList<>();
        if (fileListResBean.getData() != null && fileListResBean.getData().getDataList() != null) {
            newDataList.addAll(fileListResBean.getData().getDataList());
        }

        List<FileListResBean.DataBean.DataListBean> oldDataList = mFileAdapter.getData();
        if (oldDataList == null) {
            oldDataList = new ArrayList<>();
        }
        switch (state) {
            case Constants.REFRESH_STATE:
            case Constants.NORMAL_STATE:
                oldDataList.clear();
                mFileAdapter.setEnableLoadMore(true);
                break;
            case Constants.LOAD_STATE:
                mFileAdapter.loadMoreEnd();
                break;
            default:
                break;
        }
        if (newDataList.size() > 0) {
            oldDataList.addAll(newDataList);
            mFileAdapter.notifyDataSetChanged();
        }
        PageInfo pageInfo = fileListResBean.getData().getPageInfo();
        totalPages = pageInfo.getTotalPages();
        currentPageNo = pageInfo.getPageNum();
        totalNum = pageInfo.getTotalRows();
        if (mFileAdapter.getData().size() == totalNum) {
            mFileAdapter.loadMoreComplete();
        }
        removeSelf();
        mFileAdapter.notifyDataSetChanged();
        if (currentPageNo == 1) {
            //缓存数据
            CacheDataHelper.saveCacheData(CacheDataHelper.FILE_LIB_FOLDER_CACHE_DATA,
                    folderStyle + "_" + folderId, JSONObject.toJSONString(mDataList));
        }
        if (totalNum == mFileAdapter.getData().size()) {
            viewDelegate.mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    /**
     * 去除自己
     */
    private void removeSelf() {
        List<FileListResBean.DataBean.DataListBean> data = mFileAdapter.getData();
        Iterator<FileListResBean.DataBean.DataListBean> iterator = data.iterator();
        while (iterator.hasNext()) {
            FileListResBean.DataBean.DataListBean next = iterator.next();
            if (fileId.equals(next.getId())) {
                iterator.remove();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //发送文件
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.CHOOSE_LOCAL_FILE) {
            Uri uri = data.getData();
            String path = UriUtil.getPhotoPathFromContentUri(MoveFileActivity.this, uri);
            File file = new File(path);

            if (file.exists()) {
                //  CommonUtil.showToast(file.getAbsolutePath());
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
