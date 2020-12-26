package com.pam.tmdbmovielist.model.credits;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class ExternalIds implements Parcelable {
    
    private String facebookId;
    private String instagramId;
    private String twitterId;
    
    public ExternalIds(@Nullable String facebookId, @Nullable String instagramId, @Nullable String twitterId) {
        this.facebookId = setField(facebookId);
        this.instagramId = setField(instagramId);
        this.twitterId = setField(twitterId);
    }
    
    protected ExternalIds(Parcel in) {
        facebookId = in.readString();
        instagramId = in.readString();
        twitterId = in.readString();
    }
    
    public static final Creator<ExternalIds> CREATOR = new Creator<ExternalIds>() {
        @Override
        public ExternalIds createFromParcel(Parcel in) {
            return new ExternalIds(in);
        }
        
        @Override
        public ExternalIds[] newArray(int size) {
            return new ExternalIds[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facebookId);
        dest.writeString(instagramId);
        dest.writeString(twitterId);
    }
    
    private String setField(String source) {
        if (source == null || source.length() == 0 || source.equalsIgnoreCase("null")) {
            return null;
        } else {
            return source;
        }
    }
    
    public String getFacebookId() {
        return facebookId;
    }
    
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
    
    public String getInstagramId() {
        return instagramId;
    }
    
    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }
    
    public String getTwitterId() {
        return twitterId;
    }
    
    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }
}
