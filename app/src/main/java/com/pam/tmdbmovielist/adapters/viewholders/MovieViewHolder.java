package com.pam.tmdbmovielist.adapters.viewholders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.MovieAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    
    private final String viewMode;
    
    public ShapeableImageView imgPoster;
    public ProgressBar progressBar;
    public TextView tvIndex, tvTitle, tvYear, tvCertification,
            tvGenre, tvRuntime, tvOverview, tvRating;
    public CircleImageView imgFavorite;
    
    public MovieViewHolder(@NonNull View itemView, String viewMode) {
        super(itemView);
        this.viewMode = viewMode;
        imgPoster = itemView.findViewById(R.id.img_poster);
        progressBar = itemView.findViewById(R.id.pb_item_poster);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvYear = itemView.findViewById(R.id.tv_release_date);
        tvCertification = itemView.findViewById(R.id.tv_certification);
        tvRuntime = itemView.findViewById(R.id.tv_runtime);
        tvRating = itemView.findViewById(R.id.tv_rating);
        imgFavorite = itemView.findViewById(R.id.img_favorite);
        if (viewMode.equals(MovieAdapter.MODE_LIST)) {
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvGenre = itemView.findViewById(R.id.tv_genre);
            tvOverview = itemView.findViewById(R.id.tv_overview);
        }
    }
    
    public String getViewMode() {
        return viewMode;
    }
}