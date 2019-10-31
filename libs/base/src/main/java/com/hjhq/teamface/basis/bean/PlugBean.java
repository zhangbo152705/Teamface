package com.hjhq.teamface.basis.bean;

/**
 * Describe：插件
 */

public class PlugBean extends BaseBean {


    private String id;
    private String plugin_name;
    private String plugin_note;
    private String plugin_status;
    private String plugin_type;

    public String getId() {
        return id;
    }

    public String getPlugin_name() {
        return plugin_name;
    }

    public String getPlugin_note() {
        return plugin_note;
    }

    public String getPlugin_status() {
        return plugin_status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlugin_name(String plugin_name) {
        this.plugin_name = plugin_name;
    }

    public void setPlugin_note(String plugin_note) {
        this.plugin_note = plugin_note;
    }

    public void setPlugin_status(String plugin_status) {
        this.plugin_status = plugin_status;
    }

    public String getPlugin_type() {
        return plugin_type;
    }

    public void setPlugin_type(String plugin_type) {
        this.plugin_type = plugin_type;
    }
}
