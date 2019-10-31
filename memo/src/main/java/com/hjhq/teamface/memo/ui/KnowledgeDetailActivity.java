package com.hjhq.teamface.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AttachmentBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.ToolMenu;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.database.SocketMessage;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ClickUtil;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.FileTypeUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.FullscreenViewImageActivity;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.adapter.EmailAttachmentAdapter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.voice.VoicePlayActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.download.utils.FileTransmitUtils;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.adapter.KnowledgeAnswerListAdapter;
import com.hjhq.teamface.memo.adapter.VideoListAdapter;
import com.hjhq.teamface.memo.bean.AnswerListBean;
import com.hjhq.teamface.memo.bean.KnowledgeBean;
import com.hjhq.teamface.memo.bean.KnowledgeDetailBean;
import com.hjhq.teamface.memo.bean.RevelantDataListBean;
import com.hjhq.teamface.memo.bean.VideoItemBean;
import com.hjhq.teamface.memo.view.KnowledgeDetailDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-3.
 * Describe：知识库详情
 */
@RouteNode(path = "/knowledge_detail", desc = "知识库详情")
public class KnowledgeDetailActivity extends ActivityPresenter<KnowledgeDetailDelegate, MemoModel> {
    private CommentInputView mCommentInputView;
    private CommentAdapter mCommentAdapter;
    private EmailAttachmentAdapter mAttAdapter;
    private KnowledgeAnswerListAdapter mAnswerAdapter;
    private VideoListAdapter mVideoAdapter;
    private TaskItemAdapter mRelevantAdapter;
    private ArrayList<TaskInfoBean> relevantList = new ArrayList<>();
    private ArrayList<AttachmentBean> attList = new ArrayList<>();
    private ArrayList<KnowledgeBean> answerList = new ArrayList<>();
    private ArrayList<VideoItemBean> videoList = new ArrayList<>();
    private ArrayList<CommentDetailResultBean.DataBean> commentList = new ArrayList<>();
    private boolean switchedTitle = false;
    private boolean switchedState = false;
    private boolean webStateReady = false;
    private boolean topStatus = false;
    private int top1;
    //回答/邀请回答/查看/学习
    private int type;
    private String dataId;
    private String questionId;
    private int currentSotrtType = 0;
    private KnowledgeBean mDataBean;
    private boolean isAnswer = false;
    private String answerOrderBy = "create_time";
    private int typeStatus =0;// //0知识1提问


