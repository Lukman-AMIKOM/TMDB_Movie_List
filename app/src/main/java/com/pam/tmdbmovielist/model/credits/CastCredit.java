package com.pam.tmdbmovielist.model.credits;

import android.os.Parcel;

public class CastCredit extends Credit {
    
    private String character;
    
    public CastCredit(String creditId, int id, String mediaType, int episodeCount, String character) {
        super(creditId, id, mediaType, episodeCount);
        this.character = character;
    }
    
    protected CastCredit(Parcel in) {
        super(in);
        character = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(character);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<CastCredit> CREATOR = new Creator<CastCredit>() {
        @Override
        public CastCredit createFromParcel(Parcel in) {
            return new CastCredit(in);
        }
        
        @Override
        public CastCredit[] newArray(int size) {
            return new CastCredit[size];
        }
    };
    
    public String getCharacter() {
        return character;
    }
    
    public void setCharacter(String character) {
        this.character = character;
    }
}
