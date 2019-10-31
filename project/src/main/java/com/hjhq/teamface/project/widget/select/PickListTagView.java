package com.hjhq.teamface.project.widget.select;

import android.view.View;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;

import rx.Observable;


/**
 * 标签
 *
 * @author lx
 * @date 2017/8/23
 */

public class PickListTagView extends PickListView {
    private boolean flag = false;
    public PickListTagView(CustomBean bean) {
        super(bean);
    }
    public PickListTagView(CustomBean bean,boolean flag) {
        super(bean);
        this.flag =flag;
    }

    @Override
    public void setDefaultView() {
        super.setDefaultView();
        if (flag){
            tvTitle.setVisibility(View.GONE);
            bottom_line.setVisibility(View.GONE);
        }

    }

    @Override
    public Object getValue() {
        if (CollectionUtils.isEmpty(checkEntrys)) {
            return "";
        }
        if (ProjectConstants.PROJECT_TASK_LABEL.equals(keyName)) {
            StringBuilder sb = new StringBuilder();
            Observable.from(checkEntrys).subscribe(entry -> {
                sb.append(",").append(entry.getValue());
            });
            sb.deleteCharAt(0);
            return sb.toString();
        } else {
            return checkEntrys;
        }
    }

}
