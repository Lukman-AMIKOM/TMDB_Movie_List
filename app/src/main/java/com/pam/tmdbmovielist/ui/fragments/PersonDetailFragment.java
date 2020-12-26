package com.pam.tmdbmovielist.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.credits.DepartmentAdapter;
import com.pam.tmdbmovielist.adapters.moviedetail.DetailListAdapter;
import com.pam.tmdbmovielist.adapters.credits.KnownForAdapter;
import com.pam.tmdbmovielist.model.Media;
import com.pam.tmdbmovielist.model.Person;
import com.pam.tmdbmovielist.model.PersonDetail;
import com.pam.tmdbmovielist.model.credits.Credit;
import com.pam.tmdbmovielist.model.credits.CreditsManager;
import com.pam.tmdbmovielist.model.credits.ExternalIds;
import com.pam.tmdbmovielist.model.credits.MediaList;
import com.pam.tmdbmovielist.ui.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

public class PersonDetailFragment extends Fragment {
    
    public static final String ARG_PERSON = "person_detail";
    
    private static final String SOCIAL_TYPE_FACEBOOK = "facebook";
    private static final String SOCIAL_TYPE_TWITTER = "twitter";
    private static final String SOCIAL_TYPE_INSTAGRAM = "instagram";
    
    private MainFragment mainFragment;
    private Person person;
    private PersonDetail personDetail;
    
    private AppBarLayout appBarLayout;
    private boolean isExpanded = true;
    
    private TextView tvBiography;
    private TextView[] tvKnownFor;
    
    private ProgressBar pbProfile, pbBiography;
    private ShapeableImageView imgProfilePhoto;
    private ImageView imgFacebook, imgTwitter, imgInstagram;
    private Drawable iconFacebook, iconTwitter, iconInstagram;
    
