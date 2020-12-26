package com.pam.tmdbmovielist.adapters.credits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.model.credits.Credit;
import com.pam.tmdbmovielist.model.credits.MediaList;

import java.util.ArrayList;
import java.util.TreeMap;

public class YearGroupAdapter extends RecyclerView.Adapter<YearGroupAdapter.YearGroupViewHolder> {
    
    private final TreeMap<Integer, ArrayList<Credit>> creditTreeMap;
    private final MediaList mediaList;
    private final ArrayList<Integer> keys = new ArrayList<>();
    private final OnMovieClickCallback onMovieClickCallback;
    
    public YearGroupAdapter(TreeMap<Integer, ArrayList<Credit>> creditTreeMap, MediaList mediaList, OnMovieClickCallback onMovieClickCallback) {
        this.creditTreeMap = creditTreeMap;
        this.mediaList = mediaList;
        this.onMovieClickCallback = onMovieClickCallback;
        keys.addAll(creditTreeMap.keySet());
    }
    
    @NonNull
    @Override
    public YearGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_credit_year_group, parent, false);
        return new YearGroupViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull YearGroupViewHolder holder, int position) {
        int year = keys.get(position);
        
        holder.rvYearGroup.setHasFixedSize(true);
        holder.rvYearGroup.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        
        CreditAdapter creditAdapter = new CreditAdapter(creditTreeMap.get(year), mediaList, onMovieClickCallback);
        holder.rvYearGroup.setAdapter(creditAdapter);
    }
    
    @Override
    public int getItemCount() {
        return creditTreeMap.size();
    }
    
    public static class YearGroupViewHolder extends RecyclerView.ViewHolder {
        
        RecyclerView rvYearGroup;
        
        public YearGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            rvYearGroup = itemView.findViewById(R.id.rv_year_group);
        }
    }
}
