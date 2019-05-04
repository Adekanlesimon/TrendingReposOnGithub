package com.example.trendingrepos;

public class ExampleItem {
    private String mImageUrl;
    private String mOwnerName;
    private int mStars;
    private String mRepoDescription;
    private String mRepoName;

    public ExampleItem(String imageUrl, String ownerName, int stars,String description,String repoName) {
        mImageUrl = imageUrl;
        mOwnerName = ownerName;
        mStars = stars;
        mRepoDescription=description;
        mRepoName=repoName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public int getStarCount() {
        return mStars;
    }

    public String getRepoName() {
        return mRepoName;
    }

    public String getRepoDescription() {
        return mRepoDescription;
    }
}