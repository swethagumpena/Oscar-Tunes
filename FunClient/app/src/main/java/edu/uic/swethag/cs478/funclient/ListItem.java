package edu.uic.swethag.cs478.funclient;

import android.graphics.Bitmap;

public class ListItem {
    private Bitmap mImage;
    private String mTitle;
    private String mArtist;

    public ListItem(Bitmap imageResource, String text1, String text2) {
        mImage = imageResource;
        mTitle = text1;
        mArtist = text2;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }
}
