package com.hjhq.teamface.common.view.banner;

/**
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class LoopModel {

    private String title;

    private int resId;
    private String url;

    public LoopModel(String url) {
        this.url = url;
    }

    public LoopModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public LoopModel(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
