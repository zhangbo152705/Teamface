package com.hjhq.teamface.memo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.ApproveListBean;
import com.hjhq.teamface.basis.bean.AttachmentBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.basis.bean.KnowledgeTagBean;
import com.hjhq.teamface.basis.bean.MemoListItemBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.QuoteTaskResultBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.FileHelper;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.UriUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.EmailAttachmentAdapter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.customcomponent.util.WidgetDialogUtil;
import com.hjhq.teamface.download.service.DownloadService;
import com.hjhq.teamface.download.utils.FileTransmitUtils;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.bean.AddReferenceBean;
import com.hjhq.teamface.memo.bean.KnowledgeBean;
import com.hjhq.teamface.common.bean.KnowledgeClassListBean;
import com.hjhq.teamface.memo.view.AddKnowledgeDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import rx.Observable;

@RouteNode(path = "/add_knowledge", desc = "新增/编辑知识/提问")
public class AddKnowledgeActivity extends ActivityPresenter<AddKnowledgeDelegate, MemoModel> {
    private int type = 0;
    private TaskItemAdapter mTaskAdapter;
    private EmailAttachmentAdapter mAttachAdapter;
    private List<UploadFileBean> attachmentList = new ArrayList<>();
    private List<TaskInfoBean> taskList = new ArrayList<>();
    private List<KnowledgeClassBean> classList = new ArrayList<>();
    private KnowledgeClassBean chooseedClass;
    private KnowledgeTagBean choosedTag;
    private ArrayList<KnowledgeClassBean> tagList = new ArrayList<>();
    private ArrayList<KnowledgeClassBean> choosedTagList = new ArrayList<>();
    private File imageFromCamera;
    private boolean isAnythingChanged = false;
    private List<AttachmentBean> choosedFileList = new ArrayList<>();
    private List<File> localFileList = new ArrayList<>();
    private List<AttachmentBean> netFileList = new ArrayList<>();
    //0知识1提问
    private int typeStatus;
    private KnowledgeBean mDataBean;
    private String questionId;
    private String answerId;
    private boolean catgAndTagDataReady = false;
    private String dataId = "";

