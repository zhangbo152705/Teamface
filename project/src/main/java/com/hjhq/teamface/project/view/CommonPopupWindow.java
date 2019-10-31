package com.hjhq.teamface.project.view;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.ProjectCommonPopupWindowAdapter;

import java.util.List;

/**
 * Created by Administrator on 2019/5/13.
 */

public class CommonPopupWindow{
    private AppCompatActivity mContext;
    private OnClickListener listener;
    private List<String> datalist;
    private RecyclerView recycler_pop;
    private View view;
    private ProjectCommonPopupWindowAdapter adapter;
    private PopupWindow popupWindow;
    private int type;

    public CommonPopupWindow(AppCompatActivity context) {
        this.mContext = context;
    }

    public CommonPopupWindow(AppCompatActivity context,List<String> datalist,View view,int type,OnClickListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.datalist = datalist;
        this.view = view;
        this.type = type;
        initPopupWindow();
    }

    public void setDataList(List<String> datalist){
        this.datalist = datalist;
        if (adapter != null){
            adapter.setData(datalist);
        }
    }

    public void initPopupWindow(){

        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.project_commom_popupwindow, null);

       popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        recycler_pop = contentView.findViewById(R.id.recycler_pop);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //完成layoutManager设置
        recycler_pop.setLayoutManager(layoutManager);
        recycler_pop.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        //创建IconAdapter的实例同时将iconList传入其构造函数
        adapter = new ProjectCommonPopupWindowAdapter(datalist,new ProjectCommonPopupWindowAdapter.ItemClickListener(){

            @Override
            public void onItemClick(View view, int postion) {
                listener.onClick(view,postion,type);
            }
        });
        //完成adapter设置
        recycler_pop.setAdapter(adapter);


        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
       // popupWindow.showAsDropDown(view);

    }
    public void show(View view,int type){
        if (type == 1 && popupWindow != null){
                int width = view.getWidth()/2;
                popupWindow.showAsDropDown(view,-width,10);
        }else if(type == 2 && popupWindow != null){
            int width = view.getWidth()/2;
            popupWindow.showAsDropDown(view,50,10);
        }
    }
    public void dimiss(){
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }

    public interface OnClickListener{
        void onClick(View view,int position,int type);
    }

}
