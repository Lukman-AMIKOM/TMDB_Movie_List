package com.pam.tmdbmovielist.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.viewholders.MovieViewHolder;
import com.pam.tmdbmovielist.interfaces.OnFavoriteClickCallback;
import com.pam.tmdbmovielist.model.Movie;

public class MovieDataSetter {
    
    private final Movie movie;
    private final int index;
    private final OnFavoriteClickCallback onFavoriteClickCallback;
    
    private final MovieViewHolder holder;
    private final Context context;
    private final Drawable imgFavoriteDefault;
    private final Drawable imgFavoriteSelected;
    
    public MovieDataSetter(Movie movie, int index, MovieViewHolder holder, OnFavoriteClickCallback onFavoriteClickCallback) {
        this.movie = movie;
        this.index = index;
        this.holder = holder;
        this.onFavoriteClickCallback = onFavoriteClickCallback;
        context = holder.itemView.getContext();
        
        String viewMode = holder.getViewMode();
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        
        if (viewMode.equals(MovieAdapter.MODE_LIST)) {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                imgFavoriteDefault = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_default, null);
            } else {
                imgFavoriteDefault = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_default_white, null);
            }
        } else {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                imgFavoriteDefault = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_default, null);
            } else {
                imgFavoriteDefault = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_default_white, null);
            }
        }
        
        imgFavoriteSelected = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_selected, null);
        
        setData(viewMode);
    }
    
    private void setData(String viewMode) {
        holder.progressBar.setVisibility(View.VISIBLE);
        if (movie.getPosterPath() == null || movie.getPosterPath().equals("null")) {
            holder.imgPoster.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_movie));
            holder.progressBar.setVisibility(View.GONE);
        } else {
            Glide.with(context)
                    .load(movie.getPosterPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.imgPoster.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_broken_image));
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                        
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().override(300, 450))
                    .into(holder.imgPoster);
        }
        setText(holder.tvCertification, movie.getCertification());
        holder.tvRuntime.setText(movie.getRuntimeString());
        holder.tvRating.setText(String.valueOf(movie.getRating()));
        setFavorite(movie.isFavorite() ? imgFavoriteSelected : imgFavoriteDefault, movie.isFavorite());
        holder.imgFavorite.setOnClickListener(v -> {
            setFavorite(!movie.isFavorite() ? imgFavoriteSelected : imgFavoriteDefault, !movie.isFavorite());
            onFavoriteClickCallback.onFavoriteClicked(movie);
        });
        
        if (viewMode.equals(MovieAdapter.MODE_LIST)) {
            holder.tvIndex.setText(String.valueOf(index));
            holder.tvTitle.setText(String.format("%s %s", movie.getTitle(), getValidYear(movie.getYear())));
            setText(holder.tvGenre, movie.getGenre());
            holder.tvOverview.setText(movie.getOverview());
        } else {
            holder.tvTitle.setText(movie.getTitle());
            holder.tvYear.setText(String.valueOf(movie.getYear()));
        }
    }
    
    private void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }
    
    private String getValidYear(int year) {
        return year == Short.MAX_VALUE ? "" : "(" + year + ")";
    }
    
    private void setFavorite(Drawable drawable) {
        setFavorite(drawable, false);
    }
    
    private void setFavorite(Drawable drawable, boolean isFavorite) {
        holder.imgFavorite.setImageDrawable(drawable);
        movie.setFavorite(isFavorite);
    }
}
