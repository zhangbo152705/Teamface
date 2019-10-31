package kankan.wheel.widget.regionselect.model;

/**
 * @author Administrator
 * @date 2017/11/8
 */

public class WheelModel {

    private String name;

    private String id;

    private boolean check;
    private boolean show;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Model [name=" + name + ", id=" + id + "]";
    }

}
