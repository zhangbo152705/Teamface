package com.hjhq.teamface.custom.ui.template;

import android.content.Context;

import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.FilterFieldResultBean;
import com.hjhq.teamface.basis.bean.InsertSubformBean;
import com.hjhq.teamface.basis.bean.LinkDataBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
import com.hjhq.teamface.basis.bean.WebLinkDataListBean;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.MainRetrofit;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.factory.FastJsonConverterFactory;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.util.file.SPUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.IModel;
import com.hjhq.teamface.custom.CustomApiService;
import com.hjhq.teamface.custom.bean.FindAutoByBean;
import com.hjhq.teamface.custom.bean.RelationDataRequestBean;
import com.hjhq.teamface.custom.bean.RepeatCheckResponseBean;
import com.hjhq.teamface.custom.bean.TabListDataReqBean;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * 数据列表Model
 *
 * @author lx
 * @date 2017/9/5
 */

public class DataTempModel implements IModel<CustomApiService> {
    @Override
    public CustomApiService getApi() {
        return new ApiManager<CustomApiService>().getAPI(CustomApiService.class);
    }

    /**
     * 获取业务信息列表权限
     *  zzh->ad:增加权限接口
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getModuleFunction(ActivityPresenter mActivity, String bean, Subscriber<ModuleFunctionBean> s) {
        getApi().getModuleFunctionAuth(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
    /**
     * 获取业务信息列表
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getDataTemp(ActivityPresenter mActivity, DataListRequestBean bean, Subscriber<DataTempResultBean> s) {
        getApi().getDataTemp(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取关联模块和头部标题
     *
     * @param mActivity
     * @param reqData   固定参数
     * @param ruleId
     * @param pageSize
     * @param pageNum
     * @param s
     */
    public void getTabListData(ActivityPresenter mActivity, int dataAuth, TabListDataReqBean reqData,
                               Long ruleId, Long moduleId, Long tabId, int pageSize, int pageNum, Subscriber<DataTempResultBean> s) {
        getApi().getTabListData(dataAuth, reqData.getId(),
                reqData.getType(), ruleId, moduleId, tabId, pageSize, pageNum).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 根据bean获取自动匹配列表
     *
     * @param mActivity
     * @param s
     */
    public void findAutoByBean(ActivityPresenter mActivity, String sorceBean, String targetBean, Subscriber<FindAutoByBean> s) {
        getApi().findAutoByBean(sorceBean, targetBean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取子菜单
     *
     * @param mActivity
     * @param moduleId
     * @param s
     */
    public void getMenuList(ActivityPresenter mActivity, String moduleId, Subscriber<TempMenuResultBean> s) {
        getApi().getMenuList(moduleId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    /**
     * 获取邮件相关模块的子菜单
     *
     * @param mActivity
     * @param moduleId
     * @param s
     */
    public void getModuleSubmenus(ActivityPresenter mActivity, String moduleId, Subscriber<TempMenuResultBean> s) {
        getApi().getModuleSubmenus(moduleId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取查重字段
     *
     * @param mActivity
     * @param bean
     * @param field
     * @param value
     * @param s
     */
    public void getRecheckingFields(ActivityPresenter mActivity, String bean, String field, String label, String value, Subscriber<RepeatCheckResponseBean> s) {
        getApi().getRecheckingFields(bean, field, label, value).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    /**
     * 获取搜索记录
     *
     * @return
     */
    public List<String> getSearchHistory(Context context, String bean) {
        return SPUtils.getDataList(context, bean);
    }

    /**
     * 保存搜索记录(10条)
     *
     * @param list
     */
    public void saveSearchHistory(Context context, List<String> list, String bean) {
        if (null != list && list.size() > 10) {
            SPUtils.setDataList(context, bean, list.subList(0, 10));
        } else {
            //可以保存空的集合
            SPUtils.setDataList(context, bean, list);
        }
    }

    /**
     * 获取筛选字段
     *
     * @param mActivity
     * @param moduleBean
     * @param s
     */
    public void getFilterFields(ActivityPresenter mActivity, String moduleBean, Subscriber<FilterFieldResultBean> s) {
        getApi().getFilterFields(moduleBean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 关联组件列表
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void findRelationDataList(ActivityPresenter mActivity, RelationDataRequestBean bean,
                                     Subscriber<ReferDataTempResultBean> s) {
        CustomApiService service = new MainRetrofit.Builder<CustomApiService>()
                .addConverterFactory(new FastJsonConverterFactory())
                .build(CustomApiService.class);
        service.findRelationDataList(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取WEB表单列表
     *
     * @param mActivity
     * @param moduleBean
     * @param s
     */
    public void getWebformList(ActivityPresenter mActivity, String moduleBean,
                               Subscriber<LinkDataBean> s) {
        getApi().getWebformList(moduleBean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取WEB表单链接列表
     *
     * @param mActivity
     * @param moduleBean
     * @param s
     */
    public void getWebformListForAdd(ActivityPresenter mActivity, String moduleBean,
                                     int source, String seasPoolId, String relevanceModule,
                                     String relevanceField, String relevanceValue,
                                     Subscriber<WebLinkDataListBean> s) {
        getApi().getWebformListForAdd(moduleBean, source, seasPoolId, relevanceModule, relevanceField, relevanceValue).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void findSubRelationDataList(ActivityPresenter mActivity, Map<String, Serializable> map,
                                        Subscriber<InsertSubformBean> s) {
        getApi().findSubRelationDataList(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
}
