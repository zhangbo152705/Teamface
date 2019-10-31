package com.hjhq.teamface.project.ui.temp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.hjhq.teamface.basis.constants.Constants
import com.hjhq.teamface.basis.util.device.DeviceUtils
import com.hjhq.teamface.basis.util.file.SPHelper
import com.hjhq.teamface.basis.view.load.LoadingDialog
import com.hjhq.teamface.basis.zygote.AppDelegate
import com.hjhq.teamface.common.view.ScaleTransitionPagerTitleView
import com.hjhq.teamface.project.R
import com.hjhq.teamface.project.adapter.ProjectTempFlowAdapter
import com.hjhq.teamface.project.bean.NodeBean
import com.hjhq.teamface.project.bean.TempNoteDataList
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * 项目模板 的流程节点
 * Created by Administrator on 2018/7/20.
 */
class ProjectTempFlowDelegate : AppDelegate() {
   // private lateinit var mMagicIndicator: MagicIndicator
    //private lateinit var mViewPager: ViewPager
    //lateinit var mAdapter: ProjectTempFlowAdapter


    lateinit var mWebView: com.tencent.smtt.sdk.WebView
    var loadingDialog: LoadingDialog? = null
    var rootNode: NodeBean? = null
    var webLoadFinish = false
    var getDataFinish = false



    override fun getRootLayoutId(): Int = R.layout.project_activity_temp_flow

    override fun isToolBar(): Boolean = true


    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.project_temp)
        setRightMenuTexts(R.color.app_blue, mContext.getString(R.string.confirm))


       // mMagicIndicator = get(R.id.magic_indicator)
       // mViewPager = get(R.id.view_pager)

        mWebView = get(R.id.wv_task_content)
        initWebViw()
        bindListener()
    }

    /**
     * 初始化导航
     */
    /*fun initNavigator(fragmentManagers: FragmentManager, dataList: List<TempNoteDataList>) {
        mMagicIndicator.visibility = View.VISIBLE
        mAdapter = ProjectTempFlowAdapter(fragmentManagers, dataList)
        mViewPager.adapter = mAdapter
        mViewPager.offscreenPageLimit = 2

        val commonNavigator = CommonNavigator(mContext)
        commonNavigator.adapter = MyCommonNavigatorAdapter(dataList)
        commonNavigator.isFollowTouch = true
        commonNavigator.setPadding(15, 0, 15, 0)
        mMagicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mMagicIndicator, mViewPager)
    }*/

    @SuppressLint("NewApi")
            /**
     * 导航适配器
     */
   /* private inner class MyCommonNavigatorAdapter(val nodeList: List<TempNoteDataList>?) : CommonNavigatorAdapter() {

        override fun getCount(): Int = nodeList?.size ?: 0

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            val scaleTransitionPagerTitleView = ScaleTransitionPagerTitleView(context)
            scaleTransitionPagerTitleView.normalColor = ContextCompat.getColor(mContext, R.color.gray_99)
            scaleTransitionPagerTitleView.gravity = Gravity.CENTER
            scaleTransitionPagerTitleView.selectedColor = ContextCompat.getColor(mContext, R.color.app_blue)
            scaleTransitionPagerTitleView.textSize = 14f
            scaleTransitionPagerTitleView.text = nodeList!![index].name
            scaleTransitionPagerTitleView.setOnClickListener { mViewPager.currentItem = index }
            val padding = DeviceUtils.dpToPixel(context, 15f).toInt()
            scaleTransitionPagerTitleView.setPadding(padding, 0, padding, 0)
            return scaleTransitionPagerTitleView
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.lineWidth = DeviceUtils.dpToPixel(mContext, 56f)
            indicator.lineHeight = DeviceUtils.dpToPixel(mContext, 2f)
            indicator.yOffset = DeviceUtils.dpToPixel(mContext, 0f)
            indicator.setColors(ContextCompat.getColor(mContext, R.color.app_blue))
            return indicator
        }
    }
*/

    fun initWebViw() {

        if (Constants.IS_DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        val settings = mWebView.settings
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.javaScriptEnabled = true
        settings.setAllowFileAccessFromFileURLs(true)
        settings.setAllowUniversalAccessFromFileURLs(true)
        settings.builtInZoomControls = true
        settings.setSupportZoom(true)
        settings.displayZoomControls = false
        mWebView.isClickable = true
        mWebView.isFocusable = true
        mWebView.setWebChromeClient(WebChromeClient())
        mWebView.addJavascriptInterface(this, "android")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        loadingDialog = LoadingDialog(mContext)
        loadingDialog!!.setLoadingText("Loading")
        loadingDialog!!.setInterceptBack(false)
        loadingDialog!!.show()
    }

    fun bindListener() {
        mWebView.setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webLoadFinish = true
                //  getWebText(null);
                if (getDataFinish) {
                    showWebData()
                }
              if (loadingDialog != null) {
                    loadingDialog!!.close()
              }
            }

            override fun onPageStarted(webView: WebView, s: String, bitmap: Bitmap?) {
                super.onPageStarted(webView, s, bitmap)

            }

            override fun onReceivedError(webView: WebView, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError)
            }


        })
        mWebView.loadUrl(Constants.PRJECT_TASK_EDIT_URL)


    }


    /**
     * 传数据给WebView
     */
    private fun showWebData() {
        if (rootNode == null) {
            return
        }
        val token = SPHelper.getToken()
        val domain = SPHelper.getDomain()
        val obj = JSONObject.toJSON(rootNode) as JSONObject
        val jo = JSONObject()
        try {
            jo.put("html", obj)
            jo.put("token", token)
            jo.put("domain", domain)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.e("showWebData", "objstr:" + jo)
        mWebView.evaluateJavascript("javascript:getValHtml($jo)") { value -> Log.e("js 返回的结果1", "====" + value) }
    }

    /**
     * 刷新节点导航
     *
     * @param dataList
     */
    fun refreshNode(dataList: NodeBean) {
        this.getDataFinish = true
        this.rootNode = dataList
        if (webLoadFinish) {
            showWebData()
        }

    }
}