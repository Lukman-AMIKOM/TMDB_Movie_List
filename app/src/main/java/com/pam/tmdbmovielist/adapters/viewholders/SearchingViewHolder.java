package com.pam.tmdbmovielist.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;

public class SearchingViewHolder extends RecyclerView.ViewHolder {
    
    public TextView tvSearching;
    
    public SearchingViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSearching = itemView.findViewById(R.id.tv_searching);
    }
}
