package com.hjhq.teamface.memo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.PoiItem;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.ApproveListBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.MemoListItemBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.QuoteTaskResultBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.file.SPUtils;
import com.hjhq.teamface.basis.util.image.ImageformatUtil;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.ui.location.LocationPresenter;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.download.service.DownloadService;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.adapter.MemoItemAdapter;
import com.hjhq.teamface.memo.bean.AddRelevantBean;
import com.hjhq.teamface.memo.bean.MemoBean;
import com.hjhq.teamface.memo.bean.MemoContentBean;
import com.hjhq.teamface.memo.bean.MemoDetailBean;
import com.hjhq.teamface.memo.bean.MemoLocationBean;
import com.hjhq.teamface.memo.bean.NewMemoBean;
import com.hjhq.teamface.memo.view.AddMemoDelegate;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

@RouteNode(path = "/add", desc = "新增备忘录")
public class AddMemoActivity extends ActivityPresenter<AddMemoDelegate, MemoModel> {
    private MemoDetailBean mBean;
    private File imageFromCamera;
    private boolean isEdited = false;
    private List<TaskInfoBean> taskList = new ArrayList<>();
    private ImageView ivAction1;
    private ImageView ivAction2;
    private ImageView clearMember;
    private TaskItemAdapter mTaskAdapter;

