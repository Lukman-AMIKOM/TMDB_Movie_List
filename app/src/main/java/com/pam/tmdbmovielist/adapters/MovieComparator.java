package com.pam.tmdbmovielist.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import com.pam.tmdbmovielist.model.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MovieComparator implements Comparator<Movie>, Parcelable {
    
    public static final String TITLE_ASCENDING = "title_ascending";
    public static final String TITLE_DESCENDING = "title_descending";
    public static final String RELEASE_DATE_ASCENDING = "oldest_newest";
    public static final String RELEASE_DATE_DESCENDING = "newest_oldest";
    public static final String RATING_DESCENDING = "rating_descending";
    public static final String RATING_ASCENDING = "rating_ascending";
    public static final String POPULARITY_ASCENDING = "popularity_ascending";
    public static final String POPULARITY_DESCENDING = "popularity_descending";
    
    private final String type;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy", Locale.ENGLISH);
    
    public MovieComparator(String type) {
        this.type = type;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
    }
    
    protected MovieComparator(Parcel in) {
        type = in.readString();
    }
    
    public static final Creator<MovieComparator> CREATOR = new Creator<MovieComparator>() {
        @Override
        public MovieComparator createFromParcel(Parcel in) {
            return new MovieComparator(in);
        }
        
        @Override
        public MovieComparator[] newArray(int size) {
            return new MovieComparator[size];
        }
    };
    
    @Override
    public int compare(Movie movie1, Movie movie2) {
        if (movie1.hashCode() == movie2.hashCode()) {
            return 0;
        } else {
            switch (type) {
                case TITLE_ASCENDING:
                    if (movie1.getTitle().compareTo(movie2.getTitle()) < 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                case TITLE_DESCENDING:
                    if (movie1.getTitle().compareTo(movie2.getTitle()) < 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                case RELEASE_DATE_ASCENDING:
                    Date date1 = getDate(movie1.getFormattedReleaseDate());
                    Date date2 = getDate(movie2.getFormattedReleaseDate());
                    
                    if (date1.before(date2)) {
                        return -1;
                    } else {
                        return 1;
                    }
                case RELEASE_DATE_DESCENDING:
                    Date movieDate1 = getDate(movie1.getFormattedReleaseDate());
                    Date movieDate2 = getDate(movie2.getFormattedReleaseDate());
                    
                    if (movieDate1.before(movieDate2)) {
                        return 1;
                    } else {
                        return -1;
                    }
                case RATING_ASCENDING:
                    if (movie1.getRating() < movie2.getRating()) {
                        return -1;
                    } else {
                        return 1;
                    }
                case RATING_DESCENDING:
                    if (movie1.getRating() > movie2.getRating()) {
                        return -1;
                    } else {
                        return 1;
                    }
                case POPULARITY_ASCENDING:
                    if (movie1.getVoteCount() < movie2.getVoteCount()) {
                        return -1;
                    } else {
                        return 1;
                    }
                case POPULARITY_DESCENDING:
                    if (movie1.getVoteCount() > movie2.getVoteCount()) {
                        return -1;
                    } else {
                        return 1;
                    }
            }
        }
        
        return 0;
    }
    
    private Date getDate(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException | NullPointerException ignored) {
        }
        
        return new Date();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieComparator that = (MovieComparator) o;
        return Objects.equals(type, that.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
    
    public String getType() {
        return type;
    }
}