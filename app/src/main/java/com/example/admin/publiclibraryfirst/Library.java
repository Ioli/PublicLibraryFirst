package com.example.admin.publiclibraryfirst;

public class Library {
    private String mThumbnail;
    private String mTitle;
    private StringBuilder mAuthor;
    private String mUrl;

    public Library(String thumbnail, String title, StringBuilder author, String url) {
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    /**
     * The image for the book
     */

    public String getThumbnail() {
        return mThumbnail;
    }

    /**
     * The title ob the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * The author of the book
     */
    public StringBuilder getAuthor() {
        return mAuthor;
    }

    /**
     * Website URL of the book
     */
    public String getUrl() {
        return mUrl;
    }
}

