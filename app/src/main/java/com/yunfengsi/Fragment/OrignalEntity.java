package com.yunfengsi.Fragment;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class OrignalEntity implements MultiItemEntity ,Serializable{
    public  static  final int  NORMAL=0;//类型
    public  static  final int  MEDIA=1;//类型




    public static final String VideoUrl="videourl";
    public static final String Image="image";
    public static final String Title="title";
    public static final String Time="time";
    public static final String Issuer="issuer";
    public static final String Tag="tag";
    public static final String Ctr="ctr";
    public static final String Likes="likes";
    public static final String News_comment="news_comment";
    public static final String Active="active";
    public static final String Abstract="abstract";
    public static final String ID="id";



    String videoUrl, image, title, time, issuer, tag, ctr, likes, news_comment, active, info, id;
    int itemType;
    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCtr() {
        return ctr;
    }

    public void setCtr(String ctr) {
        this.ctr = ctr;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getNews_comment() {
        return news_comment;
    }

    public void setNews_comment(String news_comment) {
        this.news_comment = news_comment;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}