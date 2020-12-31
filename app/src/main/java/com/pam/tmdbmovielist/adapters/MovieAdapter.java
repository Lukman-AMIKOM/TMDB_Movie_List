package com.pam.tmdbmovielist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.viewholders.LoadingViewHolder;
import com.pam.tmdbmovielist.adapters.viewholders.MovieViewHolder;
import com.pam.tmdbmovielist.adapters.viewholders.NoDataViewHolder;
import com.pam.tmdbmovielist.adapters.viewholders.SearchingViewHolder;
import com.pam.tmdbmovielist.interfaces.OnFavoriteClickCallback;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.model.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String MODE_LIST = "mode_list";
    public static final String MODE_GRID = "mode_grid";
    
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_NO_DATA = 2;
    private final int VIEW_TYPE_SEARCHING = 3;
    
    private final String viewMode;
    private final MovieComparator comparator;
    private OnMovieClickCallback onMovieClickCallback;
    private OnFavoriteClickCallback onFavoriteClickCallback;
    
    public MovieAdapter(String viewMode, MovieComparator comparator) {
        this.viewMode = viewMode;
        this.comparator = comparator;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            int resId;
            if (viewMode.equals(MODE_LIST)) {
                resId = R.layout.list_movie_row;
            } else {
                resId = R.layout.gridview_movie;
            }
            
            View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            return new MovieViewHolder(view, viewMode);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_NO_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_data, parent, false);
            return new NoDataViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searching, parent, false);
            return new SearchingViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            new MovieDataSetter(movieSortedList.get(position), position + 1, (MovieViewHolder) holder, onFavoriteClickCallback);
            
            holder.itemView.setOnClickListener(v -> onMovieClickCallback.onMovieClicked(movieSortedList.get(position)));
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        } else if (holder instanceof SearchingViewHolder) {
            if (movieSortedList.get(position).getTitle() != null) {
                ((SearchingViewHolder) holder).tvSearching.setText(holder.itemView.getContext().getString(R.string.search_no_match));
            }
        }
    }
    
    @Override
    public int getItemCount() {
        return movieSortedList.size() == 0 ? 1 : movieSortedList.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        if (movieSortedList.size() == 0) {
            return VIEW_TYPE_NO_DATA;
        } else {
            if (movieSortedList.get(0).getId() == -2) {
                return VIEW_TYPE_SEARCHING;
            }
        }
        
        return movieSortedList.get(position).getId() == -1 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    
    public void setOnMovieClickCallback(OnMovieClickCallback onMovieClickCallback) {
        this.onMovieClickCallback = onMovieClickCallback;
    }
    
    public void setOnFavoriteClickCallback(OnFavoriteClickCallback onFavoriteClickCallback) {
        this.onFavoriteClickCallback = onFavoriteClickCallback;
    }
    
    private final SortedList<Movie> movieSortedList = new SortedList<>(Movie.class, new SortedList.Callback<Movie>() {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            return comparator.compare(movie1, movie2);
        }
        
        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }
        
        @Override
        public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.equals(newItem);
        }
        
        @Override
        public boolean areItemsTheSame(Movie item1, Movie item2) {
            return item1.hashCode() == item2.hashCode();
        }
        
        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }
        
        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }
        
        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });
    
    public void add(Movie movie) {
        movieSortedList.add(movie);
    }
    
    public void add(List<Movie> movies) {
        movieSortedList.addAll(movies);
    }
    
    public Movie get(int index) {
        return movieSortedList.get(index);
    }
    
    public int getIndex(Movie movie) {
        return movieSortedList.indexOf(movie);
    }
    
    public Movie getLastPositionMovie() {
        return movieSortedList.get(movieSortedList.size() - 1);
    }
    
    public void remove(Movie movie) {
        int index = 0;
        for (int i = 0; i < movieSortedList.size(); i++) {
            if (movie.equals(movieSortedList.get(i))) {
                index = i;
                break;
            }
        }
        
//        int idx = 0;
//        int left = 0;
//        int right = movieSortedList.size();
//        int pass = 0;
//        int maxPass = movieSortedList.size() / 2;
//
//        while (left < right) {
//            int middle = (left + right) / 2;
//            Movie item = movieSortedList.get(middle);
//            int compare = comparator.compare(movie, item);
//
//            System.out.println("Comparing " + movie.getTitle() + " with " + item.getTitle() + ": " + compare);
//            if (compare == 0) {
//                idx = middle;
//                break;
//            } else if (compare < 0) {
//                right = middle;
//            } else {
//                left = middle;
//            }
//        }
//
//        System.out.println("index: " + index + ", idx: " + idx);
        movieSortedList.removeItemAt(index);
        notifyItemRangeChanged(index, movieSortedList.size() - index);
    }
    
    public void remove(List<Movie> movies) {
        movieSortedList.beginBatchedUpdates();
        for (Movie movie : movies) {
            movieSortedList.remove(movie);
        }
        movieSortedList.endBatchedUpdates();
    }
    
    public void removeAll() {
        movieSortedList.clear();
    }
    
    public void replaceAll(List<Movie> movies) {
        movieSortedList.beginBatchedUpdates();
        for (int i = movieSortedList.size() - 1; i >= 0; i--) {
            final Movie movie = movieSortedList.get(i);
            if (!movies.contains(movie)) {
                movieSortedList.removeItemAt(i);
            }
        }
        movieSortedList.addAll(movies);
        notifyItemRangeChanged(0, movies.size() - 1);
        movieSortedList.endBatchedUpdates();
    }
    
    public void loadMore() {
        Movie nullMovie = new Movie();
        switch (comparator.getType()) {
            case MovieComparator.TITLE_ASCENDING:
                nullMovie.setTitle("~~~~~~~~~~~~~~~~~");
                break;
            case MovieComparator.TITLE_DESCENDING:
                nullMovie.setTitle("                 ");
                break;
            case MovieComparator.RELEASE_DATE_ASCENDING:
                nullMovie.setReleaseDate("4000-12-31");
                break;
            case MovieComparator.RELEASE_DATE_DESCENDING:
                nullMovie.setReleaseDate("0001-01-01");
                break;
            case MovieComparator.RATING_ASCENDING:
                nullMovie.setRating(Float.MAX_VALUE);
                break;
            case MovieComparator.RATING_DESCENDING:
                nullMovie.setRating(Float.MIN_VALUE);
                break;
        }
        movieSortedList.add(nullMovie);
    }
    
    public void removeNullMovie() {
        movieSortedList.removeItemAt(movieSortedList.size() - 1);
    }
}
