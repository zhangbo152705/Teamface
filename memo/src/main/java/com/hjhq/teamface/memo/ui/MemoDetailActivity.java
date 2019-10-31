package com.hjhq.teamface.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleItemBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.EditActivity;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.bean.MemoDetailBean;
import com.hjhq.teamface.memo.bean.NewMemoBean;
import com.hjhq.teamface.memo.bean.RelevantDataBean;
import com.hjhq.teamface.memo.view.MemoDetailDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func2;

@RouteNode(path = "/detail", desc = "备忘录详情")
public class MemoDetailActivity extends ActivityPresenter<MemoDetailDelegate, MemoModel> {
    private String memoId;

    @Override
    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            memoId = bundle.getString(Constants.DATA_TAG1);
            if (TextUtils.isEmpty(memoId)) {
                finish();
                return;
            }
            getNetData(true);
        }

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.getToolbar().setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        viewDelegate.setClick(new SimpleItemClickListener() {
            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "操作",
                        new String[]{"复制全部", "编辑文字"}, new OnMenuSelectedListener() {
                            @Override
                            public boolean onMenuSelected(int p) {
                                switch (p) {
                                    case 0:
                                        ToastUtils.showSuccess(mContext, "复制成功");
                                        SystemFuncUtils.copyTextToClipboard(mContext, viewDelegate.getMemoText());
                                        break;
                                    case 1:
                                        ToastUtils.showSuccess(mContext, "此处应该到编辑界面");
                                        Bundle bundle = new Bundle();
                                        bundle.putString(EditActivity.KEY_TITLE, "编辑文字");
                                        bundle.putString(EditActivity.KEY_HINT, "请输入标题");
                                        bundle.putString(EditActivity.KEY_ORIGINAL_TEXT, viewDelegate.getMemoText());
                                        bundle.putString(EditActivity.KEY_TAG, "提示:此处操作不会影响原备忘录数据!");
                                        bundle.putInt(EditActivity.KEY_MAX_LENGTH, Integer.MAX_VALUE);
                                        CommonUtil.startActivtiy(mContext, EditActivity.class, bundle);
                                        break;
                                    default:

                                        break;
                                }
                                return false;
                            }
                        });
            }
        });
        //zzh->ad:关联item点击事件增加权限限制
        viewDelegate.setRelVance(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                viewData(viewDelegate.mRelevanceAdapter2.getItemList().get(position));

            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {

            }
        });
    }

    private String getText() {


        return "□";
    }

    private void getNetData(boolean flag) {

        /*model.findMemoDetail(MemoDetailActivity.this, memoId, new ProgressSubscriber<MemoDetailBean>(MemoDetailActivity.this, flag) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(MemoDetailBean baseBean) {
                super.onNext(baseBean);
                viewDelegate.showData(baseBean);
            }
        });*/
        model.queryMemoDetail(mContext, memoId, new Func2<MemoDetailBean, RelevantDataBean, MemoDetailBean>() {
            @Override
            public MemoDetailBean call(MemoDetailBean memoDetailBean, RelevantDataBean relevantDataBean) {
                Log.d("memo", JSON.toJSONString(relevantDataBean));
                if (relevantDataBean != null && relevantDataBean.getData() != null) {
                    final List<TaskInfoBean> moduleDataList = relevantDataBean.getData().getModuleDataList();
                    List<RelevantDataBean.DataListBean> dataList = relevantDataBean.getData().getDataList();
                    if (!CollectionUtils.isEmpty(dataList)) {
                        for (RelevantDataBean.DataListBean dataListBean : dataList) {
                            List<TaskInfoBean> moduleDataList1 = dataListBean.getModuleDataList();
                            if (!CollectionUtils.isEmpty(moduleDataList1)) {
                                moduleDataList.addAll(moduleDataList1);
                            }

                        }
                    }
                    memoDetailBean.getData().setItemsArr((ArrayList<TaskInfoBean>) moduleDataList);
                    /*viewDelegate.showRelevantData(moduleDataList);*/
                }
                return memoDetailBean;
            }
        }, new ProgressSubscriber<MemoDetailBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                viewDelegate.showError();
            }

            @Override
            public void onNext(MemoDetailBean memoDetailBean) {
                super.onNext(memoDetailBean);
                viewDelegate.showData(memoDetailBean);
            }
        });
    }

    public void updateMemo(NewMemoBean bean) {
        model.updateMemo(MemoDetailActivity.this, bean, new ProgressSubscriber<BaseBean>(mContext, false) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constants.REQUEST_CODE1:
                    break;
                case Constants.REQUEST_CODE2:
                    getNetData(false);
                    //dataChanged = true;
                    break;
                case Constants.REQUEST_CODE3:
                    getNetData(false);
                    EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                    break;
                case Constants.REQUEST_CODE4:
                    getNetData(false);
                    //dataChanged = true;
                    break;
                case Constants.REQUEST_CODE5:
                    if (data != null) {
                        String num = data.getStringExtra(Constants.DATA_TAG1);
                        viewDelegate.setCommentNum(num);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public void deleteMemo() {
        model.memoOperation(MemoDetailActivity.this, MemoConstant.MEMO_OPERATION_DELETE, memoId, new ProgressSubscriber<BaseBean>(MemoDetailActivity.this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "操作成功");
                EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                finish();
            }
        });

    }

    public void quitMemo() {
        model.memoOperation(MemoDetailActivity.this, MemoConstant.MEMO_OPERATION_QUIT, memoId, new ProgressSubscriber<BaseBean>(MemoDetailActivity.this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "操作成功");
                EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                finish();
            }
        });

    }

    /**
     * 彻底删除
     */
    public void deleteForever() {
        model.memoOperation(MemoDetailActivity.this, MemoConstant.MEMO_OPERATION_DELETE_FOREVER, memoId, new ProgressSubscriber<BaseBean>(MemoDetailActivity.this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "操作成功");
                setResult(RESULT_OK);
                EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                finish();
            }
        });

    }

    /**
     * 恢复
     */
    public void recover() {
        model.memoOperation(MemoDetailActivity.this, MemoConstant.MEMO_OPERATION_RECOVER, memoId, new ProgressSubscriber<BaseBean>(MemoDetailActivity.this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                finish();
            }
        });

    }

    public void quitShare() {
        model.memoOperation(MemoDetailActivity.this, MemoConstant.MEMO_OPERATION_QUIT, memoId, new ProgressSubscriber<BaseBean>(MemoDetailActivity.this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                EventBusUtils.sendEvent(new MessageBean(MemoConstant.DATALIST_CHANGE, MemoConstant.MEMO_DATA_CHANGED, null));
                finish();
            }
        });

    }

    /**
     * 验证权限后查看关联
     *
     * @param moduleItemBean
     */
    public void viewData(TaskInfoBean moduleItemBean) {
        Map<String, Object> remap = new HashMap<>();
        String remapStr = "";
        if (moduleItemBean.getDataType() == 2) {
            remap.put("data_Type", moduleItemBean.getDataType());
            remap.put("taskInfoId", moduleItemBean.getBean_id());
            remap.put("beanName", moduleItemBean.getBean_name());
            remap.put("taskName", moduleItemBean.getTask_name());
            remap.put("id", moduleItemBean.getBean_id());
            remap.put("projectId", moduleItemBean.getProject_id());
            remapStr = JSON.toJSONString(remap);
        }
        model.queryAuth(mContext, moduleItemBean.getBean_name(), "", moduleItemBean.getBean_id() + "", remapStr,
                new ProgressSubscriber<ViewDataAuthResBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showError(mContext, "查询权限错误");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && !TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            switch (viewDataAuthResBean.getData().getReadAuth()) {
                                case "0":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "1":
                                    TaskHelper.INSTANCE.clickTaskItem(MemoDetailActivity.this, moduleItemBean);
                                   /* Bundle bundle = new Bundle();
                                    switch (moduleItemBean.getDataType()) {
                                        case "1":

                                            break;
                                        case "2":

                                            break;
                                        case "3":

                                            bundle.putString(Constants.MODULE_BEAN, moduleItemBean.getModule());
                                            bundle.putString(Constants.DATA_ID, moduleItemBean.getId());
                                            UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", bundle, Constants.REQUEST_CODE1);
                                            break;
                                        case "4":
                                            //bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, item.process_definition_id);
                                            // bundle.putString(ApproveConstants.PROCESS_FIELD_V, item.process_field_v);
                                            bundle.putString(ApproveConstants.MODULE_BEAN, moduleItemBean.getBeanName());
                                            bundle.putString(ApproveConstants.APPROVAL_DATA_ID, moduleItemBean.getId());
                                            bundle.putString(ApproveConstants.TASK_KEY, "");
                                            bundle.putString(ApproveConstants.TASK_NAME, moduleItemBean.getTitle());
                                            bundle.putString(ApproveConstants.TASK_ID, "");
                                            bundle.putString(Constants.DATA_ID, moduleItemBean.getDataId() + "");
                                            // bundle.putString(ApproveConstants.APPROVAL_READ, item.complete_status)
                                            //TODO 需要type入口
                                            bundle.putInt(ApproveConstants.APPROVE_TYPE, 1);
                                            UIRouter.getInstance().openUri(mContext, "DDComp://app//approve/detail", bundle, Constants.REQUEST_CODE10);
                                            break;
                                    }*/

                                    break;
                                case "2":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "3":
//                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
//                                    break;
                                    TaskHelper.INSTANCE.clickTaskItem(MemoDetailActivity.this, moduleItemBean);
                                    break;
                                case "4":
                                    TaskHelper.INSTANCE.clickTaskItem(MemoDetailActivity.this, moduleItemBean);
                                    break;
                                case "5":
                                    TaskHelper.INSTANCE.clickTaskItem(MemoDetailActivity.this, moduleItemBean);
                                    break;
                            }
                        } else {
                            ToastUtils.showError(mContext, "查询权限错误");
                        }
                    }
                });
    }
}
