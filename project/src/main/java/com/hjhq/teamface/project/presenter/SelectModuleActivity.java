package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.AppListResultBean;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.AppModuleResultBean;
import com.hjhq.teamface.basis.bean.LocalModuleBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.AppAdapter;
import com.hjhq.teamface.common.utils.ProjectDialog;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.SelectModuleDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择应用模块
 *
 * @author Administrator
 * @date 2018/5/3
 */

@RouteNode(path = "/appModule", desc = "选择应用模块")
public class SelectModuleActivity extends ActivityPresenter<SelectModuleDelegate, ProjectModel> {
    /**
     * 我的应用里每页模块的数量
     */
    public static final int MY_APP_MODULE_COUNT = 9;
    private AppAdapter myAdapter;
    private AppAdapter systemAdaper;
    private boolean longPress = false;
    /**
     * 操作类型
     */
    private int type;

    @Override
    public void init() {
        type = getIntent().getIntExtra(Constants.DATA_TAG1, Constants.NORMAL_TYPE);
        initAdapter();
        loadData();
    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData1 = CacheDataHelper.getCacheData(CacheDataHelper.SYSTEM_APP_CACHE_DATA, CacheDataHelper.SYSTEM_APP_CACHE_DATA);
        if (!TextUtils.isEmpty(cacheData1)) {
            List<AppModuleBean> cacheDataList = new Gson().fromJson(cacheData1, new TypeToken<List<AppModuleBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                systemAdaper.setNewData(cacheDataList);
            }
        }
        final String cacheData2 = CacheDataHelper.getCacheData(CacheDataHelper.CUSTOM_APP_CACHE_DATA, CacheDataHelper.CUSTOM_APP_CACHE_DATA);
        if (!TextUtils.isEmpty(cacheData2)) {
            List<AppModuleBean> cacheDataList = new Gson().fromJson(cacheData2, new TypeToken<List<AppModuleBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                myAdapter.setNewData(cacheDataList);
            }
        }
    }

    private void initAdapter() {
        myAdapter = new AppAdapter();
        viewDelegate.setMyAdapter(myAdapter);
        systemAdaper = new AppAdapter();
        viewDelegate.setSystemAdapter(systemAdaper);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        model.getAppList(this, new ProgressSubscriber<AppListResultBean>(this, false) {
            @Override
            public void onNext(AppListResultBean moduleResultBean) {
                super.onNext(moduleResultBean);
                AppListResultBean.DataBean data = moduleResultBean.getData();
                myAdapter.setNewData(data.getMyApplication());

            }
        });
        model.findModuleList(this, new ProgressSubscriber<LocalModuleBean>(this, false) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(LocalModuleBean localModuleBean) {
                super.onNext(localModuleBean);
                systemAdaper.setNewData(getSystemList(localModuleBean));
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
        AppModuleBean data1 = new AppModuleBean();
        if (isVisible(data, ApproveConstants.APPROVAL_MODULE_BEAN)) {
            AppModuleBean data3 = new AppModuleBean();
            data3.setChinese_name("审批");
            data3.setEnglish_name(ApproveConstants.APPROVAL_MODULE_BEAN);
            systemList.add(data3);
        }
        if (type != Constants.SELECT_FOR_MEMO) {
            AppModuleBean data2 = new AppModuleBean();
            data2.setChinese_name("备忘录");
            data2.setEnglish_name(MemoConstant.BEAN_NAME);
            systemList.add(data2);
        }
        if (type == Constants.SELECT_FOR_TASK || type == Constants.SELECT_FOR_KNOWLEDGE || type == Constants.SELECT_FOR_MEMO) {
            if (isVisible(data, ProjectConstants.PROJECT_BEAN_NAME)) {
                data1.setChinese_name("任务");
                data1.setEnglish_name(ProjectConstants.TASK_MODULE_BEAN);
                systemList.add(data1);
            }
            //zzh:不需要引用邮件模块
            /*if (type == Constants.SELECT_FOR_KNOWLEDGE) {
                AppModuleBean data6 = new AppModuleBean();
                data6.setChinese_name("邮件");
                data6.setEnglish_name(EmailConstant.BEAN_NAME);
                systemList.add(data6);
            }*/

        } else if (type == Constants.SELECT_FOR_MEMO) {
        } else {
            List<LocalModuleBean.DataBean> list = data.getData();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    AppModuleBean app = new AppModuleBean();
                    LocalModuleBean.DataBean bean = list.get(i);
                    if ("1".equals(bean.getOnoff_status())) {
                        switch (bean.getBean()) {
                            case "email":
                                app.setChinese_name("邮件");
                                app.setEnglish_name(EmailConstant.BEAN_NAME);
                                systemList.add(app);
                                break;
                            case "project":
                                app.setChinese_name("协作");
                                app.setEnglish_name(ProjectConstants.PROJECT_BEAN_NAME);
                                systemList.add(app);
                                break;
                            case "library":
                                app.setChinese_name("文件库");
                                app.setEnglish_name(FileConstants.BEAN_NAME);
                                systemList.add(app);
                                break;
                            default:
                                break;
                        }

                    }
                }
            }

        }


        return systemList;
    }

