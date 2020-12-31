package com.pam.tmdbmovielist.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.MainPagerAdapter;
import com.pam.tmdbmovielist.adapters.MovieAdapter;
import com.pam.tmdbmovielist.adapters.MovieComparator;
import com.pam.tmdbmovielist.data.DatabaseHelper;
import com.pam.tmdbmovielist.data.MovieList;
import com.pam.tmdbmovielist.data.PrefManager;
import com.pam.tmdbmovielist.data.RetrofitClient;
import com.pam.tmdbmovielist.interfaces.OnFavoriteClickCallback;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.interfaces.RetrofitResponseListener;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.ui.activities.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements OnMovieClickCallback,
        OnFavoriteClickCallback, SearchView.OnQueryTextListener, RetrofitResponseListener {
    
    public static final String PREF_THEME = "pref_theme";
    public static final String KEY_NIGHT_MODE = "key_theme";
    
    private PrefManager prefManager;
    
    private MainActivity mainActivity;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;
    private SearchView searchView;
    private TabLayout listTabLayout;
    private ViewPager2 listViewPager;
    
    private ArrayList<Movie> nowPlayingList, topRatedList, favoriteList, searchResultList;
    private ListFragment nowPlayingFragment, topRatedFragment, favoriteFragment, selectedListFragment;
    private ArrayList<ListFragment> pages;
    
    private String searchQuery;
    private ArrayMap<String, Integer> pageNumbers;
    private boolean isExpanded = true;
    private boolean nightMode = false;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            nowPlayingList = getArguments().getParcelableArrayList(MainActivity.EXTRA_MOVIE_LIST);
            favoriteList = getArguments().getParcelableArrayList(MainActivity.EXTRA_FAVORITE_LIST);
        }
        topRatedList = new ArrayList<>();
        
        pages = new ArrayList<>();
        pageNumbers = new ArrayMap<>();
        initListFragments();
        selectedListFragment = nowPlayingFragment;
        
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);
        setToolbar();
        setTabs();
    }
    
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        
        if (mainActivity != null) {
            prefManager = new PrefManager(mainActivity, PREF_THEME);
            nightMode = prefManager.getDarkMode(KEY_NIGHT_MODE);
        }
        MenuItem itemNightMode = menu.findItem(R.id.menu_night_mode);
        itemNightMode.setTitle(nightMode ? getString(R.string.light_mode) : getString(R.string.dark_mode));
        
        initSearchView(menu);
        
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        if (!TextUtils.isEmpty(searchQuery)) {
            searchView.setQuery(searchQuery, false);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
    }
    
    private void init(View view) {
        mainActivity = (MainActivity) getActivity();
        appBarLayout = view.findViewById(R.id.app_bar_layout_main);
        toolbar = view.findViewById(R.id.main_toolbar);
        listTabLayout = view.findViewById(R.id.list_tab_layout);
        listViewPager = view.findViewById(R.id.list_view_pager);
    }
    
    private void setToolbar() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int expanded = Math.abs(verticalOffset);
            int height = appBarLayout.getHeight();
            isExpanded = expanded < height;
        });
        appBarLayout.setExpanded(isExpanded);
        toolbar.setTitle("");
        
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setIcon(ContextCompat.getDrawable(mainActivity, R.drawable.ic_tmdb_primary_logo_small));
            }
        }
    }
    
    private void initSearchView(Menu menu) {
        MenuItem itemSearch = menu.findItem(R.id.menu_search);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setQueryHint("Search movie...");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
        
        if (!TextUtils.isEmpty(searchQuery)) {
            if (selectedListFragment.isSearchMode()) {
                searchView.setQuery(searchQuery, false);
            }
            searchView.setIconified(false);
            searchView.clearFocus();
        }
    }
    
    private void setTabs() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int nowPlayingIcon, topRatedIcon, favoriteIcon;
        
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            nowPlayingIcon = R.drawable.ic_now_playing_night;
            topRatedIcon = R.drawable.ic_top_rated_night;
            favoriteIcon = R.drawable.ic_favorite_night;
        } else {
            nowPlayingIcon = R.drawable.ic_now_playing;
            topRatedIcon = R.drawable.ic_top_rated;
            favoriteIcon = R.drawable.ic_favorite;
        }
        
        nowPlayingFragment.setTitleIcon(getIconDrawable(nowPlayingIcon));
        topRatedFragment.setTitleIcon(getIconDrawable(topRatedIcon));
        favoriteFragment.setTitleIcon(getIconDrawable(favoriteIcon));
        
        listViewPager.setAdapter(new MainPagerAdapter(this, pages));
        new TabLayoutMediator(listTabLayout, listViewPager, (tab, position) -> {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
            ImageView imgTabIcon = view.findViewById(R.id.img_tab_icon);
            TextView tvTabTitle = view.findViewById(R.id.tv_tab_title);
            
            imgTabIcon.setImageDrawable(pages.get(position).getTitleIcon());
            tvTabTitle.setText(pages.get(position).getTitle());

//                tab.setText(pages.get(position).getTitle());
//                tab.setIcon(pages.get(position).getTitleIcon());

//                TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_alternative, null);
//                view.setText(pages.get(position).getTitle());
//                view.setCompoundDrawablesWithIntrinsicBounds(pages.get(position).getTitleIcon(), null, null, null);
            tab.setCustomView(view);
        }).attach();
        
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedListFragment = getSelectedTabFragment();
                
                if (selectedListFragment != null) {
                    if (selectedListFragment.getListType().equals(ListFragment.TYPE_TOP_RATED)) {
                        if (topRatedList.isEmpty() && !topRatedFragment.isLoading()) {
                            topRatedFragment.setLoading(true);
                            MovieList.getInstance().getMovieList("top_rated", 1, MainFragment.this, 100);
                        }
                    }
                }
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectedListFragment = getSelectedTabFragment(tab.getPosition());
                if (selectedListFragment != null) {
                    if (selectedListFragment.isSearchMode()) {
                        restoreList();
                        searchView.setQuery(null, false);
                        searchView.setIconified(true);
                        searchView.clearFocus();
                    }
                }
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            
            }
        };
        
        listTabLayout.addOnTabSelectedListener(onTabSelectedListener);
    }
    
    public void initListFragments() {
        nowPlayingFragment = initListFragment("Now Playing", ListFragment.TYPE_NOW_PLAYING, nowPlayingList);
        nowPlayingFragment.setSelectedSorter(getString(R.string.release_date));
        topRatedFragment = initListFragment("Top Rated", ListFragment.TYPE_TOP_RATED, topRatedList);
        topRatedFragment.setSelectedSorter(getString(R.string.rating));
        favoriteFragment = initListFragment("Favorite", ListFragment.TYPE_FAVORITES, favoriteList);
        favoriteFragment.setSelectedSorter(getString(R.string.rating));
    }
    
    private ListFragment initListFragment(String title, String listType, ArrayList<Movie> movieList) {
        String sorterRating = getString(R.string.rating);
        String sorterTitle = getString(R.string.title);
        String sorterReleaseDate = getString(R.string.release_date);
        String sorterPopularity = getString(R.string.popularity);
        ListFragment listFragment = new ListFragment(title, listType, MovieAdapter.MODE_LIST,
                new MovieComparator(MovieComparator.RATING_DESCENDING), movieList,
                sorterRating, sorterTitle, sorterReleaseDate, sorterPopularity);
        listFragment.setOnMovieClickCallback(this);
        listFragment.setOnFavoriteClickCallback(this);
        
        pages.add(listFragment);
        pageNumbers.put(listType, 1);
        
        return listFragment;
    }
    
    private Drawable getIconDrawable(int resId) {
        return ContextCompat.getDrawable(Objects.requireNonNull(getContext()), resId);
    }
    
    private ListFragment getSelectedTabFragment() {
        int tabIndex = getSelectedTabPosition();
        return tabIndex != -1 ? getSelectedTabFragment(tabIndex) : null;
    }
    
    private ListFragment getSelectedTabFragment(int tabIndex) {
        return pages.get(tabIndex);
    }
    
    private int getSelectedTabPosition() {
        return listTabLayout.getSelectedTabPosition();
    }
    
    private ArrayList<Movie> getSelectedTabList(String listType) {
        switch (listType) {
            case ListFragment.TYPE_NOW_PLAYING:
                return nowPlayingList;
            case ListFragment.TYPE_TOP_RATED:
                return topRatedList;
            case ListFragment.TYPE_FAVORITES:
                return favoriteList;
            default:
                return new ArrayList<>();
        }
    }
    
    @Override
    public void onMovieClicked(Movie movie) {
        if (mainActivity != null) {
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            Call<String> call = RetrofitClient.getInstance().getMyApi().getDetailedMovie(movie.getId());
            
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    MovieDetailFragment movieDetailFragment;
                    movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentByTag(MovieDetailFragment.class.getSimpleName() + ":" + movie.getId());
                    if (movieDetailFragment != null) {
                        try {
                            if (response.body() != null) {
                                movieDetailFragment.setResponseJsonObject(new JSONObject(response.body()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                }
            });
            
            showMovieDetail(movie, fragmentManager);
        }
    }
    
    private void showMovieDetail(Movie movie, FragmentManager fragmentManager) {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MovieDetailFragment.ARG_MOVIE, movie);
        movieDetailFragment.setArguments(args);
        movieDetailFragment.setOnFavoriteClickCallback(this);
        
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, movieDetailFragment, MovieDetailFragment.class.getSimpleName() + ":" + movie.getId())
                .addToBackStack(null)
                .commit();
    }
    
    @Override
    public void onFavoriteClicked(Movie movie) {
        boolean isFavorite = movie.isFavorite();
        ListFragment listFragment = getSelectedTabFragment();
        DatabaseHelper db = new DatabaseHelper(getContext());
        String action;
        
        if (isFavorite) {
            if (!favoriteList.contains(movie)) {
                favoriteList.add(movie);
                favoriteFragment.setMovieList(favoriteList);
                db.addFavorite(movie);
            }
            
            action = getString(R.string.favorite_add);
        } else {
            favoriteList.remove(movie);
            db.deleteFavorite(movie);
    
            if (listFragment != null) {
                if (listFragment.equals(favoriteFragment)) {
                    try {
                        favoriteFragment.getMovieAdapter().remove(movie);
                    } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    }
                } else {
                    favoriteFragment.setMovieList(favoriteList);
                }
            }
    
            action = getString(R.string.favorite_remove);
        }
    
        Toast.makeText(getContext(), getString(R.string.favorite_notification, movie.getTitle(), action), Toast.LENGTH_SHORT).show();
        updateList(movie);
    }
    
    private void updateList(Movie movie) {
        ListFragment listFragment = getSelectedTabFragment();
        if (listFragment != null) {
            if (!listFragment.equals(favoriteFragment)) {
                if (listFragment.equals(nowPlayingFragment)) {
                    updateFavorite(topRatedFragment, topRatedList, movie);
                } else if (listFragment.equals(topRatedFragment)) {
                    updateFavorite(nowPlayingFragment, nowPlayingList, movie);
                }
            } else {
                updateFavorite(topRatedFragment, topRatedList, movie);
                updateFavorite(nowPlayingFragment, nowPlayingList, movie);
            }
        }
    }
    
    private void updateFavorite(ListFragment listFragment, ArrayList<Movie> movieList, Movie movie) {
        int index = movieList.indexOf(movie);
        if (index != -1) {
            movieList.get(index).setFavorite(movie.isFavorite());
            listFragment.setMovieList(movieList);
        }
    }
    
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!selectedListFragment.isSearchMode()) {
            selectedListFragment.saveLastPosition();
        }
        selectedListFragment.getMovieAdapter().removeAll();
        ArrayList<Movie> tmpList = new ArrayList<>();
        tmpList.add(new Movie(-2, null));
        selectedListFragment.setSearchMode(true);
        selectedListFragment.setMovieList(tmpList);
        
        searchQuery = query;
        MovieList.getInstance().getSearchResult(query, this, 300);
        return true;
    }
    
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText) && selectedListFragment.isSearchMode()) {
            restoreList();
            searchQuery = null;
        }
        return true;
    }
    
    private void setSearchResult() {
        selectedListFragment = getSelectedTabFragment();
        
        if (selectedListFragment != null) {
            selectedListFragment.setComparator(new MovieComparator(MovieComparator.POPULARITY_DESCENDING));
            selectedListFragment.setMovieList(searchResultList);
            selectedListFragment.setSearchModeSorter();
        }
    }
    
    private void restoreList() {
        selectedListFragment.setSearchMode(false);
        selectedListFragment.restoreComparator();
        selectedListFragment.setMovieList(getSelectedTabList(selectedListFragment.getListType()));
        selectedListFragment.restoreScrollPosition();
    }
    
    @Override
    public void onResponse(int requestCode, String response) {
        switch (requestCode) {
            case 100:
                topRatedFragment.loadData();
                MovieList.getInstance().getDetailedMovieList(response, topRatedList, this, 101, favoriteList);
                break;
            case 101:
                topRatedFragment.setMovieList(topRatedList);
                topRatedFragment.setLoading(false);
                break;
            case 300:
                try {
                    JSONObject responseJsonObject = new JSONObject(response);
                    int totalResult = responseJsonObject.getInt("total_results");
                    if (totalResult == 0) {
                        selectedListFragment.getMovieAdapter().removeAll();
                        ArrayList<Movie> tmpList = new ArrayList<>();
                        tmpList.add(new Movie(-2, "no match"));
                        selectedListFragment.setMovieList(tmpList);
                        selectedListFragment.hideProgressBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                searchResultList = new ArrayList<>();
                MovieList.getInstance().getDetailedMovieList(response, searchResultList, this, 301, favoriteList);
                break;
            case 301:
                setSearchResult();
                break;
        }
    }
    
    public ArrayList<Movie> getFavoriteList() {
        return favoriteList;
    }
    
    public ArrayMap<String, Integer> getPageNumbers() {
        return pageNumbers;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            AboutFragment aboutFragment = new AboutFragment();
            
            if (mainActivity != null) {
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.exit_to_bottom)
                        .replace(R.id.main_container, aboutFragment, AboutFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        } else if (item.getItemId() == R.id.menu_night_mode) {
            if (mainActivity != null) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!nightMode) {
                        mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    nightMode = !nightMode;
                    prefManager.setDarkMode(KEY_NIGHT_MODE, nightMode);
                }, 400);
            }
        }
        
        return true;
    }
}