package com.hjhq.teamface.custom.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.custom.R;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/29.
 * Describe：
 */
@RouteNode(path = "/choose_email", desc = "选择邮件")
public class ChooseEmailActivity extends ActivityPresenter<ChooseEmailDelegate, DataDetailModel> {

    EmailBoxFragment mFragment1;
    EmailBoxFragment mFragment2;
    private int currentBoxTag = EmailConstant.RECEVER_BOX;

    @Override
    public void init() {
        viewDelegate.setTitle(getResources().getString(R.string.email));
        mFragment1 = EmailBoxFragment.newInstance(EmailConstant.INBOX);
        mFragment2 = EmailBoxFragment.newInstance(EmailConstant.SENDED);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl1, mFragment1).commit();
        fragmentManager.beginTransaction().replace(R.id.fl2, mFragment2).commit();

    }


    @Override
    protected void bindEvenListener() {

        viewDelegate.get(R.id.search_bar).setOnClickListener(v -> {
            searchEmail();
        });
        viewDelegate.get(R.id.rl1).setOnClickListener(v -> {
            viewDelegate.setSelect(EmailConstant.RECEVER_BOX);
            viewDelegate.get(R.id.fl1).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.fl2).setVisibility(View.GONE);
            currentBoxTag = EmailConstant.RECEVER_BOX;
        });
        viewDelegate.get(R.id.rl2).setOnClickListener(v -> {
            viewDelegate.setSelect(EmailConstant.SENDED_BOX);
            currentBoxTag = EmailConstant.SENDED_BOX;
            viewDelegate.get(R.id.fl1).setVisibility(View.GONE);
            viewDelegate.get(R.id.fl2).setVisibility(View.VISIBLE);
        });
        super.bindEvenListener();

    }

    private void searchEmail() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, currentBoxTag);
        bundle.putInt(Constants.DATA_TAG2, 2);
        UIRouter.getInstance().openUri(mContext, "DDComp://email/search_email", bundle, Constants.REQUEST_CODE1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<EmailBean> checkedEmail = getCheckedEmail();
        if (checkedEmail.size() == 0) {
            ToastUtils.showError(mContext, "未选择数据");
            return true;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.DATA_TAG1, checkedEmail);
        setResult(RESULT_OK, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE1 && data != null) {
            ArrayList<EmailBean> checkedEmail = getCheckedEmail();
            EmailBean bean = (EmailBean) data.getSerializableExtra(Constants.DATA_TAG1);
            checkedEmail.add(bean);
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_TAG1, checkedEmail);
            setResult(RESULT_OK, intent);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private ArrayList<EmailBean> getCheckedEmail() {
        ArrayList<EmailBean> list = new ArrayList<>();
        list.addAll(mFragment1.getCheckedData());
        list.addAll(mFragment2.getCheckedData());
        return list;
    }

    public void setNum(String boxTag, int totalRows) {
        if (EmailConstant.INBOX.equals(boxTag)) {
            viewDelegate.setText(EmailConstant.RECEVER_BOX, "收件箱(" + totalRows + ")");
        } else if (EmailConstant.SENDED.equals(boxTag)) {
            viewDelegate.setText(EmailConstant.SENDED_BOX, "发件箱(" + totalRows + ")");
        }
    }
}
