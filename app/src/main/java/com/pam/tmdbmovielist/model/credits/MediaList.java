package com.pam.tmdbmovielist.model.credits;

import android.util.ArrayMap;

import com.pam.tmdbmovielist.model.Media;

public class MediaList {
    
    private final ArrayMap<Integer, Media> mediaList;
    
    public MediaList() {
        mediaList = new ArrayMap<>();
    }
    
    public void add(Media media) {
        mediaList.put(media.getId(), media);
    }
    
    public boolean contains(int id) {
        return mediaList.containsKey(id);
    }
    
    public Media getMedia(int id) {
        return mediaList.get(id);
    }
}
