package com.pam.tmdbmovielist.ui.fragments;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.MovieAdapter;
import com.pam.tmdbmovielist.adapters.MovieComparator;
import com.pam.tmdbmovielist.data.MovieList;
import com.pam.tmdbmovielist.interfaces.OnFavoriteClickCallback;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.interfaces.RetrofitResponseListener;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.ui.custom.CustomSpinnerAdapter;
import com.pam.tmdbmovielist.ui.custom.MySpinner;
import com.pam.tmdbmovielist.ui.custom.ResponsiveGridLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment implements RetrofitResponseListener {
    public static final String TYPE_NOW_PLAYING = "now_playing";
    public static final String TYPE_TOP_RATED = "top_rated";
    public static final String TYPE_FAVORITES = "type_favorites";
    private static final String EMPTY_STRING = "";
    
    private final String sorterRating, sorterTitle, sorterReleaseDate, sorterPopularity;
    private final String title;
    private final String listType;
    
    private MySpinner spinnerSorter;
    private ImageView imgOrderingMode, imgViewMode;
    private ProgressBar pbListMovie;
    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private String viewMode;
    private LinearLayoutManager linearLayoutManager;
    private ResponsiveGridLayoutManager gridLayoutManager;
    private MovieComparator comparator;
    private OnMovieClickCallback onMovieClickCallback;
    private OnFavoriteClickCallback onFavoriteClickCallback;
    private boolean loading = false;
    
    private ArrayList<Movie> movieList;
    private ArrayList<Movie> loadMoreList;
    private int lastPosition;
    
    private List<String> sorterList;
    private String selectedSorter, searchSorter = EMPTY_STRING;
    private String selectedViewMode = MovieAdapter.MODE_LIST;
    private boolean isListAscending = false;
    private boolean ascendingState = false;
    private boolean isSearchMode = false;
    
    private Drawable titleIcon;
    private Drawable listIcon, gridIcon;
    private Drawable ascendingIcon, descendingIcon;
    
    public ListFragment(String title, String listType, String viewMode,
                        MovieComparator comparator, ArrayList<Movie> movieList,
                        String sorterRating, String sorterTitle, String sorterReleaseDate,
                        String sorterPopularity) {
        this.sorterRating = sorterRating;
        this.sorterTitle = sorterTitle;
        this.sorterReleaseDate = sorterReleaseDate;
        this.sorterPopularity = sorterPopularity;
        
        this.title = title;
        this.listType = listType;
        this.viewMode = viewMode;
        this.comparator = comparator;
        this.movieList = movieList;
        setLayoutManager(viewMode);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);
        setMovieAdapter();
    }
    
    private void init(View view) {
        spinnerSorter = view.findViewById(R.id.spinner_sorter);
        imgOrderingMode = view.findViewById(R.id.img_ordering_mode);
        imgViewMode = view.findViewById(R.id.img_view_mode);
        rvMovies = view.findViewById(R.id.rv_movies);
        pbListMovie = view.findViewById(R.id.pb_list_movie);
        
        initIcons();
        initSorter();
        initOrderingMode();
        initViewMode();
        
        rvMovies.setHasFixedSize(true);
        rvMovies.setLayoutManager(getLayoutManager());
    }
    
    private void initIcons() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            listIcon = getIconDrawable(R.drawable.ic_list_white);
            gridIcon = getIconDrawable(R.drawable.ic_grid_white);
            
            ascendingIcon = getIconDrawable(R.drawable.ic_ascending_triangle_night);
            descendingIcon = getIconDrawable(R.drawable.ic_descending_triangle_night);
        } else {
            listIcon = getIconDrawable(R.drawable.ic_list);
            gridIcon = getIconDrawable(R.drawable.ic_grid);
            
            ascendingIcon = getIconDrawable(R.drawable.ic_ascending_triangle);
            descendingIcon = getIconDrawable(R.drawable.ic_descending_triangle);
        }
    }
    
    private void initSorter() {
        sorterList = Arrays.asList(getResources().getStringArray(R.array.sorter_array));
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_item, sorterList);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_row);
        
        spinnerSorter.setAdapter(spinnerAdapter);
        spinnerSorter.setBackground(ContextCompat.getDrawable(getContext(), android.R.color.transparent));
        spinnerSorter.setSelection(sorterList.indexOf(selectedSorter), false);
        spinnerSorter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sorter = isSearchMode ? searchSorter : selectedSorter;
                if (!spinnerSorter.getSelectedItem().toString().equals(sorter)) {
                    if (isSearchMode) {
                        searchSorter = spinnerSorter.getSelectedItem().toString();
                    } else {
                        selectedSorter = spinnerSorter.getSelectedItem().toString();
                    }
                    changeSorter();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void initOrderingMode() {
        setOrderingMode();
        imgOrderingMode.setOnClickListener(v -> {
            isListAscending = !isListAscending;
            setOrderingMode();
        });
    }
    
    private void initViewMode() {
        setViewMode();
        imgViewMode.setOnClickListener(v -> {
            selectedViewMode = selectedViewMode.equals(MovieAdapter.MODE_LIST) ? MovieAdapter.MODE_GRID : MovieAdapter.MODE_LIST;
            saveLastPosition();
            setViewMode();
        });
    }
    
    private void setOrderingMode() {
        if (isListAscending) {
            imgOrderingMode.setImageDrawable(ascendingIcon);
        } else {
            imgOrderingMode.setImageDrawable(descendingIcon);
        }
        
        if (!isSearchMode) {
            changeSorter();
        } else if (movieAdapter.getItemCount() > 0) {
            changeSorter();
        }
    }
    
    private void setViewMode() {
        switch (selectedViewMode) {
            case MovieAdapter.MODE_LIST:
                imgViewMode.setImageDrawable(gridIcon);
                break;
            case MovieAdapter.MODE_GRID:
                imgViewMode.setImageDrawable(listIcon);
                break;
        }
        
        setViewMode(selectedViewMode);
    }
    
    private void setViewMode(String viewMode) {
        setLayoutManager(viewMode);
        
        restoreScrollPosition();
    }
    
    private void changeSorter() {
        setComparator(new MovieComparator(getComparator()));
    }
    
    private Drawable getIconDrawable(int resId) {
        return ContextCompat.getDrawable(Objects.requireNonNull(getContext()), resId);
    }
    
    private String getComparator() {
        String sorter = isSearchMode ? searchSorter : selectedSorter;
        
        if (sorter.equals(sorterTitle)) {
            if (isListAscending) {
                return MovieComparator.TITLE_ASCENDING;
            } else {
                return MovieComparator.TITLE_DESCENDING;
            }
        } else if (sorter.equals(sorterReleaseDate)) {
            if (isListAscending) {
                return MovieComparator.RELEASE_DATE_ASCENDING;
            } else {
                return MovieComparator.RELEASE_DATE_DESCENDING;
            }
        } else if (sorter.equals(sorterRating)) {
            if (isListAscending) {
                return MovieComparator.RATING_ASCENDING;
            } else {
                return MovieComparator.RATING_DESCENDING;
            }
        } else if (sorter.equals(sorterPopularity)) {
            if (isListAscending) {
                return MovieComparator.POPULARITY_ASCENDING;
            } else {
                return MovieComparator.POPULARITY_DESCENDING;
            }
        }
        
        return MovieComparator.RATING_DESCENDING;
    }
    
    private RecyclerView.LayoutManager getLayoutManager() {
        if (viewMode.equals(MovieAdapter.MODE_LIST)) {
            return linearLayoutManager = new LinearLayoutManager(getContext());
        } else if (viewMode.equals(MovieAdapter.MODE_GRID)) {
            gridLayoutManager = new ResponsiveGridLayoutManager(getContext(),
                    GridLayoutManager.VERTICAL, false, TypedValue.COMPLEX_UNIT_DIP, 148f);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (movieAdapter != null) {
                        int viewType = movieAdapter.getItemViewType(position);
                        if (viewType == 1 || viewType == 2) {
                            return gridLayoutManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    } else {
                        return -1;
                    }
                }
            });
            return gridLayoutManager;
        }
        
        return new LinearLayoutManager(getContext());
    }
    
    private void setLayoutManager(String viewMode) {
        this.viewMode = viewMode;
        
        setMovieAdapter();
    }
    
    private void setMovieAdapter() {
        movieAdapter = new MovieAdapter(viewMode, comparator);
        movieAdapter.add(movieList);
        checkMovieList();
        
        movieAdapter.setOnMovieClickCallback(onMovieClickCallback);
        movieAdapter.setOnFavoriteClickCallback(onFavoriteClickCallback);
        
        if (rvMovies != null) {
            rvMovies.setLayoutManager(getLayoutManager());
            rvMovies.setAdapter(movieAdapter);
            
            if (!listType.equals(TYPE_FAVORITES)) {
                rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (isSearchMode) {
                            return;
                        }
                        
                        if (viewMode.equals(MovieAdapter.MODE_LIST)) {
                            if (!loading && linearLayoutManager.findLastCompletelyVisibleItemPosition() == movieList.size() - 1) {
                                lastPosition = movieList.size() - 1;
                                loading = true;
                                loadMore();
                            }
                        } else {
                            if (!loading && gridLayoutManager.findLastVisibleItemPosition() == movieList.size() - 1) {
                                lastPosition = movieList.size() - 1;
                                loading = true;
                                loadMore();
                            }
                        }
                    }
                });
            }
        }
    }
    
    private void loadMore() {
        Runnable runnable = () -> {
            movieAdapter.loadMore();
            MainFragment mainFragment = (MainFragment) getParentFragment();
            if (mainFragment != null) {
                int pageNum = mainFragment.getPageNumbers().get(listType) + 1;
                MovieList.getInstance().getMovieList(listType, pageNum, this, 200);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    
    private void checkMovieList() {
        try {
            if ((listType.equals(TYPE_FAVORITES) && !isSearchMode) ||
                    (!movieList.isEmpty() && movieList.get(0).getId() != -2)) {
                pbListMovie.setVisibility(View.GONE);
            } else {
                loadData();
            }
        } catch (Exception ignored) {
        }
    }
    
    private String getSorter() {
        String comparatorType = comparator.getType();
        if (comparatorType.contains("title")) {
            return sorterTitle;
        } else if (comparatorType.contains("rating")) {
            return sorterRating;
        } else if (comparatorType.contains("release_date")) {
            return sorterReleaseDate;
        } else {
            return sorterPopularity;
        }
    }
    
    public void setComparator(MovieComparator comparator) {
        this.comparator = comparator;
        
        setLayoutManager(viewMode);
    }
    
    public void restoreComparator() {
        changeSorter();
    }
    
    public void loadData() {
        pbListMovie.setVisibility(View.VISIBLE);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> pbListMovie.setVisibility(View.GONE), 15000);
    }
    
    public void saveLastPosition() {
        lastPosition = getLastScrollPosition();
    }
    
    public int getLastScrollPosition() {
        return rvMovies == null ? 0 : viewMode.equals(MovieAdapter.MODE_LIST) ?
                linearLayoutManager.findFirstCompletelyVisibleItemPosition() :
                gridLayoutManager.findFirstVisibleItemPosition();
    }
    
    public void restoreScrollPosition() {
        setScrollPosition(lastPosition);
    }
    
    public void setScrollPosition(int index) {
        try {
            if (viewMode.equals(MovieAdapter.MODE_LIST)) {
                linearLayoutManager.scrollToPositionWithOffset(index, 0);
            } else {
                gridLayoutManager.scrollToPositionWithOffset(index, 0);
            }
        } catch (Exception ignored) {
        }
    }
    
    public void setMovieList(ArrayList<Movie> movieList) {
        this.movieList = movieList;
        checkMovieList();
        movieAdapter.replaceAll(movieList);
    }
    
    public void setOnMovieClickCallback(OnMovieClickCallback onMovieClickCallback) {
        this.onMovieClickCallback = onMovieClickCallback;
    }
    
    public void setOnFavoriteClickCallback(OnFavoriteClickCallback onFavoriteClickCallback) {
        this.onFavoriteClickCallback = onFavoriteClickCallback;
    }
    
    public void setLoading(boolean loading) {
        this.loading = loading;
    }
    
    public void setSearchMode(boolean searchMode) {
        isSearchMode = searchMode;
        if (!isSearchMode) {
            spinnerSorter.setSelection(sorterList.indexOf(selectedSorter));
            isListAscending = ascendingState;
            imgOrderingMode.setImageDrawable(isListAscending ? ascendingIcon : descendingIcon);
            searchSorter = EMPTY_STRING;
        } else {
            ascendingState = isListAscending;
        }
    }
    
    public void setSearchModeSorter() {
        if (isSearchMode) {
            searchSorter = getSorter();
            spinnerSorter.setSelection(sorterList.indexOf(searchSorter));
            isListAscending = false;
            imgOrderingMode.setImageDrawable(descendingIcon);
        }
    }
    
    public void setSelectedSorter(String selectedSorter) {
        this.selectedSorter = selectedSorter;
    }
    
    public void setTitleIcon(Drawable titleIcon) {
        this.titleIcon = titleIcon;
    }
    
    public void hideProgressBar() {
        pbListMovie.setVisibility(View.GONE);
    }
    
    public String getListType() {
        return listType;
    }
    
    public boolean isLoading() {
        return loading;
    }
    
    public boolean isSearchMode() {
        return isSearchMode;
    }
    
    public MovieAdapter getMovieAdapter() throws NullPointerException {
        return movieAdapter;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Drawable getTitleIcon() {
        return titleIcon;
    }
    
    @Override
    public void onResponse(int requestCode, String response) {
        MainFragment mainFragment = (MainFragment) getParentFragment();
        switch (requestCode) {
            case 200:
                loadMoreList = new ArrayList<>();
                
                ArrayList<Movie> favoriteList = null;
                if (mainFragment != null) {
                    favoriteList = mainFragment.getFavoriteList();
                }
                
                MovieList.getInstance().getDetailedMovieList(response, loadMoreList, this, 101, favoriteList);
                break;
            case 101:
                movieAdapter.removeNullMovie();
                movieAdapter.add(loadMoreList);
                movieList.addAll(loadMoreList);
                
                rvMovies.scrollToPosition(lastPosition);
                
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    movieAdapter.notifyItemRangeChanged(0, movieList.size(), new Object());
                    setLoading(false);
                }, 500);
                
                if (mainFragment != null) {
                    ArrayMap<String, Integer> pageNumbers = mainFragment.getPageNumbers();
                    pageNumbers.put(listType, pageNumbers.get(listType) + 1);
                }
                break;
        }
    }
}