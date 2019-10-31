package com.hjhq.teamface.attendance.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class RangeDataAdapter extends BaseQuickAdapter<RangeListBean.DataBean.DataListBean, BaseViewHolder> {
    int type;

    public RangeDataAdapter(int type, List<RangeListBean.DataBean.DataListBean> data) {
        super(R.layout.attendance_range_data_item, data);
        this.type = type;
    }


    @Override
    protected void convert(BaseViewHolder helper, RangeListBean.DataBean.DataListBean item) {
        helper.setText(R.id.tv_name, item.getEmployee_name());
        helper.setText(R.id.tv_pos, item.getPost_name());
        ImageView icon = helper.getView(R.id.next);
        ImageView avatar = helper.getView(R.id.avatar);
        TextView info = helper.getView(R.id.tv_num);
        ImageLoader.loadCircleImage(helper.getConvertView().getContext(), item.getPicture(), avatar, item.getEmployee_name());
        if (type == 1 || type == 2) {
            helper.setVisible(R.id.next, true);
            switch (helper.getAdapterPosition()) {
                case 0:
                    icon.setImageResource(R.drawable.attendance_gold);
                    break;
                case 1:
                    icon.setImageResource(R.drawable.attendance_silver);

                    break;
                case 2:
                    icon.setImageResource(R.drawable.attendance_bronze);

                    break;
                default:
                    helper.setVisible(R.id.next, false);//zzh:增加除了 0,1,2 其他Item隐藏皇冠图标
                    break;
            }

        } else {
            helper.setVisible(R.id.next, false);
        }
        switch (type) {
            case 1:
                info.setTextColor(ColorUtils.hexToColor("#666666"));
                TextUtil.setText(info, DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "HH:mm"));
                break;
            case 2:
                info.setTextColor(ColorUtils.hexToColor("#666666"));
                int minutes = (int) TextUtil.parseDouble(item.getMonth_work_time());
                int hour = minutes / 60;
                int min = minutes % 60;
                String infoStr = "";
                if (hour > 0) {
                    infoStr = hour + "小时" + min + "分钟";
                } else {
                    infoStr = min + "分钟";
                }
                TextUtil.setText(info, infoStr);
                break;
            case 3:
                info.setTextColor(ColorUtils.hexToColor("#F9A244"));
                TextUtil.setText(info, item.getLate_count_number() + "次    共" + item.getLate_time_number() + "分钟");
                break;
        }


    }


}