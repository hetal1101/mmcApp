package com.makemusiccount.android.model;

/**
 * Created by Welcome on 14-02-2018.
 */

public class TutorialSelectList {
    String ID;
    String name;
    String url;
    String image;
    String status;
    String line1;

    public TutorialSelectList(String ID, String name, String url, String image, String status, String line1, String line2, String line3) {
        this.ID = ID;
        this.name = name;
        this.url = url;
        this.image = image;
        this.status = status;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    String line2;
    String line3;

    public TutorialSelectList() {
    }

    public String getID() {

        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
