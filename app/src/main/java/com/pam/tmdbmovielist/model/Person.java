package com.pam.tmdbmovielist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    
    private int id;
    private String name;
    private String profilePath;
    
    public Person(int id, String name, String profilePath) {
        this.id = id;
        this.name = name;
        this.profilePath = profilePath;
    }
    
    protected Person(Parcel in) {
        id = in.readInt();
        name = in.readString();
        profilePath = in.readString();
    }
    
    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }
        
        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(profilePath);
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProfilePath() {
        return profilePath;
    }
    
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
