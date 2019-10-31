package com.hjhq.teamface.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hjhq.lib_zxing.ZxingUtils;
import com.hjhq.lib_zxing.activity.CaptureFragment;
import com.hjhq.lib_zxing.activity.CodeUtils;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.QueryBarcodeDataBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonApiService;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.uuzuche.lib_zxing.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Initial the camera
 * <p>
 * 默认的二维码扫描Activity
 *
 * @author Administrator
 */
public class CaptureActivity extends ActivityPresenter<CaptureDelegate, CommonModel> {
    public static final int FOR_RESULT = 1;
    public static final int FOR_SCAN = 2;
    public static final int FOR_BARCODE = 3;
    public static final String FLAG = "CaptureActivity";
    //private Pattern p = Pattern.compile("^((http|ftp|https)://).{1,1000}$", Pattern.CASE_INSENSITIVE);
    private Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_#\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
    private FrameLayout rootView;
    private CaptureFragment captureFragment;
    private int scanType = FOR_RESULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SystemFuncUtils.requestPermissions(mContext, android.Manifest.permission.CAMERA, aBoolean -> {
            if (!aBoolean) {
                ToastUtils.showError(mContext, "必须获得必要的权限才能正常使用！");
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            scanType = bundle.getInt(Constants.DATA_TAG1);
        }
        captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
    }

    @Override
    public void init() {
        initView();
    }


    protected void initView() {
        rootView = (FrameLayout) findViewById(R.id.fl_zxing_container);
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#000000"));
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        ImageView ivFlash = viewDelegate.get(R.id.tv_flash);
        ImageView ivGallery = viewDelegate.get(R.id.tv_pic);
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.getImageFromAlbumByMultiple(mContext, 1);
            }
        });

        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (captureFragment != null) {
                    ivFlash.setSelected(captureFragment.switchFlash());
                }
            }
        });
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            handleResult(result);
            /*switch (scanType) {
                case FOR_BARCODE:
                    handleBarcode(getFromat(mBitmap), result);
                    break;
                case FOR_RESULT:
                    handleResult(Activity.RESULT_OK, getFromat(mBitmap), result);
                    break;
                case FOR_SCAN:

                    break;
            }*/

        }

        @Override
        public void onAnalyzeFailed() {
            captureFragment.restartPreview();
            /*Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(Activity.RESULT_OK, resultIntent);
            CaptureActivity.this.finish();*/
        }
    };

    private BarcodeFormat getFromat(Bitmap mBitmap) {

        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(mBitmap.getWidth(), mBitmap.getHeight(), pixels);
        BinaryBitmap bb = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        try {
            final Result result2 = reader.decodeWithState(bb);
            return result2.getBarcodeFormat();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理扫码结果
     *
     * @param result
     */
    private void handleResult(String result) {
        if (TextUtils.isEmpty(result)) {
            showPreviewWithCopy(result);
            return;
        }

        if (scanType == FOR_BARCODE) {
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_TAG1, result);
            setResult(RESULT_OK, intent);
            finish();
        } else if (scanType == FOR_RESULT) {
            try {
                JSONObject jo = new JSONObject(result);
                handleQRcode(result);
            } catch (JSONException e) {
                e.printStackTrace();

                Matcher matcher = p.matcher(result);
                if (matcher.find()) {
                    //打开网页
                    openWebpage(matcher.group(0));
                } else {
                    //查询数据
                    findData(result);
                }
            }

        }
    }

    /**
     * 查询后台数据
     *
     * @param result
     */
    private void findData(final String result) {
        model.findDetailByBarcode(mContext, result,
                new ProgressSubscriber<QueryBarcodeDataBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showPreviewWithCopy(result);
                        return;
                    }

                    @Override
                    public void onNext(QueryBarcodeDataBean baseBean) {
                        super.onNext(baseBean);
                        if (baseBean != null && baseBean.getData() != null) {
                            if ("0".equals(baseBean.getData().getReadAuth())) {
                                //showPreviewWithCopy("无权查看", result);
                                showPreview2("提示", "无权查看");
                            } else if ("1".equals(baseBean.getData().getReadAuth())) {
                                viewDataDetail(result, baseBean);
                            } else if ("2".equals(baseBean.getData().getReadAuth())) {
                                showPreviewWithCopy("数据不存在", result);
                            }
                        } else {
                            showPreviewWithCopy(result);
                        }
                    }
                });
    }

    /**
     * 处理条形码结果
     *
     * @param format
     * @param result
     *//*
    private void handleBarcode(BarcodeFormat format, String result) {

        Log.e("条形码格式", format.name());
        switch (format.name()) {
            //商品条形码
            case "UPC_A":
            case "UPC_E":
            case "UPC_EAN_EXTENSION":
            case "EAN_8":
            case "EAN_13":
                //普通条形码
            case "CODABAR":
            case "CODE_39":
            case "CODE_93":
            case "CODE_128":
            case "ITF":
            case "PDF_417":
            case "RSS_14":
            case "RSS_EXPANDED":
                if (scanType == FOR_BARCODE) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, result);
                    intent.putExtra(Constants.DATA_TAG2, format.name());
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                } else if (scanType == FOR_RESULT) {
                    model.findDetailByBarcode(mContext, result,
                            new ProgressSubscriber<QueryBarcodeDataBean>(mContext) {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    showPreviewWithCopy(result);
                                    return;
                                }

                                @Override
                                public void onNext(QueryBarcodeDataBean baseBean) {
                                    super.onNext(baseBean);
                                    if (baseBean != null && baseBean.getData() != null) {
                                        viewDataDetail(result, baseBean);
                                        return;
                                    }

                                }
                            });
                    return;
                }
                break;
            case "QR_CODE":
            case "AZTEC":
            case "DATA_MATRIX":
            case "MAXICODE":
                //二维码
                if (scanType == FOR_RESULT) {
                    handleQRcode(result);
                } else if (scanType == FOR_BARCODE) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, result);
                    intent.putExtra(Constants.DATA_TAG2, format.name());
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                break;

            default:

                break;


        }
    }*/

    /**
     * 处理二维码结果
     *
     * @param result
     */
    private void handleQRcode(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject == null) {
                // ToastUtils.showError(mContext, "json为空");
                return;
            }
            //扫描二维码登录
            if ("ScanLogin".equals(jsonObject.optString("useType"))) {
                final String code = jsonObject.optString("uniqueId");
                scanLogin(code);
            } else if ("interiorLink".equals(jsonObject.optString("identifier"))) {
                if (SPHelper.getCompanyId().equals(jsonObject.optString("companyId"))) {
                    String id = jsonObject.optString("id");
                    String bean = jsonObject.optString("bean");
                    queryAuth(result, id, bean);

                } else {
                    DialogUtils.getInstance().sureOrCancel(mContext, "提示",
                            "该数据不属于当前公司,请切换到相应公司后查看。",
                            viewDelegate.getRootView(), "确定", "取消", new DialogUtils.OnClickSureOrCancelListener() {
                                @Override
                                public void clickSure() {
                                    finish();
                                }

                                @Override
                                public void clickCancel() {
                                    finish();
                                }
                            });
                }


            } else {
                Matcher matcher = p.matcher(result);
                if (matcher.find()) {
                    //打开网页
                    openWebpage(matcher.group(0));
                } else {
                    showPreviewWithCopy(result);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Matcher matcher = p.matcher(result);
            if (matcher.find()) {
                //打开网页
                openWebpage(matcher.group(0));
            } else {
                showPreviewWithCopy(result);
            }

        }
    }

    private void queryAuth(String result, String id, String bean) {
        model.getAuth(mContext, bean, id, "", "", new ProgressSubscriber<ViewDataAuthResBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                super.onNext(viewDataAuthResBean);
                if (viewDataAuthResBean != null && viewDataAuthResBean.getData() != null) {
                    if ("0".equals(viewDataAuthResBean.getData().getReadAuth())) {
                        //showPreviewWithCopy("无权查看", result);
                        showPreview2("提示", "无权查看");
                    } else if ("1".equals(viewDataAuthResBean.getData().getReadAuth())) {
                        viewDataDetail(id, bean);
                    } else if ("2".equals(viewDataAuthResBean.getData().getReadAuth())) {
                        //showPreviewWithCopy("数据不存在", result);
                        openWebpage(result);
                    }
                } else {
                    showPreviewWithCopy(result);
                }
            }
        });
    }

    /**
     * 查看条形码关联数据详情
     */
    private void viewDataDetail(String result, QueryBarcodeDataBean baseBean) {
        String id = baseBean.getData().getId();
        String bean = baseBean.getData().getBean();
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(bean)) {
            showPreviewWithCopy(result);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, bean);
        bundle.putString(Constants.DATA_ID, id);
        UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", bundle, Constants.REQUEST_CODE1);
        finish();

    }

    private void viewDataDetail(String id, String bean) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, bean);
        bundle.putString(Constants.DATA_ID, id);
        UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", bundle, Constants.REQUEST_CODE1);
        finish();

    }

    /**
     * 文本内容
     *
     * @param str
     */
    private void showPreviewWithCopy(String... str) {
        String title = "内容";
        String subTitle = "";
        if (str != null && str.length == 1) {
            subTitle = str[0];
        }
        if (str != null && str.length == 2) {
            title = str[0];
            subTitle = str[1];
        }

        if (true) {
            String finalSubTitle = subTitle;
            DialogUtils.getInstance().sureOrCancel(CaptureActivity.this, title + " ",
                    subTitle + " ", rootView.getRootView(), "复制", "取消",
                    new DialogUtils.OnClickSureOrCancelListener() {
                        @Override
                        public void clickSure() {
                            SystemFuncUtils.copyTextToClipboard(CaptureActivity.this, finalSubTitle);
                            ToastUtils.showSuccess(CaptureActivity.this, "复制成功");
                            Intent intent = new Intent();
                            intent.putExtra(Constants.DATA_TAG1, finalSubTitle);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void clickCancel() {
                            finish();
                        }
                    });
        }
    }

    /**
     * @param str
     */
    private void showPreview2(String... str) {
        String title = "内容";
        String subTitle = "";
        if (str != null && str.length == 1) {
            subTitle = str[0];
        }
        if (str != null && str.length == 2) {
            title = str[0];
            subTitle = str[1];
        }

        if (true) {
            String finalSubTitle = subTitle;
            DialogUtils.getInstance().sureOrCancel(CaptureActivity.this, title + " ", subTitle + " ", rootView.getRootView(), "确定", "取消", new DialogUtils.OnClickSureOrCancelListener() {
                @Override
                public void clickSure() {
                    /*SystemFuncUtils.copyTextToClipboard(CaptureActivity.this, finalSubTitle);
                    ToastUtils.showSuccess(CaptureActivity.this, "复制成功");*/
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, finalSubTitle);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void clickCancel() {
                    finish();
                }
            });
        }
    }

    /**
     * 扫码登录
     *
     * @param code
     */
    private void scanLogin(final String code) {
        DialogUtils.getInstance().sureOrCancel(this, "确认登录",
                "确定授权网页端登录码?",
                rootView, "确定", "取消",
                new DialogUtils.OnClickSureOrCancelListener() {
                    @Override
                    public void clickSure() {
                        Map<String, String> map = new HashMap<>();
                        map.put("userName", SPHelper.getUserAccount());
                        map.put("id", code);
                        final Observable<BaseBean> observable = new ApiManager<CommonApiService>().getAPI(CommonApiService.class)
                                .valiLogin(map).map(new HttpResultFunc<>())
                                .compose(CaptureActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
                        observable.subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ProgressSubscriber<BaseBean>(mContext) {
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                    }

                                    @Override
                                    public void onNext(BaseBean baseBean) {
                                        super.onNext(baseBean);
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void clickCancel() {
                        finish();
                        //继续扫描
//                        captureFragment.restartPreview();
                    }
                });
    }

    /**
     * 打开网页
     *
     * @param resultString
     */
    private void openWebpage(final String resultString) {
        if (resultString.contains("fileLink")) {
            openSharedFile(resultString);
        } else {
            viewWebpage(resultString);
        }
    }

    /**
     * 调用浏览器打开网页
     *
     * @param resultString
     */
    private void viewWebpage(String resultString) {
        DialogUtils.getInstance().sureOrCancel(CaptureActivity.this, "打开网页", resultString, rootView.getRootView(), "确定", "取消", new DialogUtils.OnClickSureOrCancelListener() {
            @Override
            public void clickSure() {
                Uri uri = Uri.parse(resultString);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }

            @Override
            public void clickCancel() {
                finish();
            }
        });

    }

    /**
     * 打开共享文件页面
     *
     * @param resultString
     */
    private void openSharedFile(String resultString) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, resultString);
        UIRouter.getInstance().openUri(mContext, "DDComp://filelib/shared_file", bundle);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos != null && photos.size() > 0) {
                    File file = new File(photos.get(0));
                    Result result = ZxingUtils.scanningImage(mContext, file.getAbsolutePath());
                    if (result != null) {
                        String resultText = result.getText();
                        Log.e("resultText", "--" + resultText);
                        handleResult(resultText);
                    } else {
                        ToastUtils.showError(mContext, "未识别");
                    }

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}