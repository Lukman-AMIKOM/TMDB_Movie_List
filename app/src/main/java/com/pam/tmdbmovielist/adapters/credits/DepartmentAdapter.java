package com.pam.tmdbmovielist.adapters.credits;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.model.credits.Credit;
import com.pam.tmdbmovielist.model.credits.MediaList;

import java.util.ArrayList;
import java.util.TreeMap;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {
    
    private final ArrayList<String> departments;
    private final ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap;
    private final MediaList mediaList;
    private final OnMovieClickCallback onMovieClickCallback;
    
    public DepartmentAdapter(ArrayList<String> departments,
                             ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap,
                             MediaList mediaList, OnMovieClickCallback onMovieClickCallback) {
        this.departments = departments;
        this.creditMap = creditMap;
        this.mediaList = mediaList;
        this.onMovieClickCallback = onMovieClickCallback;
    }
    
    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_credit_department, parent, false);
        return new DepartmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        TreeMap<Integer, ArrayList<Credit>> creditTreeMap = creditMap.get(departments.get(position));
        
        holder.tvDepartment.setText(departments.get(position));
        
        holder.rvFilmographyItems.setHasFixedSize(true);
        holder.rvFilmographyItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        
        if (creditTreeMap != null) {
            YearGroupAdapter yearGroupAdapter = new YearGroupAdapter(creditTreeMap, mediaList, onMovieClickCallback);
            holder.rvFilmographyItems.setAdapter(yearGroupAdapter);
        }
    }
    
    @Override
    public int getItemCount() {
        return departments.size();
    }
    
    public static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        
        TextView tvDepartment;
        RecyclerView rvFilmographyItems;
        
        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            rvFilmographyItems = itemView.findViewById(R.id.rv_filmography_items);
        }
    }
}
