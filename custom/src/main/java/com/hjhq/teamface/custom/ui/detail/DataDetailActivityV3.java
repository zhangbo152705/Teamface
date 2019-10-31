package com.hjhq.teamface.custom.ui.detail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.database.SocketMessage;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.lable.entity.GameLabel;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.FullscreenViewImageActivity;
import com.hjhq.teamface.common.activity.ViewUserProtocolActivity;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.ui.dynamic.DynamicFragment;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.voice.VoicePlayActivity;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.componentservice.custom.CustomService;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.bean.AutoModuleResultBean;
import com.hjhq.teamface.custom.bean.DataRelationResultBean;
import com.hjhq.teamface.custom.bean.SeasPoolResponseBean;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.custom.bean.TransferDataReqBean;
import com.hjhq.teamface.custom.bean.TransformationRequestBean;
import com.hjhq.teamface.custom.bean.TransformationResultBean;
import com.hjhq.teamface.custom.ui.add.EditCustomActivity;
import com.hjhq.teamface.custom.ui.file.FileDetailActivity;
import com.hjhq.teamface.custom.ui.funcation.SharePresenter;
import com.hjhq.teamface.custom.ui.funcation.TransferPrincipalPresenter;
import com.hjhq.teamface.custom.ui.template.AutoModuleActivity;
import com.hjhq.teamface.custom.ui.template.RelevanceTempActivity;
import com.hjhq.teamface.custom.utils.CustomDialogUtil;
import com.hjhq.teamface.custom.utils.RelevanceModuleDialog;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.ReferenceViewInterface;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 自定义数据详情
 *
 * @author Administrator
 */
