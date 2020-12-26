package com.pam.tmdbmovielist.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;

public class NoDataViewHolder extends RecyclerView.ViewHolder {
    
    public TextView tvNoData;
    
    public NoDataViewHolder(@NonNull View itemView) {
        super(itemView);
        tvNoData = itemView.findViewById(R.id.tv_no_data);
    }
}
