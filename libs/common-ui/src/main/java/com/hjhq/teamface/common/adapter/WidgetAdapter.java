package com.hjhq.teamface.common.adapter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.bean.ApproveUnReadCountResponseBean;
import com.hjhq.teamface.basis.bean.EmailUnreadNumBean;
import com.hjhq.teamface.basis.bean.FolderNaviData;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
import com.hjhq.teamface.basis.constants.AppConstant;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.bean.WidgetBean;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019-1-19.
 * Describe：
 */

public class WidgetAdapter extends BaseMultiItemQuickAdapter<WidgetBean, BaseViewHolder> {
    private BaseActivity activity;
    private boolean APPROVE_DATA_RADDY = false;
    private boolean EMAIL_DATA_RADDY = false;
    private Map<String, Boolean> mMap = new HashMap<>();

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WidgetAdapter(BaseActivity activity, List<WidgetBean> data) {
        super(data);
        this.activity = activity;
        addItemType(AppConstant.TYPE_APPROVE, R.layout.workbench_widget_type_approve);
        addItemType(AppConstant.TYPE_PROJECT, R.layout.workbench_widget_type_project);
        addItemType(AppConstant.TYPE_MEMO, R.layout.workbench_widget_type_memo);
        addItemType(AppConstant.TYPE_KNOWLEDGE, R.layout.workbench_widget_type_knowledge);
        addItemType(AppConstant.TYPE_EMAIL, R.layout.workbench_widget_type_email);
        addItemType(AppConstant.TYPE_ATTENDANCE, R.layout.workbench_widget_type_attendance);
        addItemType(AppConstant.TYPE_FILELIB, R.layout.workbench_widget_type_filelib);
        addItemType(AppConstant.TYPE_FRIEND_CIRCLE, R.layout.workbench_widget_type_friend);
        addItemType(AppConstant.TYPE_DATA_ANALYSIS, R.layout.workbench_widget_type_data_analysis);
        addItemType(AppConstant.TYPE_DIY_MODULE, R.layout.workbench_widget_type_diy_module);
    }

