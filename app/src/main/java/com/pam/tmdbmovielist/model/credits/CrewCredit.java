package com.pam.tmdbmovielist.model.credits;

import android.os.Parcel;

public class CrewCredit extends Credit {
    
    private String department;
    private String job;
    
    public CrewCredit(String creditId, int id, String mediaType, int episodeCount, String department, String job) {
        super(creditId, id, mediaType, episodeCount);
        this.department = department;
        this.job = job;
    }
    
    protected CrewCredit(Parcel in) {
        super(in);
        department = in.readString();
        job = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(department);
        dest.writeString(job);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<CrewCredit> CREATOR = new Creator<CrewCredit>() {
        @Override
        public CrewCredit createFromParcel(Parcel in) {
            return new CrewCredit(in);
        }
        
        @Override
        public CrewCredit[] newArray(int size) {
            return new CrewCredit[size];
        }
    };
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getJob() {
        return job;
    }
    
    public void setJob(String job) {
        this.job = job;
    }
}
