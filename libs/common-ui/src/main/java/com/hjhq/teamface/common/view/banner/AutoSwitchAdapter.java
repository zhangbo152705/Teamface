package com.hjhq.teamface.common.view.banner;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class AutoSwitchAdapter extends AutoLoopSwitchBaseAdapter {

    private Context mContext;

    private List<LoopModel> mDatas;

    public AutoSwitchAdapter() {
        super();
    }

    public AutoSwitchAdapter(Context mContext, List<LoopModel> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getDataCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public View getView(int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT));
        LoopModel model = (LoopModel) getItem(position);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (TextUtils.isEmpty(model.getUrl())) {
            ImageLoader.loadRoundImage(mContext, model.getResId(), imageView);
            //Glide.with(mContext).load(model.getResId()).into(imageView);
        } else {
            //Glide.with(mContext).load(model.getUrl()).into(imageView);
            ImageLoader.loadRoundImage(mContext, model.getUrl(), imageView, R.drawable.banner_default_pic);
        }
        return imageView;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < getDataCount()) {

            return mDatas.get(position);
        }
        return null;
    }


    @Override
    public View getEmptyView() {
        return null;
    }

    @Override
    public void updateView(View view, int position) {

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    public void setNewData(List<LoopModel> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

}
