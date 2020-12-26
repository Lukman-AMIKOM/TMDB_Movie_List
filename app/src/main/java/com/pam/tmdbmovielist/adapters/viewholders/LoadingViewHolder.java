package com.pam.tmdbmovielist.adapters.viewholders;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    
    public ProgressBar progressBar;
    
    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.pb_item_loading);
    }
}
