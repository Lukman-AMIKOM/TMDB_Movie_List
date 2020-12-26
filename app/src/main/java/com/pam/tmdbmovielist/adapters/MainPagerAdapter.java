package com.pam.tmdbmovielist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pam.tmdbmovielist.ui.fragments.ListFragment;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentStateAdapter {
    
    private final ArrayList<ListFragment> pages;
    
    public MainPagerAdapter(@NonNull Fragment fragment, ArrayList<ListFragment> pages) {
        super(fragment);
        this.pages = pages;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pages.get(position);
    }
    
    @Override
    public int getItemCount() {
        return pages.size();
    }
}
