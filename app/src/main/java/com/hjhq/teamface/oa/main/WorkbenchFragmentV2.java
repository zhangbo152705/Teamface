package com.hjhq.teamface.oa.main;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.BaseFragment;
import com.hjhq.teamface.basis.bean.AppListResultBean;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.AppModuleResultBean;
import com.hjhq.teamface.basis.bean.CommonModuleResultBean;
import com.hjhq.teamface.basis.bean.ImMessage;
import com.hjhq.teamface.basis.bean.LocalModuleBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.AppConstant;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.EventConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.bean.QueryBannerBean;
import com.hjhq.teamface.common.adapter.AppAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.ProjectDialog;
import com.hjhq.teamface.common.view.banner.AutoSwitchAdapter;
import com.hjhq.teamface.common.view.banner.AutoSwitchView;
import com.hjhq.teamface.common.view.banner.LoopModel;
import com.hjhq.teamface.oa.approve.ui.ApproveActivity;
import com.hjhq.teamface.oa.main.logic.MainLogic;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台
 *
 * @author lx
 * @date 2017/3/16
 */

public class WorkbenchFragmentV2 extends BaseFragment {

    RelativeLayout mRlRoot;
    AutoSwitchView topBanner;
    ImageView ivAppModule;
    SmartRefreshLayout mRefreshLayoutAll;
    RecyclerView mRvRecent;
    RecyclerView mRvSysApp;
    RecyclerView mRvMyApp;
    AppAdapter mAdapter1;
    AppAdapter mAdapter2;
    AppAdapter mAdapter3;
    ImageView mEmptyView;
    private Map<String, List<List<AppModuleBean>>> moduleData = new HashMap<>();

    /**
     * 我的应用里每页模块的数量
     */
    public static final int MY_APP_MODULE_COUNT = 9;


    private AutoSwitchAdapter mAdapter;
    List<LoopModel> mDatas = new ArrayList<LoopModel>();

    private boolean isRefresh = false;
    private int completeNum = 0;

    @Override
    protected int getContentView() {
        return R.layout.fragment_workbench_v2;
    }


    @Override
    protected void initView(View view) {
        findViews();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        mRlRoot.setPadding(0, statusBarHeight, 0, 0);
        initAdapter();
        mAdapter = new AutoSwitchAdapter(getActivity(), mDatas);
        topBanner.setAdapter(mAdapter);
        {
            //禁用懒加载,懒加载会导致程序从后台恢复时数据不正常的问题
            initData();
            firstLoad = false;
        }
        if (getActivity() instanceof MainActivity) {
            if (((MainActivity) getActivity()).getAppModuleBean() != null) {
                final AppModuleBean appModuleBean = ((MainActivity) getActivity()).getAppModuleBean();
                openModule(appModuleBean, appModuleBean.getId());
            }
        }

    }

