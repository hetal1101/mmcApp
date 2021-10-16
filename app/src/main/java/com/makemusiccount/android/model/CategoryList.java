package com.makemusiccount.android.model;

/**
 * Created by Welcome on 25-01-2018.
 */

public class CategoryList {


    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSub_cats() {
        return sub_cats;
    }

    public void setSub_cats(String sub_cats) {
        this.sub_cats = sub_cats;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getBar_color() {
        return bar_color;
    }

    public void setBar_color(String bar_color) {
        this.bar_color = bar_color;
    }

    private String catID, name, image, sub_cats,type,short_desc,percentage,bar_color;



    public CategoryList() {

    }

    public CategoryList(String catID, String name, String image, String sub_cats, String type, String short_desc, String percentage, String bar_color) {
        this.catID = catID;
        this.name = name;
        this.image = image;
        this.sub_cats = sub_cats;
        this.type = type;
        this.short_desc = short_desc;
        this.percentage = percentage;
        this.bar_color = bar_color;
    }
}
