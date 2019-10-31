package com.hjhq.teamface.common.ui.location;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.FileUtils;
import com.hjhq.teamface.basis.util.LocationUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.MapToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 地图定位
 *
 * @author lx
 * @date 2017/7/10
 */

public class LocationPresenterV2 extends ActivityPresenter<LocationDelegateV2, CommonModel> implements AMap.OnMapClickListener, LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener {
    public int mSearchRadius = 5000;
    List<PoiItem> nearList = new ArrayList<>();
    private String address;
    private String city;
    public static String LOCATION_RESULT_CODE = "location_result_code";
    private AMap aMap;
    private UiSettings mUiSettings;
    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private Map<String, Serializable> paramMap;
    private File imageFromCamera;
    private ScheduledExecutorService scheduledExecutorService;
    private MyRunnable progressRunnable;
    private long locationTime = 0L;
    /**
     * 是否首次定位
     */
    private boolean isFrist = true;
    private Marker locationMarker;
    private Marker destinationMarker;
    /**
     * 是否关键字搜索
     */
    private boolean isKeySearch = false;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private int currentPage = 0;

    private LatLng mLatLng;
    private double mLat;
    private double mLng;
    private double mRadius;
    private String photoUrl;


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            address = getIntent().getStringExtra(AppConst.LOCATION);
            mLat = TextUtil.parseDouble(getIntent().getStringExtra(Constants.DATA_TAG1));
            mLng = TextUtil.parseDouble(getIntent().getStringExtra(Constants.DATA_TAG2));
            mRadius = TextUtil.parseDouble(getIntent().getStringExtra(Constants.DATA_TAG3));
            paramMap = (Map<String, Serializable>) getIntent().getSerializableExtra(Constants.DATA_TAG4);

