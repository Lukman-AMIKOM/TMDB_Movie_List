package com.pam.tmdbmovielist.model;

import android.os.Parcel;

public class Movie extends Media {
    
    private String certification;
    private int runtime;
    
    public Movie() {
        super(-1, null, null, null, null,
                null, -1, -1, false);
    }
    
    public Movie(int id, String title) {
        super(id, title, null, null, null,
                null, -1, -1, false);
    }
    
    public Movie(int id, String title, String posterPath, String releaseDate,
                 String[] genres, int voteCount, float rating, boolean isFavorite,
                 String certification, String overview, int runtime) {
        super(id, title, posterPath, releaseDate, overview, genres, voteCount, rating, isFavorite);
        this.certification = certification;
        this.runtime = runtime;
    }
    
    public Movie(Movie movie) {
        super(movie.getId(), movie.getTitle(), movie.getPosterPath(), movie.getReleaseDate(),
                movie.getOverview(), movie.getGenres(), movie.getVoteCount(), movie.getRating(),
                movie.isFavorite());
        this.certification = movie.getCertification();
        this.runtime = movie.getRuntime();
    }
    
    protected Movie(Parcel in) {
        super(in);
        certification = in.readString();
        runtime = in.readInt();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(certification);
        dest.writeInt(runtime);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }
        
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    
    public String getCertification() {
        return certification;
    }
    
    public void setCertification(String certification) {
        this.certification = certification;
    }
    
    public int getRuntime() {
        return runtime;
    }
    
    public String getRuntimeString() {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        
        return hours > 0 ? hours + "h " + minutes + "min" : minutes + "m";
    }
    
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
