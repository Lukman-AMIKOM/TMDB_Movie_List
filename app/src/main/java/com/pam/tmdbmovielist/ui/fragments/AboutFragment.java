package com.pam.tmdbmovielist.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.ui.activities.MainActivity;

import java.util.Objects;

public class AboutFragment extends Fragment {
    
    private static final String GITHUB_LINK = "https://github.com/Lukman-AMIKOM/";
    
    private AppBarLayout appBarLayout;
    private boolean isExpanded = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    
        init(view);
    
        appBarLayout.setExpanded(isExpanded);
    }
    
    private void init(View view) {
        appBarLayout = view.findViewById(R.id.app_bar_layout_about);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_about);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int expanded = Math.abs(verticalOffset);
            int height = appBarLayout.getHeight();
            isExpanded = expanded < height;
        });
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> mainActivity.onBackPressed());
        
            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        
        CircularImageView imgProfilePhoto = view.findViewById(R.id.img_profile_photo);
        Glide.with(this)
                .load(R.drawable.img_profile_photo)
                .apply(new RequestOptions().override(330, 330))
                .into(imgProfilePhoto);
    
        ImageView imgGithubLogo = view.findViewById(R.id.img_github_logo);
        Glide.with(this)
                .load(R.drawable.github_logo_white)
                .apply(new RequestOptions().override(100, 100))
                .into(imgGithubLogo);
        imgGithubLogo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            builder.setMessage(getString(R.string.redirect_confirmation))
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
    }
    
    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Uri uri = Uri.parse(GITHUB_LINK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    };
}