    /**
     * 初始化应用列表参数及点击事件
     */
    private void initAdapter() {
        mRvRecent.setLayoutManager(new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvSysApp.setLayoutManager(new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvMyApp.setLayoutManager(new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mAdapter1 = new AppAdapter();
        mAdapter2 = new AppAdapter();
        mAdapter3 = new AppAdapter();
        mRvRecent.setAdapter(mAdapter1);
        mRvSysApp.setAdapter(mAdapter2);
        mRvMyApp.setAdapter(mAdapter3);
    }

    /**
     * 初始化view
     */
    private void findViews() {
        mEmptyView = getActivity().findViewById(R.id.iv_empty);
        mRlRoot = getActivity().findViewById(R.id.rl_workbeanch);
        topBanner = getActivity().findViewById(R.id.top_banner);
        ivAppModule = getActivity().findViewById(R.id.iv_app_module);
        ivAppModule.setVisibility(View.GONE);
        mRefreshLayoutAll = getActivity().findViewById(R.id.refresh_all_2);
        mRvRecent = getActivity().findViewById(R.id.rv_recent);
        mRvSysApp = getActivity().findViewById(R.id.rv_sys_app);
        mRvMyApp = getActivity().findViewById(R.id.rv_my_app);
    }

    /**
     * 初始数据
     */
    @Override
    protected void initData() {
        //顶部图片栏
        initBanner();
        //加载应用列表
        loadAppData();
    }


    /**
     * 加载应用数据
     */
    private void loadAppData() {

        getCommonUseAppList();

        getDiyAppList();

        getSystemAppList();
    }

    /**
     * 获取常用应用列表
     */
    private void getCommonUseAppList() {
        //常用应用
        MainLogic.getInstance().getQuicklyAdd(((BaseActivity) getActivity()),
                new ProgressSubscriber<CommonModuleResultBean>(((BaseActivity) getActivity()), false) {
                    @Override
                    public void onNext(CommonModuleResultBean appListResultBean) {
                        super.onNext(appListResultBean);
                        finishRefresh();
                        List<AppModuleBean> commonApplication = appListResultBean.getData();
                        mAdapter1.setNewData(commonApplication);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }
                });
    }

    /**
     * 获取自定义应用列表
     */
    private void getDiyAppList() {
        //自定义应用
        MainLogic.getInstance().getApplist(((BaseActivity) getActivity()),
                new ProgressSubscriber<AppListResultBean>(((BaseActivity) getActivity()), false) {
                    @Override
                    public void onNext(AppListResultBean moduleResultBean) {
                        super.onNext(moduleResultBean);
                        finishRefresh();
                        AppListResultBean.DataBean data = moduleResultBean.getData();
                        mAdapter3.setNewData(data.getMyApplication());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }
                });
    }

    /**
     * 获取系统应用列表
     */
    private void getSystemAppList() {
        //系统应用
        MainLogic.getInstance().findModuleList(((BaseActivity) getActivity()),
                new ProgressSubscriber<LocalModuleBean>(getActivity(), false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }

                    @Override
                    public void onNext(LocalModuleBean localModuleBean) {
                        super.onNext(localModuleBean);
                        finishRefresh();
                        mAdapter2.getData().clear();
                        List<AppModuleBean> systemList = getSystemList(localModuleBean);
                        if (systemList != null && systemList.size() > 0) {
                            mAdapter2.getData().addAll(systemList);
                        }
                        mAdapter2.notifyDataSetChanged();
                    }
                });
    }


    /**
     * 获取系统模块集合
     *
     * @return
     */
    private List<AppModuleBean> getSystemList(LocalModuleBean data) {
        List<AppModuleBean> systemList = new ArrayList<>();
        AppModuleBean data3 = new AppModuleBean();
        data3.setChinese_name("审批");
        data3.setEnglish_name(ApproveConstants.APPROVAL_MODULE_BEAN);
        systemList.add(data3);
        AppModuleBean data2 = new AppModuleBean();
        data2.setChinese_name("备忘录");
        data2.setEnglish_name(MemoConstant.BEAN_NAME);
        systemList.add(data2);

        List<LocalModuleBean.DataBean> list = data.getData();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                AppModuleBean data1 = new AppModuleBean();
                LocalModuleBean.DataBean bean = list.get(i);
                data1.setChinese_name(bean.getName());
                data1.setEnglish_name(bean.getBean());
                if ("1".equals(bean.getOnoff_status())) {
                    switch (bean.getBean()) {
                        case "email":
                            systemList.add(data1);
                            break;
                        case "project":
                            systemList.add(data1);
                            break;
                        case "library":
                            systemList.add(data1);
                            break;
                        case "attendance":
                            systemList.add(data1);
                            break;
                        case "repository_libraries":
                            systemList.add(data1);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return systemList;
    }

    /**
     * 初始化轮播图
     */
    private void initBanner() {
        List<String> companyBanner = new ArrayList();
        loadBanner(companyBanner);
        MainLogic.getInstance().queryCompanyBanner((RxAppCompatActivity) getActivity(), new ProgressSubscriber<QueryBannerBean>(getActivity(), false) {
            @Override
            public void onNext(QueryBannerBean queryBannerBean) {
                super.onNext(queryBannerBean);
                finishRefresh();
                String banners = queryBannerBean.getData().getBanner();
                if (!TextUtils.isEmpty(banners)) {
                    final JSONArray parse = JSONObject.parseArray(banners);
                    List<String> banner = new ArrayList<String>();
                    if (parse != null && parse.size() > 0) {
                        for (Object o : parse) {
                            banner.add(o + "");
                        }
                        loadBanner(banner);
                        topBanner.setVisibility(View.VISIBLE);
                    }else {
                        topBanner.setVisibility(View.GONE);
                    }
                }else {
                    topBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                topBanner.setVisibility(View.GONE);
                dismissWindowView();
                e.printStackTrace();
                finishRefresh();
            }
        });
    }

    /**
     * 加载轮播图
     *
     * @param companyBanner
     */
    private void loadBanner(List<String> companyBanner) {
        List<LoopModel> datas = new ArrayList<LoopModel>();
        LoopModel model = null;
        if (companyBanner == null) {
            model = new LoopModel("", R.drawable.banner_default_pic);
            datas.add(model);
        } else {
            for (int i = 0; i < companyBanner.size(); i++) {
                model = new LoopModel(companyBanner.get(i));
                datas.add(model);
            }
            if (datas.size() == 0) {
                model = new LoopModel("", R.drawable.banner_default_pic);
                datas.add(model);
            }
        }

        mAdapter.setNewData(datas);
    }


    @Override
    protected void setListener() {
        //setOnClicks(ivAppModule, tvCompanyName);
        ivAppModule.setOnClickListener(this);
        mRefreshLayoutAll.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshAllData();
                mRefreshLayoutAll.finishRefresh(3000);
            }
        });
        mRefreshLayoutAll.setEnableLoadMore(false);
        mRvMyApp.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AppModuleBean item = (AppModuleBean) adapter.getItem(position);
                getAppModule(item.getChinese_name(), item.getId());
            }
        });
        mRvSysApp.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AppModuleBean appModeluBean = (AppModuleBean) adapter.getItem(position);
                openModuleTemp(appModeluBean);
            }
        });
        mRvRecent.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AppModuleBean item = (AppModuleBean) adapter.getItem(position);
                openModule(item, item.getId());
            }
        });

    }

    private void refreshAllData() {
        moduleData.clear();
        isRefresh = true;
        completeNum = 0;
        initBanner();
        loadAppData();
    }


    public void openModule(AppModuleBean item, String moduleId) {
        String moduleBean = item.getEnglish_name();
        if (moduleBean == null) moduleBean = "";
        //系统模块
        Bundle bundle = new Bundle();
        switch (moduleBean) {
            case ApproveConstants.APPROVAL_MODULE_BEAN:
                bundle.putString(Constants.MODULE_BEAN, ApproveConstants.APPROVAL_MODULE_BEAN);
                CommonUtil.startActivtiy(getActivity(), ApproveActivity.class);
                break;
            case ProjectConstants.PROJECT_BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://project/main", bundle);
                break;
            case MemoConstant.BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/main", bundle);
                break;
            case EmailConstant.BEAN_NAME:
                bundle.putInt(Constants.DATA_TAG3, EmailConstant.ADD_NEW_EMAIL);
                UIRouter.getInstance().openUri(mContext, "DDComp://email/email", bundle);
                break;
            case FileConstants.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://filelib/netdisk", bundle);
                break;
            default:
                if (TextUtil.isEmpty(moduleId)) {
                    //ToastUtils.showError(mContext, "未获取到模块ID");
                    return;
                }
                bundle.putString(Constants.MODULE_BEAN, item.getEnglish_name());
                bundle.putString(Constants.NAME, item.getChinese_name());
                bundle.putString(Constants.MODULE_ID, moduleId);
                UIRouter.getInstance().openUri(mContext, "DDComp://custom/temp", bundle);
                break;
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setAppModuleBean(null);
        }
    }


    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onDataChange(ImMessage bean) {
        if (TextUtils.isEmpty(bean.getTag())) {
            return;
        }
        switch (bean.getTag()) {
            //应用被删除
            case EventConstant.TYPE_APPLICATION_DEL:
                //应用被修改
            case EventConstant.TYPE_APPLICATION_UPDATE:
                //模块被修改
            case EventConstant.TYPE_MODULE_UPDATE:
                moduleData.clear();
                getDiyAppList();
                getCommonUseAppList();
                break;
            default:
                break;
        }
    }

    /**
     * 获取应用下的模块集合
     *
     * @param appName
     * @param id
     */
    private void getAppModule(String appName, String id) {
        final List<List<AppModuleBean>> lists = moduleData.get(id);
        if (lists != null && lists.size() > 0) {
            showAppModule(appName, lists);
            return;
        }

        MainLogic.getInstance().getModule(((BaseActivity) getActivity()), id,
                new ProgressSubscriber<AppModuleResultBean>(((BaseActivity) getActivity()), true) {
                    @Override
                    public void onNext(AppModuleResultBean moduleResultBean) {
                        super.onNext(moduleResultBean);
                        finishRefresh();
                        ArrayList<AppModuleBean> data = moduleResultBean.getData();
                        if (data == null) {
                            data = new ArrayList<>();
                        }

                        List<List<AppModuleBean>> list = new ArrayList<>();
                        List<AppModuleBean> appModelu = new ArrayList<>(MY_APP_MODULE_COUNT);

                        for (AppModuleBean bean : data) {
                            appModelu.add(bean);
                            if (!list.contains(appModelu)) {
                                list.add(appModelu);
                            }
                            if (appModelu.size() == MY_APP_MODULE_COUNT) {
                                appModelu = new ArrayList<>();
                            }
                        }
                        moduleData.put(id, list);
                        showAppModule(appName, list);
                        // CacheDataHelper.saveCacheData(CacheDataHelper.CUSTOM_MODULE_CACHE_DATA, id, JSONObject.toJSONString(list));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }
                });
    }

    private void finishRefresh() {
        if (isRefresh) {
            completeNum++;
        }
        if (completeNum == 9) {
            mRefreshLayoutAll.finishRefresh();
            isRefresh = false;
        }
    }

    /**
     * 显示应用下的模块
     */
    public void showAppModule(String appName, List<List<AppModuleBean>> list) {

        ProjectDialog instance = ProjectDialog.getInstance();
        instance.showAppModule(appName, list, mRvMyApp, new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppModuleBean appModeluBean = (AppModuleBean) adapter.getItem(position);
                openModuleTemp(appModeluBean);
            }


            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                AppModuleBean appModeluBean = (AppModuleBean) adapter.getItem(position);
                String name = appModeluBean.getChinese_name();
                Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                //addIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
                Parcelable icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.mipmap.ic_launcher);
                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                myIntent.putExtra(Constants.DATA_TAG1, appModeluBean.getEnglish_name());
                myIntent.putExtra(Constants.DATA_TAG2, appModeluBean.getModule_id());
                myIntent.putExtra(Constants.DATA_TAG3, appModeluBean.getChinese_name());
                myIntent.putExtra(Constants.DATA_TAG4, appModeluBean.getId());
                myIntent.setAction(Intent.ACTION_VIEW);
                addIntent.putExtra("duplicate", true);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
                getActivity().sendBroadcast(addIntent);
                //ToastUtils.showToast(getActivity(), "创建桌面快捷方式成功!");
                if (Build.VERSION.SDK_INT >= 26) {
                    ShortcutManager mShortcutManager = (ShortcutManager) getActivity().getSystemService(Context.SHORTCUT_SERVICE);
                    if (mShortcutManager.isRequestPinShortcutSupported()) {
                        ShortcutInfo info = new ShortcutInfo.Builder(getActivity(), appModeluBean.getEnglish_name())
                                .setShortLabel(name)
                                .setLongLabel(appModeluBean.getApplication_name() + "-" + name)
                                .setIcon(Icon.createWithResource(getActivity(), R.mipmap.ic_launcher))
                                .setIntent(myIntent)
                                .build();
                        //当添加快捷方式的确认弹框弹出来时，将被回调
                        PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        mShortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
                    }
                }
            }
        });

    }

    /**
     * 跳转模块列表
     *
     * @param appModeluBean
     */
    private void openModuleTemp(AppModuleBean appModeluBean) {
        String moduleBean = appModeluBean.getEnglish_name();
        Bundle bundle = new Bundle();
        switch (moduleBean) {
            case ApproveConstants.APPROVAL_MODULE_BEAN:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/main", bundle);
                break;
            case ProjectConstants.PROJECT_BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://project/main", bundle);
                break;
            case MemoConstant.BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/main", bundle);
                break;
            case MemoConstant.BEAN_NAME_KNOWLEDGE2:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/konwledge_main2", bundle);
                break;

            case EmailConstant.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://email/email", null);
                break;
            case FileConstants.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://filelib/netdisk", null);
                break;
            case AttendanceConstants.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://attendance/attendance_main", null);
                /*Intent intent = new Intent(getActivity(), com.ali.fluttergo.MainActivity.class);
                getActivity().startActivity(intent);*/

                break;
            case "statistics":
                EventBusUtils.sendEvent(new MessageBean(0, AppConstant.SHOW_STATISTIC, null));
                break;
            default:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                bundle.putString(Constants.MODULE_ID, appModeluBean.getId());
                bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                UIRouter.getInstance().openUri(mContext, "DDComp://custom/temp", bundle);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    /**
     * 停止或自动播放图片
     *
     * @param flag
     */
    public void setAutoPlay(boolean flag) {
        if (topBanner == null) {
            return;
        }
        //1更新2停止3恢复
        if (flag) {
            topBanner.startPlay();
            topBanner.handleMesssage(3);
            // refreshAllData();
        } else {
            topBanner.stopPlay();
            topBanner.handleMesssage(2);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
