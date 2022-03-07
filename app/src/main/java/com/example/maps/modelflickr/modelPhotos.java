package com.example.maps.modelflickr;

import java.util.ArrayList;

public class modelPhotos {
    public int page;
    public int pages;
    public int perpages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpages() {
        return perpages;
    }

    public void setPerpages(int perpages) {
        this.perpages = perpages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<modelPhoto> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<modelPhoto> photo) {
        this.photo = photo;
    }

    public int total;
    ArrayList<modelPhoto> photo;

}
