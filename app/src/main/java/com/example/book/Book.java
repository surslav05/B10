package com.example.book;

import java.util.Date;
import java.util.UUID;

public class Book {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mReaded;
    public Book(){
        this(UUID.randomUUID());
    }
    public Book(UUID id) {
        mId = id;
        mDate = new Date();
    }
    public Date getDate(){
        return mDate;
    }
    public void setDate(Date date){
        mDate = date;
    }
    public boolean isReaded(){
        return mReaded;
    }
    public void setReaded(boolean readed){
        mReaded = readed;
    }
    public UUID getId(){
        return mId;
    }
    public String getTitle(){
        return mTitle;
    }
    public void setTitle(String title){
        mTitle = title;
    }
    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}