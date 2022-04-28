package com.example.maps.modelflickr;

public class modelPhoto {
    public String id;
    public String owner;
    public String secret;
    public String server;

    public modelPhoto() {
    }

    public String getOwner() {
        return owner;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }

    public int getIspublic() {
        return ispublic;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public int farm;
    public String title;
    public int ispublic;
    public int isFriend;
    public int isFamily;

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public int getIsFamily() {
        return isFamily;
    }
}
