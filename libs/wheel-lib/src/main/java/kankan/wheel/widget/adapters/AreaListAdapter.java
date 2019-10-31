package kankan.wheel.widget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mrwujay.cascade.R;

import java.util.List;

import kankan.wheel.widget.regionselect.model.WheelModel;

/**
 * Created by Administrator on 2018/9/27.
 * Describe：省市区列表适配器
 */

public class AreaListAdapter extends BaseQuickAdapter<WheelModel, BaseViewHolder> {
    private boolean isSearchState = false;

    public AreaListAdapter(List<WheelModel> data) {
        super(R.layout.area_item_select, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WheelModel item) {
        helper.addOnClickListener(R.id.iv_select);
        LinearLayout llRoot = helper.getView(R.id.ll_root);
        /*if (isSearchState) {
            if (item.isShow()) {
                helper.getView(R.id.content).setVisibility(View.VISIBLE);
                helper.getView(R.id.blank).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.content).setVisibility(View.GONE);
                helper.getView(R.id.blank).setVisibility(View.VISIBLE);
            }
        } else {
            helper.getView(R.id.blank).setVisibility(View.GONE);
        }*/
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) llRoot.getLayoutParams();
        if (isSearchState) {
            if (item.isShow()) {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                llRoot.setVisibility(View.VISIBLE);
            } else {
                layoutParams.height = 0;
                layoutParams.width = 0;
                llRoot.setVisibility(View.GONE);
            }
        } else {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            llRoot.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tv_title, item.getName());
        ImageView ivSelect = helper.getView(R.id.iv_select);
        if (item.isCheck()) {
            ivSelect.setImageResource(R.drawable.icon_selected);
        } else {
            ivSelect.setImageResource(R.drawable.icon_unselect);
        }
    }


    public void setSearchState(boolean searchState) {
        isSearchState = searchState;
    }
}
