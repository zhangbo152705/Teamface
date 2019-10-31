package com.hjhq.teamface.common.ui.location;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/9/4
 */

public class LocationDelegateV2 extends AppDelegate {
    public MapView mapView;
    public ImageView ivLocation;
    public static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    public static final int FILL_COLOR = Color.argb(90, 3, 145, 255);
    public TextView myAddress;
    public EditText etRemark;
    public ImageView ivPhoto;
    public TextView tvDistance;
    public Button btnDaka;

    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.create(inflater, container, savedInstanceState);
        mapView = get(R.id.mapview_location);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_location_v2;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setLeftIcon(-1);
        setLeftText(R.color.gray_69, "取消");
        setTitle("当前位置");
        tvDistance = get(R.id.tv_distance);
        etRemark = get(R.id.et);
        ivPhoto = get(R.id.add_photo);
        myAddress = get(R.id.tv_my_address);
        btnDaka = get(R.id.action_btn2);

       /* float mapHeight = (ScreenUtils.getScreenHeight(mContext) - DeviceUtils.dpToPixel(mContext, 50)) / 2;
        View rlMap = get(R.id.rl_map);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlMap.getLayoutParams();
        layoutParams.height = (int) mapHeight;
        rlMap.setLayoutParams(layoutParams);*/
        ivLocation = get(R.id.iv_location);
    }

    /**
     * 设置选中
     *
     * @param selectPosition
     */
    public void setSelectPosition(int selectPosition) {

    }


    /**
     * 设置新值
     *
     * @param nearList
     */
    public void setNewData(List<PoiItem> nearList) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void close() {
        super.close();
        mapView.onDestroy();
    }

    /**
     * 设置地图UI样式A
     */
    public void setMapViewUI() {
        //隐藏LOGO
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置自定义定位蓝点
     */
    public void setLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.icon_first_group));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(1);
        myLocationStyle.showMyLocation(false);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        getMap().setMyLocationStyle(myLocationStyle);
    }

    public AMap getMap() {
        return mapView.getMap();
    }

    public void showAction() {
        get(R.id.rl_action).setVisibility(View.VISIBLE);

    }

    public void hideAction() {
        get(R.id.rl_action).setVisibility(View.GONE);

    }

    public void setTime(String s) {
        btnDaka.setText(s);
    }
}