@RouteNode(path = "/detail_v3", desc = "自定义数据详情")
public class DataDetailActivityV3 extends ActivityPresenter<DataDetailDelegateV3, DataDetailModel>
        implements View.OnClickListener, ReferenceViewInterface {
    /**
     * 编辑请求码
     */
    public static final int REQUEST_EDIT_CODE = 0x503;
    /**
     * 复制请求码
     */
    public static final int REQUEST_COPY_CODE = 0x5013;
    /**
     * 转移负责人
     */
    public static final int REQUEST_TRANSFOR_CODE = 0x504;
    /**
     * 分享
     */
    public static final int REQUEST_SHARE_CODE = 0x5104;
    /**
     * 公海池分配选人
     */
    public static final int REQUEST_ALLOCATE_CODE = 0x5105;
    /**
     * 移动公海池
     */
    public static final int REQUEST_POOL_MOVE_CODE = 0x5106;
    /**
     * 退回公海池
     */
    public static final int REQUEST_POOL_BACK_CODE = 0x5107;

    public static final String POOL_PULL = "领取";
    public static final String POOL_EDIT = "编辑";
    public static final String POOL_ALLOCATE = "分配";
    public static final String POOL_MOVE = "移动";
    public static final String POOL_DEL = "删除";
    public static final String BACK_POOL = "退回公海池";

    private int minY = 0;
    private int maxY = 0;
    private boolean scrollUp = false;
    private boolean scrollDown = false;

    private String[] menuArray;
    private Map<String, String> menuMap = new HashMap<>();

    /**
     * 操作菜单
     */
    protected List<ModuleFunctionBean.DataBean> functionAuth;
    /**
     * 子表单
     */
    private String subformFields = "";
    protected ArrayList<TabListBean.DataBean.DataListBean> tabList;
    /**
     * 详情
     */
    protected Map<String, Object> detailMap = new HashMap<>();
    private CustomLayoutResultBean.DataBean customLayoutData;
    private RowBean operationInfo;
    /**
     * 公海池菜单ID 为null则代表不是公海池数据
     */
    private String seasPoolId;
    //是否是公海池管理员
    private boolean isPoolAdmin;
    //模块 bean
    protected String moduleBean;
    //数据ID
    protected String dataId;
    //模块id
    protected String moduleId;
    //公海池列表
    private List<SeasPoolResponseBean.DataBean> seasPoolList;

    private boolean isChange;
    //是否显示加载更多
    private boolean showLoadMore = true;
    private List<DataRelationResultBean.DataRelation.RefModule> refModules;

    private List<Fragment> fragments = new ArrayList<>();
    private int viewPagerHeight;
    private int toolBarPositionY;
    // private CommentFragment commentFragment;
    /**
     * 是否锁定
     */
    private boolean isLockedState;
    private List<AutoModuleResultBean.DataBean.DataListBean> autoModuleList;

    private int top1;
    private boolean switchedTitle = false;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
       /* getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            dataId = intent.getStringExtra(Constants.DATA_ID);
            seasPoolId = intent.getStringExtra(Constants.POOL);
            isPoolAdmin = intent.getBooleanExtra(Constants.IS_POOL_ADMIN, false);
        }
    }


    @Override
    public void init() {
        menuArray = getResources().getStringArray(R.array.custom_detail_menu_array);
        for (int i = 0; i < menuArray.length; i++) {
            menuMap.put(i + "", menuArray[i]);
        }
        loadData();
        getCommentDetail();
        CustomUtil.handleHidenFields(hashCode(), toString(), viewDelegate.getViewList());
        LinearLayout llFixed = viewDelegate.get(R.id.ll_fixed);
        LinearLayout llTab = viewDelegate.get(R.id.ll_tab);
        RelativeLayout rlSub = viewDelegate.get(R.id.rl_sub);
        RelativeLayout rlTab = viewDelegate.get(R.id.rl_tab);
        RelativeLayout rlTitle = viewDelegate.get(R.id.rl_title);
        RelativeLayout root = viewDelegate.get(R.id.root);
        rlTitle.post(() -> {
            int[] location0 = new int[2];
            rlTitle.getLocationOnScreen(location0);
            top1 = location0[1] + rlTitle.getHeight();
            int[] location1 = new int[2];
            rlTitle.getLocationOnScreen(location1);
            Log.e("root", location1[1] + "");
        });
        rlSub.getViewTreeObserver().addOnDrawListener(() -> {
            int[] location1 = new int[2];
            rlSub.getLocationOnScreen(location1);
            int[] location2 = new int[2];
            rlTab.getLocationOnScreen(location2);
            //标题变化
            if (location1[1] < top1 - rlSub.getHeight() / 2) {
                if (!switchedTitle) {
                    viewDelegate.setTitle("这是标题显示区域标题最多二十五个字全部显示在这里的");
                    viewDelegate.get(R.id.iv_icon).setVisibility(View.INVISIBLE);
                    viewDelegate.get(R.id.tv_sub_title).setVisibility(View.INVISIBLE);
                    switchedTitle = true;
                }
            } else {
                if (switchedTitle) {
                    viewDelegate.setTitle("详情");
                    viewDelegate.get(R.id.iv_icon).setVisibility(View.VISIBLE);
                    viewDelegate.get(R.id.tv_sub_title).setVisibility(View.VISIBLE);
                    switchedTitle = false;
                }
            }
            //写回答/邀请回答
            if (location2[1] < top1) {
                if (llTab.getChildCount() > 0) {
                    llTab.removeAllViews();
                    llFixed.addView(rlTab);
                }
            } else if (location2[1] > top1) {
                if (llFixed.getChildCount() > 0) {
                    llFixed.removeAllViews();
                    llTab.addView(rlTab);
                }
            }
        });
    }

    @Override
    protected void translucentstatus() {
        //StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           /* getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);*/
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * 加载数据
     */
    protected void loadData() {
        loadDataTetail();
        //loadDataRelation();
        //getAutoModule();
        //页签
        loadRelationTab();
        getModuleFunctionAuth();
        getSeasPool();
    }

    /**
     * 得到评论列表
     */
    public void getCommentDetail() {
        model.getCommentDetail(mContext, dataId, moduleBean, new ProgressSubscriber<CommentDetailResultBean>(mContext, false) {
            @Override
            public void onNext(CommentDetailResultBean commentDetailResultBean) {
                super.onNext(commentDetailResultBean);
                List<CommentDetailResultBean.DataBean> data = commentDetailResultBean.getData();
                CollectionUtils.notifyDataSetChanged(viewDelegate.mCommentAdapter, viewDelegate.mCommentAdapter.getData(), data);
                if (showLoadMore && viewDelegate.mCommentAdapter.getData().size() > 5) {
                    viewDelegate.mCommentAdapter.setShowMore(false);
                    viewDelegate.get(R.id.tv_show_more_comment).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 得到公海池
     */
    private void getSeasPool() {
        model.getSeapools(this, moduleBean, new ProgressSubscriber<SeasPoolResponseBean>(this) {
            @Override
            public void onNext(SeasPoolResponseBean baseBean) {
                super.onNext(baseBean);
                seasPoolList = baseBean.getData();
            }
        });
    }

    /**
     * 得到功能权限
     */
    public void getModuleFunctionAuth() {
        if (!TextUtil.isEmpty(seasPoolId)) {
            return;
        }
        AuthHelper.getInstance().getModuleFunctionAuth(this, moduleBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                functionAuth = moduleFunctionBean.getData();
            }

            @Override
            public void error() {
                ToastUtils.showError(mContext, "获取权限失败，请退出重试");
            }
        });
    }

    /**
     * 加载关联数据模块和title
     */
    private void loadDataRelation() {
        model.getDataRelation(this, dataId, moduleBean, new ProgressSubscriber<DataRelationResultBean>(this) {
            @Override
            public void onNext(DataRelationResultBean dataRelationResultBean) {
                super.onNext(dataRelationResultBean);
                DataRelationResultBean.DataRelation data = dataRelationResultBean.getData();
                if (data == null) {
                    ToastUtils.showError(mContext, "数据异常");
                    return;
                }
                viewDelegate.setDetailHeadView(data.getOperationInfo());
                refModules = data.getRefModules();
                // handleModule();
            }
        });
    }

    /**
     * 加载页签列表
     */
    private void loadRelationTab() {
        model.getCustomDataTabList(this, dataId, moduleBean, new ProgressSubscriber<TabListBean>(this) {
            @Override
            public void onNext(TabListBean dataRelationResultBean) {
                super.onNext(dataRelationResultBean);
                //页签数据
                viewDelegate.setTabData(dataRelationResultBean.getData().getDataList());
                tabList = dataRelationResultBean.getData().getDataList();
            }
        });
    }

    /**
     * 获取所有自动化模块
     */
    private void getAutoModule() {
        model.getAutoModule(this, moduleBean, new ProgressSubscriber<AutoModuleResultBean>(this) {
            @Override
            public void onNext(AutoModuleResultBean autoModuleResultBean) {
                super.onNext(autoModuleResultBean);
                autoModuleList = autoModuleResultBean.getData().getDataList();
                // handleModule();
            }
        });
    }

    /*private void handleModule() {
        viewDelegate.setAdapterData(refModules);
        viewDelegate.setAuthModule(autoModuleList);
    }*/


    /**
     * 资料
     */
    protected void loadDataTetail() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("id", dataId);
        map.put("bean", moduleBean);
        model.getDataDetail(this, map, new ProgressSubscriber<DetailResultBean>(this) {
            @Override
            public void onNext(DetailResultBean detailResultBean) {
                super.onNext(detailResultBean);
                detailMap = detailResultBean.getData();
                moduleId = detailMap.get("module_id") + "";
                operationInfo = JSONObject.parseObject(JSONObject.toJSONString(detailMap.get("operationInfo")), new TypeReference<RowBean>() {
                });
                if (operationInfo != null) {
                    viewDelegate.setDetailHeadView(operationInfo);
                }
                showDetail();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
        //客户资料
        Map<String, Object> layoutMap = new HashMap<>();
        layoutMap.put("bean", moduleBean);
        layoutMap.put("operationType", CustomConstants.DETAIL_STATE + "");
        layoutMap.put("dataId", dataId);
        //1 是公海池
        layoutMap.put("isSeasPool", TextUtil.isEmpty(seasPoolId) ? null : "1");
        model.getCustomLayout(this, layoutMap, new ProgressSubscriber<CustomLayoutResultBean>(this) {
            @Override
            public void onNext(CustomLayoutResultBean customLayoutResultBean) {
                super.onNext(customLayoutResultBean);
                customLayoutData = customLayoutResultBean.getData();
                showDetail();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 显示详情数据
     */
    private synchronized void showDetail() {
        if (customLayoutData != null && detailMap.size() > 0) {
            fragments.clear();
            /*if ("1".equals(customLayoutData.getCommentControl())) {
                viewDelegate.showComment();
                commentFragment = CommentFragment.newInstance(moduleBean, dataId);
                fragments.add(commentFragment);
                commentFragment.setOnChangeListener(new CommentFragment.OnChangeListener() {
                    @Override
                    public void onLoad(int state) {
                        scrollToBottom();
                    }
                });
            }*/
            if ("1".equals(customLayoutData.getDynamicControl())) {
                viewDelegate.showDynamic();
                DynamicFragment dynamicFragment = DynamicFragment.newInstance(moduleBean, dataId);
                fragments.add(dynamicFragment);
            }
            isLockedState = "1".equals(detailMap.get(CustomConstants.LOCKED_STATE));
            String email = CustomUtil.getEmail(detailMap);
            if (email != null) {
                /*viewDelegate.showEmail();
                EmailBoxFragment emailBoxFragment = EmailBoxFragment.newInstance(email);
                fragments.add(emailBoxFragment);*//*
            }*/
            }
            viewDelegate.drawLayout(customLayoutData, detailMap, CustomConstants.DETAIL_STATE, moduleBean);
            if ("1".equals(customLayoutData.getCommentControl())) {
                viewDelegate.get(R.id.tv_comment).setVisibility(View.VISIBLE);
                viewDelegate.get(R.id.line_1).setVisibility(View.GONE);

            }
            if ("1".equals(customLayoutData.getDynamicControl())) {
                viewDelegate.get(R.id.tv_comment).setVisibility(View.VISIBLE);
                if ("1".equals(customLayoutData.getCommentControl())) {
                    viewDelegate.get(R.id.line_1).setVisibility(View.VISIBLE);
                }
                viewDelegate.get(R.id.tv_dynamic).setVisibility(View.VISIBLE);
            }
        }
    }

    private void scrollToBottom() {
        viewDelegate.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
        viewDelegate.scrollView.clearFocus();
        /*viewDelegate.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SoftKeyboardUtils.isShown(viewDelegate.commentInputView.getInputView())) {
                    //SoftKeyboardUtils.show(viewDelegate.commentInputView.getInputView());
                    // viewDelegate.getRootView().scrollTo(0, -StatusBarUtil.getStatusBarHeight(mContext));
                    viewDelegate.commentInputView.getInputView().requestFocus();
                }
            }
        }, 100);*/

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.get(R.id.iv_back).setOnClickListener(v -> onBackPressed());
        viewDelegate.get(R.id.iv_menu).setOnClickListener(v -> optionsMenu());
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_DATA_DETAIL_CODE, tag -> CommonUtil.startActivtiy(mContext, DataDetailActivityV3.class, (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_CODE, tag -> {
            CommonUtil.startActivtiy(mContext, FileDetailActivity.class, (Bundle) tag);
        });

        viewDelegate.recyclerTab.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                // clickRelevance(position);
                viewTabData(position);
            }
        });
        viewDelegate.setOnClickListener(this, R.id.tv_dynamic, R.id.tv_comment, R.id.tv_email, R.id.iv_more);
      /*  viewDelegate.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                // viewDelegate.get(R.id.ll_bottom_option).getLocationOnScreen(location);
                int yPosition = location[1];

                int[] moduleLocation = new int[2];
                viewDelegate.recyclerTab.getLocationOnScreen(moduleLocation);
                float v1 = moduleLocation[1] + DeviceUtils.dpToPixel(mContext, 50);
                if (yPosition < v1) {
                    viewDelegate.scrollView.setNeedScroll(false);
                } else {
                    viewDelegate.scrollView.setNeedScroll(true);
                }
            }
        });*/
        viewDelegate.commentInputView.setData(moduleBean, dataId);
        viewDelegate.commentInputView.setOnChangeListener(new CommentInputView.OnChangeListener() {
            @Override
            public void onSend(int state) {
                // SoftKeyboardUtils.hide(viewDelegate.commentInputView.getInputView());
                if (SoftKeyboardUtils.isShown(viewDelegate.commentInputView.getInputView())) {
                    return;
                }
                scrollToBottom();
            }

            @Override
            public void onLoad(int state) {

            }

            @Override
            public void onSuccess(CommentDetailResultBean.DataBean bean) {
                viewDelegate.mCommentAdapter.getData().add(bean);
                viewDelegate.mCommentAdapter.notifyDataSetChanged();
                SoftKeyboardUtils.hide(viewDelegate.commentInputView.getInputView());
                scrollToBottom();
            }
        });
        viewDelegate.get(R.id.tv_show_more_comment).setOnClickListener(v -> {
            viewDelegate.get(R.id.tv_show_more_comment).setVisibility(View.GONE);
            viewDelegate.mCommentAdapter.setShowMore(true);
            viewDelegate.mCommentAdapter.notifyDataSetChanged();
            showLoadMore = false;
        });
        //点击
        viewDelegate.recyclerComment.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                ArrayList<Photo> list = Photo.toPhotoList(uploadFileBean);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, list);
                bundle.putInt(ImagePagerActivity.SELECT_INDEX, 0);
                CommonUtil.startActivtiy(mContext, ImagePagerActivity.class, bundle);
            }
        });
        //
        /*viewDelegate.commentInputView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                int[] location = new int[2];
                viewDelegate.commentInputView.getLocationOnScreen(location);

                if (location[1] > maxY) {
                    maxY = location[1];
                }
                if (minY == 0 && location[1] < maxY) {
                    minY = location[1];
                }
                Log.e("commentInputView", location[1] + "<<");
                Log.e("commentInputView", maxY + "<<" + minY);
                if (location[1] == maxY && !scrollDown) {
                    scrollDown = true;
                    getWindow().getDecorView().getRootView().scrollBy(0, StatusBarUtil.getStatusBarHeight(mContext));
                }
                if (location[1] == minY && !scrollUp) {
                    scrollUp = true;
                    getWindow().getDecorView().getRootView().scrollBy(0, StatusBarUtil.getStatusBarHeight(mContext));
                    viewDelegate.mCoordinatorLayout.setPadding(0, 0, 0, 30);
                }
            }
        });*/

    }

    /**
     * JS调用-查看图片
     *
     * @param value
     */
    @JavascriptInterface
    public void viewPicture(String value) {
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        Photo p = new Photo(value);
        p.setName(System.currentTimeMillis() + ".jpg");
        photoList.add(p);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
        CommonUtil.startActivtiy(this, FullscreenViewImageActivity.class, bundle);
        Log.i("内容1=    ", value);
    }

    @JavascriptInterface
    public void viewPicture() {
        String value = "https://cdn.flutterchina.club/images/homepage/header-illustration.png";
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        Photo p = new Photo(value);
        photoList.add(p);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
        CommonUtil.startActivtiy(this, FullscreenViewImageActivity.class, bundle);
        Log.i("内容1=    ", value);
    }

    @JavascriptInterface
    public void viewLink(String src){
        if (!TextUtil.isEmpty(src)){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DATA_TAG1, src);
            CommonUtil.startActivtiy(this, ViewUserProtocolActivity.class, bundle);
        }
    }

    /**
     * 富文本组件接收到高度信息
     *
     * @param value
     */
    @JavascriptInterface
    public void receiveHeight(String value) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(value);
            String bean = jsonObject.optString("bean");
            int height = jsonObject.optInt("height");
           // RxManager.$(DataDetailActivityV3.this.hashCode()).post(bean, height);
            Log.e("receiveHeight", bean + "----" + height);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void getContent2(Object value) {
        Log.i("内容2=    ", JSONObject.toJSONString(value));
    }

    /**
     * 点击关联模块
     *
     * @param position
     */
    private void clickRelevance(int position) {
        Bundle bundle = new Bundle();
        if (position < CollectionUtils.size(refModules)) {
            DataRelationResultBean.DataRelation.RefModule item = refModules.get(position);
            bundle.putString(Constants.MODULE_BEAN, item.getModuleName());
            bundle.putString(Constants.NAME, item.getModuleLabel());
            bundle.putString(Constants.DATA_ID, dataId);
            bundle.putString(Constants.FIELD_NAME, item.getFieldName());

            bundle.putString(CustomConstants.FIELD_VALUE_TAG, detailMap.get(item.getReferenceField()) + "");
            CommonUtil.startActivtiy(mContext, RelevanceTempActivity.class, bundle);
        } else {
            AutoModuleResultBean.DataBean.DataListBean dataListBean = autoModuleList.get(position - CollectionUtils.size(refModules));
            bundle.putString(Constants.DATA_TAG1, dataListBean.getSorce_bean());
            bundle.putString(Constants.DATA_TAG2, dataListBean.getTarget_bean());
            bundle.putString(Constants.DATA_ID, dataId);
            bundle.putString(Constants.NAME, dataListBean.getTitle());
            CommonUtil.startActivtiy(mContext, AutoModuleActivity.class, bundle);
        }
    }

    private void viewTabData(int position) {
        TabListBean.DataBean.DataListBean data = tabList.get(position);
        String type = data.getCondition_type();
        Bundle bundle = new Bundle();
        switch (type) {

            case "0":
                //自定义
                bundle.putSerializable(Constants.DATA_TAG1, tabList.get(position));
                bundle.putString(Constants.DATA_TAG2, dataId);
                bundle.putSerializable(Constants.DATA_TAG3, (Serializable) detailMap);
                CommonUtil.startActivtiy(mContext, RelevanceTempActivity.class, bundle);
                break;
            case "1":
                //关联关系
                bundle.putSerializable(Constants.DATA_TAG1, tabList.get(position));
                bundle.putString(Constants.DATA_TAG2, dataId);
                if (operationInfo != null) {
                    bundle.putString(CustomConstants.FIELD_VALUE_TAG, operationInfo.getValue() + "");
                    bundle.putString(Constants.FIELD_NAME, operationInfo.getName() + "");
                }
                bundle.putSerializable(Constants.DATA_TAG3, (Serializable) detailMap);
                CommonUtil.startActivtiy(mContext, RelevanceTempActivity.class, bundle);
                break;
            case "2":
                //邮件
                String email = CustomUtil.getEmail(detailMap);
                bundle.putString(Constants.DATA_TAG1, email);
                bundle.putString(Constants.DATA_TAG2, data.getSorce_bean());

                CommonUtil.startActivtiy(mContext, EmailBoxActivity.class, bundle);
                break;
            case "3":
                //自动化匹配
                bundle.putString(Constants.DATA_TAG1, data.getSorce_bean());
                bundle.putString(Constants.DATA_TAG2, data.getTarget_bean());
                bundle.putSerializable(Constants.DATA_TAG3, data);
                bundle.putString(Constants.DATA_ID, dataId);
                bundle.putString(Constants.NAME, data.getChinese_name());
                CommonUtil.startActivtiy(mContext, AutoModuleActivity.class, bundle);
                break;
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_comment) {
            viewDelegate.showComment();
        } else if (id == R.id.tv_dynamic) {
            viewDelegate.showDynamic();
        } else if (id == R.id.tv_email) {
            viewDelegate.showEmail();
        } else if (id == R.id.iv_more) {
            showRelevanceDialog();
        }
    }


    /**
     * 显示关联模块弹窗
     */
    private void showRelevanceDialog() {
        List<GameLabel> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tabList)) {
            for (TabListBean.DataBean.DataListBean remodule : tabList) {
                GameLabel label = new GameLabel();
                if ("2".equals(remodule.getCondition_type())) {
                    label.name = "邮件";
                } else {
                    label.name = remodule.getChinese_name();
                }
                list.add(label);
            }
        }
        new RelevanceModuleDialog(mContext)
                .builder().setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setListener((name, position) -> viewTabData(position))
                .addLabel(list).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        optionsMenu();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 操作事件
     */
    private void optionsMenu() {
        String[] options;
        if (!TextUtil.isEmpty(seasPoolId)) {
            options = getSeasPoolOptions();
            PopUtils.showBottomMenu(this, viewDelegate.getRootView(), "设置", options, position -> {
                switch (options[position]) {
                    case POOL_PULL:
                        pull();
                        break;
                    case POOL_EDIT:
                        editCustom();
                        break;
                    case POOL_ALLOCATE:
                        allocate();
                        break;
                    case POOL_MOVE:
                        moveSeasPool();
                        break;
                    case POOL_DEL:
                        delData();
                        break;
                    default:
                        break;
                }
                return true;
            });

        } else {
            //普通数据
            if (CollectionUtils.isEmpty(functionAuth)) {
                ToastUtils.showError(mContext, "未获取到功能权限");
                return;
            }
            options = getDataOptions();
            PopUtils.showBottomMenu(this, viewDelegate.getRootView(), "设置", options, position -> {
                if (BACK_POOL.equals(options[position])) {
                    //退回公海池
                    backSeasPool();
                    return true;
                }
                switch (functionAuth.get(position).getAuth_code()) {
                    case CustomConstants.UPDATE:
                        editCustom();
                        break;
                    case CustomConstants.TRANSFER:
                        transferPrincipal();
                        break;
                    case CustomConstants.ADD_NEW:
                        copyData();
                        break;
                    case CustomConstants.CONVERT:
                        convert();
                        break;
                    case CustomConstants.SHARE:
                        shareData();
                        break;
                    case CustomConstants.DELETE:
                        delData();
                        break;
                    default:
                        break;
                }
                return true;
            });
        }

    }

    /**
     * 得到公海池的操作功能
     */
    private String[] getSeasPoolOptions() {
        String[] options;
        if (isPoolAdmin) {
            if (CollectionUtils.isEmpty(seasPoolList)) {
                options = new String[4];
                options[0] = POOL_PULL;
                options[1] = POOL_EDIT;
                options[2] = POOL_ALLOCATE;
                options[3] = POOL_DEL;
            } else {
                options = new String[5];
                options[0] = POOL_PULL;
                options[1] = POOL_EDIT;
                options[2] = POOL_ALLOCATE;
                options[3] = POOL_MOVE;
                options[4] = POOL_DEL;
            }
        } else {
            options = new String[1];
            options[0] = POOL_PULL;
        }
        return options;
    }

    /**
     * 得到正常数据的操作功能
     */
    private String[] getDataOptions() {
        String[] options;
        Iterator<ModuleFunctionBean.DataBean> iterator = functionAuth.iterator();
        while (iterator.hasNext()) {
            ModuleFunctionBean.DataBean next = iterator.next();
            switch (next.getAuth_code()) {
                case CustomConstants.ADD_NEW:
                    next.setFunc_name("复制");
                    break;
                case CustomConstants.UPDATE:
                case CustomConstants.SHARE:
                case CustomConstants.DELETE:
                case CustomConstants.CONVERT:
                case CustomConstants.TRANSFER:
                    if (isLockedState) {
                        iterator.remove();
                    }
                    break;
                default:
                    iterator.remove();
                    break;
            }
        }
        //编辑、转移负责人、复制、转换、删除、共享；
        int size = functionAuth.size();
        if (!CollectionUtils.isEmpty(seasPoolList)) {
            options = new String[size + 1];
            options[size] = BACK_POOL;
        } else {
            options = new String[size];
        }
        int i = 0;
        for (ModuleFunctionBean.DataBean dataBean : functionAuth) {
            options[i++] = menuMap.get(dataBean.getAuth_code());
        }
        return options;
    }

    /**
     * 退回公海池
     */
    private void backSeasPool() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, BACK_POOL);
        bundle.putSerializable(Constants.DATA_TAG2, (Serializable) seasPoolList);
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.MODULE_ID, dataId);
        CommonUtil.startActivtiyForResult(mContext, SeasPoolActivity.class, REQUEST_POOL_BACK_CODE, bundle);
    }

    /**
     * 公海池移动
     */
    private void moveSeasPool() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, POOL_MOVE);
        bundle.putSerializable(Constants.DATA_TAG2, (Serializable) seasPoolList);
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.MODULE_ID, dataId);
        bundle.putString(Constants.POOL, seasPoolId);
        CommonUtil.startActivtiyForResult(mContext, SeasPoolActivity.class, REQUEST_POOL_MOVE_CODE, bundle);
    }

    /**
     * 公海池分配
     */
    private void allocate() {
        Bundle bundle = new Bundle();
        bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
        CommonUtil.startActivtiyForResult(mContext, SelectMemberActivity.class, REQUEST_ALLOCATE_CODE, bundle);
    }

    /**
     * 公海池领取
     */
    private void pull() {
        model.take(this, dataId, moduleBean, seasPoolId, new ProgressSubscriber<BaseBean>(this) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "领取成功！");
                seasPoolId = null;
                isPoolAdmin = false;
                loadData();
            }
        });
    }


    /**
     * 转换
     */
    private void convert() {
        model.getFieldTransformations(this, moduleBean, new ProgressSubscriber<TransformationResultBean>(this) {
            @Override
            public void onNext(TransformationResultBean baseBean) {
                super.onNext(baseBean);
                convertDialog(baseBean.getData());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void convertDialog(List<TransformationResultBean.DataBean> dataBeanList) {
        if (CollectionUtils.isEmpty(dataBeanList)) {
            ToastUtils.showToast(this, "无可转换的模块");
            return;
        }
        CustomDialogUtil.mutilDialog(this, "可转换目标模块", dataBeanList, viewDelegate.getRootView(), positions -> {
            if (!CollectionUtils.isEmpty(positions)) {
                TransformationRequestBean bean = new TransformationRequestBean();
                bean.setDataId(dataId);
                bean.setBean(moduleBean);
                bean.setIds(positions);
                model.transformations(this, bean, new ProgressSubscriber<BaseBean>(this) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        ToastUtils.showSuccess(mContext, "转换成功！");
                        isChange = true;
                    }
                });
            }
        });
    }

    /**
     * 共享数据
     */
    private void shareData() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.DATA_ID, dataId);
        CommonUtil.startActivtiyForResult(this, SharePresenter.class, REQUEST_SHARE_CODE, bundle);
    }

    /**
     * 复制
     */
    private void copyData() {
       /* model.copy(this, dataId, moduleBean, moduleId, new ProgressSubscriber<DetailResultBean>(this) {
            @Override
            public void onNext(DetailResultBean detailResultBean) {
                super.onNext(detailResultBean);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
                CommonUtil.startActivtiyForResult(DataDetailActivity.this, AddCustomActivity.class, REQUEST_COPY_CODE, bundle);
            }
        });*/

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.DATA_ID, dataId);
        bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
        bundle.putBoolean(Constants.DATA_TAG2, false);
        bundle.putSerializable(Constants.POOL, seasPoolId);
        CommonUtil.startActivtiyForResult(this, EditCustomActivity.class, REQUEST_COPY_CODE, bundle);

    }

    /**
     * 删除
     */
    private void delData() {
        /*DialogUtils.getInstance().sureOrCancel(this, "提示", "删除后不可恢复，确认要删除吗？", viewDelegate.getRootView(), () -> {
            model.del(this, dataId, moduleBean, new ProgressSubscriber<BaseBean>(this) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "删除成功");
                    setResult(RESULT_OK);
                    finish();
                }
            });
        });*/
        deleteData();
    }

    /**
     * 删除
     */
    private void deleteData() {
        final Set<String> strings = detailMap.keySet();
        final Iterator<String> iterator = strings.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            if (next.startsWith("subform")) {
                sb.append("," + next);
            }
        }

        if (!TextUtils.isEmpty(sb)) {
            subformFields = sb.toString().substring(1);
        }
        DialogUtils.getInstance().sureOrCancel(this, "提示", "删除后不可恢复，确认要删除吗？", viewDelegate.getRootView(), () -> {
            model.deleteData(this, dataId, moduleBean, subformFields, new ProgressSubscriber<BaseBean>(this) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "删除成功");
                    setResult(RESULT_OK);
                    finish();
                }
            });
        });
    }

    /**
     * 编辑
     */
    protected void editCustom() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.DATA_ID, dataId);
        bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
        bundle.putBoolean(Constants.DATA_TAG2, true);
        bundle.putSerializable(Constants.POOL, seasPoolId);
        CommonUtil.startActivtiyForResult(this, EditCustomActivity.class, REQUEST_EDIT_CODE, bundle);
    }

    /**
     * 转移负责人
     */
    private void transferPrincipal() {


        CommonUtil.startActivtiyForResult(this, TransferPrincipalPresenter.class, REQUEST_TRANSFOR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == REQUEST_EDIT_CODE) {
            isChange = true;
            loadDataTetail();
            //loadDataRelation();
            loadRelationTab();
        } else if (REQUEST_TRANSFOR_CODE == requestCode) {
            int share = data.getIntExtra(Constants.DATA_TAG1, 1);
            Member member = (Member) data.getSerializableExtra(Constants.DATA_TAG2);
            TransferDataReqBean bean = new TransferDataReqBean();
            bean.setBean(moduleBean);
            bean.setShare(share);
            bean.setTarget(member.getId() + "");
            TransferDataReqBean.DataBean dataBean = new TransferDataReqBean.DataBean();
            dataBean.setId(dataId);
            Object principal = detailMap.get("personnel_principal");
            JSONArray array = JSONObject.parseArray(JSONObject.toJSONString(principal));
            if (array.size() > 0) {
                final JSONObject jsonObject = array.getJSONObject(0);
                dataBean.setName(jsonObject.getString("name"));
                dataBean.setPicture(jsonObject.getString("picture"));
                dataBean.setPrincipal(jsonObject.getString("id"));
            }
            List<TransferDataReqBean.DataBean> list = new ArrayList<>();
            list.add(dataBean);
            bean.setData(list);
            model.transforV2(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "转移负责人成功");
                    isChange = true;
                    init();
                }
            });
            /*model.transfor(this, dataId, moduleBean, member.getId() + "", share + "", new ProgressSubscriber<BaseBean>(this) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "转移负责人成功");
                    isChange = true;
                    init();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });*/
        } else if (REQUEST_ALLOCATE_CODE == requestCode) {
            List<Member> members = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            Member member = members.get(0);
            model.allocate(mContext, dataId, moduleBean, member.getId() + "", new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    seasPoolId = null;
                    isPoolAdmin = false;
                    isChange = true;
                    loadData();
                }
            });
        } else if (REQUEST_POOL_BACK_CODE == requestCode) {
            setResult(RESULT_OK);
            finish();
        } else if (REQUEST_POOL_MOVE_CODE == requestCode) {
            setResult(RESULT_OK);
            finish();
        } else if (REQUEST_COPY_CODE == requestCode) {
            isChange = true;
        }
        viewDelegate.commentInputView.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == Constants.TAKE_PHOTO_NEW_REQUEST_CODE) {
            //上传拍照图片
            viewDelegate.commentInputView.prepareUploadPic();
        } else if (requestCode == Constants.PHOTO_ABLUM_NEW_REQUEST_CODE) {
            //上传相册图片
            Uri selectedImage = data.getData();
            viewDelegate.commentInputView.picktrueUpload(selectedImage);
        } else if (requestCode == Constants.SELECT_FILE_NEW_REQUEST_CODE) {
            //文件
            Uri uri = data.getData();
            viewDelegate.commentInputView.fileUpload(uri);
        } else if (requestCode == Constants.RESULT_CODE_SELECT_MEMBER) {
            //@功能
            ArrayList<Member> members = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            viewDelegate.commentInputView.appendMention(members);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (isChange) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private void dealWithViewPager() {
        /*int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
        int toolBarHeight = (int) (viewDelegate.getToolbar().getHeight() + statusBarHeight + DeviceUtils.dpToPixel(mContext, 40));
        toolBarPositionY = (int) (toolBarHeight + DeviceUtils.dpToPixel(mContext, 45));
        ViewGroup.LayoutParams params = viewDelegate.mViewPager.getLayoutParams();
        params.height = (int) (ScreenUtils.getScreenHeight(mContext) - toolBarHeight + 1);
        viewDelegate.mViewPager.setLayoutParams(params);*/
    }

    @Override
    public JSONObject getReferenceValue() {
        JSONObject json = new JSONObject();
        CustomService service = (CustomService) Router.getInstance().getService(CustomService.class.getSimpleName());
        //  service.putReference(viewDelegate.mViewList, json);
        return json;
    }
}