    @Override
    public void init() {
        type = getIntent().getIntExtra(Constants.DATA_TAG1, 0);
        switch (type) {
            case MemoConstant.ADD_KNOWLEDGE:
                viewDelegate.setTitle("新建知识");
                viewDelegate.setContentTitle(true, "内容");
                viewDelegate.loadUrl();
                typeStatus = 0;
                getClassAndTagData(false);
                break;
            case MemoConstant.ADD_QUESTION:
                viewDelegate.setTitle("新建提问");
                viewDelegate.setContentTitle(false, "描述");
                viewDelegate.loadUrl();
                typeStatus = 1;
                getClassAndTagData(false);
                break;
            case MemoConstant.EDIT_KNOWLEDGE:
                viewDelegate.setTitle("编辑知识");
                viewDelegate.setContentTitle(true, "内容");
                viewDelegate.loadUrl();
                mDataBean = (KnowledgeBean) getIntent().getExtras().getSerializable(Constants.DATA_TAG2);
                dataId = getIntent().getStringExtra(Constants.DATA_TAG3);
                getClassAndTagData(false);
                break;
            case MemoConstant.EDIT_QUESTION:
                viewDelegate.setTitle("编辑提问");
                viewDelegate.setContentTitle(false, "描述");
                viewDelegate.loadUrl();
                mDataBean = (KnowledgeBean) getIntent().getExtras().getSerializable(Constants.DATA_TAG2);
                dataId = getIntent().getStringExtra(Constants.DATA_TAG3);
                getClassAndTagData(false);
                break;
            case MemoConstant.ADD_ANSWER:
                viewDelegate.setTitle("写回答");
                viewDelegate.setContentTitle(true, "内容");
                viewDelegate.loadUrl();
                questionId = getIntent().getStringExtra(Constants.DATA_TAG3);
                viewDelegate.get(R.id.ll_title).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_catg).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_tag).setVisibility(View.GONE);
                break;
            case MemoConstant.EDIT_ANSWER:
                viewDelegate.setTitle("编辑回答");
                viewDelegate.setContentTitle(true, "内容");
                viewDelegate.loadUrl();
                answerId = getIntent().getStringExtra(Constants.DATA_TAG3);
                questionId = getIntent().getStringExtra(Constants.DATA_TAG4);
                mDataBean = (KnowledgeBean) getIntent().getSerializableExtra(Constants.DATA_TAG2);
                viewDelegate.get(R.id.ll_title).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_catg).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_tag).setVisibility(View.GONE);
                break;
            case MemoConstant.CHOOSE_CATG_TAG:
                viewDelegate.setTitle("选择分类");
                mDataBean = (KnowledgeBean) getIntent().getSerializableExtra(Constants.DATA_TAG2);
                dataId = getIntent().getStringExtra(Constants.DATA_TAG3);
                viewDelegate.get(R.id.ll_title).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_content).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_relevant).setVisibility(View.GONE);
                viewDelegate.get(R.id.ll_attachment).setVisibility(View.GONE);
                viewDelegate.get(R.id.text_content).setVisibility(View.GONE);
                viewDelegate.get(R.id.rl_content_title).setVisibility(View.GONE);
                viewDelegate.get(R.id.rl_content).setVisibility(View.GONE);
                getClassAndTagData(false);
                break;
            default:
                break;
        }
        mTaskAdapter = new TaskItemAdapter(taskList, false);
        mTaskAdapter.setType(1);
        mAttachAdapter = new EmailAttachmentAdapter(EmailAttachmentAdapter.ADD_EMAIL_TAG, choosedFileList);
        viewDelegate.setAttaAdapter(mAttachAdapter);
        viewDelegate.setRelevantAdapter(mTaskAdapter);
        if (type != MemoConstant.CHOOSE_CATG_TAG && mDataBean != null) {
            setData();
        }
    }

    private void setData() {
        //标题
        viewDelegate.setKnowledgeTitle(mDataBean.getTitle());
        //内容(已处理)

        //关联
        taskList.clear();
        taskList.addAll(mDataBean.getRelevantList());
        mTaskAdapter.setItemList(taskList);
        //分类(已处理)

        //附件
        choosedFileList.clear();
        if (type == MemoConstant.EDIT_ANSWER) {
            choosedFileList.addAll(mDataBean.getRepository_answer_attachment());
        } else if (type == MemoConstant.EDIT_KNOWLEDGE || type == MemoConstant.EDIT_QUESTION) {
            choosedFileList.addAll(mDataBean.getRepository_lib_attachment());
        }
        mAttachAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoftKeyboardUtils.hide(viewDelegate.getRootView());
        switch (type) {
            case MemoConstant.ADD_ANSWER:
                //保存回答
                addAnswer();
                break;
            case MemoConstant.EDIT_ANSWER:
                //保存已编辑回答
                updateAnswer();
                break;
            case MemoConstant.ADD_KNOWLEDGE:
                //新建知识
                addKnowledge();
                break;
            case MemoConstant.ADD_QUESTION:
                //新建问题知识
                addKnowledge();
                break;
            case MemoConstant.EDIT_KNOWLEDGE:
                //编辑知识
                updateKnowledge();
                break;
            case MemoConstant.EDIT_QUESTION:
                //编辑问题型知识
                updateKnowledge();
                break;
            case MemoConstant.CHOOSE_CATG_TAG:
                //保存分类与标签
                saveCatgAndTag();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存答案
     */
    private void addAnswer() {

        viewDelegate.textContent.getWebText(new TextWebView.TextWebInterface() {
            @Override
            public void getWebText(String text) {
                Map<String, Object> map = new HashMap<>();
                map.put("repository_id", questionId);
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(mContext, "内容不能为空");
                    return;
                }
                map.put("content", text);
                getRelevant(map, false);
                getAttachment(map);
                model.saveAnswer(mContext, map, new ProgressSubscriber<BaseBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            }
        });

    }

    /**
     * 保存编辑答案
     */
    private void updateAnswer() {
        //内容,引用,附件

        viewDelegate.textContent.getWebText(new TextWebView.TextWebInterface() {
            @Override
            public void getWebText(String text) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", answerId);
                data.put("repository_id", questionId);
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(mContext, "内容不能为空");
                    return;
                }
                data.put("content", text);
                getRelevant(data, false);
                getAttachment(data);
                model.updateAnswer(mContext, data, new ProgressSubscriber<BaseBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            }
        });

    }

    /**
     * 新建知识
     */
    private void addKnowledge() {
        Map<String, Object> data = new HashMap<>();
        data.put("type_status", typeStatus);
        getTitleText(data);
        viewDelegate.textContent.getWebText(new TextWebView.TextWebInterface() {
            @Override
            public void getWebText(String text) {
                if (typeStatus == 0 && TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(mContext, "内容不能为空");
                    return;
                } else {
                    data.put("content", text);
                    getAttachment(data);
                    getRelevant(data, false);
                    if (!getCatgAndTagText(data, false)) {
                        ToastUtils.showToast(mContext, "分类不能为空");
                        return;
                    }
                    model.addKnowledge(mContext, data, new ProgressSubscriber<BaseBean>(mContext) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }

                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        });

    }

    /**
     * 保存编辑知识
     */
    private void updateKnowledge() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", dataId);
        data.put("type_status", typeStatus);
        getTitleText(data);
        viewDelegate.textContent.getWebText(new TextWebView.TextWebInterface() {
            @Override
            public void getWebText(String text) {
                if (typeStatus == 0 && TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(mContext, "内容不能为空");
                } else {
                    data.put("content", text);
                    getAttachment(data);
                    getRelevant(data, false);
                    if (!getCatgAndTagText(data, false)) {
                        ToastUtils.showToast(mContext, "分类不能为空");
                        return;
                    }
                    model.editKnowledge(mContext, data, new ProgressSubscriber<BaseBean>(mContext) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }

                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 保存分类/标签
     */
    private void saveCatgAndTag() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dataId);
        if (!getCatgAndTagText(map, false)) {
            ToastUtils.showToast(mContext, "分类不能为空");
            return;
        }
        model.updateMove(mContext, map, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }
        });


    }

    /**
     * 标题
     *
     * @param data
     */
    private void getTitleText(Map<String, Object> data) {
        String title = viewDelegate.getTitle();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showToast(mContext, "标题不能为空");
            return;
        }
        data.put("title", title);
    }

    /**
     * 分类与标签
     *
     * @param data
     */
    private boolean getCatgAndTagText(Map<String, Object> data, boolean nullable) {
        if (chooseedClass == null) {
            if (nullable) {
                return true;
            } else {
                ToastUtils.showToast(mContext, "分类不能为空");
                return false;
            }

        }
        data.put("classification_id", chooseedClass.getId());
        if (choosedTagList != null && choosedTagList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < choosedTagList.size(); i++) {
                if (TextUtils.isEmpty(sb)) {
                    sb.append(choosedTagList.get(i).getId());
                } else {
                    sb.append(",");
                    sb.append(choosedTagList.get(i).getId());
                }
            }
            if (TextUtils.isEmpty(sb)) {
                data.put("label_ids", "");
            } else {
                data.put("label_ids", sb.toString());
            }
        }
        return true;
    }

    /**
     * 关联
     *
     * @param data
     * @param nullable
     */
    private void getRelevant(Map<String, Object> data, boolean nullable) {
        List<AddReferenceBean> list = new ArrayList<>();
        final List<TaskInfoBean> itemList = mTaskAdapter.getItemList();
        if (itemList == null || itemList.size() == 0) {
            data.put("references", new ArrayList<>());
            return;
        }
        for (TaskInfoBean bean : itemList) {
            AddReferenceBean referenceBean = new AddReferenceBean();
            switch (bean.getDataType()) {
                case ProjectConstants.DATA_APPROVE_TYPE:
                    referenceBean.setProjectId("-1");
                    referenceBean.setBean_name(bean.getBean_name());
                    referenceBean.setRelation_id(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_MEMO_TYPE:
                    referenceBean.setProjectId("-1");
                    referenceBean.setBean_name(MemoConstant.BEAN_NAME);
                    referenceBean.setRelation_id(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_EMAIL_TYPE:
                    referenceBean.setProjectId("-1");
                    referenceBean.setBean_name(EmailConstant.BEAN_NAME);
                    referenceBean.setRelation_id(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_CUSTOM_TYPE:
                    referenceBean.setProjectId("-1");
                    referenceBean.setBean_name(bean.getBean_name() + "");
                    referenceBean.setRelation_id(bean.getBean_id() + "");
                    break;
                case ProjectConstants.DATA_TASK_TYPE:
                    String projectId =bean.getProject_id()+"";
                    if (TextUtil.isEmpty(projectId) || "0".equals(projectId)){
                        projectId ="-1";
                    }
                    referenceBean.setProjectId(projectId);
                    referenceBean.setBean_name(bean.getBean_name() + "");
                    referenceBean.setRelation_id(bean.getId() + "");
                    break;
                default:

                    break;
            }
            list.add(referenceBean);
        }
        if (!nullable && list.size() == 0) {
            return;
        }

        data.put("references", list);

    }

    /**
     * 获取附件
     *
     * @param data
     */
    private void getAttachment(Map<String, Object> data) {
        String key = "";
        switch (type) {
            case MemoConstant.ADD_ANSWER:
            case MemoConstant.EDIT_ANSWER:
                key = "repository_answer_attachment";
                break;
            case MemoConstant.ADD_KNOWLEDGE:
            case MemoConstant.ADD_QUESTION:
            case MemoConstant.EDIT_KNOWLEDGE:
            case MemoConstant.EDIT_QUESTION:
                key = "repository_lib_attachment";
                break;
            default:
                break;
        }
        data.put(key, choosedFileList);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.get(R.id.rl_add_relevant).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, Constants.SELECT_FOR_KNOWLEDGE);
            UIRouter.getInstance().openUri(mContext, "DDComp://project/appModule", bundle, Constants.REQUEST_CODE7);
        });
        viewDelegate.get(R.id.rl_add_attachment).setOnClickListener(v -> {
            showAttachmentOption();
        });
        viewDelegate.get(R.id.rl_choose_catg).setOnClickListener(v -> {
            chooseCatg();
        });
        viewDelegate.get(R.id.rl_choose_tag).setOnClickListener(v -> {
            chooseTag();
        });
        viewDelegate.setAttaClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                choosedFileList.remove(position);
                mAttachAdapter.notifyDataSetChanged();
                super.onItemChildClick(adapter, view, position);
            }
        });
        viewDelegate.textContent.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mDataBean != null) {
                    viewDelegate.textContent.setWebText(mDataBean.getContent());
                }else {
                    viewDelegate.textContent.setWebText("");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });

    }

    /**
     * 选择知识分类
     */
    private void chooseCatg() {
        if (!catgAndTagDataReady) {
            getClassAndTagData(true);
            return;
        }
        WidgetDialogUtil.mutilDialog2(mContext, false, classList, viewDelegate.getRootView(), entryBeanList -> {
            Observable.from(classList).filter(entrysBean -> entrysBean.isCheck()).subscribe(entrysBean -> {
                chooseedClass = (KnowledgeClassBean) entrysBean;
                viewDelegate.setCatg(chooseedClass.getName());
                if (tagList != null && tagList.size() > 0) {
                    for (KnowledgeClassBean data : tagList) {
                        data.setCheck(false);
                    }
                }
                if (chooseedClass != null) {
                    tagList = chooseedClass.getLabels();
                }
                if (tagList != null && tagList.size() > 0) {
                    for (KnowledgeClassBean data : tagList) {
                        data.setCheck(false);
                    }
                }
                viewDelegate.setTag("");
            });
        });
    }

    /**
     * 选择分类
     */
    private void chooseTag() {
        WidgetDialogUtil.mutilDialog2(mContext, true, tagList, viewDelegate.getRootView(), entryBeanList -> {
            choosedTagList.clear();
            viewDelegate.setTag("");
            Observable.from(tagList).filter(entrysBean -> entrysBean.isCheck()).subscribe(entrysBean -> {
                choosedTagList.add(entrysBean);
            });
            showTags();
        });
    }

    /**
     * 显示选中的标签
     */
    private void showTags() {
        viewDelegate.setTag("");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choosedTagList.size(); i++) {
            if (TextUtils.isEmpty(sb)) {
                sb.append(choosedTagList.get(i).getName());
            } else {
                sb.append("、");
                sb.append(choosedTagList.get(i).getName());
            }

        }
        viewDelegate.setTag(sb.toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE7) {
            //引用
            AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);
            String moduleBean = appModeluBean.getEnglish_name();
            String moduleChineseName = appModeluBean.getChinese_name();
            String moduleId = appModeluBean.getId();
            String icon_color = appModeluBean.getIcon_color();
            String icon_type = appModeluBean.getIcon_type();
            String icon_url = appModeluBean.getIcon_url();
            Bundle bundle = new Bundle();
            switch (moduleBean) {
                case ProjectConstants.TASK_MODULE_BEAN:
                    //任务
                    UIRouter.getInstance().openUri(mContext, "DDComp://project/quote_task", bundle, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE);
                    //UIRouter.getInstance().openUri(mContext, "DDComp://project/choose_task", bundle, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE);
                    break;
                case MemoConstant.BEAN_NAME:
                    //备忘录
                    UIRouter.getInstance().openUri(mContext, "DDComp://memo/choose_memo", bundle, ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE);
                    break;
                case EmailConstant.BEAN_NAME:
                case EmailConstant.MAIL_BOX_SCOPE:
                    //邮件
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/choose_email", bundle, ProjectConstants.QUOTE_TASK_EMAIL_REQUEST_CODE);
                    break;
                default:
                    if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                        //审批下面的模块
                        bundle.putString(Constants.DATA_TAG1, moduleBean);
                        bundle.putString(Constants.DATA_TAG2, moduleChineseName);
                        bundle.putString(Constants.DATA_TAG3, moduleId);
                        UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/select", bundle, ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE);
                    } else {
                        //自定义模块
                        bundle.putString(Constants.MODULE_BEAN, moduleBean);
                        bundle.putString(Constants.MODULE_ID, moduleId);
                        bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                        bundle.putString(Constants.DATA_TAG3, icon_color);
                        bundle.putString(Constants.DATA_TAG4, icon_type);
                        bundle.putString(Constants.DATA_TAG5, icon_url);
                        UIRouter.getInstance().openUri(mContext, "DDComp://custom/select", bundle, ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE);
                    }
                    break;
            }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE) {
            //引用备忘录
            ArrayList<MemoListItemBean> choosedItem = (ArrayList<MemoListItemBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (CollectionUtils.isEmpty(choosedItem)) {
                return;
            }
            List<TaskInfoBean> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (MemoListItemBean memo : choosedItem) {
                TaskInfoBean bean = new TaskInfoBean();
                bean.setDataType(ProjectConstants.DATA_MEMO_TYPE);
                bean.setTitle(memo.getTitle());
                bean.setPic_url(memo.getPic_url());
                bean.setId(TextUtil.parseLong(memo.getId()));
                final MemoListItemBean.CreateObjBean createObj = memo.getCreateObj();
                Member member = new Member();
                member.setName(createObj.getEmployee_name());
                member.setPicture(createObj.getPicture());
                member.setId(TextUtil.parseLong(createObj.getId()));
                bean.setCreateObj(member);
                list.add(bean);
            }
            if (list.size() > 0) {
                taskList.addAll(list);
                sortData();
            }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE) {
            //引用审批
            ArrayList<ApproveListBean> datas = (ArrayList<ApproveListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            //模块中文名
            List<TaskInfoBean> list = new ArrayList<>();
            String moduleChineseName = data.getStringExtra(Constants.DATA_TAG2);
            String moduleId = data.getStringExtra(Constants.DATA_TAG3);
            String moduleBean = data.getStringExtra(Constants.DATA_TAG4);
            if (CollectionUtils.isEmpty(datas)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (ApproveListBean approve : datas) {
                sb.append("," + approve.getApproval_data_id());
                TaskInfoBean bean = new TaskInfoBean();
                bean.setDataType(ProjectConstants.DATA_APPROVE_TYPE);
                bean.setBegin_user_name(approve.getBegin_user_name());
                bean.setCreate_time(TextUtil.parseLong(approve.getCreate_time()));
                bean.setProcess_name(approve.getProcess_name());
                bean.setPassed_status(approve.getProcess_status());
                bean.setId(TextUtil.parseLong(approve.getApproval_data_id()));
                bean.setBean_name(moduleBean);
                bean.setPicture(approve.getPicture());
                list.add(bean);
            }
            sb.deleteCharAt(0);
            if (list.size() > 0) {
                taskList.addAll(list);
                sortData();
            }

        } else if (requestCode == ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE) {
            //引用任务
            if (data != null) {
                String ids = data.getStringExtra(Constants.DATA_TAG1);
                String moduleBean = data.getStringExtra(Constants.DATA_TAG2);
                String projectId = "";
                int beanType = data.getIntExtra(Constants.DATA_TAG3, 0);
                if (beanType == 0) {
                    projectId = data.getStringExtra(Constants.DATA_TAG5);
                } else {
                    projectId = "-1";
                }
                List<QuoteTaskResultBean.DataBean.DataListBean> list = (List<QuoteTaskResultBean.DataBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG4);
                List<TaskInfoBean> selectLsit = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        TaskInfoBean bean = new TaskInfoBean();
                        bean.setDataType(ProjectConstants.DATA_TASK_TYPE);
                        bean.setText_name(list.get(i).getTask_name());
                        bean.setDatetime_deadline(list.get(i).getEnd_time());
                        bean.setModule_name(list.get(i).getModule_name());
                        bean.setProject_id(TextUtil.parseLong(projectId));
                        bean.setBean_name(moduleBean);
                        bean.setId(TextUtil.parseLong(list.get(i).getId()));
                        selectLsit.add(bean);
                    }
                }
                if (selectLsit.size() > 0) {
                    taskList.addAll(selectLsit);
                    sortData();
                }
            }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE) {
            //引用自定义
            String moduleBean = data.getStringExtra(Constants.MODULE_BEAN);
            String moduleId = data.getStringExtra(Constants.MODULE_ID);
            String moduleName = data.getStringExtra(Constants.NAME);
            String icon_color = data.getStringExtra(Constants.DATA_TAG3);
            String icon_type = data.getStringExtra(Constants.DATA_TAG4);
            String icon_url = data.getStringExtra(Constants.DATA_TAG5);
            ArrayList<DataTempResultBean.DataBean.DataListBean> datas = (ArrayList<DataTempResultBean.DataBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (CollectionUtils.isEmpty(datas)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            List<TaskInfoBean> list = new ArrayList<>();
            for (DataTempResultBean.DataBean.DataListBean c : datas) {
                sb.append("," + c.getId().getValue());
                TaskInfoBean bean = new TaskInfoBean();
                bean.setDataType(ProjectConstants.DATA_CUSTOM_TYPE);
                bean.setId(TextUtil.parseLong(c.getId().getValue()));
                bean.setBean_id(TextUtil.parseLong(c.getId().getValue()));
                bean.setRows(c.getRow());
                bean.setModule_name(moduleName);
                bean.setBean_name(moduleBean);
                bean.setIcon_color(icon_color);
                bean.setIcon_type(icon_type);
                bean.setIcon_url(icon_url);
                list.add(bean);
            }
            sb.deleteCharAt(0);
            if (list.size() > 0) {
                taskList.addAll(list);
                sortData();
            }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_EMAIL_REQUEST_CODE) {
            //引用邮件
            String moduleBean = data.getStringExtra(Constants.MODULE_BEAN);
            String moduleId = data.getStringExtra(Constants.MODULE_ID);
            String moduleName = data.getStringExtra(Constants.NAME);
            ArrayList<EmailBean> datas = (ArrayList<EmailBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (CollectionUtils.isEmpty(datas)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            List<TaskInfoBean> list = new ArrayList<>();
            for (EmailBean dataListBean : datas) {
                sb.append("," + dataListBean.getId());
                TaskInfoBean bean = new TaskInfoBean();
                bean.setDataType(ProjectConstants.DATA_EMAIL_TYPE);
                bean.setTitle(dataListBean.getSubject());
                bean.setCreate_time(TextUtil.parseLong(dataListBean.getCreate_time()));
                bean.setText_name(dataListBean.getFrom_recipient());
                bean.setId(TextUtil.parseLong(dataListBean.getId()));
                bean.setBean_id(TextUtil.parseLong(dataListBean.getId()));
                list.add(bean);
            }
            sb.deleteCharAt(0);
            if (list.size() > 0) {
                taskList.addAll(list);
                sortData();
            }
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE2:
                //选择知识分类
                if (data != null) {
                    chooseedClass = (KnowledgeClassBean) data.getSerializableExtra(Constants.DATA_TAG1);
                    if (chooseedClass != null) {
                        viewDelegate.setCatg(chooseedClass.getName());
                    }
                }
                break;
            case Constants.REQUEST_CODE3:
                //选择知识标签
                break;
            case Constants.REQUEST_CODE6:
                isAnythingChanged = true;
                //文件库选择的文件
                if (data != null) {
                    AttachmentBean bean = (AttachmentBean) data.getSerializableExtra(Constants.DATA_TAG1);
                    bean.setFromWhere(EmailConstant.FROM_UPLOAD);
                    choosedFileList.add(bean);
                    updateAttInfo();
                }
                break;
            case PhotoPicker.REQUEST_CODE:
                isAnythingChanged = true;
                //相册
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos != null) {
                    for (int i = 0; i < photos.size(); i++) {
                        File file = new File(photos.get(i));
                        if (file.exists()) {
                            checkAndUploadFile(file);
                        }
                    }
                }
                break;
            case Constants.CHOOSE_LOCAL_FILE:
                isAnythingChanged = true;
                //本地文件
                Uri uri = data.getData();
                String path = UriUtil.getPhotoPathFromContentUri(mContext, uri);
                File file = new File(path);
                if (file.exists()) {
                    checkAndUploadFile(file);

                }
                break;
            case Constants.TAKE_PHOTO_NEW_REQUEST_CODE:
                isAnythingChanged = true;
                //拍照
                if (imageFromCamera != null && imageFromCamera.exists()) {
                    checkAndUploadFile(imageFromCamera);
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 排序
     */
    private void sortData() {
        List<TaskInfoBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {

            for (int j = 0; j < taskList.size(); j++) {
                switch (i) {
                    case 0:
                        if (taskList.get(j).getDataType() == ProjectConstants.DATA_APPROVE_TYPE) {
                            list.add(taskList.get(j));
                        }
                        break;
                    case 1:
                        if (taskList.get(j).getDataType() == ProjectConstants.DATA_TASK_TYPE) {
                            list.add(taskList.get(j));
                        }
                        break;
                    case 2:
                        if (taskList.get(j).getDataType() == ProjectConstants.DATA_EMAIL_TYPE) {
                            list.add(taskList.get(j));
                        }
                        break;
                    case 3:
                        if (taskList.get(j).getDataType() == ProjectConstants.DATA_MEMO_TYPE) {
                            list.add(taskList.get(j));
                        }
                        break;
                    case 4:
                        if (taskList.get(j).getDataType() == ProjectConstants.DATA_CUSTOM_TYPE) {
                            list.add(taskList.get(j));
                        }
                        break;
                }
            }
        }
        taskList.clear();
        taskList.addAll(list);
        mTaskAdapter.notifyDataSetChanged();
    }

    /**
     * 获取分类与标签
     *
     * @param showChooseDialog
     */
    private void getClassAndTagData(boolean showChooseDialog) {
        model.getRepositoryClassificationList(mContext, new ProgressSubscriber<KnowledgeClassListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(KnowledgeClassListBean knowledgeClassListBean) {
                super.onNext(knowledgeClassListBean);
                catgAndTagDataReady = true;
                classList.clear();
                classList.addAll(knowledgeClassListBean.getData());
                if (mDataBean != null) {
                    for (KnowledgeClassBean classBean : classList) {
                        if (mDataBean.getClassification_id().equals(classBean.getId())) {
                            viewDelegate.setCatg(classBean.getName());
                            chooseedClass = classBean;
                            classBean.setCheck(true);
                            tagList = chooseedClass.getLabels();
                            break;
                        }
                    }
                    ArrayList<ProjectLabelBean> ids = mDataBean.getLabel_ids();
                    for (ProjectLabelBean bean : ids) {
                        for (KnowledgeClassBean bean2 : tagList) {
                            if (bean.getId().equals(bean2.getId())) {
                                bean2.setCheck(true);
                                choosedTagList.add(bean2);
                            }
                        }
                    }
                    showTags();
                }
                if (showChooseDialog) {
                    chooseCatg();
                }
            }
        });
    }

    private void checkFile() {
        List<File> selectedFile = getFileList();
        boolean limit = FileTransmitUtils.checkLimit(selectedFile);
        if (limit) {
            DialogUtils.getInstance().sureOrCancel(mContext,
                    "提示",
                    "当前为非WiFi网络环境,且文件总大小超过10M,是否继续？",
                    viewDelegate.getRootView(),
                    new DialogUtils.OnClickSureListener() {
                        @Override
                        public void clickSure() {
                            uploadFile(selectedFile);
                        }

                    });
        } else {
            uploadFile(selectedFile);
        }
    }

    /**
     * 检查文件大小并上传
     *
     * @param file
     */
    private void checkAndUploadFile(final File file) {
        boolean limit = FileTransmitUtils.checkLimit(file);
        if (limit) {
            DialogUtils.getInstance().sureOrCancel(mContext,
                    "提示",
                    "当前为非WiFi网络环境,且文件总大小超过10M,是否继续？",
                    viewDelegate.getRootView(),
                    new DialogUtils.OnClickSureListener() {
                        @Override
                        public void clickSure() {
                            uploadFile(file);
                        }

                    });
        } else {
            uploadFile(file);
        }
    }

    private void uploadFile(File file) {
        List<File> list = new ArrayList<>();
        list.add(file);

        DownloadService.getInstance().uploadFile(list, new ProgressSubscriber<UpLoadFileResponseBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                super.onNext(upLoadFileResponseBean);
                List<UploadFileBean> data = upLoadFileResponseBean.getData();
                List<AttachmentBean> list = new ArrayList<AttachmentBean>();
                for (int i = 0; i < data.size(); i++) {
                    AttachmentBean attachmentBean = new AttachmentBean();
                    attachmentBean.setFileName(data.get(i).getFile_name());
                    attachmentBean.setFileSize(data.get(i).getFile_size());
                    attachmentBean.setFileType(data.get(i).getFile_type());
                    attachmentBean.setFileUrl(data.get(i).getFile_url());
                    attachmentBean.setUpload_time(data.get(i).getUpload_time());
                    attachmentBean.setUpload_by(SPHelper.getUserName());
                    attachmentBean.setFromWhere(EmailConstant.FROM_UPLOAD);
                    list.add(attachmentBean);
                }
                choosedFileList.addAll(list);
                mAttachAdapter.notifyDataSetChanged();
            }
        });
    }

    private void uploadFile(List<File> selectedFile) {
        DownloadService.getInstance().uploadFile(selectedFile, new ProgressSubscriber<UpLoadFileResponseBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                super.onNext(upLoadFileResponseBean);
                List<UploadFileBean> data = upLoadFileResponseBean.getData();
                List<AttachmentBean> list = new ArrayList<AttachmentBean>();
                for (int i = 0; i < data.size(); i++) {
                    AttachmentBean attachmentBean = new AttachmentBean();
                    attachmentBean.setFileName(data.get(i).getFile_name());
                    attachmentBean.setFileSize(data.get(i).getFile_size());
                    attachmentBean.setFileType(data.get(i).getFile_type());
                    attachmentBean.setFileUrl(data.get(i).getFile_url());
                    attachmentBean.setFromWhere(EmailConstant.FROM_UPLOAD);
                    list.add(attachmentBean);
                }
            }
        });

    }

    /**
     * 循环检测未上传的文件
     */
    private void uploadFile() {
        for (int i = 0; i < choosedFileList.size(); i++) {
            if (choosedFileList.get(i).getFromWhere() == EmailConstant.FROM_LOCAL_FILE) {

            }

        }
    }


    private List<File> getFileList() {
        List<File> list = new ArrayList<>();
        List<AttachmentBean> attList = mAttachAdapter.getData();
        if (attList != null) {
            for (int i = 0; i < attList.size(); i++) {
                if (attList.get(i).getFromWhere() == EmailConstant.FROM_LOCAL_FILE) {
                    File file = new File(attList.get(i).getFileUrl());
                    if (file.exists()) {
                        list.add(file);
                    }
                } else {
                    netFileList.add(attList.get(i));
                }

            }
        }

        return list;
    }

    /**
     * 弹出添加附件菜单
     */
    private void showAttachmentOption() {
        String[] menu = {"拍照", "从相册中选择", "本地文件", "从文件库中选择"};
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "添加附件", menu,
                new OnMenuSelectedListener() {
                    @Override
                    public boolean onMenuSelected(int position) {
                        Bundle bundle = new Bundle();
                        switch (position) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                //从相册中选择
                                CommonUtil.getImageFromAlbumByMultiple(mContext, 9);
                                break;
                            case 2:
                                //本地文件
                                FileHelper.showFileChooser(mContext);
                                break;
                            case 3:
                                //从文件夹选择
                                UIRouter.getInstance().openUri(mContext, "DDComp://filelib/select_file", bundle, Constants.REQUEST_CODE6);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (FileHelper.isSdCardExist()) {
            SystemFuncUtils.requestPermissions(mContext, android.Manifest.permission.CAMERA, aBoolean -> {
                if (aBoolean) {
                    imageFromCamera = CommonUtil.getImageFromCamera(mContext, Constants.TAKE_PHOTO_NEW_REQUEST_CODE);
                } else {
                    ToastUtils.showError(mContext, "必须获得必要的权限才能拍照！");
                }
            });
        } else {
            ToastUtils.showToast(mContext, "暂无外部存储");
        }

    }

    /**
     * 更新附件信息
     */
    private void updateAttInfo() {
        mAttachAdapter.notifyDataSetChanged();
    }

}
