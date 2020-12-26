package com.pam.tmdbmovielist.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.data.DatabaseHelper;
import com.pam.tmdbmovielist.data.MovieList;
import com.pam.tmdbmovielist.interfaces.RetrofitResponseListener;
import com.pam.tmdbmovielist.model.Movie;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity implements RetrofitResponseListener {
    
    ProgressBar progressBar;
    
    private ArrayList<Movie> movieList;
    private ArrayList<Movie> favoriteList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        progressBar = findViewById(R.id.splash_screen_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        
        DatabaseHelper db = new DatabaseHelper(this);
        favoriteList = db.getAllFavorites();
        
        MovieList.getInstance().getMovieList("now_playing", 1, this, 100);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    public void onResponse(int requestCode, String response) {
        switch (requestCode) {
            case 100:
                movieList = new ArrayList<>();
                MovieList.getInstance().getDetailedMovieList(response, movieList, this, 101, favoriteList);
                break;
            case 101:
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_MOVIE_LIST, movieList);
                intent.putExtra(MainActivity.EXTRA_FAVORITE_LIST, favoriteList);
                startActivity(intent);
                finish();
        }
    }
}