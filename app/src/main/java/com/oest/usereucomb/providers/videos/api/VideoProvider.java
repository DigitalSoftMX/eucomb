package com.oest.usereucomb.providers.videos.api;

public interface VideoProvider {

    void requestVideos(String pageToken);

    void requestVideos(String pageToken, String query);

    boolean isYoutubeLive();

    boolean supportsSearch();
}
