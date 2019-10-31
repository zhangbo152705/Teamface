package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Row implements Serializable {
    private ArrayList<RowBean> row1;
    private ArrayList<RowBean> row2;
    private ArrayList<RowBean> row3;

    public ArrayList<RowBean> getRow1() {
        return row1;
    }

    public void setRow1(ArrayList<RowBean> row1) {
        this.row1 = row1;
    }

    public ArrayList<RowBean> getRow2() {
        return row2;
    }

    public void setRow2(ArrayList<RowBean> row2) {
        this.row2 = row2;
    }

    public ArrayList<RowBean> getRow3() {
        return row3;
    }

    public void setRow3(ArrayList<RowBean> row3) {
        this.row3 = row3;
    }

}