package com.pam.tmdbmovielist.ui.fragments;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.adapters.moviedetail.CastAdapter;
import com.pam.tmdbmovielist.adapters.moviedetail.CrewListAdapter;
import com.pam.tmdbmovielist.adapters.moviedetail.DetailListAdapter;
import com.pam.tmdbmovielist.adapters.moviedetail.PosterGridAdapter;
import com.pam.tmdbmovielist.data.RetrofitClient;
import com.pam.tmdbmovielist.interfaces.OnFavoriteClickCallback;
import com.pam.tmdbmovielist.interfaces.OnPersonClickCallback;
import com.pam.tmdbmovielist.model.Cast;
import com.pam.tmdbmovielist.model.Crew;
import com.pam.tmdbmovielist.model.Languages;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.model.Person;
import com.pam.tmdbmovielist.ui.activities.MainActivity;
import com.pam.tmdbmovielist.ui.custom.CastSnapHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class MovieDetailFragment extends Fragment implements OnPersonClickCallback {
    
    public static final String ARG_MOVIE = "movie";
    
    private Movie movie;
    private JSONObject responseJsonObject;
    
    private MainActivity mainActivity;
    
    private AppBarLayout appBarLayout;
    private TextView tvTitle, tvReleaseDate, tvCertification, tvRuntime, tvRating,
            tvGenre, tvTagline, tvOverview, tvCastTitle;
    private CircleImageView imgFavorite;
    private ShapeableImageView imgPoster;
    private RecyclerView rvCast, rvCrew, rvOther, rvPosters;
    private CastAdapter castAdapter;
    private CrewListAdapter crewAdapter;
    private DetailListAdapter otherAdapter;
    
    private Drawable imgFavoriteDefault, imgFavoriteSelected;
    
    private OnFavoriteClickCallback onFavoriteClickCallback;
    
    private String[] posterUrls;
    private int currentPosition = 0;
    
    private boolean isExpanded = true;
    private boolean isReturning = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }
        
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);
        setMainInfo();
        
        if (responseJsonObject != null) {
            reloadData();
        }
        
        prepareTransitions();
        postponeEnterTransition();
        
        scrollToPosition();
        
        appBarLayout.setExpanded(isExpanded);
    }
    
    private void init(View view) {
        appBarLayout = view.findViewById(R.id.app_bar_layout_detail);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int expanded = Math.abs(verticalOffset);
            int height = appBarLayout.getHeight();
            isExpanded = expanded < height;
        });
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_movie_detail);
        
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> mainActivity.onBackPressed());
            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        
        tvTitle = view.findViewById(R.id.tv_title);
        tvReleaseDate = view.findViewById(R.id.tv_release_date);
        tvCertification = view.findViewById(R.id.tv_certification);
        tvRuntime = view.findViewById(R.id.tv_runtime);
        tvRating = view.findViewById(R.id.tv_rating);
        tvGenre = view.findViewById(R.id.tv_genre);
        tvTagline = view.findViewById(R.id.tv_tagline);
        tvOverview = view.findViewById(R.id.tv_overview);
        tvCastTitle = view.findViewById(R.id.tv_cast_title);
        imgPoster = view.findViewById(R.id.img_poster);
        imgFavorite = view.findViewById(R.id.img_favorite);
        initRecyclerView(rvCast = view.findViewById(R.id.rv_cast), new GridLayoutManager(getContext(),
                1, GridLayoutManager.HORIZONTAL, false));
        initRecyclerView(rvCrew = view.findViewById(R.id.rv_crew), new LinearLayoutManager(getContext()));
        initRecyclerView(rvOther = view.findViewById(R.id.rv_other), new LinearLayoutManager(getContext()));
        initRecyclerView(rvPosters = view.findViewById(R.id.rv_posters), new GridLayoutManager(getContext(),
                1, GridLayoutManager.HORIZONTAL, false));
        
        ArrayList<Cast> emptyCast = new ArrayList<>(1);
        emptyCast.add(new Cast(-1, "", "null", ""));
        rvCast.setAdapter(new CastAdapter(emptyCast));
        rvCrew.setAdapter(new DetailListAdapter(new ArrayList<>(), new ArrayList<>()));
        rvOther.setAdapter(new DetailListAdapter(new ArrayList<>(), new ArrayList<>()));
        rvPosters.setAdapter(new DetailListAdapter(new ArrayList<>(), new ArrayList<>()));
        
        CastSnapHelper castSnapHelper = new CastSnapHelper();
        castSnapHelper.attachToRecyclerView(rvCast);
        
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvPosters);
    }
    
    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    
    private void setMainInfo() {
        tvTitle.setText(movie.getTitle());
        setText(tvReleaseDate, movie.getFormattedReleaseDate());
        setText(tvCertification, movie.getCertification());
        setText(tvRuntime, movie.getRuntimeString());
        tvRating.setText(String.valueOf(movie.getRating()));
        setText(tvGenre, movie.getGenre());
        tvOverview.setText(movie.getOverview());
        Glide.with(Objects.requireNonNull(getContext()))
                .load(movie.getPosterPath())
                .apply(new RequestOptions().override(300, 450))
                .into(imgPoster);
        
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            imgFavoriteDefault = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_favorite_default, null);
        } else {
            imgFavoriteDefault = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_favorite_default_orange, null);
        }
        imgFavoriteSelected = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_favorite_selected, null);
        
        setFavorite(movie.isFavorite() ? imgFavoriteSelected : imgFavoriteDefault);
        imgFavorite.setOnClickListener(v -> {
            setFavorite(!movie.isFavorite() ? imgFavoriteSelected : imgFavoriteDefault, !movie.isFavorite() ? 1 : 0);
            onFavoriteClickCallback.onFavoriteClicked(movie);
        });
    }
    
    private void setData() {
        try {
            setText(tvTagline, responseJsonObject.getString("tagline"));
            
            JSONObject credits = responseJsonObject.getJSONObject("credits");
            JSONArray cast = credits.getJSONArray("cast");
            JSONArray crew = credits.getJSONArray("crew");
            
            setCast(cast);
            setCrew(crew);
            setDetailInfo();
            setPosters();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private void reloadData() {
        try {
            setText(tvTagline, responseJsonObject.getString("tagline"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (castAdapter == null || castAdapter.getItemCount() == 0) {
            tvCastTitle.setVisibility(View.GONE);
        }
        rvCast.setAdapter(castAdapter);
        rvCrew.setAdapter(crewAdapter);
        rvOther.setAdapter(otherAdapter);
        setPosters();
        if (isReturning) {
            requestPosterGridFocus();
        }
    }
    
    private void setCast(JSONArray cast) {
        int castCount = Math.min(cast.length(), 10);
        
        if (castCount > 0) {
            ArrayList<Cast> casts = new ArrayList<>();
            for (int i = 0; i < castCount; i++) {
                try {
                    JSONObject castJsonObject = cast.getJSONObject(i);
                    casts.add(new Cast(
                            castJsonObject.getInt("id"),
                            castJsonObject.getString("name"),
                            castJsonObject.getString("profile_path"),
                            castJsonObject.getString("character")
                    ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            castAdapter = new CastAdapter(casts);
            castAdapter.setOnPersonClickCallback(this);
            rvCast.setAdapter(castAdapter);
        } else {
            tvCastTitle.setVisibility(View.GONE);
        }
    }
    
    private void setCrew(JSONArray crew) {
        ArrayList<Crew> directorList = new ArrayList<>(1);
        ArrayList<Crew> writerList = new ArrayList<>(1);
        ArrayList<Crew> novelList = new ArrayList<>(1);
        ArrayList<Crew> screenplayList = new ArrayList<>(1);
        for (int i = 0; i < crew.length(); i++) {
            try {
                JSONObject crewJsonObject = crew.getJSONObject(i);
                Crew mCrew = new Crew(
                        crewJsonObject.getInt("id"),
                        crewJsonObject.getString("name"),
                        crewJsonObject.getString("profile_path"),
                        crewJsonObject.getString("job")
                );
                
                String job = mCrew.getJob();
                if (job.equalsIgnoreCase("Director")) {
                    directorList.add(mCrew);
                }
                if (job.equalsIgnoreCase("Writer")) {
                    writerList.add(mCrew);
                }
                if (job.equalsIgnoreCase("Novel")) {
                    novelList.add(mCrew);
                }
                if (job.equalsIgnoreCase("Screenplay")) {
                    screenplayList.add(mCrew);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        crewAdapter = getCrewListAdapter(directorList, writerList, novelList, screenplayList);
        crewAdapter.setOnPersonClickCallback(this);
        rvCrew.setAdapter(crewAdapter);
    }
    
    private void setDetailInfo() {
        try {
            double dBudget = responseJsonObject.getDouble("budget");
            double dBoxOffice = responseJsonObject.getDouble("revenue");
            StringBuilder status = new StringBuilder(responseJsonObject.getString("status"));
            StringBuilder originalLanguage = Languages.getOriginalLanguage(
                    Objects.requireNonNull(getContext()),
                    responseJsonObject.getString("original_language"));
            StringBuilder budget = getMoney(dBudget);
            StringBuilder boxOffice = getMoney(dBoxOffice);
            
            String[] otherTitles = {
                    getString(R.string.status),
                    getString(R.string.original_language),
                    getString(R.string.budget),
                    getString(R.string.box_office)
            };
            StringBuilder[] otherContents = {status, originalLanguage, budget, boxOffice};
            otherAdapter = getDetailListAdapter(otherTitles, otherContents);
            rvOther.setAdapter(otherAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private void setPosters() {
        try {
            JSONObject imagesJsonObject = responseJsonObject.getJSONObject("images");
            JSONArray postersJsonArray = imagesJsonObject.getJSONArray("posters");
            posterUrls = new String[postersJsonArray.length()];
            for (int i = 0; i < postersJsonArray.length(); i++) {
                JSONObject obj = postersJsonArray.getJSONObject(i);
                posterUrls[i] = "https://image.tmdb.org/t/p/w500" + obj.getString("file_path");
            }
    
            PosterGridAdapter posterGridAdapter = new PosterGridAdapter(this);
            rvPosters.setAdapter(posterGridAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private void setFavorite(Drawable drawable) {
        setFavorite(drawable, -1);
    }
    
    private void setFavorite(Drawable drawable, int isFavorite) {
        imgFavorite.setImageDrawable(drawable);
        if (isFavorite != -1) {
            movie.setFavorite(isFavorite);
        }
    }
    
    private void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
        }
    }
    
    private CrewListAdapter getCrewListAdapter(ArrayList<Crew> directorList,
                                               ArrayList<Crew> writerList,
                                               ArrayList<Crew> novelList,
                                               ArrayList<Crew> screenplayList) {
        ArrayList<String> crewTitles = new ArrayList<>();
        ArrayList<ArrayList<Crew>> crewList = new ArrayList<>();
        
        addCrewList(crewTitles, getString(R.string.director), crewList, directorList);
        addCrewList(crewTitles, getString(R.string.writer_s), crewList, writerList);
        addCrewList(crewTitles, getString(R.string.novel), crewList, novelList);
        addCrewList(crewTitles, getString(R.string.screenplay), crewList, screenplayList);
        
        return new CrewListAdapter(crewTitles, crewList);
    }
    
    private void addCrewList(ArrayList<String> crewTitles, String crewTitle,
                             ArrayList<ArrayList<Crew>> crewList, ArrayList<Crew> crews) {
        if (!crews.isEmpty()) {
            crewTitles.add(crewTitle);
            crewList.add(crews);
        }
    }
    
    private StringBuilder getMoney(double money) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.getDefault());
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00", symbols);
        
        if (money > 0) {
            return new StringBuilder().append("$").append(decimalFormat.format(money));
        } else {
            return new StringBuilder().append("-");
        }
    }
    
    private DetailListAdapter getDetailListAdapter(String[] detailTitles, StringBuilder[] detailContents) {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        
        for (int i = 0; i < detailTitles.length; i++) {
            if (detailContents[i].length() > 0) {
                titles.add(detailTitles[i]);
                contents.add(detailContents[i].toString());
            }
        }
        
        return new DetailListAdapter(titles, contents);
    }
    
    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.grid_exit_transition));
        
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                RecyclerView.ViewHolder selectedViewHolder = rvPosters.findViewHolderForAdapterPosition(currentPosition);
                if (selectedViewHolder == null) {
                    return;
                }
                
                sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.img_posters));
            }
        });
    }
    
    private void scrollToPosition() {
        rvPosters.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                rvPosters.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = rvPosters.getLayoutManager();
                View viewAtPosition = null;
                if (layoutManager != null) {
                    viewAtPosition = layoutManager.findViewByPosition(currentPosition);
                }
    
                if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
                    rvPosters.post(() -> {
                        if (layoutManager != null) {
                            layoutManager.scrollToPosition(currentPosition);
                        }
                    });
                }
            }
        });
    }
    
    private void requestPosterGridFocus() {
        rvPosters.requestFocus();
    }
    
    public void setResponseJsonObject(JSONObject responseJsonObject) {
        this.responseJsonObject = responseJsonObject;
        setData();
    }
    
    public void setOnFavoriteClickCallback(OnFavoriteClickCallback onFavoriteClickCallback) {
        this.onFavoriteClickCallback = onFavoriteClickCallback;
    }
    
    public void setReturning(boolean returning) {
        isReturning = returning;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    
    public String[] getPosterUrls() {
        return posterUrls;
    }
    
    @Override
    public void onPersonClicked(Person person) {
        Call<String> call = RetrofitClient.getInstance().getMyApi().getPersonDetail(person.getId());
        
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull retrofit2.Response<String> response) {
                try {
                    if (response.body() != null) {
                        JSONObject responseJsonObject = new JSONObject(response.body());
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        PersonDetailFragment personDetailFragment =
                                (PersonDetailFragment) fragmentManager.findFragmentByTag(
                                        PersonDetailFragment.class.getSimpleName() + ":" + person.getId());
    
                        if (personDetailFragment != null) {
                            personDetailFragment.setPersonDetail(responseJsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
            }
        });
        
        showPersonDetail(person);
    }
    
    private void showPersonDetail(Person person) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        PersonDetailFragment personDetailFragment = new PersonDetailFragment();
        
        Bundle args = new Bundle();
        args.putParcelable(PersonDetailFragment.ARG_PERSON, person);
        personDetailFragment.setArguments(args);
        
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, personDetailFragment, PersonDetailFragment.class.getSimpleName() + ":" + person.getId())
                .addToBackStack(null)
                .commit();
    }
}