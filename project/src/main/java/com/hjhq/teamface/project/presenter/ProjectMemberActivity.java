package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.ActionSheetDialog;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.adapter.ProjectMemberAdapter;
import com.hjhq.teamface.project.bean.ProjectMemberResultBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.bean.TaskCountBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.ProjectMemberDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.hjhq.teamface.basis.constants.Constants.DATA_TAG1;

/**
 * 项目成员管理
 *
 * @author Administrator
 * @date 2018/4/10
 */
public class ProjectMemberActivity extends ActivityPresenter<ProjectMemberDelegate, ProjectModel> {
    private String[] MEMU = {"项目角色", "移除成员"};
    private ProjectMemberAdapter mAdapter;
    private long projectId;
    private String priviledgeIds;
    private boolean hasAuth = false;
    private boolean getAuthReady = false;


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            projectId = getIntent().getLongExtra(ProjectConstants.PROJECT_ID, 0);
        }
    }

    @Override
    public void init() {
        initAdapter();
        getProjectRoleInfo();
        queryProjectMember();
        viewDelegate.showMenu();

    }

    private void initAdapter() {
        mAdapter = new ProjectMemberAdapter(null);
        viewDelegate.setAdapter(mAdapter);
    }

    /**
     * 查询项目成员
     */
    private void queryProjectMember() {
        model.queryProjectMember(mContext, projectId, new ProgressSubscriber<ProjectMemberResultBean>(mContext) {
            @Override
            public void onNext(ProjectMemberResultBean projectMemberResultBean) {
                super.onNext(projectMemberResultBean);
                List<ProjectMemberResultBean.DataBean.DataListBean> data = projectMemberResultBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(mAdapter, mAdapter.getData(), data);
                viewDelegate.setTitle("成员管理(" + data.size() + ")");
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setItemClick(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProjectMemberResultBean.DataBean.DataListBean item = mAdapter.getItem(position);
                if ("1".equals(item.getProject_role())) {
                    return;
                }
                ProjectUtil.INSTANCE.checkProjectStatus(mContext, ProjectDetailActivity.projectStatus, () -> {
                    if (!getAuthReady) {
                        ToastUtils.showToast(mContext, "等请待获取权限完成");
                        return;
                    }
                    if (!hasAuth) {
                        ToastUtils.showToast(mContext, "无权限");
                        return;
                    }
                    List<ActionSheetDialog.SheetItem> sheetItemList = new ArrayList<>();
                    sheetItemList.add(new ActionSheetDialog.SheetItem(MEMU[0], which -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.DATA_TAG1, item.getProject_role());
                        bundle.putString(Constants.DATA_TAG2, item.getId() + "");
                        CommonUtil.startActivtiyForResult(mContext, ProjectRoleActivity.class, Constants.REQUEST_CODE2, bundle);
                    }));
                    sheetItemList.add(new ActionSheetDialog.SheetItem(MEMU[1], ActionSheetDialog.SheetItemColor.Red, which -> {
                        if (!ProjectUtil.INSTANCE.checkProjectPermission(mContext, priviledgeIds, 12)) {
                            return;
                        }
                        model.queryHasTaskNotComplete(mContext, item.getId(), new ProgressSubscriber<TaskCountBean>(mContext) {
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                            @Override
                            public void onNext(TaskCountBean baseBean) {
                                super.onNext(baseBean);
                                if (baseBean != null && baseBean.getData() != null && !"0".equals(baseBean.getData().getTaskCount())) {
                                    ArrayList<Member> members = new ArrayList<Member>();
                                    Bundle bundle = new Bundle();
                                    final List<ProjectMemberResultBean.DataBean.DataListBean> data = mAdapter.getData();
                                    for (int i = 0; i < data.size(); i++) {
                                        if (item.getId() != data.get(i).getId()) {
                                            Member member = new Member();
                                            member.setId(data.get(i).getEmployee_id());
                                            member.setEmployee_name(data.get(i).getEmployee_name());
                                            member.setName(data.get(i).getEmployee_name());
                                            member.setPicture(data.get(i).getEmployee_pic());
                                            member.setPost_name(data.get(i).getProject_role());
                                            //放置项目成员id
                                            member.setEmail(data.get(i).getProject_member_id());
                                            members.add(member);
                                        }
                                    }
                                    bundle.putSerializable(Constants.DATA_TAG1, members);
                                    bundle.putLong(Constants.DATA_TAG2, TextUtil.parseLong(item.getProject_member_id()));
                                    CommonUtil.startActivtiyForResult(mContext, HandleTaskActivity.class, Constants.REQUEST_CODE3, bundle);
                                } else {
                                    DialogUtils.getInstance().sureOrCancel(mContext, "确定要移除该项目成员吗?", null, viewDelegate.getRootView(), () ->
                                            model.deleteProjectMember(mContext, item.getId(), new ProgressSubscriber<BaseBean>(mContext) {
                                                @Override
                                                public void onNext(BaseBean baseBean) {
                                                    super.onNext(baseBean);
                                                    ToastUtils.showSuccess(mContext, "移除成功！");
                                                    adapter.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                }
                            }
                        });
                    }));

                    new ActionSheetDialog(mContext)
                            .builder()
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .addSheetItem(sheetItemList).show();
                });

            }
        });
        super.bindEvenListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, ProjectDetailActivity.projectStatus, () -> {
            addMember();
        });
        return super.onOptionsItemSelected(item);
    }

    private void addMember() {
        if (!ProjectUtil.INSTANCE.checkProjectPermission(mContext, priviledgeIds, 11)) {
            return;
        }
        Bundle bundle = new Bundle();
        ArrayList<Member> list = new ArrayList<>();
        final List<ProjectMemberResultBean.DataBean.DataListBean> data = mAdapter.getData();
        for (ProjectMemberResultBean.DataBean.DataListBean dd : data) {
            Member mm = new Member();
            mm.setName(dd.getEmployee_name());
            mm.setEmployee_name(dd.getEmployee_name());
            mm.setId(dd.getEmployee_id());
            mm.setCheck(false);
            mm.setSelectState(C.CAN_NOT_SELECT);
            list.add(mm);
        }
        bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, list);
        bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
        CommonUtil.startActivtiyForResult(mContext,
                SelectMemberActivity.class, Constants.REQUEST_CODE1, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE1) {
            List<Member> memberList = (List<Member>) data.getSerializableExtra(DATA_TAG1);
            if (CollectionUtils.isEmpty(memberList)) {
                ToastUtils.showError(mContext, "请选项新增成员");
                return;
            }
            StringBuilder sb = new StringBuilder();
            Observable.from(memberList).subscribe(member -> sb.append("," + member.getId()));
            sb.deleteCharAt(0);
            model.addProjectMember(mContext, projectId, sb.toString(), new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "添加成功！");
                    queryProjectMember();
                }
            });
        } else if (requestCode == Constants.REQUEST_CODE2) {
            String projectRole = data.getStringExtra(Constants.DATA_TAG1);
            String id = data.getStringExtra(Constants.DATA_TAG2);
            model.updateMemberRole(mContext, id, projectRole, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    getProjectRoleInfo();
                    queryProjectMember();
                }
            });
        } else if (requestCode == Constants.REQUEST_CODE3) {
            getProjectRoleInfo();
            queryProjectMember();
        }
    }

    /**
     * 获取项目角色信息
     */
    public void getProjectRoleInfo() {
        //查询管理员权限
        new TaskModel().queryManagementRoleInfo(this, TextUtil.parseLong(SPHelper.getEmployeeId()), projectId, new ProgressSubscriber<QueryManagerRoleResultBean>(this) {
            @Override
            public void onNext(QueryManagerRoleResultBean queryManagerRoleResultBean) {
                super.onNext(queryManagerRoleResultBean);
                getAuthReady = true;
                priviledgeIds = queryManagerRoleResultBean.getData().getPriviledge().getPriviledge_ids();
                if (ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 11)) {
                    viewDelegate.showMenu(0);
                }
                hasAuth = ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 12);
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mContext, "获取项目角色权限失败");
            }
        });
    }

}
