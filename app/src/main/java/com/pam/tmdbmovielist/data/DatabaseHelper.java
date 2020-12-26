package com.pam.tmdbmovielist.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pam.tmdbmovielist.model.Movie;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "db_favorite";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_NAME = "table_favorite";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String GENRES = "genres";
    private static final String VOTE_COUNT = "vote_count";
    private static final String RATING = "rating";
    private static final String CERTIFICATION = "certification";
    private static final String OVERVIEW = "overview";
    private static final String RUNTIME = "runtime";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + ID + " INTEGER PRIMARY KEY, "
                        + TITLE + " TEXT, "
                        + POSTER_PATH + " TEXT, "
                        + RELEASE_DATE + " TEXT, "
                        + GENRES + " TEXT, "
                        + VOTE_COUNT + " INTEGER, "
                        + RATING + " REAL, "
                        + CERTIFICATION + " TEXT, "
                        + OVERVIEW + " TEXT, "
                        + RUNTIME + " INTEGER)";
        db.execSQL(sql);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    
    public ArrayList<Movie> getAllFavorites() {
        ArrayList<Movie> favoriteList = new ArrayList<>();
    
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ID, TITLE, POSTER_PATH, RELEASE_DATE, GENRES,
                VOTE_COUNT, RATING, CERTIFICATION, OVERVIEW, RUNTIME};
        @SuppressLint("Recycle") Cursor c = db.query(TABLE_NAME, columns, null, null, null, null, null);
        
        if (c != null && c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(ID));
                String title = c.getString(c.getColumnIndex(TITLE));
                String posterPath = c.getString(c.getColumnIndex(POSTER_PATH));
                String releaseDate = c.getString(c.getColumnIndex(RELEASE_DATE));
                String strGenres = c.getString(c.getColumnIndex(GENRES));
                String[] genres = strGenres.split(",");
                int voteCount = c.getInt(c.getColumnIndex(VOTE_COUNT));
                float rating = c.getFloat(c.getColumnIndex(RATING));
                String certification = c.getString(c.getColumnIndex(CERTIFICATION));
                String overview = c.getString(c.getColumnIndex(OVERVIEW));
                int runtime = c.getInt(c.getColumnIndex(RUNTIME));
        
                Movie movie = new Movie(id, title, posterPath, releaseDate, genres, voteCount,
                        rating, true, certification, overview, runtime);
                favoriteList.add(movie);
            } while (c.moveToNext());
        }
        
        db.close();
        return favoriteList;
    }
    
    public void addFavorite(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
    
        values.put(ID, movie.getId());
        values.put(TITLE, movie.getTitle());
        values.put(POSTER_PATH, movie.getPosterPath());
        values.put(RELEASE_DATE, movie.getReleaseDate());
        values.put(GENRES, getGenres(movie.getGenres()));
        values.put(VOTE_COUNT, movie.getVoteCount());
        values.put(RATING, movie.getRating());
        values.put(CERTIFICATION, movie.getCertification());
        values.put(OVERVIEW, movie.getOverview());
        values.put(RUNTIME, movie.getRuntime());
    
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    
    public void deleteFavorite(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
    
        db.delete(TABLE_NAME, ID + "=" + movie.getId(), null);
        db.close();
    }
    
    private String getGenres(String[] genresArray) {
        StringBuilder genres = new StringBuilder();
        for (String s : genresArray) {
            genres.append(s).append(",");
        }
        
        return genres.length() > 0 ? genres.substring(0, genres.length() - 1) : genres.toString();
    }
}
