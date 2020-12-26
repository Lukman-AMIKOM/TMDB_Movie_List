package com.pam.tmdbmovielist.adapters.moviedetail;

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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.interfaces.OnPersonClickCallback;
import com.pam.tmdbmovielist.model.Cast;

import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    
    private final ArrayList<Cast> castList;
    private OnPersonClickCallback onPersonClickCallback;
    
    public CastAdapter(ArrayList<Cast> castList) {
        this.castList = castList;
    }
    
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cast, parent, false);
        return new CastViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);
        
        if (!cast.getProfilePath().equals("null")) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load("https://themoviedb.org/t/p/w500" + cast.getProfilePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.imgCastPhoto.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_person));
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                        
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().override(200, 300))
                    .into(holder.imgCastPhoto);
        } else {
            holder.imgCastPhoto.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_person));
            holder.progressBar.setVisibility(View.GONE);
        }
        holder.tvCastName.setText(cast.getName());
        holder.tvCharacterName.setText(cast.getCharacterName());
        
        holder.itemView.setOnClickListener(v -> onPersonClickCallback.onPersonClicked(cast));
    }
    
    @Override
    public int getItemCount() {
        return castList.size();
    }
    
    public void setOnPersonClickCallback(OnPersonClickCallback onPersonClickCallback) {
        this.onPersonClickCallback = onPersonClickCallback;
    }
    
    public static class CastViewHolder extends RecyclerView.ViewHolder {
        
        ShapeableImageView imgCastPhoto;
        ProgressBar progressBar;
        TextView tvCastName, tvCharacterName;
        
        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCastPhoto = itemView.findViewById(R.id.img_cast_photo);
            progressBar = itemView.findViewById(R.id.pb_cast);
            tvCastName = itemView.findViewById(R.id.tv_cast_name);
            tvCharacterName = itemView.findViewById(R.id.tv_character);
        }
    }
}