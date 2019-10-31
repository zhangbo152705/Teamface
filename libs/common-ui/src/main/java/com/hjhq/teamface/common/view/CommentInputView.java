package com.hjhq.teamface.common.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.hjhq.teamface.basis.bean.AttachmentBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ClickUtil;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.FileUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.UriUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.JYFileHelper;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.bean.AddCommentRequestBean;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.ui.comment.CommentView;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.emoji.Emoji;
import com.hjhq.teamface.emoji.EmojiUtil;
import com.hjhq.teamface.emoji.FaceFragment;
import com.hjhq.teamface.emoji.FaceFragment.OnEmojiClickListener;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/17.
 * Describe：
 */

public class CommentInputView extends FrameLayout implements View.OnClickListener,
        OnEmojiClickListener, ActivityPresenter.OnActivityResult {
    private CommentAdapter mCommentAdapter;
    //文件所属文件夹类型,只有文件评论用
    private String folderStyle = "";
    private String moduleBean;
    private String objectId;
    private boolean isInputByKeyBoard = true;
    private File imageFromCamera;
    private RxAppCompatActivity mContext;


    private TableLayout mMoreMenuTl;
    private RecordVoiceButton mVoiceBtn;
    //public EditText mChatInputEt;
    public EmoticonsEditText mChatInputEt;
    private ImageView mSwitchIb;
    private ImageView mExpressionIb;
    private ImageView mPickFileIb;
    private ImageView mPickFileLibIb;
    private ImageView mAddFileIb;
    private ImageView mTakePhotoIb;
    private ImageView mPickPictureIb;
    private ImageView mAtSbIb;
    private LinearLayout mSendMsgLl;
    private Button mSendMsgBtn;
    private FrameLayout mContainer;
    private CommentView.OnSizeChangedListener mListener;
    private OnKeyBoardChangeListener mKeyboardListener;
    private OnChangeListener mOnChangeListener;
    public static final byte KEYBOARD_STATE_SHOW = -3;
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;
    private boolean mHasInit;
    private boolean mHasKeybord;
    private int mHeight;
    private boolean showAtIcon = true;
    private ArrayList<Member> mAllMemberList = new ArrayList<Member>();
    private List<Member> atList = new ArrayList<>();
    private List<Member> forDel = new ArrayList<>();

    public CommentInputView(@NonNull Context context) {
        super(context, null);
        this.mContext = ((RxAppCompatActivity) context);
        View view = LayoutInflater.from(context).inflate(R.layout.comment_input_bar_layout, null);
        addView(view);
        initView();
    }


    public CommentInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = (RxAppCompatActivity) context;
    }

    public CommentInputView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = (RxAppCompatActivity) context;

    }

    private void initView() {
        mContainer = findViewById(R.id.container);
        mTakePhotoIb = findViewById(R.id.jmui_pick_from_camera_btn);
        mPickPictureIb = findViewById(R.id.jmui_pick_from_local_btn);
        mPickFileIb = findViewById(R.id.jmui_pick_from_file_btn);
        mPickFileLibIb = findViewById(R.id.jmui_pick_from_filelib_btn);
        mMoreMenuTl = findViewById(R.id.jmui_more_menu_tl);
        mAddFileIb = findViewById(R.id.jmui_add_file_btn);
        mAtSbIb = findViewById(R.id.jmui_pick_from_member_btn);
        mSendMsgBtn = findViewById(R.id.jmui_send_msg_btn);
        mVoiceBtn = findViewById(R.id.jmui_voice_btn);
        mExpressionIb = findViewById(R.id.jmui_expression_btn);
        mChatInputEt = findViewById(R.id.jmui_chat_input_et);
        mChatInputEt.setFocusable(true);
        //mChatInputEt.requestFocus();
        mSwitchIb = findViewById(R.id.jmui_switch_voice_ib);
        mSendMsgLl = findViewById(R.id.jmui_send_msg_layout);
        FaceFragment faceFragment = FaceFragment.Instance();
        faceFragment.setListener(this);
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.container, faceFragment).commit();
        mChatInputEt.addTextChangedListener(watcher);
        mChatInputEt.setMaxLines(4);
        setMoreMenuHeight();
        setListeners();
        mChatInputEt.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnChangeListener != null) {
                    mOnChangeListener.onSend(0);
                }
                mExpressionIb.setImageResource(R.drawable.icon_emoji);
                dismissAll();
                SoftKeyboardUtils.show(mChatInputEt);
                return true;
            }
        });


    }

    public void setData(String moduleBean, String objectId) {
        this.moduleBean = moduleBean;
        this.objectId = objectId;
    }

    public void setListeners() {
        mChatInputEt.setOnClickListener(v -> {
            dismissAll();
            SoftKeyboardUtils.show(mChatInputEt);
            mExpressionIb.setImageResource(R.drawable.icon_emoji);
        });
        mSendMsgBtn.setOnClickListener(this);
        mSwitchIb.setOnClickListener(this);
        mVoiceBtn.setOnClickListener(this);
        mExpressionIb.setOnClickListener(this);
        mAtSbIb.setOnClickListener(this);
        mAddFileIb.setOnClickListener(this);
        mTakePhotoIb.setOnClickListener(this);
        mPickPictureIb.setOnClickListener(this);
        mPickFileIb.setOnClickListener(this);
        mPickFileLibIb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.jmui_switch_voice_ib) {
            isInputByKeyBoard = !isInputByKeyBoard;
            //当前为语音输入，点击后切换为文字输入，弹出软键盘
            if (isInputByKeyBoard) {
                isKeyBoard();
                dismissAll();
                //showSoftInputAndDismissMenu();
                showKeyboard();
            } else {
                //否则切换到语音输入
                SoftKeyboardUtils.hide(mContext);
                notKeyBoard();
                dismissSoftInput();
                dismissMoreMenu();
            }
            mContainer.setVisibility(GONE);
            mMoreMenuTl.setVisibility(GONE);
            //发送文本消息
        } else if (i == R.id.jmui_send_msg_btn) {
            String msgContent = getText().trim();
            ClickUtil.click(() -> {
                sendText(msgContent);
            });

            //点击添加按钮，弹出更多选项菜单
            //如果在语音输入时点击了添加按钮，则显示菜单并切换到输入框
        } else if (i == R.id.jmui_add_file_btn) {
            mContainer.setVisibility(GONE);

            if (mMoreMenuTl.getVisibility() == VISIBLE) {
                isInputByKeyBoard = true;
                mMoreMenuTl.setVisibility(GONE);
                isKeyBoard();
                SoftKeyboardUtils.show(mChatInputEt);
            } else {
                SoftKeyboardUtils.hide(mChatInputEt);
                isInputByKeyBoard = true;
                mSwitchIb.setBackgroundResource(R.drawable.ic_voice);
                mChatInputEt.setVisibility(View.VISIBLE);
                mVoiceBtn.setVisibility(View.GONE);
                dismissAll();
                mContainer.postDelayed(() -> {
                    mMoreMenuTl.setVisibility(VISIBLE);
                }, 50);
            }
            //拍照
        } else if (i == R.id.jmui_pick_from_camera_btn) {
            takePhoto();
            isInputByKeyBoard = false;
            SoftKeyboardUtils.hide(mChatInputEt);
            //从相册选择
        } else if (i == R.id.jmui_pick_from_local_btn) {
            if (getMoreMenu().getVisibility() == View.VISIBLE) {
                dismissMoreMenu();
            }
            isInputByKeyBoard = false;
            SoftKeyboardUtils.hide(mChatInputEt);
            CommonUtil.getImageFromAlbum(mContext, Constants.PHOTO_ABLUM_NEW_REQUEST_CODE);

            //文件
        } else if (i == R.id.jmui_pick_from_file_btn) {
            selectFile();
            isInputByKeyBoard = false;
            SoftKeyboardUtils.hide(mChatInputEt);
        } else if (i == R.id.jmui_pick_from_filelib_btn) {
            //文件库
            UIRouter.getInstance().openUri(mContext, "DDComp://filelib/select_file", new Bundle(), Constants.REQUEST_CODE10);
            isInputByKeyBoard = false;
            SoftKeyboardUtils.hide(mChatInputEt);
        } else if (i == R.id.jmui_expression_btn) {
            showEmoji();

        } else if (i == R.id.jmui_pick_from_member_btn) {
            //@成员
            CommonUtil.startActivtiyForResult(mContext, SelectMemberActivity.class, Constants.RESULT_CODE_SELECT_MEMBER);
            dismissAll();
        } else if (i == R.id.tv_show_more_comment) {
            //显示更多评论
            mCommentAdapter.setShowMore(true);
            mCommentAdapter.notifyDataSetChanged();
            showMore(true);
        }
    }

    public void showMore(boolean isShow) {
        //加载更多
        /*setVisibility(R.id.tv_show_more_comment, !isShow);*/
    }

    public void showEmoji() {
        if (mContainer.getVisibility() == VISIBLE) {
            mContainer.setVisibility(GONE);
            mExpressionIb.setImageResource(R.drawable.icon_emoji);
            dismissAll();
            SoftKeyboardUtils.show(mChatInputEt);

        } else {
            mChatInputEt.setVisibility(View.VISIBLE);
            mVoiceBtn.setVisibility(View.GONE);
            SoftKeyboardUtils.hide(mChatInputEt);
            mMoreMenuTl.setVisibility(View.GONE);
            mContainer.postDelayed(() -> {
                mContainer.setVisibility(VISIBLE);
                if (mOnChangeListener != null) {
                    mOnChangeListener.onSend(0);
                }
                mExpressionIb.setImageResource(R.drawable.ic_keyboard);
                mSwitchIb.setBackgroundResource(R.drawable.ic_voice);
            }, 50);
        }

    }

    public void dismissSoftInputAndShowMenu() {
        //隐藏软键盘
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 隐藏软键盘
        showMoreMenu();
        InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(getInputView().getWindowToken(), 0); //强制隐藏键盘
        setMoreMenuHeight();
    }

    //隐藏键盘
    public void dismissSoftInput() {
        //隐藏软键盘
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 隐藏软键盘

        InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(getInputView().getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 添加@
     *
     * @param members
     */
    public void appendMention(List<Member> members) {
        if (members == null) {
            return;
        }
        int size = members.size();
        if (size > 0) {
            String[] mentions = new String[size];
            for (int i = 0; i < size; i++) {
                mentions[i] = members.get(i).getName();
            }
            atList.addAll(members);
            appendMention(mentions);
        }
    }

    /**
     * 添加@文字
     *
     * @param mentions
     */
    public void appendMention(String[] mentions) {
        getInputView().appendMention(mentions);
        getInputView().setSelection(getInputView().getText().length());
    }

    /**
     * 选择文件
     */
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mContext.startActivityForResult(intent, Constants.SELECT_FILE_NEW_REQUEST_CODE);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        dismissMoreMenu();
        if (JYFileHelper.existSDCard()) {
            SystemFuncUtils.requestPermissions(mContext, android.Manifest.permission.CAMERA, aBoolean -> {
                if (aBoolean) {
                    imageFromCamera = CommonUtil.getImageFromCamera(mContext, Constants.TAKE_PHOTO_NEW_REQUEST_CODE);
                } else {
                    ToastUtils.showError(mContext, "必须获得必要的权限才能拍照！");
                }
            });
        } else {
            ToastUtils.showError(mContext, "暂无外部存储");
        }
    }

    /**
     * 发表文字评论
     *
     * @param content
     */
    public void sendText(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(mContext, "内容不能为空");
            return;
        }
        if (content.length() > 140) {
            ToastUtils.showToast(mContext, "内容不能超过140个字符");
            return;
        }
        content = content.trim();
        AddCommentRequestBean bean = new AddCommentRequestBean();
        bean.setBean(moduleBean);
        bean.setContent(content);
        bean.setRelation_id(objectId);
        bean.setStyle(folderStyle);
        bean.setType(1);
        //@人员数据
        StringBuilder sb = new StringBuilder();
        for (Member member : atList) {
            sb.append(member.getSign_id() + ",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        bean.setAt_employee(sb.toString());
        if (atList != null) {
            atList.clear();
        }
        sentComment(bean);
    }

    /**
     * 发表评论
     *
     * @param bean
     */
    private void sentComment(AddCommentRequestBean bean) {
        new CommonModel().addComment(mContext, bean, new ProgressSubscriber<BaseBean>(mContext, false) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setKeyboardStateHide();
                clearInput();
                if (mOnChangeListener != null) {
                    mOnChangeListener.onLoad(0);
                    CommentDetailResultBean.DataBean data = new CommentDetailResultBean.DataBean();
                    data.setId(0);
                    data.setPicture(SPHelper.getUserAvatar());
                    data.setEmployee_name(SPHelper.getUserName());

                    data.setBean(moduleBean);
                    data.setContent(bean.getContent());
                    data.setDatetime_time(System.currentTimeMillis() + "");
                    data.setEmployee_id(SPHelper.getEmployeeId());
                    data.setRelation_id(objectId);
                    data.setSign_id(SPHelper.getUserId());
                    data.setInformation((ArrayList<UploadFileBean>) bean.getInformation());
                    mOnChangeListener.onSuccess(data);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 文件上传
     *
     * @param uri
     */
    public void fileUpload(Uri uri) {
        String photoPathFromContentUri = UriUtil.getPhotoPathFromContentUri(mContext, uri);
        File pickPic = new File(photoPathFromContentUri);
        commentFileUpload(null, pickPic);
    }

    /**
     * 相册上传
     *
     * @param uri
     */
    public void picktrueUpload(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //picturePath就是图片在储存卡所在的位置
            String picturePath = cursor.getString(columnIndex);
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
            File pickPic = new File(picturePath);
            commentFileUpload(null, pickPic);
        } else {
            ToastUtils.showError(mContext, "图片读取失败!");
        }
    }

    /**
     * 拍照上传
     */
    public void prepareUploadPic() {
        if (imageFromCamera == null || !imageFromCamera.exists()) {
            return;
        }
        LogUtil.i("照片路径" + imageFromCamera.getAbsolutePath());
        commentFileUpload(null, imageFromCamera);
    }

    /**
     * 附件上传 ,检查文件大小
     *
     * @param voiceDuration
     * @param file
     */
    public void commentFileUpload(Integer voiceDuration, File file) {
        if (FileUtils.checkLimit(mContext, file)) {
            DialogUtils.getInstance().sureOrCancel(mContext, "",
                    "当前为移动网络且文件大小超过10M,继续上传吗?",
                    this
                    , () -> uploadFile(voiceDuration, file));
        } else {
            uploadFile(voiceDuration, file);
        }
    }

    /**
     * 附件上传
     *
     * @param voiceDuration
     * @param file
     */
    private void uploadFile(Integer voiceDuration, File file) {
        new CommonModel().uploadFile(mContext, file.getAbsolutePath(), moduleBean,
                new ProgressSubscriber<UpLoadFileResponseBean>(mContext) {
                    @Override
                    public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                        super.onNext(upLoadFileResponseBean);
                        List<UploadFileBean> data = upLoadFileResponseBean.getData();
                        AddCommentRequestBean bean = new AddCommentRequestBean();
                        if (voiceDuration != null && !CollectionUtils.isEmpty(data)) {
                            for (UploadFileBean uploadFileBean : data) {
                                uploadFileBean.setVoiceTime(voiceDuration);
                            }
                        }
                        bean.setBean(moduleBean);
                        bean.setRelation_id(objectId);
                        bean.setInformation(data);
                        bean.setContent("");
                        sentComment(bean);
                    }
                });
    }

    public void showSoftInputAndDismissMenu() {
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 隐藏软键盘
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invisibleMoreMenu();
                getInputView().requestFocus();
                InputMethodManager mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.showSoftInput(getInputView(),
                        InputMethodManager.SHOW_FORCED);//强制显示键盘
                setMoreMenuHeight();
            }
        }, 100);
    }

    public void setMoreMenuHeight() {
//        int softKeyboardHeight = SharePreferenceManager.getCachedKeyboardHeight();
//        if (softKeyboardHeight > 0) {
//            mMoreMenuTl.setLayoutParams(new LinearLayout
//                    .LayoutParams(LayoutParams.MATCH_PARENT, softKeyboardHeight));
//        }

    }

    public void focusToInput(boolean inputFocus) {
        if (inputFocus) {
            mChatInputEt.requestFocus();
            Log.i("ChatView", "show softInput");
        } else {
            mAddFileIb.requestFocusFromTouch();
        }
    }


    public void dismissAll() {
        mMoreMenuTl.setVisibility(View.GONE);
        mContainer.setVisibility(GONE);
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        mMoreMenuTl.setVisibility(View.GONE);
        mContainer.setVisibility(GONE);
        SoftKeyboardUtils.show(mChatInputEt);
        isKeyBoard();
    }


    //如果是文字输入
    public void isKeyBoard() {
        mSwitchIb.setBackgroundResource(R.drawable.ic_voice);
        mChatInputEt.setVisibility(View.VISIBLE);
        mVoiceBtn.setVisibility(View.GONE);
        if (mChatInputEt.getText().length() > 0) {
            mSendMsgBtn.setVisibility(View.VISIBLE);
            mAddFileIb.setVisibility(View.GONE);
        } else {
            mSendMsgBtn.setVisibility(View.GONE);
            mAddFileIb.setVisibility(View.VISIBLE);
        }
        if (mOnChangeListener != null) {
            mOnChangeListener.onSend(0);
        }
    }

    //语音输入
    public void notKeyBoard() {
        mChatInputEt.setVisibility(View.GONE);
        mSwitchIb.setBackgroundResource(R.drawable.ic_keyboard);
        mExpressionIb.setImageResource(R.drawable.icon_emoji);
        mVoiceBtn.setVisibility(View.VISIBLE);
        mSendMsgBtn.setVisibility(View.GONE);
        mAddFileIb.setVisibility(View.VISIBLE);
        mContainer.setVisibility(GONE);
        mMoreMenuTl.setVisibility(GONE);
    }

    public String getText() {
        return mChatInputEt.getText().toString();
    }

    public void clearInput() {
        mChatInputEt.setText("");
        atList.clear();
    }


    public EmoticonsEditText getInputView() {
        return mChatInputEt;
    }

    public void setKeyboardStateHide() {
        dismissAll();
        isKeyBoard();
        SoftKeyboardUtils.hide(mChatInputEt);
    }

    public TableLayout getMoreMenu() {
        return mMoreMenuTl;
    }

    public void showMoreMenu() {
        mMoreMenuTl.setVisibility(View.VISIBLE);
        mContainer.setVisibility(GONE);
        if (mOnChangeListener != null) {
            mOnChangeListener.onSend(0);
        }
        isKeyBoard();
    }

    public void invisibleMoreMenu() {
        mMoreMenuTl.setVisibility(INVISIBLE);
    }

    public void dismissMoreMenu() {
        mMoreMenuTl.setVisibility(View.GONE);
    }

    public void dismissRecordDialog() {
        mVoiceBtn.dismissDialog();
    }

    public void releaseRecorder() {
        mVoiceBtn.releaseRecorder();
    }


    public void setAtList(List<Member> atList) {
        this.atList = atList;
    }

    @Override
    public void onEmojiDelete() {
        String text = mChatInputEt.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                mChatInputEt.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView();
                return;
            }
            mChatInputEt.getText().delete(index, text.length());
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        mChatInputEt.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        displayTextView();
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        Editable editable = mChatInputEt.getEditableText();
        if (emoji != null) {
            int index = mChatInputEt.getSelectionStart();

            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView();
//        mChatInputEt.setText(editable);
    }

    private void displayTextView() {
        try {
            EmojiUtil.handlerEmojiText(mChatInputEt, mChatInputEt.getText().toString(), mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        private CharSequence temp = "";

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
            if (temp.length() > 0) {
                mAddFileIb.setVisibility(View.GONE);
                mSendMsgBtn.setVisibility(View.VISIBLE);
            } else {
                mAddFileIb.setVisibility(View.VISIBLE);
                mSendMsgBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
            if (!showAtIcon) {
                return;
            }
            if (!TextUtils.isEmpty(s.toString()) && s.toString().endsWith("@")) {
                SoftKeyboardUtils.hide(mChatInputEt);
                CommonUtil.startActivtiyForResult(mContext, SelectMemberActivity.class, Constants.RESULT_CODE_SELECT_MEMBER);
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Constants.TAKE_PHOTO_NEW_REQUEST_CODE) {
            //上传拍照图片
            prepareUploadPic();
        } else if (requestCode == Constants.PHOTO_ABLUM_NEW_REQUEST_CODE) {
            //上传相册图片
            Uri selectedImage = data.getData();
            picktrueUpload(selectedImage);
        } else if (requestCode == Constants.SELECT_FILE_NEW_REQUEST_CODE) {
            //文件
            Uri uri = data.getData();
            fileUpload(uri);
        } else if (requestCode == Constants.REQUEST_CODE10) {
            //文件库
            //文件库
            AttachmentBean bean = null;
            if (data != null) {
                bean = (AttachmentBean) data.getSerializableExtra(Constants.DATA_TAG1);
            }
            if (bean == null) {
                return;
            }
            List<UploadFileBean> list = new ArrayList<>();
            UploadFileBean fileBean = new UploadFileBean();
            fileBean.setFile_name(bean.getFileName());
            fileBean.setFile_size(bean.getFileSize());
            fileBean.setFile_type(bean.getFileType());
            fileBean.setFile_url(bean.getFileUrl());
            list.add(fileBean);
            AddCommentRequestBean bean2 = new AddCommentRequestBean();
            bean2.setBean(moduleBean);
            bean2.setRelation_id(objectId);
            bean2.setInformation(list);
            bean2.setContent("");
            sentComment(bean2);
        } else if (requestCode == Constants.RESULT_CODE_SELECT_MEMBER) {
            //@功能
            ArrayList<Member> members = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            appendMention(members);
        }
    }

    public interface OnKeyBoardChangeListener {
        void onKeyBoardStateChange(int state);
    }

    public void setOnKbdStateListener(OnKeyBoardChangeListener listener) {
        mKeyboardListener = listener;
    }

    public interface OnChangeListener {
        void onSend(int state);

        void onLoad(int state);

        void onSuccess(CommentDetailResultBean.DataBean bean);
    }

    public void setOnChangeListener(OnChangeListener l) {
        mOnChangeListener = l;
    }
}
