package com.pam.tmdbmovielist.model;

import android.os.Parcel;

public class Cast extends Person {
    
    private String characterName;
    
    public Cast(int id, String name, String profilePath, String characterName) {
        super(id, name, profilePath);
        this.characterName = characterName;
    }
    
    protected Cast(Parcel in) {
        super(in);
        characterName = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(characterName);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }
        
        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
    
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}