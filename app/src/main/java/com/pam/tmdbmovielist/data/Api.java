package com.pam.tmdbmovielist.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "https://api.themoviedb.org/3/";
    String apiKey = "?api_key=11512374632a84036dd7e6cada9ae4cd";
    String language = "&language=en";
    String append = "&append_to_response=";
    
    @GET("movie/{list_type}" + apiKey + language)
    Call<String> getBaseMovieList(@Path("list_type") String listType, @Query("page") int page);
    
    @GET("movie/{movie_id}" + apiKey + language + append + "release_dates")
    Call<String> getBaseMovie(@Path("movie_id") int id);
    
    @GET("movie/{movie_id}" + apiKey + language + append + "credits,images")
    Call<String> getDetailedMovie(@Path("movie_id") int id);
    
    @GET("person/{person_id}" + apiKey + language + append + "movie_credits,tv_credits,external_ids,images")
    Call<String> getPersonDetail(@Path("person_id") int id);
    
    @GET("search/movie" + apiKey + language + "&page=1")
    Call<String> getSearchResult(@Query("query") String query);
}