    @Override
    public void init() {
        ivAction1 = viewDelegate.get(R.id.action1);
        ivAction2 = viewDelegate.get(R.id.action2);
        clearMember = viewDelegate.get(R.id.iv_del_member);
        ivAction1.setSelected(false);
        ivAction2.setSelected(false);
        mTaskAdapter = new TaskItemAdapter(taskList, false);
        mTaskAdapter.setType(TaskItemAdapter.EDIT_MODE);
        viewDelegate.setRelevantAdapter(mTaskAdapter);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBean = (MemoDetailBean) bundle.getSerializable(Constants.DATA_TAG1);
            if (mBean != null) {
                viewDelegate.setData(mBean);
                sortData(mBean.getData().getItemsArr());
            }
        }
        initListener();

    }


    private void initListener() {
        viewDelegate.getToolbar().setNavigationOnClickListener(v -> onBackPressed());
        viewDelegate.get(R.id.sv).setOnClickListener(v -> {
            SoftKeyboardUtils.showKeyboard(mContext, viewDelegate.getEditText());
        });
        viewDelegate.get(R.id.action1).setOnClickListener(v -> {
            //待办
            viewDelegate.changeCheckState();
            if (ivAction1.isSelected()) {
                ivAction1.setSelected(false);
                ivAction1.setImageResource(R.drawable.memo_add_check_false);
            } else {
                ivAction1.setImageResource(R.drawable.memo_add_check_true);
                ivAction1.setSelected(true);
            }
        });
        viewDelegate.get(R.id.action2).setOnClickListener(v -> {
            //编号
            viewDelegate.changeNumState();
            if (ivAction2.isSelected()) {
                ivAction2.setSelected(false);
                ivAction2.setImageResource(R.drawable.memo_add_num_false);
            } else {
                ivAction2.setSelected(true);
                ivAction2.setImageResource(R.drawable.memo_added_num);
            }
        });
        viewDelegate.get(R.id.action3).setOnClickListener(v -> {
            //附件/图片
            showAttachmentOption();
            SoftKeyboardUtils.hide(viewDelegate.getEditText());
        });
        viewDelegate.get(R.id.action4).setOnClickListener(v -> {
            //关联
            SoftKeyboardUtils.hide(viewDelegate.getEditText());
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, Constants.SELECT_FOR_MEMO);
            UIRouter.getInstance().openUri(mContext, "DDComp://project/appModule", bundle, Constants.REQUEST_CODE5);

        });
        viewDelegate.get(R.id.action5).setOnClickListener(v -> {
            //提醒
            Bundle bundle = new Bundle();
            Calendar calendar = Calendar.getInstance();
            if (viewDelegate.getRemindBean().getTime() <= 0) {
                calendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                calendar.setTime(new Date(viewDelegate.getRemindTime()));
            }

            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, calendar);
            bundle.putInt(Constants.DATA_TAG1, MemoConstant.REMIND_MODE_SELF);
            bundle.putBoolean(Constants.DATA_TAG3, viewDelegate.getRemindBean().getTime() > 0L);
            CommonUtil.startActivtiyForResult(AddMemoActivity.this,
                    MemoRemindTimeAndModeActivity.class, Constants.REQUEST_CODE1, bundle);
            SoftKeyboardUtils.hide(viewDelegate.getEditText());
        });
        viewDelegate.get(R.id.action6).setOnClickListener(v -> {
            //分享人
            Bundle bundle = new Bundle();
            ArrayList<Member> list = viewDelegate.getMembers();
            Member m = new Member();
            String id = SPUtils.getString(mContext, AppConst.EMPLOYEE_ID);
            m.setId(TextUtil.parseLong(id));
            m.setCheck(false);
            m.setSelectState(C.CAN_NOT_SELECT);
            list.add(m);
            bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
            bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, list);
            CommonUtil.startActivtiyForResult(AddMemoActivity.this,
                    SelectMemberActivity.class, Constants.REQUEST_CODE4, bundle);
            SoftKeyboardUtils.hide(viewDelegate.getEditText());

        });
        clearMember.setOnClickListener(v -> {
            DialogUtils.getInstance().sureOrCancel(mContext, "", "确认移除全部分享人?", viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
                @Override
                public void clickSure() {
                    viewDelegate.clearMember();
                }
            });

        });
        viewDelegate.get(R.id.action7).setOnClickListener(v -> {
            //位置
            CommonUtil.startActivtiyForResult(mContext, LocationPresenter.class,
                    Constants.REQUEST_CODE_SEND_LOCATION);
            SoftKeyboardUtils.hide(viewDelegate.getEditText());
        });
        viewDelegate.get(R.id.action8).setOnClickListener(v -> {
            //键盘
            if (SoftKeyboardUtils.isShown(mContext)) {
                SoftKeyboardUtils.toggleKeyboard(mContext, viewDelegate.getEditText());
            } else {
                SoftKeyboardUtils.toggleKeyboard(mContext, viewDelegate.getEditText());
            }
        });
        viewDelegate.get(R.id.rl_root).setOnClickListener(v -> {
            viewDelegate.requestInputFocus();
        });
        viewDelegate.setFocusChangeListener(new MemoItemAdapter.OnFocusChangeListener() {
            @Override
            public void onFocus(boolean check, boolean num) {
                ivAction1.setSelected(check);
                ivAction2.setSelected(num);
                if (check) {
                    ivAction1.setImageResource(R.drawable.memo_add_check_true);
                } else {
                    ivAction1.setImageResource(R.drawable.memo_add_check_false);
                }
                if (num) {
                    ivAction2.setImageResource(R.drawable.memo_added_num);

                } else {
                    ivAction2.setImageResource(R.drawable.memo_add_num_false);
                }
            }
        });
        viewDelegate.get(R.id.rl_content).setOnClickListener(v -> {
            SoftKeyboardUtils.show(viewDelegate.getEditText());
        });

    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onEvent(MessageBean bean) {
        if (bean != null && MemoConstant.MEMO_RELEVANT_IS_EMPTY.equals(bean.getTag())) {
            viewDelegate.get(R.id.rl_relevant).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveData(true);
        SoftKeyboardUtils.hide(viewDelegate.getEditText());
        return super.onOptionsItemSelected(item);
    }

    private void saveData(boolean needCheckNull) {
        boolean flag = true;
        NewMemoBean bean = new NewMemoBean();
        if (mBean != null) {
            bean.setId(mBean.getData().getId());
        } else {
            bean.setId("");
        }

        bean.setPicUrl("");
        bean.setRemindStatus(viewDelegate.getRemindBean().getMode());
        bean.setRemindTime(viewDelegate.getRemindTime());
        bean.setShareIds(viewDelegate.getSharedIds());
        List<MemoContentBean> cbList = new ArrayList<>();

        bean.setLocation(viewDelegate.getLocationData());
        //之前提交关联数据的字段
        //bean.setItemsArr(viewDelegate.getRelevantData());
        bean.setItemsArr(new ArrayList<>());

        List<MemoBean> data = viewDelegate.getData();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getType() == MemoConstant.ITEM_TEXT) {
                MemoContentBean cb = new MemoContentBean();
                cb.setType(MemoConstant.ITEM_TEXT);
                String content = data.get(i).getText().getContent();
                //转义",影响后台解析
                cb.setContent(content);
                String titleTemp = new String(content);
                if (data.get(i).getText().getNum() > 0) {
                    titleTemp = (data.get(i).getText().getNum() + ".") + content;
                }
                if (TextUtils.isEmpty(sb)) {
                    if (!TextUtils.isEmpty(titleTemp)) {
                        sb.append(titleTemp);
                    }
                } else {
                    if (!TextUtils.isEmpty(titleTemp)) {
                        if (!sb.toString().endsWith("\n")) {
                            sb.append("\n");
                        }
                        sb.append(titleTemp);
                    }
                }
                cb.setCheck(data.get(i).getText().getCheck());
                cb.setNum(data.get(i).getText().getNum());
                if (!TextUtils.isEmpty(cb.getContent()) || cb.getCheck() > 0 || cb.getNum() > 0) {
                    flag = false;
                }
                cbList.add(cb);
            } else if (data.get(i).getType() == MemoConstant.ITEM_IMAGE) {
                MemoContentBean cb = new MemoContentBean();
                cb.setType(MemoConstant.ITEM_IMAGE);
                cb.setContent(data.get(i).getImg().getUrl());
                cbList.add(cb);
                flag = false;
                if (TextUtils.isEmpty(bean.getPicUrl())) {
                    bean.setPicUrl(cb.getContent());
                }
            }

        }
        if (TextUtils.isEmpty(sb.toString())) {
            bean.setTitle("");
        } else {
            String title = sb.toString();
            if (title.length() <= 200) {
                bean.setTitle(title.toString());
            } else {
                bean.setTitle(title.substring(0, 200));
            }
        }

        bean.setContent(cbList);
        if (flag) {
            if (needCheckNull) {
                ToastUtils.showError(mContext, "请输入内容");
                return;
            }
        }
        if (!needCheckNull && checkContent(bean)) {
            finish();
            return;
        }
        if (mBean != null) {
            model.updateMemo(AddMemoActivity.this, bean, new ProgressSubscriber<BaseBean>(mContext) {

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    updateRevelantData(mBean.getData().getId());

                }
            });
        } else {
            model.saveMemo(AddMemoActivity.this, bean, new ProgressSubscriber<CommonNewResultBean>(mContext) {
                @Override
                public void onNext(CommonNewResultBean baseBean) {
                    super.onNext(baseBean);
                    updateRevelantData(baseBean.getData().getId() + "");

                }
            });
        }
    }

    private List<AddRelevantBean.BeanArrBean> getRelevantData() {
        List<AddRelevantBean.BeanArrBean> list = new ArrayList<>();
        if (taskList.size() == 0) {
            return list;
        }
        for (TaskInfoBean bean : taskList) {
            AddRelevantBean.BeanArrBean referenceBean = new AddRelevantBean.BeanArrBean();
            referenceBean.setType(bean.getDataType());
            switch (bean.getDataType()) {
                case ProjectConstants.DATA_APPROVE_TYPE:
                    referenceBean.setBean(bean.getBean_name());
                    referenceBean.setIds(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_MEMO_TYPE:
                    referenceBean.setBean(bean.getBean_name());
                    referenceBean.setIds(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_EMAIL_TYPE:
                    referenceBean.setBean(EmailConstant.BEAN_NAME);
                    referenceBean.setIds(bean.getId() + "");
                    break;
                case ProjectConstants.DATA_CUSTOM_TYPE:
                    referenceBean.setBean(bean.getBean_name() + "");
                    referenceBean.setIds(bean.getBean_id() + "");
                    break;
                case ProjectConstants.DATA_TASK_TYPE:
                    String projectId =bean.getProject_id()+"";
                    if (TextUtil.isEmpty(projectId) || "0".equals(projectId)){
                        projectId ="-1";
                    }
                    referenceBean.setProjectId(projectId);
                    referenceBean.setBean(bean.getBean_name() + "");
                    referenceBean.setIds(bean.getId() + "");
                    referenceBean.setType(bean.getBean_type());
                    break;
                default:

                    break;
            }
            list.add(referenceBean);
        }
        return list;
    }

    /**
     * 提交或更新关联数据
     */
    private void updateRevelantData(String id) {
        if (taskList.size() > 0) {
            AddRelevantBean rlBean = new AddRelevantBean();
            rlBean.setId(id);
            rlBean.setStatus("0");
            rlBean.setBeanArr(getRelevantData());
            model.updateRelationById(mContext, rlBean, new ProgressSubscriber<BaseBean>(mContext, true) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    saveMemoSuccess(id);
                }
            });
        } else {
            saveMemoSuccess(id);
        }
    }

    /**
     * 保存成功
     *
     * @param id
     */
    private void saveMemoSuccess(String id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.DATA_TAG1, TextUtil.parseLong(id));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 点击返回时检查内容为空直接关闭
     *
     * @param bean
     */
    private boolean checkContent(NewMemoBean bean) {
        if (TextUtils.isEmpty(bean.getTitle())
                && TextUtils.isEmpty(bean.getPicUrl())
                && TextUtils.isEmpty(bean.getShareIds())
                && bean.getLocation().size() <= 0
                && bean.getItemsArr().size() <= 0
                && bean.getRemindTime() <= 0
                ) {
            return true;
        } else {
            return false;
        }
    }

    private void showAttachmentOption() {
        String[] menu = {"拍照", "从相册中选择"};
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "选择图片", menu,
                new OnMenuSelectedListener() {
                    @Override
                    public boolean onMenuSelected(int position) {
                        Bundle bundle = new Bundle();
                        switch (position) {
                            case 0:
                                requestCamera();
                                break;
                            case 1:
                                //从相册中选择
                                CommonUtil.getImageFromAlbumByMultiple(mContext, 1);
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
        // TODO: 2018/3/23 外部存储状态判断


        imageFromCamera = CommonUtil.getImageFromCamera(mContext, Constants.TAKE_PHOTO_NEW_REQUEST_CODE);
        /*if (FileHelper.isSdCardExist()) {
        } else {
            ToastUtils.showToast(mContext, "暂无外部存储");
        }*/

    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 权限判断
     *
     * @return
     */
    private boolean requestCamera() {
        if (afterM()) {
            getRxPermissions().request(Manifest.permission.CAMERA).subscribe(aBoolean -> {
                if (aBoolean) {
                    takePhoto();
                } else {
                    ToastUtils.showToast(mContext, "必须获得必要的权限才能运行！");
                }
            });
        } else {
            takePhoto();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
                //相册
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (photos != null) {
                        for (int i = 0; i < photos.size(); i++) {
                            File file = new File(photos.get(i));
                            try {
                                if (file.exists()) {
                                    List<File> fileList = new ArrayList<>();
                                    File file2 = ImageformatUtil.saveBitmapFile(mContext, ImageformatUtil.getimage(file.getAbsolutePath()));
                                    fileList.add(file2);
                                    DownloadService.getInstance().uploadFile(fileList, new ProgressSubscriber<UpLoadFileResponseBean>(AddMemoActivity.this) {
                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                        }

                                        @Override
                                        public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                                            super.onNext(upLoadFileResponseBean);
                                            viewDelegate.addImage(upLoadFileResponseBean.getData().get(0).getFile_url());
                                            isEdited = true;
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else {

                }

                break;
            case Constants.TAKE_PHOTO_NEW_REQUEST_CODE:
                //拍照
                if (imageFromCamera != null && imageFromCamera.exists()) {
                    List<File> fileList = new ArrayList<>();
                    File file = ImageformatUtil.saveBitmapFile(mContext, ImageformatUtil.getimage(imageFromCamera.getAbsolutePath()));
                    fileList.add(file);
                    DownloadService.getInstance().uploadFile(fileList, new ProgressSubscriber<UpLoadFileResponseBean>(AddMemoActivity.this) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }

                        @Override
                        public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                            super.onNext(upLoadFileResponseBean);
                            viewDelegate.addImage(upLoadFileResponseBean.getData().get(0).getFile_url());
                            isEdited = true;
                        }
                    });

                }
                break;
            //定位 位置
            case Constants.REQUEST_CODE_SEND_LOCATION:
                //位置
                if (data != null) {
                    PoiItem poiInfo = data.getParcelableExtra(LocationPresenter.LOCATION_RESULT_CODE);
                    MemoLocationBean bean = new MemoLocationBean();
                    bean.setAddress(poiInfo.getProvinceName() + poiInfo.getCityName() + poiInfo.getAdName() + poiInfo.getBusinessArea() + poiInfo.getSnippet());
                    bean.setName(poiInfo.getTitle());
                    bean.setLat(poiInfo.getLatLonPoint().getLatitude() + "");
                    bean.setLng(poiInfo.getLatLonPoint().getLongitude() + "");
                    viewDelegate.addLocation(bean);
                    isEdited = true;
                }
                break;
            case Constants.REQUEST_CODE1:
                //提醒
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Calendar calendar = (Calendar) data.getSerializableExtra(Constants.DATA_TAG1);

                        int mode = data.getIntExtra(Constants.DATA_TAG2, MemoConstant.REMIND_MODE_SELF);
                        if (calendar != null) {
                            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                                ToastUtils.showToast(mContext, "提醒时间不能小于当前时间");
                            } else {
                                viewDelegate.addRemind(calendar.getTimeInMillis(), mode);
                                isEdited = true;
                            }

                        }
                    }
                } else {

                }

                break;
            case Constants.REQUEST_CODE4:
                //分享人
                if (data != null) {
                    ArrayList<Member> list = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
                    if (list == null || list.size() == 0) {
                        viewDelegate.setMember(new ArrayList<MemoBean.MemberBean>());
                    } else {
                        List<MemoBean.MemberBean> list2 = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (!TextUtils.isEmpty(SPHelper.getEmployeeId()) && SPHelper.getEmployeeId().equals(list.get(i).getId() + "")) {
                                continue;
                            }
                            MemoBean.MemberBean bean = new MemoBean.MemberBean();
                            bean.setName(list.get(i).getEmployee_name());
                            bean.setId(list.get(i).getId() + "");
                            bean.setAvatar(list.get(i).getPicture());
                            list2.add(bean);
                        }
                        viewDelegate.setMember(list2);
                        isEdited = true;
                    }
                }
                break;
            default:

                break;
        }
        if (requestCode == Constants.REQUEST_CODE5) {
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
                   // UIRouter.getInstance().openUri(mContext, "DDComp://project/choose_task", bundle, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE);
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
            sortData(list);
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
                bean.setBean_id(TextUtil.parseLong(approve.getApproval_data_id()));
                bean.setBean_name(moduleBean);
                bean.setPicture(approve.getPicture());
                list.add(bean);
            }
            sb.deleteCharAt(0);
            sortData(list);

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
                        if (beanType ==1){
                            bean.setBean_type(5);
                        }else {
                            bean.setBean_type(2);
                        }
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
                sortData(selectLsit);
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
            sortData(list);
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
            sortData(list);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 排序
     */
    private void sortData(List<TaskInfoBean> list1) {
        taskList = mTaskAdapter.getItemList();
        if (list1 != null && list1.size() > 0) {
            taskList.addAll(list1);
        }
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
        if (taskList.size() > 0) {
            viewDelegate.get(R.id.rl_relevant).setVisibility(View.VISIBLE);
        } else {
            viewDelegate.get(R.id.rl_relevant).setVisibility(View.GONE);
        }
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDelegate.requestInputFocus();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().sureOrCancel(mContext, "提示", "是否放弃编辑?", viewDelegate.getRootView(), "确定", "取消", new DialogUtils.OnClickSureOrCancelListener() {
            @Override
            public void clickSure() {
                finish();
            }

            @Override
            public void clickCancel() {
            }
        });
    }
}
