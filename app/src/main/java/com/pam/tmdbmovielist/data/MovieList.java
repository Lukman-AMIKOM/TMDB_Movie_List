package com.pam.tmdbmovielist.data;

import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.interfaces.RetrofitResponseListener;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.ui.fragments.MainFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieList {
    
    private static MovieList instance = null;
    
    public static final String STATUS_FAILED = "status_failed";
    
    private ArrayList<Movie> movieList;
    private ArrayList<JSONObject> detailedMovieJsonObjects;
    private int index, itemCount;
    
    public static MovieList getInstance() {
        if (instance == null) {
            instance = new MovieList();
        }
        
        return instance;
    }
    
    public void getMovieList(String listType, int pageNumber, RetrofitResponseListener retrofitResponseListener, int requestCode) {
        Call<String> call = RetrofitClient.getInstance().getMyApi().getBaseMovieList(listType, pageNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                retrofitResponseListener.onResponse(requestCode, response.body());
            }
            
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                retrofitResponseListener.onResponse(requestCode, STATUS_FAILED);
            }
        });
    }
    
    public void getDetailedMovieList(String response, ArrayList<Movie> movieList,
                                      RetrofitResponseListener retrofitResponseListener,
                                      int requestCode, ArrayList<Movie> favoriteList) {
        this.movieList = movieList;
    
        try {
            JSONObject responseJsonObject = new JSONObject(response);
            JSONArray results = responseJsonObject.getJSONArray("results");
            detailedMovieJsonObjects = new ArrayList<>();
            itemCount = results.length();
            index = 0;
            
            for (int i = 0; i < itemCount; i++) {
                JSONObject movieJsonObject = results.getJSONObject(i);
                int id = movieJsonObject.getInt("id");
            
                Call<String> call = RetrofitClient.getInstance().getMyApi().getBaseMovie(id);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        try {
                            if (response.body() != null) {
                                detailedMovieJsonObjects.add(new JSONObject(response.body()));
                                index++;
                                if (index == itemCount) {
                                    addMovies(favoriteList);
                                    itemCount = 0;
        
                                    retrofitResponseListener.onResponse(requestCode, null);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                
                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        retrofitResponseListener.onResponse(requestCode, STATUS_FAILED);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public void getBaseMovie(Movie movie, OnMovieClickCallback onMovieClickCallback) {
        Call<String> call = RetrofitClient.getInstance().getMyApi().getBaseMovie(movie.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                try {
                    if (response.code() != 404 && response.body() != null) {
                        MainFragment mainFragment = (MainFragment) onMovieClickCallback;
    
                        Movie movie = getMovie(new JSONObject(response.body()), mainFragment.getFavoriteList());
                        onMovieClickCallback.onMovieClicked(movie);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
            }
        });
    }
    
    public void getSearchResult(String query, RetrofitResponseListener retrofitResponseListener, int requestCode) {
        Call<String> call = RetrofitClient.getInstance().getMyApi().getSearchResult(query);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                retrofitResponseListener.onResponse(requestCode, response.body());
            }
            
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
            }
        });
    }
    
    private void addMovies(ArrayList<Movie> favoriteList) {
        for (JSONObject detailedMovieJsonObject : detailedMovieJsonObjects) {
            Movie movie = getMovie(detailedMovieJsonObject, favoriteList);
            movieList.add(movie);
        }
    }
    
    private Movie getMovie(JSONObject detailedMovieJsonObject, ArrayList<Movie> favoriteList) {
        Movie movie = null;
        try {
            int id = detailedMovieJsonObject.getInt("id");
            String title = detailedMovieJsonObject.getString("title");
            String posterPath =  getPosterPath(detailedMovieJsonObject.getString("poster_path"));
            String releaseDate = detailedMovieJsonObject.getString("release_date");
            String[] genres = getGenres(detailedMovieJsonObject);
            int voteCount = detailedMovieJsonObject.getInt("vote_count");
            float rating = (float) detailedMovieJsonObject.getDouble("vote_average");
            String certification = getCertification(detailedMovieJsonObject);
            int runtime = 0;
            try {
                runtime = detailedMovieJsonObject.getInt("runtime");
            } catch (JSONException ignored) {
            }
            String overview = detailedMovieJsonObject.getString("overview");
    
            movie = new Movie(id, title, posterPath, releaseDate, genres, voteCount,
                    rating, false, certification, overview, runtime);
            
            if (favoriteList != null) {
                int index = favoriteList.indexOf(movie);
                if (index != -1) {
                    movie.setFavorite(favoriteList.get(index).isFavorite());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return movie;
    }
    
    private String getPosterPath(String posterPath) {
        return posterPath.equals("null") ? null : "https://themoviedb.org/t/p/w500" + posterPath;
    }
    
    private String[] getGenres(JSONObject detailedMovieJsonObject) {
        String[] genres;
        
        try {
            JSONArray genresJsonArray = detailedMovieJsonObject.getJSONArray("genres");
            genres = new String[genresJsonArray.length()];
            
            for (int i = 0; i < genresJsonArray.length(); i++) {
                JSONObject obj = genresJsonArray.getJSONObject(i);
                genres[i] = obj.getString("name");
            }
            
            return genres;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private String getCertification(JSONObject detailedMovieJsonObject) {
        try {
            JSONObject releaseDatesJsonObject = detailedMovieJsonObject.getJSONObject("release_dates");
            JSONArray resultsJsonArray = releaseDatesJsonObject.getJSONArray("results");
            for (int i = 0; i < resultsJsonArray.length(); i++) {
                JSONObject arrJsonObject = resultsJsonArray.getJSONObject(i);
                if (arrJsonObject.getString("iso_3166_1").equals("US")) {
                    JSONArray rdJsonArray = arrJsonObject.getJSONArray("release_dates");
                    JSONObject obj = rdJsonArray.getJSONObject(0);
                    return obj.getString("certification");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
