package com.hjhq.teamface.view.quickaction;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Action item, displayed as menu with icon and text.
 *
 * @author Lorensius. W. L. T <lorenz@londatiga.net>
 *         <p>
 *         Contributors:
 *         - Kevin Peck <kevinwpeck@gmail.com>
 */
public class ActionItem implements Serializable {
    private String icon;
    private Bitmap thumb;
    private String title;
    private String moduleId;
    private String moduleBean;
    private String iconColor;
    private String iconType;
    private int actionId = -1;
    private boolean selected;
    private boolean sticky;

    /**
     * Constructor
     *
     * @param actionId Action id for case statements
     * @param title    Title
     * @param icon     Icon to use
     */
    public ActionItem(int actionId, String title, String icon) {
        this.title = title;
        this.icon = icon;
        this.actionId = actionId;
    }

    public ActionItem( String moduleBean, String title) {
        this.moduleBean = moduleBean;
        this.title = title;
    }

    public ActionItem(String moduleId, String moduleBean, String title, String iconUrl, String iconColor, String iconType) {
        this.moduleId = moduleId;
        this.moduleBean = moduleBean;
        this.title = title;
        this.icon = iconUrl;
        this.iconColor = iconColor;
        this.iconType = iconType;
    }


    /**
     * Constructor
     */
    public ActionItem() {
        this(-1, null, null);
    }

    /**
     * Constructor
     *
     * @param actionId Action id of the item
     * @param title    Text to show for the item
     */
    public ActionItem(int actionId, String title) {
        this(actionId, title, null);
    }

    /**
     * Constructor
     *
     * @param icon {@link Drawable} action icon
     */
    public ActionItem(String icon) {
        this(-1, null, icon);
    }


    /**
     * Set action title
     *
     * @param title action title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get action title
     *
     * @return action title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set action icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Get action icon
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * Set action id
     *
     * @param actionId Action id for this action
     */
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    /**
     * @return Our action id
     */
    public int getActionId() {
        return actionId;
    }

    /**
     * Set sticky status of button
     *
     * @param sticky true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    /**
     * @return true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * Set selected flag;
     *
     * @param selected Flag to indicate the item is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Check if item is selected
     *
     * @return true or false
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Set thumb
     *
     * @param thumb Thumb image
     */
    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    /**
     * Get thumb image
     *
     * @return Thumb image
     */
    public Bitmap getThumb() {
        return this.thumb;
    }

    public String getModuleBean() {
        return moduleBean;
    }

    public void setModuleBean(String moduleBean) {
        this.moduleBean = moduleBean;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }


    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }
}