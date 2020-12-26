package com.pam.tmdbmovielist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Media implements Parcelable {
    
    private int id;
    private String title;
    private String posterPath;
    private String releaseDate;
    private String formattedReleaseDate;
    private int year;
    private String overview;
    private String[] genres;
    private int voteCount;
    private float rating;
    private boolean isFavorite;
    
    public Media() {
    }
    
    public Media(int id, String title, String posterPath, String releaseDate, String overview,
                 String[] genres, int voteCount, float rating, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.formattedReleaseDate = getFormattedReleaseDate(releaseDate);
        this.overview = overview;
        this.genres = genres;
        this.voteCount = voteCount;
        this.rating = rating;
        this.isFavorite = isFavorite;
        setYear();
    }
    
    private String getFormattedReleaseDate(String releaseDate) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String strDate;
        
        try {
            if (releaseDate == null) {
                return null;
            }
            
            Date sourceDate = sdf1.parse(releaseDate);
            
            if (sourceDate != null) {
                strDate = sdf2.format(sourceDate);
                return strDate;
            }
        } catch (ParseException | NullPointerException ignored) {
        }
        
        return null;
    }
    
    private void setYear() {
        if (formattedReleaseDate == null) {
            year = Short.MAX_VALUE;
        } else {
            if (formattedReleaseDate.length() == 0) {
                year = Short.MAX_VALUE;
            } else {
                try {
                    year = Integer.parseInt(formattedReleaseDate.substring(formattedReleaseDate.length() - 4));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    year = 0;
                }
            }
        }
    }
    
    protected Media(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        formattedReleaseDate = in.readString();
        year = in.readInt();
        overview = in.readString();
        genres = in.createStringArray();
        voteCount = in.readInt();
        rating = in.readFloat();
        isFavorite = in.readByte() != 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(formattedReleaseDate);
        dest.writeInt(year);
        dest.writeString(overview);
        dest.writeStringArray(genres);
        dest.writeInt(voteCount);
        dest.writeFloat(rating);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }
        
        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return id == media.id &&
                Objects.equals(title, media.title);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPosterPath() {
        return posterPath;
    }
    
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public String getFormattedReleaseDate() {
        return formattedReleaseDate;
    }
    
    public String getValidReleaseDate() {
        if (releaseDate == null || releaseDate.length() == 0) {
            return "3000-12-31";
        } else {
            return releaseDate;
        }
    }
    
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        this.formattedReleaseDate = getFormattedReleaseDate(releaseDate);
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getOverview() {
        return overview;
    }
    
    public void setOverview(String overview) {
        this.overview = overview;
    }
    
    public String[] getGenres() {
        return genres;
    }
    
    public String getGenre() {
        StringBuilder genre = new StringBuilder();
        for (int i = 0; i < genres.length && i < 4; i++) {
            genre.append(genres[i]).append(", ");
        }
        
        return genre.length() > 0 ? genre.substring(0, genre.length() - 2) : genre.toString();
    }
    
    public void setGenres(String[] genres) {
        this.genres = genres;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    
    public float getRating() {
        return rating;
    }
    
    public void setRating(float rating) {
        this.rating = rating;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public void setFavorite(int favorite) {
        isFavorite = favorite == 1;
    }
}
