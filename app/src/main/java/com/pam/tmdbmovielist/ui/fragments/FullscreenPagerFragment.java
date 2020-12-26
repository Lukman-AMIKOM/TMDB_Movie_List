package com.pam.tmdbmovielist.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.moviedetail.FullscreenPosterPagerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FullscreenPagerFragment extends Fragment {
    
    private ViewPager viewPager;
    private final MovieDetailFragment movieDetailFragment;
    
    public FullscreenPagerFragment(MovieDetailFragment movieDetailFragment) {
        this.movieDetailFragment = movieDetailFragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_fullscreen_pager, container, false);
        viewPager.setAdapter(new FullscreenPosterPagerAdapter(this, movieDetailFragment.getPosterUrls()));
        viewPager.setCurrentItem(movieDetailFragment.getCurrentPosition());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                movieDetailFragment.setCurrentPosition(position);
            }
        });
        
        prepareSharedElementTransition();
        
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        
        return viewPager;
    }
    
    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);
        
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                Fragment currentFragment = (Fragment) Objects.requireNonNull(viewPager.getAdapter()).instantiateItem(viewPager, movieDetailFragment.getCurrentPosition());
                View view = currentFragment.getView();
                if (view == null) {
                    return;
                }
                
                sharedElements.put(names.get(0), view.findViewById(R.id.img_fullscreen_poster));
            }
        });
    }
}