            if (mLng > 0 && mLat > 0) {
                mLatLng = new LatLng(mLat, mLng);
            }
        }
    }

    @Override
    public void init() {
        if (setMap() && mLatLng != null) {
            CircleOptions circleOptions = new CircleOptions();
            LatLng latLng = new LatLng(mLat, mLng);
            circleOptions.radius(mRadius)
                    .fillColor(Color.RED)
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(3.0f)
                    .center(latLng);
            aMap.addCircle(circleOptions);
            destinationMarker = aMap.addMarker(
                    new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_company_location)))
                            .position(mLatLng)
                            .title("公司位置"));
            // destinationMarker.showInfoWindow();
            onceLelocation();
        }
        if (paramMap != null) {
            viewDelegate.showAction();
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        progressRunnable = new MyRunnable();
        scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置地图
     */
    private boolean setMap() {
        if (aMap == null) {
            aMap = viewDelegate.getMap();
            if (aMap != null) {
                mUiSettings = aMap.getUiSettings();
                aMap.setOnMapClickListener(this);
                aMap.setLocationSource(this);
                mUiSettings.setZoomControlsEnabled(false);
                mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
                aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
                viewDelegate.setMapViewUI();
                viewDelegate.setLocationStyle();
            }
        } else {
            ToastUtils.showError(mContext, "初始地图错误");
        }
        return aMap != null;

    }


    /**
     * 单次定位
     */
    private void onceLelocation() {
        if (aMap == null || mLocationOption == null || mlocationClient == null) {
            ToastUtils.showError(mContext, "地图定位异常！");
            return;
        }
        aMap.clear();

        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.ivLocation.setOnClickListener(v -> onceLelocation());
        viewDelegate.ivPhoto.setOnClickListener(v -> takePhoto());
        viewDelegate.btnDaka.setOnClickListener(v -> realPunch(paramMap));
    }

    private void realPunch(Map<String, Serializable> map) {
        map.put("remark", viewDelegate.etRemark.getText().toString().trim());
        map.put("photo", photoUrl);
        model.punchClock(mContext, map, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(mContext, e.getMessage());
            }
        });
    }

    private void takePhoto() {
        SystemFuncUtils.requestPermissions(mContext, android.Manifest.permission.CAMERA, aBoolean -> {
            if (aBoolean) {
                imageFromCamera = CommonUtil.getImageFromCamera(mContext, Constants.TAKE_PHOTO_NEW_REQUEST_CODE);
            } else {
                ToastUtils.showError(mContext, "必须获得必要的权限才能拍照！");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {
       /* addMarker(latLng);
        aroundSearch(latLng.latitude, latLng.longitude);*/
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //默认高精度定位模式
            mLocationOption = new AMapLocationClientOption();
            //获取最近3s内精度最高的一次定位结果：
            mLocationOption.setOnceLocationLatest(true);
            mlocationClient.setLocationOption(mLocationOption);
        }
    }

    @Override
    public void deactivate() {
        // Log.e("deactivate", "结束定位");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation.getErrorCode() == 0) {
            Log.e("success", aMapLocation.toString());
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            city = aMapLocation.getCity();
            double latitude = aMapLocation.getLatitude();
            double longitude = aMapLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            //移动位置
            CameraPosition cameraPosition = aMap.getCameraPosition();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(latLng.latitude, latLng.longitude), isFrist ? 16 : cameraPosition.zoom, cameraPosition.tilt, cameraPosition.bearing));
            aMap.animateCamera(cameraUpdate);
            locationMarker = aMap.addMarker(
                    new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_location)))
                            .position(mLatLng)
                            .title("我的位置"));
            locationMarker.showInfoWindow();
           /* aroundSearch(latitude, longitude);*/
            isFrist = false;

            CircleOptions circleOptions = new CircleOptions();
            LatLng latLng2 = new LatLng(mLat, mLng);
            circleOptions.radius(mRadius)
                    .fillColor(viewDelegate.FILL_COLOR)
                    .strokeColor(viewDelegate.STROKE_COLOR)
                    .strokeWidth(3.0f)
                    .center(latLng2);
            aMap.addCircle(circleOptions);
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(3.0f);
            polylineOptions.add(latLng, latLng2);
            polylineOptions.color(Color.RED);
            aMap.addPolyline(polylineOptions);

            destinationMarker = aMap.addMarker(
                    new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_company_location)))
                            .position(latLng2)
                            .title("公司位置"));
            if (paramMap != null) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        double d1, d2, d3, d4;
                        d1 = mLng;
                        d2 = mLat;
                        d4 = aMapLocation.getLatitude();
                        d3 = aMapLocation.getLongitude();
                        int d = (int) LocationUtils.getDistance(d1, d2, d3, d4);
                        viewDelegate.tvDistance.setText("(距最近的考勤范围" + d + "米)");
                        address = aMapLocation.getAddress();
                        viewDelegate.myAddress.setText(address);
                    } else {

                    }
                }

            }
        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
        }
    }

    /**
     * 添加标签
     *
     * @param latLng 坐标
     */
    private void addMarker(LatLng latLng) {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(latLng.latitude, latLng.longitude), cameraPosition.zoom, cameraPosition.tilt, cameraPosition.bearing));
        aMap.animateCamera(cameraUpdate);

        if (locationMarker != null) {
            locationMarker.remove();
        }

        locationMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_location))).position(latLng));
        locationMarker.showInfoWindow();

    }


    /**
     * 附近poi
     *
     * @param latitude
     * @param longitude
     */
    private void aroundSearch(double latitude, double longitude) {
        isKeySearch = false;
        query = new PoiSearch.Query("", "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,
                longitude), mSearchRadius, true));//设置周边搜索的中心点以及半径
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == Constants.TAKE_PHOTO_NEW_REQUEST_CODE) {
            prepareUploadPic();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void prepareUploadPic() {
        if (imageFromCamera == null || !imageFromCamera.exists()) {
            return;
        }
        LogUtil.i("照片路径" + imageFromCamera.getAbsolutePath());
        commentFileUpload(imageFromCamera);
    }

    private void commentFileUpload(File file) {
        if (FileUtils.checkLimit(mContext, file)) {
            DialogUtils.getInstance().sureOrCancel(mContext, "",
                    "当前为移动网络且文件大小超过10M,继续上传吗?",
                    viewDelegate.getRootView()
                    , () -> uploadFile(file));
        } else {
            uploadFile(file);
        }
    }

    private void uploadFile(File file) {
        model.uploadFile(mContext, file.getAbsolutePath(), AttendanceConstants.BEAN_NAME,
                new ProgressSubscriber<UpLoadFileResponseBean>(mContext) {
                    @Override
                    public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                        super.onNext(upLoadFileResponseBean);
                        List<UploadFileBean> data = upLoadFileResponseBean.getData();
                        if (!CollectionUtils.isEmpty(data)) {
                            photoUrl = data.get(0).getFile_url();
                            ImageLoader.loadImage(mContext, imageFromCamera, viewDelegate.ivPhoto);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    /**
     * 开始进行poi搜索
     */
    protected void keySearch(String keyWord) {
        if (TextUtil.isEmpty(keyWord)) {
            ToastUtils.showError(this, "搜索内容为空");
            return;
        }
        isKeySearch = true;
        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    ArrayList<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        nearList.clear();
                        nearList.addAll(poiItems);
                        viewDelegate.setNewData(nearList);

                        if (isKeySearch) {
                            //关键字搜索需要移动中心位置
                            PoiItem poiItem = nearList.get(0);
                            LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                            addMarker(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                        }
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        MapToastUtil.show(this,
                                R.string.no_result);
                    } else {
                        MapToastUtil.show(this,
                                R.string.no_result);
                    }
                    return;
                }
            } else {
                MapToastUtil
                        .show(this, R.string.no_result);
            }
        } else {
            MapToastUtil.showerror(this.getApplicationContext(), rcode);
        }
        nearList.clear();
        viewDelegate.setNewData(nearList);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if (System.currentTimeMillis() - locationTime > 5000) {
                locationTime = System.currentTimeMillis();
                onceLelocation();
            }
            viewDelegate.setTime(DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm:ss") + "  " + "外勤打卡");
        }
    }
}
