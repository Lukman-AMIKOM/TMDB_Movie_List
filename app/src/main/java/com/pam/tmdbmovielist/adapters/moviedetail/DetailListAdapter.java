package com.pam.tmdbmovielist.adapters.moviedetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;

import java.util.ArrayList;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.DetailListViewHolder> {
    
    private final ArrayList<String> detailTitles;
    private final ArrayList<String> detailContents;
    
    public DetailListAdapter(ArrayList<String> detailTitles, ArrayList<String> detailContents) {
        this.detailTitles = detailTitles;
        this.detailContents = detailContents;
    }
    
    @NonNull
    @Override
    public DetailListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_row_other, parent, false);
        return new DetailListViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DetailListViewHolder holder, int position) {
        holder.tvDetailTitle.setText(detailTitles.get(position));
        holder.tvDetailContent.setText(detailContents.get(position));
    }
    
    @Override
    public int getItemCount() {
        return detailTitles.size();
    }
    
    public static class DetailListViewHolder extends RecyclerView.ViewHolder {
        
        TextView tvDetailTitle, tvDetailContent;
        
        public DetailListViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDetailTitle = itemView.findViewById(R.id.tv_detail_title);
            tvDetailContent = itemView.findViewById(R.id.tv_detail_content);
        }
    }
}