    /**
     * 菜单条目列表
     */
    private List<ToolMenu> list = new ArrayList<>();
    private String[] menuString;
    private String[] optionMenuArr;
    private String[] optionMenuArr2;
    private String[] sortMenuValueArr = new String[]{"create_time", "modify_time"};
    boolean isManager = false;
    boolean isCreator = false;
    /**
     * 当前版本
     */
    private String currentVersion = "";


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState != null) {
            dataId = savedInstanceState.getString(Constants.DATA_TAG1);
            isAnswer = savedInstanceState.getBoolean(Constants.DATA_TAG2);
            mDataBean = (KnowledgeBean) savedInstanceState.getSerializable(Constants.DATA_TAG3);
            questionId = savedInstanceState.getString(Constants.DATA_TAG4);
            typeStatus = savedInstanceState.getInt(Constants.DATA_TAG5,0);
        } else {
            dataId = getIntent().getStringExtra(Constants.DATA_TAG1);
            isAnswer = getIntent().getBooleanExtra(Constants.DATA_TAG2, false);
            mDataBean = (KnowledgeBean) getIntent().getExtras().getSerializable(Constants.DATA_TAG3);
            questionId = getIntent().getStringExtra(Constants.DATA_TAG4);
            typeStatus = getIntent().getIntExtra(Constants.DATA_TAG5,0);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.DATA_TAG1, dataId);
        outState.putBoolean(Constants.DATA_TAG2, isAnswer);
        outState.putSerializable(Constants.DATA_TAG3, mDataBean);
        outState.putString(Constants.DATA_TAG4, questionId);
    }

    @Override
    public void init() {
        viewDelegate.get(R.id.rl_relevant_root).setVisibility(View.GONE);
        viewDelegate.get(R.id.rl_attachment_root).setVisibility(View.GONE);
        mCommentInputView = new CommentInputView(this);
        FrameLayout flComment = viewDelegate.get(R.id.fl_comment);
        if (typeStatus ==1) {
            mCommentInputView.setData(MemoConstant.BEAN_NAME_KNOWLEDGE_ANSWER, dataId);
        } else {
            mCommentInputView.setData(MemoConstant.BEAN_NAME_KNOWLEDGE2, dataId);
        }
        flComment.addView(mCommentInputView);
        if (isAnswer) {
            viewDelegate.get(R.id.rl_head1).setVisibility(View.GONE);
            viewDelegate.get(R.id.ll_tags).setVisibility(View.GONE);
            viewDelegate.get(R.id.ll_answer_and_invest).setVisibility(View.GONE);
            viewDelegate.get(R.id.rl_study_state2).setVisibility(View.GONE);
            viewDelegate.get(R.id.ll_nums).setVisibility(View.GONE);
            viewDelegate.get(R.id.ll_answer).setVisibility(View.GONE);
            viewDelegate.get(R.id.ll_answer).setVisibility(View.GONE);
            flComment.setVisibility(View.GONE);//隐藏评论
        } else {
            flComment.setVisibility(View.VISIBLE);
            RelativeLayout rlHeader1 = viewDelegate.get(R.id.rl_head1);
            LinearLayout llHeader1 = viewDelegate.get(R.id.ll_head1);
            LinearLayout llAnswerAndInvest = viewDelegate.get(R.id.ll_answer_and_invest);
            RelativeLayout rlStudyState1 = viewDelegate.get(R.id.rl_study_state1);
            RelativeLayout rlStudyState2 = viewDelegate.get(R.id.rl_study_state2);
            LinearLayout llStudyState = viewDelegate.get(R.id.ll_study_state);
            LinearLayout llAnswerInvest = viewDelegate.get(R.id.ll_answer_invest);
            rlHeader1.post(new Runnable() {
                @Override
                public void run() {
                    //获取固定栏底部在屏幕坐标系中的位置
                    int[] location0 = new int[2];
                    rlHeader1.getLocationOnScreen(location0);
                    top1 = location0[1] + rlHeader1.getHeight();
                }
            });

            viewDelegate.setTitle("详情");
            viewDelegate.get(R.id.tv_knowledge_title).getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    int[] location1 = new int[2];
                    viewDelegate.get(R.id.tv_knowledge_title).getLocationOnScreen(location1);
                    int[] location2 = new int[2];
                    viewDelegate.get(R.id.tv_name).getLocationOnScreen(location2);
                    int[] location3 = new int[2];
                    viewDelegate.get(R.id.ll_answer_and_invest).getLocationOnScreen(location3);

                    //标题变化
                    if (location1[1] < top1 - viewDelegate.get(R.id.tv_knowledge_title).getHeight() / 2) {
                        if (!switchedTitle) {
                            viewDelegate.setTitle(mDataBean == null ? "详情" : mDataBean.getTitle());
                            viewDelegate.get(R.id.tv_knowledge_title).setVisibility(View.INVISIBLE);
                            switchedTitle = true;
                        }
                    } else {
                        if (switchedTitle) {
                            viewDelegate.setTitle("详情");
                            viewDelegate.get(R.id.tv_knowledge_title).setVisibility(View.VISIBLE);
                            switchedTitle = false;
                        }
                    }
                    //学习状态变化
                    if (location2[1] < top1) {
                        if (rlStudyState2.getChildCount() > 0) {
                            rlStudyState2.removeAllViews();
                            rlStudyState1.addView(llStudyState);
                            viewDelegate.get(R.id.tv_catg).setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (rlStudyState1.getChildCount() > 0) {
                            rlStudyState1.removeAllViews();
                            rlStudyState2.addView(llStudyState);
                            viewDelegate.get(R.id.tv_catg).setVisibility(View.VISIBLE);
                        }
                    }
                    //写回答/邀请回答
                    if (location3[1] < top1) {
                        if (llAnswerAndInvest.getChildCount() > 0) {
                            llAnswerAndInvest.removeAllViews();
                            llHeader1.addView(llAnswerInvest);
                        }
                    } else if (location3[1] > top1) {
                        if (llHeader1.getChildCount() > 0) {
                            llHeader1.removeAllViews();
                            llAnswerAndInvest.addView(llAnswerInvest);
                        }
                    }
                }
            });
        }

        mRelevantAdapter = new TaskItemAdapter(relevantList, false);
        viewDelegate.setRelevantAdapter(mRelevantAdapter);
        mAttAdapter = new EmailAttachmentAdapter(EmailAttachmentAdapter.EMAIL_DETAIL_TAG, attList);
        viewDelegate.setAttaAdapter(mAttAdapter);
        mCommentAdapter = new CommentAdapter(commentList);
        mAnswerAdapter = new KnowledgeAnswerListAdapter(answerList);
        mVideoAdapter = new VideoListAdapter(this, videoList);
        viewDelegate.setVideoAdapter(mVideoAdapter);
        viewDelegate.setCommentAdapter(mCommentAdapter);
        menuString = getResources().getStringArray(R.array.knowledge_sort_menu_array);
        ToolMenu menu1 = new ToolMenu(1, menuString[0], null);
        ToolMenu menu2 = new ToolMenu(2, menuString[1], null);
        list.add(menu1);
        list.add(menu2);
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (!TextUtils.isEmpty(dataId)) {
            if (isAnswer) {
                if (mDataBean != null) {
                    setData();
                }
                getRevelantData(1);
            } else {
                getDetailData();

            }
            getCommentData();
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mTextWebView.loadUrl(1, Constants.EMAIL_DETAIL_URL);
        viewDelegate.mTextWebView.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webStateReady = true;
                if (mDataBean != null) {
                    viewDelegate.mTextWebView.setWebText(mDataBean.getContent());
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                webStateReady = false;
            }
        });
        mCommentInputView.setOnChangeListener(new CommentInputView.OnChangeListener() {
            @Override
            public void onSend(int state) {

            }

            @Override
            public void onLoad(int state) {

            }

            @Override
            public void onSuccess(CommentDetailResultBean.DataBean bean) {
                commentList.add(bean);
                mCommentAdapter.notifyDataSetChanged();
                viewDelegate.mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


        viewDelegate.setAttaClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<AttachmentBean> data = ((EmailAttachmentAdapter) adapter).getData();
                if (FileTypeUtils.isImage(data.get(position).getFileType())) {
                    ArrayList<Photo> photoList = new ArrayList<Photo>();
                    Photo p = new Photo(data.get(position).getFileUrl());
                    p.setName(data.get(position).getFileName());
                    photoList.add(p);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
                    CommonUtil.startActivtiy(mContext, FullscreenViewImageActivity.class, bundle);
                } else {
                    ToastUtils.showToast(mContext, "暂不支持预览");
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                List<AttachmentBean> data = ((EmailAttachmentAdapter) adapter).getData();
                UploadFileBean bena = new UploadFileBean();
                bena.setFile_size(data.get(position).getFileSize());
                bena.setFile_url(data.get(position).getFileUrl());
                bena.setSerial_number(data.get(position).getSerial_number());
                bena.setFile_type(data.get(position).getFileType());
                bena.setOriginal_file_name(data.get(position).getOriginal_file_name());
                bena.setFile_name(data.get(position).getFileName());
                bena.setUpload_by(data.get(position).getUpload_by());
                if (FileTransmitUtils.checkLimit(TextUtil.parseLong(data.get(position).getFileSize()))) {

                    DialogUtils.getInstance().sureOrCancel(mContext, "",
                            "当前为移动网络且文件大小超过10M,继续下载吗?", viewDelegate.getRootView(),
                            new DialogUtils.OnClickSureListener() {
                                @Override
                                public void clickSure() {
                                    //zzh;修改逻辑为先跳转文件详情再下载或查看
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constants.DATA_TAG1,(Serializable)bena);
                                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/file_by_downloader", bundle);
                                   // FileTransmitUtils.downloadFileFromUrl(data.get(position).getFileUrl(), data.get(position).getFileName());
                                }
                            });
                } else {
                    //zzh;修改逻辑为先跳转文件详情再下载或查看
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.DATA_TAG1, (Serializable)bena);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/file_by_downloader", bundle);
                    //FileTransmitUtils.downloadFileFromUrl(data.get(position).getFileUrl(), data.get(position).getFileName());
                }
            }
        });
        viewDelegate.setAnswerAdapter(mAnswerAdapter);
        viewDelegate.setAnsweClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, mAnswerAdapter.getData().get(position).getId());
                bundle.putBoolean(Constants.DATA_TAG2, true);
                final KnowledgeBean knowledgeBean = mAnswerAdapter.getData().get(position);
                knowledgeBean.setAllot_manager(mDataBean.getAllot_manager());
                knowledgeBean.setTitle(mDataBean.getTitle());
                knowledgeBean.setCreate_by(knowledgeBean.getCreate_by());
                knowledgeBean.setCreate_time(knowledgeBean.getCreate_time());
                bundle.putSerializable(Constants.DATA_TAG3, knowledgeBean);
                bundle.putString(Constants.DATA_TAG4, dataId);
                CommonUtil.startActivtiyForResult(mContext, KnowledgeDetailActivity.class, Constants.REQUEST_CODE7, bundle);
            }
        });
        viewDelegate.setCommentClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                Bundle bundle = new Bundle();
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                if (CollectionUtils.isEmpty(bean.getInformation())) {
                    return;
                }
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                if (bean.getItemType() == 2) {
                    //语音
                    bundle.putString(Constants.DATA_TAG1, uploadFileBean.getFile_url());
                    bundle.putInt(Constants.DATA_TAG2, uploadFileBean.getVoiceTime());
                    CommonUtil.startActivtiy(mContext, VoicePlayActivity.class, bundle);
                } else if (bean.getItemType() == 3) {
                    //文件
                    SocketMessage socketMessage = new SocketMessage();
                    socketMessage.setMsgID(bean.getId());
                    socketMessage.setFileName(uploadFileBean.getFile_name());
                    socketMessage.setFileUrl(uploadFileBean.getFile_url());
                    socketMessage.setFileSize(TextUtil.parseInt(uploadFileBean.getFile_size()));
                    socketMessage.setFileType(uploadFileBean.getFile_type());
                    socketMessage.setSenderName(bean.getEmployee_name());
                    socketMessage.setServerTimes(TextUtil.parseLong(bean.getDatetime_time()));
                    bundle.putSerializable(MsgConstant.MSG_DATA, socketMessage);
                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle);
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                final CommentDetailResultBean.DataBean dataBean = mCommentAdapter.getData().get(position);
                UploadFileBean uploadFileBean = dataBean.getInformation().get(0);
                ArrayList<Photo> list = Photo.toPhotoList(uploadFileBean);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, list);
                bundle.putInt(ImagePagerActivity.SELECT_INDEX, 0);
                CommonUtil.startActivtiy(mContext, ImagePagerActivity.class, bundle);
            }
        });
        mRelevantAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
               /* queryAuth(relevantList.get(position).getBeanName(), relevantList.get(position).getId() + "", position);*/
               // viewData(position);
                viewData(relevantList.get(position));
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {

            }
        });

        viewDelegate.get(R.id.rl_answer).setOnClickListener(v -> {
            addAnswer();
        });
        viewDelegate.get(R.id.rl_invest).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
            CommonUtil.startActivtiyForResult(mContext, SelectMemberActivity.class, Constants.REQUEST_CODE2, bundle);
        });
        viewDelegate.get(R.id.rl_collect).setOnClickListener(v -> {
            ClickUtil.click(() -> changeCollectState());
        });
        viewDelegate.get(R.id.rl_zan).setOnClickListener(v -> {
            ClickUtil.click(() -> changePraiseState());

        });
        viewDelegate.get(R.id.ll_study_state).setOnClickListener(v -> {
            ClickUtil.click(() -> changeStudyState());
        });
        viewDelegate.get(R.id.rl_view_num).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, ViewMemberActivity.TYPE1);
            bundle.putString(Constants.DATA_TAG2, dataId);
            CommonUtil.startActivtiy(mContext, ViewMemberActivity.class, bundle);
        });
        viewDelegate.get(R.id.rl_star_num).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, ViewMemberActivity.TYPE2);
            bundle.putString(Constants.DATA_TAG2, dataId);
            CommonUtil.startActivtiy(mContext, ViewMemberActivity.class, bundle);
        });
        viewDelegate.get(R.id.rl_zan_num).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, ViewMemberActivity.TYPE3);
            bundle.putString(Constants.DATA_TAG2, dataId);
            CommonUtil.startActivtiy(mContext, ViewMemberActivity.class, bundle);
        });
        viewDelegate.get(R.id.rl_study_num).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, ViewMemberActivity.TYPE4);
            bundle.putString(Constants.DATA_TAG2, dataId);
            CommonUtil.startActivtiy(mContext, ViewMemberActivity.class, bundle);
        });
        viewDelegate.get(R.id.rl_sort_answer).setOnClickListener(v -> {
            PopUtils.showMenuPopupWindow(mContext, 2, viewDelegate.get(R.id.rl_sort_answer), list, position -> {
                currentSotrtType = position;
                answerOrderBy = sortMenuValueArr[position];
                viewDelegate.setSortType(menuString[currentSotrtType]);
                getAnswerList();
                return true;
            });
        });
    }

    /**
     * 写回答
     */
    private void addAnswer() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, MemoConstant.ADD_ANSWER);
        bundle.putSerializable(Constants.DATA_TAG2, mDataBean);
        bundle.putString(Constants.DATA_TAG3, dataId);
        CommonUtil.startActivtiyForResult(mContext, AddKnowledgeActivity.class, Constants.REQUEST_CODE1, bundle);
    }

    /**
     * 收藏与取消收藏
     */
    private void changeCollectState() {
        boolean selected = viewDelegate.get(R.id.iv_collect).isSelected();
        viewDelegate.get(R.id.iv_collect).setSelected(!selected);
        Map<String, Object> map = new HashMap<>();
        map.put("repositoryId", dataId);
        //0 收藏、1 取消收藏
        map.put("status", selected ? 1 : 0);
        model.updateCollection(mContext, map, new ProgressSubscriber<BaseBean>(mContext, false) {
            @Override
            public void onCompleted() {
                super.onCompleted();
                int num = TextUtil.parseInt(mDataBean.getCollectioncount());
                if (selected) {
                    num--;
                } else {
                    num++;
                }
                if (num < 0) {
                    num = 0;
                }
                viewDelegate.setText(R.id.tv_star_num, num + "");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                viewDelegate.get(R.id.iv_collect).setSelected(selected);
            }
        });
    }

    /**
     * 修改学习状态
     */
    private void changeStudyState() {
        boolean selected = viewDelegate.get(R.id.iv_check).isSelected();
        String subTitle = "";
        if (selected) {
            subTitle = "确定要取消学习记录吗？";
        } else {
            subTitle = "如果你已经学习完该知识，请点击“确定”按钮，系统会将你的学习状态标记下来，以便管理员查询员工学习记录。";
        }
        DialogUtils.getInstance().sureOrCancel(mContext, "提示", subTitle, viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
            @Override
            public void clickSure() {
                viewDelegate.changeStudyState(!selected);
                Map<String, Object> map = new HashMap<>();
                map.put("repositoryId", dataId);
                //0确认学习1取消学习
                map.put("status", selected ? 1 : 0);
                model.updateLearning(mContext, map, new ProgressSubscriber<BaseBean>(mContext, false) {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        int num = TextUtil.parseInt(mDataBean.getStudycount());
                        if (selected) {
                            num--;
                        } else {
                            num++;
                        }
                        if (num < 0) {
                            num = 0;
                        }
                        viewDelegate.setText(R.id.tv_study_num, num + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        viewDelegate.changeStudyState(selected);
                        ToastUtils.showToast(mContext, "更改学习状态失败");
                    }
                });
            }
        });

    }

    /**
     * 点赞与取消点赞
     */
    private void changePraiseState() {
        boolean selected = viewDelegate.get(R.id.iv_zan).isSelected();
        viewDelegate.get(R.id.iv_zan).setSelected(!selected);
        Map<String, Object> map = new HashMap<>();
        map.put("repositoryId", dataId);
        //0 点赞、 1 取消点赞
        map.put("status", selected ? 1 : 0);
        model.updatePraise(mContext, map, new ProgressSubscriber<BaseBean>(mContext, false) {
            @Override
            public void onCompleted() {
                super.onCompleted();
                //修改点赞人数
                int num = TextUtil.parseInt(mDataBean.getPraisecount());
                if (selected) {
                    num--;
                } else {
                    num++;
                }
                if (num < 0) {
                    num = 0;
                }
                viewDelegate.setText(R.id.tv_zan_num, num + "");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                viewDelegate.get(R.id.iv_zan).setSelected(selected);
            }
        });
    }

    /**
     * 获取详情
     */
    private void getDetailData() {
        model.getDataDetail(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<KnowledgeDetailBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(KnowledgeDetailBean baseBean) {
                super.onNext(baseBean);
                mDataBean = baseBean.getData();
                setData();
                getRevelantData(0);
            }
        });
    }

    /**
     * 获取答案详情
     */
    private void getAnswerDetail() {
        setResult(RESULT_OK);
        model.getAnswerDetail(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<KnowledgeDetailBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(KnowledgeDetailBean knowledgeDetailBean) {
                super.onNext(knowledgeDetailBean);
                if (isAnswer) {
                    if (mDataBean != null) {
                        knowledgeDetailBean.getData().setAllot_manager(mDataBean.getAllot_manager());
                        knowledgeDetailBean.getData().setTitle(mDataBean.getTitle());
                        knowledgeDetailBean.getData().setCreate_by(mDataBean.getCreate_by());
                        knowledgeDetailBean.getData().setCreate_time(mDataBean.getCreate_time());
                        mDataBean = knowledgeDetailBean.getData();
                    } else {
                        mDataBean = knowledgeDetailBean.getData();
                    }
                }
                getRevelantData(1);
                setData();
            }
        });

    }

    /**
     * 获取评论数据
     */
    private void getCommentData() {
        String beanName = MemoConstant.BEAN_NAME_KNOWLEDGE2;
        if (typeStatus ==1) {
            beanName = MemoConstant.BEAN_NAME_KNOWLEDGE_ANSWER;
        }
        model.getCommentDetail(mContext, dataId, beanName, new ProgressSubscriber<CommentDetailResultBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(CommentDetailResultBean commentDetailResultBean) {
                super.onNext(commentDetailResultBean);
                commentList.clear();
                commentList.addAll(commentDetailResultBean.getData());
                mCommentAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取关联数据
     *
     * @param dataType 0:知识库 1：回答
     */
    private void getRevelantData(int dataType) {
        model.queryAssociatesByRepositoryId(mContext, TextUtil.parseLong(dataId), dataType, new ProgressSubscriber<RevelantDataListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(RevelantDataListBean bean) {
                super.onNext(bean);
                relevantList.clear();
                final ArrayList<RevelantDataListBean.DataListBean> dataList = bean.getData().getDataList();
                for (RevelantDataListBean.DataListBean data : dataList) {
                    relevantList.addAll(data.getModuleDataList());
                }
                if (mDataBean != null) {
                    mDataBean.setRevelant(relevantList);
                }
                mRelevantAdapter.setItemList(relevantList);
                if (relevantList.size() > 0) {
                    viewDelegate.get(R.id.rl_relevant_root).setVisibility(View.VISIBLE);
                } else {
                    viewDelegate.get(R.id.rl_relevant_root).setVisibility(View.GONE);
                }

            }
        });
    }

    /**
     * 获取回答列表
     */
    private void getAnswerList() {
        viewDelegate.get(R.id.rl_sort_answer).setVisibility(View.VISIBLE);
        viewDelegate.get(R.id.rv_answer).setVisibility(View.VISIBLE);
        model.getRepositoryAnswerList(mContext, TextUtil.parseLong(dataId), answerOrderBy, new ProgressSubscriber<AnswerListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AnswerListBean baseBean) {
                super.onNext(baseBean);
                answerList.clear();
                answerList.addAll(baseBean.getData());
                viewDelegate.setAnswerNum(answerList.size());
                mAnswerAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 处理数据显示
     */
    private void setData() {
        if (mDataBean == null) {
            viewDelegate.get(R.id.rl_root).setVisibility(View.GONE);
            ToastUtils.showError(mContext, "数据错误");
            return;
        }
        typeStatus =TextUtil.parseInt(mDataBean.getType_status());
        if (typeStatus ==1){//提问
            mCommentInputView.setData(MemoConstant.BEAN_NAME_KNOWLEDGE_ANSWER, dataId);
        }else {//知识库
            mCommentInputView.setData(MemoConstant.BEAN_NAME_KNOWLEDGE2, dataId);
        }

        if (isAnswer) {
            topStatus = "1".equals(mDataBean.getTop());
        }
        viewDelegate.get(R.id.rl_root).setVisibility(View.VISIBLE);
        //提问型知识,任何人都可以写回答,管理员和创建者可以邀请回答
        if (mDataBean.getCreate_by() == null) {
            isCreator = false;
        } else {
            isCreator = SPHelper.getEmployeeId().equals(mDataBean.getCreate_by().getId() + "");
        }
        ArrayList<Member> managerList = mDataBean.getAllot_manager();
        if (managerList != null) {
            for (Member member : managerList) {
                if (SPHelper.getEmployeeId().equals(member.getId() + "")) {
                    isManager = true;
                }
            }
        }

        if (!isAnswer) {
            //收藏状态
            viewDelegate.get(R.id.iv_collect).setSelected("1".equals(mDataBean.getAlreadycollecting()));
            //点赞状态
            viewDelegate.get(R.id.iv_zan).setSelected("1".equals(mDataBean.getAlreadycollecting()));
            //学习状态
            viewDelegate.changeStudyState("1".equals(mDataBean.getAlreadystudying()));
            //学习人数
            viewDelegate.setText(R.id.tv_study_num, mDataBean.getStudycount());
            //点赞人数
            viewDelegate.setText(R.id.tv_zan_num, mDataBean.getPraisecount());
            //收藏人数
            viewDelegate.setText(R.id.tv_star_num, mDataBean.getCollectioncount());
            //查看人数
            viewDelegate.setText(R.id.tv_view_num, mDataBean.getReadcount());

            //分类
            viewDelegate.setText(R.id.tv_catg, mDataBean.getClassification_name());
            //标签
            LinearLayout llTag = viewDelegate.get(R.id.ll_tags);
            llTag.removeAllViews();
            final ArrayList<ProjectLabelBean> label_ids = mDataBean.getLabel_ids();
            for (int i = 0; i < label_ids.size(); i++) {
                View view = View.inflate(mContext, R.layout.memo_knowledge_tag_item, null);
                TextView tx = view.findViewById(R.id.tv);
                TextUtil.setText(tx, label_ids.get(i).getName());
                llTag.addView(view);
            }
            if ("1".equals(mDataBean.getType_status())) {
                //获取回答列表
                getAnswerList();
                viewDelegate.get(R.id.ll_ai).setVisibility(View.VISIBLE);
                viewDelegate.get(R.id.rl_invest).setVisibility(isCreator || isManager ? View.VISIBLE : View.GONE);
            } else if ("0".equals(mDataBean.getType_status())) {
                viewDelegate.get(R.id.ll_ai).setVisibility(View.GONE);
            }
        }

        //标题
        viewDelegate.setText(R.id.tv_knowledge_title, mDataBean.getTitle());
        //创建人名字
        viewDelegate.setText(R.id.tv_name, mDataBean.getCreate_by().getEmployee_name());

        if (webStateReady) {
            viewDelegate.mTextWebView.setWebText(mDataBean.getContent());
        }
        //创建日期
        String time = DateTimeUtil.longToStr(TextUtil.parseLong(mDataBean.getCreate_time()), "yyyy-MM-dd HH:mm");
        viewDelegate.setText(R.id.tv_position, time);
        //创建人头像
        ImageView avatar = viewDelegate.get(R.id.avatar);
        ImageLoader.loadCircleImage(mContext, mDataBean.getCreate_by().getPicture(), avatar, mDataBean.getCreate_by().getEmployee_name());
        //视频
        videoList.clear();
        /*for (int i = 0; i < 5; i++) {
            VideoItemBean bean = new VideoItemBean();
            bean.setTitle("视频" + i);
            bean.setUrl("http://player.youku.com/embed/XNDAyNjc3MTUyMA==");
            videoList.add(bean);
        }*/
        if (!TextUtils.isEmpty(mDataBean.getVideo())) {
            try {
                final JSONArray array = JSONObject.parseArray(mDataBean.getVideo());
                if (array != null && array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        VideoItemBean bean = new VideoItemBean();
                        bean.setTitle(array.getJSONObject(i).getString("title"));
                        bean.setUrl(array.getJSONObject(i).getString("url"));
                        videoList.add(bean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*videoList.addAll(mDataBean.getVideo());*/
        mVideoAdapter.notifyDataSetChanged();
        //附件
        if (isAnswer) {
            attList.clear();
            attList.addAll(mDataBean.getRepository_answer_attachment());
            mAttAdapter.notifyDataSetChanged();
        } else {
            attList.clear();
            attList.addAll(mDataBean.getRepository_lib_attachment());
            mAttAdapter.notifyDataSetChanged();
        }
        if (attList.size() == 0) {
            viewDelegate.get(R.id.rl_attachment_root).setVisibility(View.GONE);
        } else {
            viewDelegate.get(R.id.rl_attachment_root).setVisibility(View.VISIBLE);
        }
        initMenuOption();

    }

    /**
     * 初始化菜单
     */
    private void initMenuOption() {
        if (isAnswer) {
            //回答
            if (isManager) {
                optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array3);
            } else if (isCreator) {
                optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array4);
            } else {
                viewDelegate.showMenu();
            }
            if (optionMenuArr != null && optionMenuArr.length > 0) {
                if (topStatus) {
                    optionMenuArr[0] = getResources().getString(R.string.memo_knowledge_detail_menu_cancel_top);
                } else {
                    optionMenuArr[0] = getResources().getString(R.string.memo_knowledge_detail_menu_put_on_top);
                }
            }
            optionMenuArr2 = optionMenuArr;
        } else {
            if ("0".equals(mDataBean.getType_status())) {
                //知识型
                if (isManager) {
                    optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array1);
                } else if (isCreator) {
                    optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array5);
                } else {
                    optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array6);
                }
            } else if ("1".equals(mDataBean.getType_status())) {
                //提问型
                if (isManager) {
                    optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array2);
                } else if (isCreator) {
                    optionMenuArr = getResources().getStringArray(R.array.memo_knowledge_detail_menu_array7);
                } else {
                    viewDelegate.showMenu();
                }
            }
            if (optionMenuArr != null) {
                optionMenuArr2 = optionMenuArr;
            }
            if (isManager || "2".equals(SPHelper.getRole()) || "3".equals(SPHelper.getRole())) {

                if (optionMenuArr != null) {
                    optionMenuArr2 = new String[optionMenuArr.length + 1];
                    for (int i = 0; i < optionMenuArr.length; i++) {
                        optionMenuArr2[i] = optionMenuArr[i];
                    }
                    if ("0".equals(mDataBean.getTop())) {
                        optionMenuArr2[optionMenuArr2.length - 1] = "置顶";
                    } else {
                        optionMenuArr2[optionMenuArr2.length - 1] = "取消置顶";

                    }
                } else {
                    if ("0".equals(mDataBean.getTop())) {
                        optionMenuArr2 = new String[]{"置顶"};
                    } else {
                        optionMenuArr2 = new String[]{"取消置顶"};
                    }

                }
            }
        }
        if (optionMenuArr2 == null || optionMenuArr2.length == 0) {
            viewDelegate.showMenu();
        } else {
            viewDelegate.showMenu(0);
        }


    }


    /**
     * 验证权限后查看关联
     *
     * @param moduleItemBean
     */
    public void viewData(TaskInfoBean moduleItemBean) {
        Map<String,Object> remap =new HashMap<>();
        String remapStr = "";
        if(moduleItemBean.getDataType() == 2){
            remap.put("data_Type",moduleItemBean.getDataType());
            remap.put("taskInfoId",moduleItemBean.getBean_id());
            remap.put("beanName",moduleItemBean.getBean_name());
            remap.put("taskName",moduleItemBean.getTask_name());
            remap.put("id",moduleItemBean.getBean_id());
            remap.put("projectId",moduleItemBean.getProject_id());
            remapStr = JSON.toJSONString(remap);
        }
        model.queryAuth(mContext, moduleItemBean.getBean_name(), "", moduleItemBean.getBean_id()+"",remapStr,
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
                                    TaskHelper.INSTANCE.clickTaskItem(KnowledgeDetailActivity.this, moduleItemBean);

                                    break;
                                case "2":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "3":
                                 //   ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(KnowledgeDetailActivity.this, moduleItemBean);
                                    break;
                                case "4":
                                    //   ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(KnowledgeDetailActivity.this, moduleItemBean);
                                    break;
                                case "5":
                                    //   ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(KnowledgeDetailActivity.this, moduleItemBean);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            ToastUtils.showError(mContext, "查询权限错误");
                        }
                    }
                });
    }

    /**
     * 查看关联数据详情
     *
     * @param position
     */
    private void viewData(int position) {
        final TaskInfoBean taskInfoBean = relevantList.get(position);
        Bundle bundle = new Bundle();
        switch (taskInfoBean.getDataType()) {
            case ProjectConstants.DATA_TASK_TYPE:
                TaskHelper.INSTANCE.clickTaskItem(mContext, taskInfoBean);
                break;
            case ProjectConstants.DATA_MEMO_TYPE:
                Intent intent = new Intent(mContext, MemoDetailActivity.class);
                intent.putExtra(Constants.DATA_TAG1, taskInfoBean.getBean_id() + "");
                startActivityForResult(intent, Constants.REQUEST_CODE8);
                break;
            case ProjectConstants.DATA_APPROVE_TYPE:
                TaskHelper.INSTANCE.clickTaskItem(mContext, taskInfoBean);
                break;
            case ProjectConstants.DATA_CUSTOM_TYPE:

                bundle.putString(Constants.MODULE_BEAN, taskInfoBean.getBean_name());
                bundle.putString(Constants.DATA_ID, taskInfoBean.getBean_id() + "");
                UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", bundle, Constants.REQUEST_CODE6);
                break;
            case ProjectConstants.DATA_EMAIL_TYPE:
                bundle.putString(Constants.DATA_TAG1, taskInfoBean.getBean_id() + "");
                bundle.putInt(Constants.DATA_TAG2, 1);
                bundle.putSerializable(Constants.DATA_TAG3, null);
                UIRouter.getInstance().openUri(mContext, "DDComp://email/detail", bundle, Constants.REQUEST_CODE6);
                break;
            default:
                break;
        }


    }

    /**
     * 选择新的分类与标签
     */
    private void changeCatgAndTag() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, MemoConstant.CHOOSE_CATG_TAG);
        bundle.putSerializable(Constants.DATA_TAG2, mDataBean);
        bundle.putString(Constants.DATA_TAG3, dataId);
        CommonUtil.startActivtiyForResult(mContext, AddKnowledgeActivity.class, Constants.REQUEST_CODE3, bundle);
    }

    /**
     * 查看版本
     */
    private void viewVersion() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, currentVersion);
        bundle.putString(Constants.DATA_TAG2, dataId);
        CommonUtil.startActivtiyForResult(mContext, ManageKnowledgeVersionActivity.class, Constants.REQUEST_CODE4, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDataBean != null) {
            initMenuOption();
        } else {
            ToastUtils.showToast(mContext, "正在初始化");
            return true;
        }
        if (optionMenuArr2 == null || optionMenuArr2.length == 0) {
            ToastUtils.showToast(mContext, "正在初始化");
            return true;
        }

        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "选择", optionMenuArr2, new OnMenuSelectedListener() {
            @Override
            public boolean onMenuSelected(int p) {
                final String s = optionMenuArr2[p];
                if (TextUtils.isEmpty(s)) {
                    return true;
                }
                if (getResources().getString(R.string.memo_knowledge_detail_menu_delete).equals(s)) {
                    //删除
                    deleteData();
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_edit).equals(s)) {
                    //编辑
                    editData();
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_move).equals(s)) {
                    //移动
                    changeCatgAndTag();
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_put_on_top).equals(s)) {
                    //置顶答案
                    if (isAnswer) {
                        topAnswer();
                    } else {
                        topKnowledge();
                    }
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_cancel_top).equals(s)) {
                    //取消置顶
                    if (isAnswer) {
                        topAnswer();
                    } else {
                        topKnowledge();
                    }
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_version_manage).equals(s)) {
                    //版本管理
                    viewVersion();
                } else if (getResources().getString(R.string.memo_knowledge_detail_menu_top).equals(s)) {
                    //置顶
                    topKnowledge();
                }

                return false;
            }
        });

        return super.onOptionsItemSelected(item);
    }

    /**
     * 置顶
     */
    private void topKnowledge() {
        model.putKnowledgeTop(mContext, dataId, "0".equals(mDataBean.getTop()) ? 1 : 0, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                if ("0".equals(mDataBean.getTop())) {
                    mDataBean.setTop("1");
                } else {
                    mDataBean.setTop("0");
                }
                initMenuOption();
                setResult(RESULT_OK);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 置顶答案 1:置顶 0 :取消置顶
     */
    private void topAnswer() {
        model.putAnswerTop(mContext, dataId, topStatus ? 0 : 1, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                topStatus = !topStatus;
                if (optionMenuArr != null && optionMenuArr.length > 0) {
                    if (topStatus) {
                        optionMenuArr[0] = getResources().getString(R.string.memo_knowledge_detail_menu_cancel_top);
                    } else {
                        optionMenuArr[0] = getResources().getString(R.string.memo_knowledge_detail_menu_put_on_top);
                    }
                }
                ToastUtils.showSuccess(mContext, "操作成功");
            }
        });
    }


    /**
     * 编辑知识/提问/答案
     */
    private void editData() {
        Bundle bundle = new Bundle();
        if (isAnswer) {
            bundle.putInt(Constants.DATA_TAG1, MemoConstant.EDIT_ANSWER);
            bundle.putString(Constants.DATA_TAG3, dataId);
            bundle.putString(Constants.DATA_TAG4, questionId);
        } else {
            if ("0".equals(mDataBean.getType_status())) {
                bundle.putInt(Constants.DATA_TAG1, MemoConstant.EDIT_KNOWLEDGE);
                bundle.putString(Constants.DATA_TAG3, dataId);
            } else if ("1".equals(mDataBean.getType_status())) {
                bundle.putInt(Constants.DATA_TAG1, MemoConstant.EDIT_QUESTION);
                bundle.putString(Constants.DATA_TAG3, dataId);
            }
        }
        mDataBean.setRelevantList(relevantList);
        if (isAnswer) {
            mDataBean.setRepository_answer_attachment(attList);
        } else {
            mDataBean.setRepository_lib_attachment(attList);

        }
        bundle.putSerializable(Constants.DATA_TAG2, mDataBean);
        CommonUtil.startActivtiyForResult(mContext, AddKnowledgeActivity.class, Constants.REQUEST_CODE5, bundle);

    }

    /**
     * 删除知识/提问/答案
     */
    private void deleteData() {
        if (isAnswer) {
            model.deleteAnswer(mContext, dataId, new ProgressSubscriber<BaseBean>(mContext) {
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
        } else {
            model.deleteKnowledge(mContext, dataId, new ProgressSubscriber<BaseBean>(mContext) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        mCommentInputView.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE1:
                //新增回答
                getAnswerList();
                break;
            case Constants.REQUEST_CODE2:
                //邀请回答
                if (data != null) {
                    ArrayList<Member> list = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
                    if (list != null && list.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (Member m : list) {
                            if (TextUtils.isEmpty(sb)) {
                                sb.append(m.getId());
                            } else {
                                sb.append(",");
                                sb.append(m.getId());
                            }
                        }
                        if (!TextUtils.isEmpty(sb)) {
                            model.updateInvitePersonsToAnswer(mContext, dataId, sb.toString(), new ProgressSubscriber<BaseBean>(mContext) {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }

                                @Override
                                public void onNext(BaseBean baseBean) {
                                    super.onNext(baseBean);
                                    ToastUtils.showSuccess(mContext, "邀请成功");
                                }
                            });
                        } else {
                            ToastUtils.showError(mContext, "人员为空");
                        }

                    } else {
                        ToastUtils.showError(mContext, "人员为空");

                    }

                }


                break;
            case Constants.REQUEST_CODE3:
                //修改分类与标签
                getData();
                setResult(RESULT_OK);
                break;
            case Constants.REQUEST_CODE4:
                //查看指定版本
                currentVersion = data.getStringExtra(Constants.DATA_TAG1);
                String content = data.getStringExtra(Constants.DATA_TAG2);
                viewDelegate.mTextWebView.setWebText(content);
                break;
            case Constants.REQUEST_CODE5:
                //内容已编辑
                if (isAnswer) {
                    getAnswerDetail();
                } else {
                    getData();
                    setResult(RESULT_OK);
                }
                break;
            case Constants.REQUEST_CODE6:

                break;
            case Constants.REQUEST_CODE7:
                getAnswerList();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