    private RecyclerView rvKnownFor, rvPersonalInfo;
    private RecyclerView rvFilmography;
    private KnownForAdapter knownForAdapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            mainFragment = (MainFragment) fragmentManager.findFragmentByTag(MainFragment.class.getSimpleName());
        }
        
        if (getArguments() != null) {
            person = getArguments().getParcelable(ARG_PERSON);
        }
        
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);
        
        if (personDetail != null) {
            setData();
        }
        
        appBarLayout.setExpanded(isExpanded);
    }
    
    private void init(View view) {
        appBarLayout = view.findViewById(R.id.app_bar_layout_person_detail);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_person_detail);
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
    
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvKnownFor1 = view.findViewById(R.id.tv_known_for_1);
        TextView tvKnownFor2 = view.findViewById(R.id.tv_known_for_2);
        TextView tvKnownFor3 = view.findViewById(R.id.tv_known_for_3);
        tvKnownFor = new TextView[3];
        tvKnownFor[0] = tvKnownFor1;
        tvKnownFor[1] = tvKnownFor2;
        tvKnownFor[2] = tvKnownFor3;
        
        initProgressBars(pbProfile = view.findViewById(R.id.pb_profile));
        initProgressBars(pbBiography = view.findViewById(R.id.pb_biography));
        imgProfilePhoto = view.findViewById(R.id.img_profile_photo);
        tvBiography = view.findViewById(R.id.tv_biography);
        imgFacebook = view.findViewById(R.id.img_facebook_icon);
        imgTwitter = view.findViewById(R.id.img_twitter_icon);
        imgInstagram = view.findViewById(R.id.img_instagram_icon);
        iconFacebook = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_facebook_square);
        iconTwitter = ContextCompat.getDrawable(getContext(), R.drawable.ic_twitter_square);
        iconInstagram = ContextCompat.getDrawable(getContext(), R.drawable.ic_instagram_square);
        tvBiography.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                String biography = tvBiography.getText().toString().trim();
                
                if (!TextUtils.isEmpty(biography)) {
                    pbBiography.setVisibility(View.GONE);
                }
            }
        });
        
        initRecyclerView(rvKnownFor = view.findViewById(R.id.rv_known_for),
                new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        initRecyclerView(rvPersonalInfo = view.findViewById(R.id.rv_personal_info), new LinearLayoutManager(getContext()));
        initRecyclerView(rvFilmography = view.findViewById(R.id.rv_filmography), new LinearLayoutManager(getContext()));
        ArrayList<Media> emptyMediaList = new ArrayList<>(1);
        emptyMediaList.add(new Media());
    
        knownForAdapter = new KnownForAdapter(emptyMediaList, mainFragment);
        rvKnownFor.setAdapter(knownForAdapter);
        
        tvName.setText(person.getName());
        Glide.with(getContext())
                .load("https://themoviedb.org/t/p/w300" + person.getProfilePath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        pbProfile.setVisibility(View.GONE);
                        imgProfilePhoto.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_person));
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        pbProfile.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imgProfilePhoto);
    }
    
    private void setData() {
        new Handler(Looper.getMainLooper()).post(() -> {
            setKnownFors();
            setBiography();
            setKnownForList();
            setPersonalInfo();
        });
        new Handler(Looper.getMainLooper()).postDelayed(this::setFilmography, 400);
    }
    
    private void initProgressBars(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }
    
    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    
    private void setImageIcon(String id, ImageView imageView, String type) {
        Intent intent = null;
        if (id != null) {
            switch (type) {
                case SOCIAL_TYPE_FACEBOOK:
                    imageView.setImageDrawable(iconFacebook);
                    intent = getFacebookIntent(Objects.requireNonNull(getContext()).getApplicationContext(), id);
                    break;
                case SOCIAL_TYPE_TWITTER:
                    imageView.setImageDrawable(iconTwitter);
                    intent = getTwitterIntent(Objects.requireNonNull(getContext()).getApplicationContext(), id);
                    break;
                case SOCIAL_TYPE_INSTAGRAM:
                    imageView.setImageDrawable(iconInstagram);
                    intent = getInstagramIntent(Objects.requireNonNull(getContext()).getApplicationContext(), id);
                    break;
            }
            
            Intent finalIntent = intent;
            imageView.setOnClickListener(v -> startActivity(finalIntent));
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
    
    private Intent getFacebookIntent(Context context, String facebookId) {
        String url = "https://www.facebook.com/" + facebookId;
        String uri;
        PackageManager packageManager = context.getPackageManager();
        
        try {
            long versionCode;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).getLongVersionCode();
            } else {
                versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            }
            
            if (versionCode >= 3002850) {
                uri = "fb://facewebmodal/f?href=" + url;
            } else {
                uri = "fb://page/" + facebookId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            uri = url;
        }
        
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }
    
    private Intent getTwitterIntent(Context context, String twitterId) {
        String uri;
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            uri = "twitter://user?screen_name=" + twitterId;
        } catch (PackageManager.NameNotFoundException e) {
            uri = "https://twitter.com/" + twitterId;
        }
        
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }
    
    private Intent getInstagramIntent(Context context, String instagramId) {
        String uri;
        try {
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            uri = "http://instagram.com/_u/" + instagramId;
        } catch (PackageManager.NameNotFoundException e) {
            uri = "http://instagram.com/" + instagramId;
        }
        
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }
    
    private void setKnownFors() {
        String[] knownForJobs = personDetail.getKnownForJobs();
        for (int i = 0; i < tvKnownFor.length; i++) {
            if (i < knownForJobs.length) {
                if (knownForJobs[i] != null) {
                    tvKnownFor[i].setText(knownForJobs[i]);
                    tvKnownFor[i].setVisibility(View.VISIBLE);
                } else {
                    tvKnownFor[i].setVisibility(View.GONE);
                }
            } else {
                tvKnownFor[i].setVisibility(View.GONE);
            }
        }
    }
    
    private void setBiography() {
        if (personDetail.getBiography() == null || personDetail.getBiography().length() == 0) {
            tvBiography.setText(getString(R.string.no_biography, personDetail.getName()));
        } else {
            tvBiography.setText(personDetail.getBiography());
        }
        ExternalIds externalIds = personDetail.getExternalIds();
        setImageIcon(externalIds.getFacebookId(), imgFacebook, SOCIAL_TYPE_FACEBOOK);
        setImageIcon(externalIds.getTwitterId(), imgTwitter, SOCIAL_TYPE_TWITTER);
        setImageIcon(externalIds.getInstagramId(), imgInstagram, SOCIAL_TYPE_INSTAGRAM);
    }
    
    private void setKnownForList() {
        ArrayList<Media> knownForList = personDetail.getKnownForMediaList();
        knownForAdapter = new KnownForAdapter(knownForList, mainFragment);
        rvKnownFor.setAdapter(knownForAdapter);
    }
    
    private void setPersonalInfo() {
        ArrayList<String> personalInfoTitles = new ArrayList<>();
        ArrayList<String> personalInfoContents = new ArrayList<>();
        String knownForTitle = getString(R.string.known_for_colon); String knownForDepartment = personDetail.getKnownForDepartment();
        String knownCreditsTitle = getString(R.string.known_credits); int knownCredits = personDetail.getKnownCredits();
        String genderTitle = getString(R.string.gender); String gender = personDetail.getGenderString();
        String birthdayTitle = getString(R.string.birthday); String birthday = personDetail.getBirthday();
        String placeOfBirthTitle = getString(R.string.place_of_birth); String placeOfBirth = personDetail.getPlaceOfBirth();
        String deathdayTitle = getString(R.string.day_of_death); String deathday = personDetail.getDeathday();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar today = Calendar.getInstance();
            Calendar birthdayCal = Calendar.getInstance();
            
            Date birthDate = sdf.parse(birthday);
            Date deathDate;
            int age;
            
            if (birthDate != null) {
                birthdayCal.setTime(birthDate);
                if (!deathday.equals("null")) {
                    deathDate = sdf.parse(deathday);
                    
                    if (deathDate != null) {
                        Calendar deathdayCal = Calendar.getInstance();
                        deathdayCal.setTime(deathDate);
                        
                        age = getAge(deathdayCal, birthdayCal);
                        deathday = deathday + " (" + age + " years old)";
                    }
                } else {
                    age = getAge(today, birthdayCal);
                    birthday = birthday + " (" + age + " years old)";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        personalInfoTitles.add(knownForTitle);
        personalInfoTitles.add(knownCreditsTitle);
        personalInfoTitles.add(genderTitle);
        personalInfoTitles.add(birthdayTitle);
        personalInfoTitles.add(placeOfBirthTitle);
        personalInfoContents.add(knownForDepartment);
        personalInfoContents.add(String.valueOf(knownCredits));
        personalInfoContents.add(gender);
        personalInfoContents.add(birthday);
        if (!deathday.equals("null")) {
            personalInfoTitles.add(4, deathdayTitle);
            personalInfoContents.add(deathday);
        }
        personalInfoContents.add(placeOfBirth);
        
        DetailListAdapter personalInfoAdapter = new DetailListAdapter(personalInfoTitles, personalInfoContents);
        rvPersonalInfo.setAdapter(personalInfoAdapter);
    }
    
    private void setFilmography() {
        DepartmentAdapter departmentAdapter = new DepartmentAdapter(
                personDetail.getDepartments(),
                personDetail.getCreditMap(),
                personDetail.getMediaList(),
                mainFragment);
        rvFilmography.setAdapter(departmentAdapter);
    }
    
    private int getAge(Calendar today, Calendar dob) {
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        
        return age;
    }
    
    private void setPersonDetail(PersonDetail personDetail) {
        this.personDetail = personDetail;
        setData();
    }
    
    public void setPersonDetail(JSONObject response) {
        Runnable runnable = () -> {
            try {
                String strBirthday = response.getString("birthday");
                String strDeathday = response.getString("deathday");
                String placeOfBirth = response.getString("place_of_birth");
                int gender = response.getInt("gender");
                String knownForDepartment = response.getString("known_for_department");
                String biography = response.getString("biography");
                
                JSONObject movie_credits = response.getJSONObject("movie_credits");
                JSONObject tv_credits = response.getJSONObject("tv_credits");
                
                JSONArray movie_credits_cast = movie_credits.getJSONArray("cast");
                JSONArray tv_credits_cast = tv_credits.getJSONArray("cast");
                JSONArray movie_credits_crew = movie_credits.getJSONArray("crew");
                JSONArray tv_credits_crew = tv_credits.getJSONArray("crew");
                
                ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap = new ArrayMap<>();
                MediaList mediaList = new MediaList();
                ArrayList<String> departments = new ArrayList<>();
                TreeMap<String, ArrayMap<String, Integer>> jobListMap = new TreeMap<>();
                
                CreditsManager creditsManager = new CreditsManager(creditMap, mediaList, departments, jobListMap);
                creditsManager.addCredits(movie_credits_cast, CreditsManager.CREDIT_TYPE_CAST, Credit.MEDIA_TYPE_MOVIE);
                creditsManager.addCredits(tv_credits_cast, CreditsManager.CREDIT_TYPE_CAST, Credit.MEDIA_TYPE_TV);
                creditsManager.addCredits(movie_credits_crew, CreditsManager.CREDIT_TYPE_CREW, Credit.MEDIA_TYPE_MOVIE);
                creditsManager.addCredits(tv_credits_crew, CreditsManager.CREDIT_TYPE_CREW, Credit.MEDIA_TYPE_TV);
                
                JSONObject externalIdsJsonObject = response.getJSONObject("external_ids");
                String facebookId = externalIdsJsonObject.getString("facebook_id");
                String instagramId = externalIdsJsonObject.getString("instagram_id");
                String twitterId = externalIdsJsonObject.getString("twitter_id");
                ExternalIds externalIds = new ExternalIds(facebookId, instagramId, twitterId);
                
                JSONArray profiles = response.getJSONObject("images").getJSONArray("profiles");
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < profiles.length(); i++) {
                    JSONObject imgJsonObject = profiles.getJSONObject(i);
                    String filePath = imgJsonObject.getString("file_path");
                    images.add(filePath);
                }
                PersonDetail personDetail = new PersonDetail(person.getId(),
                        person.getName(),
                        person.getProfilePath(),
                        strBirthday,
                        strDeathday,
                        placeOfBirth,
                        gender,
                        knownForDepartment,
                        biography,
                        creditMap,
                        departments,
                        jobListMap,
                        mediaList,
                        externalIds,
                        images);
                
                setPersonDetail(personDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}