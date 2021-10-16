package com.makemusiccount.android.model;

public class ThemeList {


    private String shopID;

    public ThemeList(String shopID, String usershopID, String name, String coin, String image, String userPurchase, String sortId, Integer dataMaster) {
        this.shopID = shopID;
        this.usershopID = usershopID;
        this.name = name;
        this.coin = coin;
        this.image = image;
        this.userPurchase = userPurchase;
        this.sortId = sortId;
        this.dataMaster = dataMaster;
    }

    public ThemeList() {
    }

    public String getUsershopID() {
        return usershopID;
    }

    public void setUsershopID(String usershopID) {
        this.usershopID = usershopID;
    }

    private String usershopID;

    private String name;

    private String coin;

    private String image;

    private String userPurchase;

    private String sortId;

    private Integer dataMaster;

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserPurchase() {
        return userPurchase;
    }

    public void setUserPurchase(String userPurchase) {
        this.userPurchase = userPurchase;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public Integer getDataMaster() {
        return dataMaster;
    }

    public void setDataMaster(Integer dataMaster) {
        this.dataMaster = dataMaster;
    }

}
