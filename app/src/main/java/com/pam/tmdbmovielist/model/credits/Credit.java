package com.pam.tmdbmovielist.model.credits;

import android.os.Parcel;
import android.os.Parcelable;

public class Credit implements Parcelable {
    
    public static final String MEDIA_TYPE_MOVIE = "media_type_movie";
    public static final String MEDIA_TYPE_TV = "media_type_tv";
    
    private String creditId;
    private int id;
    private String mediaType;
    private int episodeCount;
    
    public Credit(String creditId, int id, String mediaType) {
        this.creditId = creditId;
        this.id = id;
        this.mediaType = mediaType;
        if (mediaType.equals(MEDIA_TYPE_TV)) {
            episodeCount = -1;
        }
    }
    
    public Credit(String creditId, int id, String mediaType, int episodeCount) {
        this.creditId = creditId;
        this.id = id;
        this.mediaType = mediaType;
        this.episodeCount = episodeCount;
    }
    
    protected Credit(Parcel in) {
        creditId = in.readString();
        id = in.readInt();
        mediaType = in.readString();
        episodeCount = in.readInt();
    }
    
    public static final Creator<Credit> CREATOR = new Creator<Credit>() {
        @Override
        public Credit createFromParcel(Parcel in) {
            return new Credit(in);
        }
        
        @Override
        public Credit[] newArray(int size) {
            return new Credit[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creditId);
        dest.writeInt(id);
        dest.writeString(mediaType);
        dest.writeInt(episodeCount);
    }
    
    public String getCreditId() {
        return creditId;
    }
    
    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public int getEpisodeCount() {
        return episodeCount;
    }
    
    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }
}
