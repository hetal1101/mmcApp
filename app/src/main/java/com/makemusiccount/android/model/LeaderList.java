package com.makemusiccount.android.model;

/**
 * Created by Welcome on 19-02-2018.
 */

public class LeaderList {
    String name;
    String point;
    String sr;
    String account_type;
    String selected;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public LeaderList() {

    }

    public LeaderList(String name, String point) {

        this.name = name;
        this.point = point;
    }
}
