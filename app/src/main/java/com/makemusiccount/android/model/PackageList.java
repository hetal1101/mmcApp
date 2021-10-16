package com.makemusiccount.android.model;

import java.io.Serializable;

public class PackageList implements Serializable {
    private String packID;
    private String name;
    private String plan_price_info;
    private String package_desc;

    boolean isSelected = false;

    public PackageList() {
    }

    public PackageList(String packID, String name, String plan_price_info, String package_desc, String price) {
        this.packID = packID;
        this.name = name;
        this.plan_price_info = plan_price_info;
        this.package_desc = package_desc;

        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String price;

    public String getPackID() {
        return packID;
    }

    public void setPackID(String packID) {
        this.packID = packID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan_price_info() {
        return plan_price_info;
    }

    public void setPlan_price_info(String plan_price_info) {
        this.plan_price_info = plan_price_info;
    }

    public String getPackage_desc() {
        return package_desc;
    }

    public void setPackage_desc(String package_desc) {
        this.package_desc = package_desc;
    }

}
