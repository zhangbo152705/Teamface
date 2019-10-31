package com.hjhq.teamface.common.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.FileHelper;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.JYFileHelper;
import com.hjhq.teamface.basis.view.image.SubsamplingScaleImageView;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.ui.ImageItemAdapter;
import com.hjhq.teamface.download.utils.FileTransmitUtils;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.File;
import java.util.ArrayList;

@RouteNode(path = "/view_image", desc = "全屏查看图片")
public class FullscreenViewImageActivity extends ActivityPresenter<ViewImageDelegate, CommonModel> implements ViewPager.OnPageChangeListener {

    /**
     * 当前显示的图片
     */
    public static final String SELECT_INDEX = "select_index";

    /**
     * 图片列表
     */
    public static final String PICTURE_LIST = "picture_list";
    /**
     * 是否可删除
     */
    public static final String IS_CAN_DELETE = "is_can_delete";

    /**
     * 广播action
     **/
    public static final String DELETE_BROADCAST_ACTION = "broadcast_action";

    /**
     * 删除index
     **/
    public static final String DELETE_POSITION_KEY = "delete_position_key";

    private String timestamp = "pic_";

    /**
     * 图片显示的容器
     */

    ViewPager pager; // 这个类可以让用户左右切换当前的view

    TextView titleTV;

    ImageButton delete; // 删除
    //旋转
    RelativeLayout mRlRotate;
    //标题
    RelativeLayout mRlTitle;
    //下载
    ImageView mIvDownload;

    /**
     * 是否可以删除
     **/
    boolean isCanDelete = false;

    /**
     * 图片总数量
     **/
    private int totalCount;

    /**
     * 图片信息集合
     **/
    ArrayList<Photo> photos;

    ImageItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.login_window_blank_bg));*/
    }

    @Override
    public void init() {
        initView();
        initData();
        setListener();
    }


    protected void initView() {
        pager = viewDelegate.get(R.id.imageviewpager);
        titleTV = viewDelegate.get(R.id.title_text);
        delete = viewDelegate.get(R.id.delete);
        mRlRotate = viewDelegate.get(R.id.rl_rotate);
        mIvDownload = viewDelegate.get(R.id.download);
        mRlTitle = viewDelegate.get(R.id.title);
        delete.setVisibility(View.GONE);
        titleTV.setVisibility(View.GONE);
        mRlRotate.setVisibility(View.VISIBLE);
        mRlTitle.setVisibility(View.GONE);

    }


    protected void setListener() {
        pager.addOnPageChangeListener(this);
        mIvDownload.setOnClickListener(v -> {
            final Photo photo = photos.get(pager.getCurrentItem());
            String url = photo.getUrl();
            if (!TextUtils.isEmpty(url)) {
                if (url.startsWith("/storage")) {
                    File file = new File(url);
                    if (file.exists()) {
                        File newFile = new File(JYFileHelper.creatDir(mContext, Constants.PATH_DOWNLOAD), timestamp + file.getName());
                        boolean flag = FileHelper.copyFile(file.getAbsolutePath(), newFile.getAbsolutePath());
                        if (flag) {
                            FileHelper.updateGallery(mContext, file);
                            ToastUtils.showToast(mContext, "文件保存在/sdcard/Teamface/download/");
                        } else {
                            downloadFile(photo);
                        }
                    } else {
                        file = new File(JYFileHelper.creatDir(mContext, Constants.PATH_DOWNLOAD), timestamp + photo.getName());
                        if (file.exists()) {
                            ToastUtils.showToast(mContext, "文件保存在/sdcard/Teamface/download/");
                            FileHelper.updateGallery(mContext, file);
                        } else {
                            downloadFile(photo);
                        }
                    }
                } else {
                    downloadFile(photo);
                }

            } else {
                ToastUtils.showError(mContext, "下载出错");
            }
        });
    }

    private void downloadFile(Photo photo) {
        FileTransmitUtils.downloadFileFromUrl(timestamp, photo.getUrl(), photo.getName());
        ToastUtils.showToast(mContext, "文件已保存在/sdcard/Teamface/download/");
    }


    protected void initData() {
        // 朋友圈的媒体信息
        photos = (ArrayList<Photo>) getIntent().getSerializableExtra(
                PICTURE_LIST);
        isCanDelete = getIntent().getBooleanExtra(IS_CAN_DELETE, false);

        if (photos == null) {
            photos = new ArrayList<>();
        }
        totalCount = photos.size();
        // 选中的图片
        int index = getIntent().getIntExtra(SELECT_INDEX, 0);

        delete.setVisibility(isCanDelete ? View.VISIBLE : View.GONE);

        adapter = new ImageItemAdapter(this, photos);

        pager.setAdapter(adapter);

        // 显示当前位置的View
        pager.setCurrentItem(index);

        int currentItem = 0;
        if (totalCount != 0) {
            currentItem = pager.getCurrentItem() + 1;
        }
        titleTV.setText(currentItem + "/" + totalCount);
    }


    /**
     * 旋转图片
     *
     * @param imageView
     * @param degrees
     */
    private void rotateImageView(final SubsamplingScaleImageView imageView,
                                 float degrees) {
        final Bitmap bitmap = imageView.getBitmap();
        if (bitmap != null) {
            int bitmapW = bitmap.getWidth();
            int bitmapH = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, bitmapW / 2, bitmapH / 2);
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapW, bitmapH,
                    matrix, true);
            imageView.setImageBitmap(bmp);

            imageView.postInvalidate();

            new Thread(() -> {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (imageView.getTag() != null) {
                    bitmap.recycle();
                } else {
                    imageView.setTag(true);
                }

//					bitmap.recycle();
            }).start();

            // bitmap.recycle();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        titleTV.setText((position + 1) + "/" + totalCount);

        if (isCanDelete) {
            Photo ph = photos.get(position);
            delete.setVisibility(ph.isCanDelete() ? View.VISIBLE : View.GONE);
        } else {
            delete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