    /**
     * 判断固定模块是否可见
     *
     * @param data
     * @param beanName
     * @return
     */
    private boolean isVisible(LocalModuleBean data, @NonNull String beanName) {
        boolean flag = false;
        for (int i = 0; i < data.getData().size(); i++) {
            if (beanName.equals(data.getData().get(i).getBean())) {
                flag = "0".equals(data.getData().get(i).getOnoff_status()) ? false : true;
            }
        }
        // TODO: 2018/9/6 固定模块显示控制 ,正式使用时移除下面一行
//        flag = true;
        return flag;
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.myRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AppModuleBean item = (AppModuleBean) adapter.getItem(position);
                getAppModule(item.getChinese_name(), item.getId());
            }
        });
        viewDelegate.systemRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AppModuleBean appModeluBean = (AppModuleBean) adapter.getItem(position);
                openModuleTemp(appModeluBean);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                longPress = true;
            }
        });
    }

    /**
     * 获取应用下的模块集合
     *
     * @param appName
     * @param id
     */
    private void getAppModule(String appName, String id) {

        model.getModule(this, id, new ProgressSubscriber<AppModuleResultBean>(this) {
            @Override
            public void onNext(AppModuleResultBean moduleResultBean) {
                super.onNext(moduleResultBean);
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
                showAppModule(appName, list);
             //  CacheDataHelper.saveCacheDataList(CacheDataHelper.CUSTOM_MODULE_CACHE_DATA, id, JSONObject.toJSONString(data));
            }
        });
    }


    /**
     * 显示应用下的模块
     */
    public void showAppModule(String appName, List<List<AppModuleBean>> list) {

        ProjectDialog instance = ProjectDialog.getInstance();
        instance.showAppModule(appName, list, viewDelegate.getRootView(), new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppModuleBean appModeluBean = (AppModuleBean) adapter.getItem(position);
                openModuleTemp(appModeluBean);
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
        //选择模式
        if (type == Constants.SELECT_FOR_TASK
                || type == Constants.SELECT_FOR_KNOWLEDGE
                || type == Constants.SELECT_FOR_MEMO) {
            if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(moduleBean)) {
                selectApprove();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_TAG1, appModeluBean);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
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

            case EmailConstant.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://email/email", null);
                break;
            case FileConstants.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://filelib/netdisk", null);
                break;
            case AttendanceConstants.BEAN_NAME:
                ToastUtils.showToast(mContext, "敬请期待");
                // UIRouter.getInstance().openUri(mContext, "DDComp://attendance/attendance_main", null);
                break;
            default:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                bundle.putString(Constants.MODULE_ID, appModeluBean.getId());
                bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                UIRouter.getInstance().openUri(mContext, "DDComp://custom/temp", bundle);
                break;
        }
    }


    /**
     * 选择审批下的模块
     */
    private void selectApprove() {
        if (longPress) {
            model.findApprovalModuleList(mContext, new ProgressSubscriber<AppModuleResultBean>(mContext) {
                @Override
                public void onNext(AppModuleResultBean baseBean) {
                    super.onNext(baseBean);
                    ArrayList<AppModuleBean> data = baseBean.getData();
                    if (data == null) {
                        data = new ArrayList<>();
                    }

                    List<List<AppModuleBean>> list = new ArrayList<>();
                    List<AppModuleBean> appModelu = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        AppModuleBean bean = data.get(i);
                        bean.setApplication_id(ApproveConstants.APPROVAL_MODULE_BEAN);
                        appModelu.add(bean);
                        if (appModelu.size() == MY_APP_MODULE_COUNT) {
                            list.add(appModelu);
                            appModelu = new ArrayList<>();
                        } else if (i == data.size() - 1 && appModelu.size() > 0) {
                            list.add(appModelu);
                            break;
                        }
                    }
                    showAppModule("审批", list);
                }
            });
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.DATA_TAG1, true);
            UIRouter.getInstance().openUri(mContext, "DDComp://app/choose_approve_module", bundle, Constants.REQUEST_CODE7);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == Constants.REQUEST_CODE7) {
            if (data != null) {
                AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, appModeluBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
