package com.pam.tmdbmovielist.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.ui.fragments.MainFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    public static final String EXTRA_MOVIE_LIST = "movie_list";
    public static final String EXTRA_FAVORITE_LIST = "favorite_list";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ArrayList<Movie> movieList = getIntent().getParcelableArrayListExtra(EXTRA_MOVIE_LIST);
        ArrayList<Movie> favoriteList = getIntent().getParcelableArrayListExtra(EXTRA_FAVORITE_LIST);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_MOVIE_LIST, movieList);
        args.putParcelableArrayList(EXTRA_FAVORITE_LIST, favoriteList);
        mainFragment.setArguments(args);
        
        if (fragmentManager.findFragmentByTag(MainFragment.class.getSimpleName()) == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.main_container, mainFragment, MainFragment.class.getSimpleName())
                    .commit();
        }
    }
    
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}