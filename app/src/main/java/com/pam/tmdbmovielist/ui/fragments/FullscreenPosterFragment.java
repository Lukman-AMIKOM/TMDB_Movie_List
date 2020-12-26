package com.pam.tmdbmovielist.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pam.tmdbmovielist.R;

import java.util.Objects;

public class FullscreenPosterFragment extends Fragment {
    
    private static final String ARG_POSTER_URL = "poster_url";
    
    public static FullscreenPosterFragment newInstance(String url) {
        FullscreenPosterFragment fragment = new FullscreenPosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSTER_URL, url);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fullscreen_poster, container, false);
        
        String url = null;
        if (getArguments() != null) {
            url = getArguments().getString(ARG_POSTER_URL);
        }
        
        view.findViewById(R.id.img_fullscreen_poster).setTransitionName(url);
        
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (getParentFragment() != null) {
                            getParentFragment().startPostponedEnterTransition();
                        }
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Objects.requireNonNull(getParentFragment()).startPostponedEnterTransition();
                        return false;
                    }
                })
                .into((ImageView) view.findViewById(R.id.img_fullscreen_poster));
        
        return view;
    }
}