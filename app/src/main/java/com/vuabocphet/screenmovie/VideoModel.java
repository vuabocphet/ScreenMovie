package com.vuabocphet.screenmovie;

public class VideoModel {

    public String urlimg, name, time, size, date;

    public VideoModel(String urlimg, String name, String time, String size, String date) {
        this.urlimg = urlimg;
        this.name = name;
        this.time = time;
        this.size = size;
        this.date = date;
    }

    public String getUrlimg() {
        return urlimg;
    }

    public void setUrlimg(String urlimg) {
        this.urlimg = urlimg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