    @Override
    protected void convert(BaseViewHolder helper, WidgetBean item) {
        Bundle bundle = new Bundle();
        switch (item.getType()) {
            case AppConstant.TYPE_DATA_ANALYSIS:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    EventBusUtils.sendEvent(new MessageBean(0, AppConstant.SHOW_STATISTIC, null));
                });
                break;
            case AppConstant.TYPE_APPROVE:
                showExpand(helper, item);
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://app/approve/main", bundle);
                });
                helper.getView(R.id.add).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://app/choose_approve_module", bundle);
                });
                if (!APPROVE_DATA_RADDY) {
                    new CommonModel().getApproveData(activity, new ProgressSubscriber<ApproveUnReadCountResponseBean>(activity) {
                        @Override
                        public void onNext(ApproveUnReadCountResponseBean bean) {
                            super.onNext(bean);
                            APPROVE_DATA_RADDY = true;
                            showNum(helper, R.id.tv_num1, bean.getData().getTreatCount());
                            showNum(helper, R.id.tv_num2, bean.getData().getCopyCount());
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
                }
                viewApprove(helper);
                break;
            case AppConstant.TYPE_ATTENDANCE:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://attendance/attendance_main", bundle);
                });
                break;
            case AppConstant.TYPE_DIY_MODULE:
                helper.setText(R.id.widget_name, item.getModuleName());
                // showExpand(helper, item);
                String moduleBean = item.getModuleBean();
                String moduleId = item.getModuleId();
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(Constants.MODULE_BEAN, moduleBean);
                    bundle2.putString(Constants.MODULE_ID, moduleId);
                    bundle2.putString(Constants.NAME, item.getModuleName());
                    UIRouter.getInstance().openUri(activity, "DDComp://custom/temp", bundle2);
                });
                helper.getView(R.id.add).setOnClickListener(v -> {
                    AuthHelper.getInstance().getModuleFunctionAuth(activity, moduleBean, new AuthHelper.InitialDataCompleteListener() {
                        @Override
                        public void complete(ModuleFunctionBean moduleFunctionBean) {
                            boolean b = AuthHelper.getInstance().checkFunctionAuth(moduleBean, CustomConstants.ADD_NEW);
                            if (b) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                                bundle.putString(Constants.MODULE_ID, moduleId);
                                /*if (isPool) {
                                    bundle.putString(Constants.POOL, menuId);
                                }*/
                                UIRouter.getInstance().openUri(activity, "DDComp://custom/add", bundle);
                            } else {
                                ToastUtils.showError(activity, "没有权限进行新增");
                            }
                        }

                        @Override
                        public void error() {
                            ToastUtils.showError(activity, "获取权限失败");
                        }
                    });
                });
                RecyclerView recyclerView = helper.getView(R.id.rv_filelib);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                ArrayList<TempMenuResultBean.DataBean> menuList = new ArrayList<>();
                MenuSortAdapter menuSortAdapter = new MenuSortAdapter(menuList);
                recyclerView.setAdapter(menuSortAdapter);
                recyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        super.onItemClick(adapter, view, position);
                        if (menuSortAdapter.getData().size() <= 0) {
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.DATA_TAG1, menuSortAdapter.getData().get(position).getId());
                        bundle.putSerializable(Constants.DATA_TAG2, menuList);
                        bundle.putString(Constants.MODULE_BEAN, moduleBean);
                        bundle.putString(Constants.MODULE_ID, moduleId);
                        bundle.putString(Constants.NAME, item.getModuleName());
                        UIRouter.getInstance().openUri(activity, "DDComp://custom/temp", bundle);
                    }
                });
                ImageView arrow = helper.getView(R.id.open);
                arrow.setOnClickListener(v -> {
                    if (helper.getView(R.id.rl_expand).getVisibility() == View.VISIBLE) {
                        helper.setVisible(R.id.rl_expand, false);
                        arrow.setImageResource(R.drawable.icon_arrow_down);
                    } else {
                        helper.setVisible(R.id.rl_expand, true);
                        arrow.setImageResource(R.drawable.icon_arrow_up);
                        if (menuSortAdapter.getData().size() <= 0) {
                            getSubMenu(helper, item, menuSortAdapter);
                        }
                    }
                });
                break;
            case AppConstant.TYPE_MEMO:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://memo/main", bundle);
                });
                helper.getView(R.id.add).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://memo/add", bundle);
                });
                break;
            case AppConstant.TYPE_FILELIB:
                showExpand(helper, item);
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://filelib/netdisk", bundle);
                });
                viewFolder(helper, item);
                break;
            case AppConstant.TYPE_EMAIL:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://email/email", bundle);
                });
                helper.getView(R.id.add).setOnClickListener(v -> {
                    bundle.putInt(Constants.DATA_TAG3, EmailConstant.ADD_NEW_EMAIL);
                    UIRouter.getInstance().openUri(activity, "DDComp://email/new_email", bundle);
                });
                showExpand(helper, item);
                if (!EMAIL_DATA_RADDY) {
                    new CommonModel().getEmailData(activity, new ProgressSubscriber<EmailUnreadNumBean>(activity) {
                        @Override
                        public void onNext(EmailUnreadNumBean bean) {
                            super.onNext(bean);
                            List<EmailUnreadNumBean.DataBean> data = bean.getData();
                            if (data != null && data.size() > 0) {
                                EMAIL_DATA_RADDY = true;
                                for (int i = 0; i < data.size(); i++) {
                                    if ("1".equals(data.get(i).getMail_box_id())) {
                                        showNum(helper, R.id.tv_num1, data.get(i).getCount());
                                    }
                                    if ("2".equals(data.get(i).getMail_box_id())) {
                                        showNum(helper, R.id.tv_num2, data.get(i).getCount());
                                    }
                                    if ("3".equals(data.get(i).getMail_box_id())) {
                                        showNum(helper, R.id.tv_num3, data.get(i).getCount());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
                }
                viewEmail(helper);
                break;
            case AppConstant.TYPE_KNOWLEDGE:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://memo/konwledge_main2", bundle);//zzh;工作台知识库入口改为和应用模块的知识库入口一致
                });
                break;
            case AppConstant.TYPE_FRIEND_CIRCLE:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.OPEN_COWORKER_CIRCLE_TAG, null));
                });
                helper.getView(R.id.add).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://app/friend/add", bundle);
                });
                break;
            case AppConstant.TYPE_PROJECT:
                helper.getView(R.id.rl_title_bar).setOnClickListener(v -> {
                    UIRouter.getInstance().openUri(activity, "DDComp://project/main", bundle);
                });
                break;
            default:
                break;
        }

    }

    private void viewEmail(BaseViewHolder helper) {
        helper.getView(R.id.rl_receive).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 1);
            UIRouter.getInstance().openUri(activity, "DDComp://email/email", bundle);
        });
        helper.getView(R.id.rl_send).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 2);
            UIRouter.getInstance().openUri(activity, "DDComp://email/email", bundle);
        });
        helper.getView(R.id.rl_draft).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 3);
            UIRouter.getInstance().openUri(activity, "DDComp://email/email", bundle);
        });

    }

    private void viewApprove(BaseViewHolder helper) {

        helper.getView(R.id.rl_receive).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 1);
            UIRouter.getInstance().openUri(activity, "DDComp://app/approve/main", bundle);
        });
        helper.getView(R.id.rl_send).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 3);
            UIRouter.getInstance().openUri(activity, "DDComp://app/approve/main", bundle);
        });
        helper.getView(R.id.rl_draft).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, 0);
            UIRouter.getInstance().openUri(activity, "DDComp://app/approve/main", bundle);
        });

    }

    private void showNum(BaseViewHolder helper, int resId, String numString) {
        TextView imUnreadNum = helper.getView(resId);
        int num = TextUtil.parseInt(numString, 0);
        if (num <= 0) {
            imUnreadNum.setVisibility(View.GONE);
            return;
        }
        imUnreadNum.setVisibility(View.VISIBLE);
        if (num > 0 && num <= MsgConstant.SHOW_MAX_EXACT_NUM) {
            if (num < 10) {
                imUnreadNum.setBackground(activity.getResources().getDrawable(R.drawable.im_unread_num_round_bg));
            } else {
                imUnreadNum.setBackground(activity.getResources().getDrawable(R.drawable.im_unread_num_bg));
            }
            imUnreadNum.setText(num + "");
        } else if (num > MsgConstant.SHOW_MAX_EXACT_NUM) {
            imUnreadNum.setBackground(activity.getResources().getDrawable(R.drawable.im_unread_num_bg));
            imUnreadNum.setText("99+");
        }

    }

    private void viewFolder(BaseViewHolder helper, WidgetBean item) {

        helper.getView(R.id.ll_company_folder).setOnClickListener(v -> {
            viewFolderData(1, "公司文件");
        });
        helper.getView(R.id.ll_app_folder).setOnClickListener(v -> {
            viewFolderData(1, "应用文件");

        });
        helper.getView(R.id.ll_personal_folder).setOnClickListener(v -> {
            viewFolderData(1, "个人文件");

        });
        helper.getView(R.id.ll_share_tome_folder).setOnClickListener(v -> {
            viewFolderData(1, "与我共享");
        });
        helper.getView(R.id.ll_my_share_folder).setOnClickListener(v -> {
            viewFolderData(1, "我共享的");
        });
        helper.getView(R.id.ll_project_folder).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            UIRouter.getInstance().openUri(activity, "DDComp://filelib/netdisk_project", bundle);
        });

    }

    private void viewFolderData(int type, String name) {
        Bundle bundle = new Bundle();
        ArrayList<FolderNaviData> naviData = new ArrayList<>();
        FolderNaviData fnd = new FolderNaviData();
        bundle.putInt(FileConstants.FROM_FOLDER_OR_SEARCH, FileConstants.FROM_FOLDER);
        bundle.putInt(FileConstants.FOLDER_TYPE, type);
        bundle.putString(FileConstants.FOLDER_ID, "");
        bundle.putString(FileConstants.FOLDER_NAME, name);
        bundle.putString(FileConstants.FOLDER_URL, "");
        bundle.putInt(FileConstants.FOLDER_LEVEL, 0);
        fnd.setFolderLevel(0);
        fnd.setFloderType(type);
        fnd.setFolderName(name);
        fnd.setFolderId("");
        naviData.clear();
        naviData.add(fnd);
        bundle.putSerializable(FileConstants.FOLDER_NAVI_DATA, naviData);
        UIRouter.getInstance().openUri(activity, "DDComp://filelib/netdisk_folder", bundle);
    }

    private void showExpand(BaseViewHolder helper, WidgetBean item) {
        ImageView arrow = helper.getView(R.id.open);
        arrow.setOnClickListener(v -> {
            if (helper.getView(R.id.rl_expand).getVisibility() == View.VISIBLE) {
                helper.setVisible(R.id.rl_expand, false);
                arrow.setImageResource(R.drawable.icon_arrow_down);
            } else {
                helper.setVisible(R.id.rl_expand, true);
                arrow.setImageResource(R.drawable.icon_arrow_up);
            }
        });
        switch (item.getType()) {
            case AppConstant.TYPE_EMAIL:
                break;
            case AppConstant.TYPE_APPROVE:
                break;
        }
    }

    private void getSubMenu(BaseViewHolder helper, WidgetBean item, MenuSortAdapter adapter) {
        String moduleId = item.getModuleId();
        List<TempMenuResultBean.DataBean> menuList = adapter.getData();
        new CommonModel().getMenuList(activity, moduleId, new ProgressSubscriber<TempMenuResultBean>(activity) {
            @Override
            public void onNext(TempMenuResultBean baseBean) {
                super.onNext(baseBean);
                menuList.clear();
                menuList.addAll(baseBean.getData().getDefaultSubmenu());
                menuList.addAll(baseBean.getData().getNewSubmenu());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });


    }
}
