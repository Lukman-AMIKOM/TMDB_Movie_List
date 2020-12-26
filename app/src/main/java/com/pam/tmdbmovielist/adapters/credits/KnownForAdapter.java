package com.pam.tmdbmovielist.adapters.credits;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.data.MovieList;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.model.Media;
import com.pam.tmdbmovielist.model.Movie;

import java.util.ArrayList;

public class KnownForAdapter extends RecyclerView.Adapter<KnownForAdapter.KnownForViewHolder> {
    
    private final ArrayList<Media> knownForList;
    private final OnMovieClickCallback onMovieClickCallback;
    
    public KnownForAdapter(ArrayList<Media> knownForList, OnMovieClickCallback onMovieClickCallback) {
        this.knownForList = knownForList;
        this.onMovieClickCallback = onMovieClickCallback;
    }
    
    @NonNull
    @Override
    public KnownForViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_known_for, parent, false);
        return new KnownForViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull KnownForViewHolder holder, int position) {
        Media media = knownForList.get(position);
        
        holder.progressBar.setVisibility(View.VISIBLE);
        if (media.getPosterPath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load("https://themoviedb.org/t/p/w300" + media.getPosterPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imgPoster.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_broken_image));
                            return false;
                        }
                    
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_movie));
            holder.progressBar.setVisibility(View.GONE);
        }
        
        holder.tvTitle.setText(media.getTitle());
        holder.itemView.setOnClickListener(v -> MovieList.getInstance().getBaseMovie(new Movie(media.getId(), media.getTitle()), onMovieClickCallback));
    }
    
    @Override
    public int getItemCount() {
        return knownForList.size();
    }
    
    public static class KnownForViewHolder extends RecyclerView.ViewHolder {
        
        ShapeableImageView imgPoster;
        ProgressBar progressBar;
        TextView tvTitle;
        
        public KnownForViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_poster);
            progressBar = itemView.findViewById(R.id.pb_known_for);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
