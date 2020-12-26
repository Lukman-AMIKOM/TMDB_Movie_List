package com.pam.tmdbmovielist.model;

import android.os.Parcel;

public class Crew extends Person {
    
    private String job;
    
    public Crew(int id, String name, String profilePath, String job) {
        super(id, name, profilePath);
        this.job = job;
    }
    
    protected Crew(Parcel in) {
        super(in);
        job = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(job);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Crew> CREATOR = new Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel in) {
            return new Crew(in);
        }
        
        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };
    
    public String getJob() {
        return job;
    }
    
    public void setJob(String job) {
        this.job = job;
    }
}
