package com.makemusiccount.android.model;

public class AvtarList {


    private String avatarID;

    private String avatarName;

    private String image;

    public AvtarList(String avatarID, String avatarName, String image) {
        this.avatarID = avatarID;
        this.avatarName = avatarName;
        this.image = image;
    }

    public AvtarList() {
    }

    public String getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(String avatarID) {
        this.avatarID = avatarID;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
