package com.pam.tmdbmovielist.adapters.moviedetail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pam.tmdbmovielist.ui.fragments.FullscreenPosterFragment;

public class FullscreenPosterPagerAdapter extends FragmentStatePagerAdapter {
    
    private final String[] posterUrls;
    
    public FullscreenPosterPagerAdapter(Fragment fragment, String[] posterUrls) {
        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.posterUrls = posterUrls;
    }
    
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FullscreenPosterFragment.newInstance(posterUrls[position]);
    }
    
    @Override
    public int getCount() {
        return posterUrls.length;
    }
}
