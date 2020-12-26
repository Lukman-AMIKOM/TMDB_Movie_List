package com.pam.tmdbmovielist.adapters.credits;

import android.content.res.Configuration;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.data.MovieList;
import com.pam.tmdbmovielist.interfaces.OnMovieClickCallback;
import com.pam.tmdbmovielist.model.Media;
import com.pam.tmdbmovielist.model.Movie;
import com.pam.tmdbmovielist.model.credits.CastCredit;
import com.pam.tmdbmovielist.model.credits.Credit;
import com.pam.tmdbmovielist.model.credits.CrewCredit;
import com.pam.tmdbmovielist.model.credits.MediaList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CreditViewHolder> {
    
    private final ArrayList<Credit> creditList;
    private final MediaList mediaList;
    private final OnMovieClickCallback onMovieClickCallback;
    
    public CreditAdapter(ArrayList<Credit> creditList, MediaList mediaList, OnMovieClickCallback onMovieClickCallback) {
        this.creditList = creditList;
        this.mediaList = mediaList;
        this.onMovieClickCallback = onMovieClickCallback;
    
        Collections.sort(this.creditList, (credit1, credit2) -> {
            String strDate1 = mediaList.getMedia(credit1.getId()).getValidReleaseDate();
            String strDate2 = mediaList.getMedia(credit2.getId()).getValidReleaseDate();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date releaseDate1 = sdf.parse(strDate1);
                Date releaseDate2 = sdf.parse(strDate2);
    
                if (releaseDate1 != null) {
                    if (releaseDate1.before(releaseDate2)) {
                        return 1;
                    } else if (releaseDate1.after(releaseDate2)) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            return 0;
        });
    }
    
    @NonNull
    @Override
    public CreditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_credit_items, parent, false);
        return new CreditViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CreditViewHolder holder, int position) {
        Credit credit = creditList.get(position);
        Media media = mediaList.getMedia(credit.getId());
        
        String year = getValidYear(media.getYear());
        StringBuilder title = new StringBuilder();
        String gig;
        StringBuilder preposition;
        StringBuilder result = new StringBuilder();
        
        int nightModeFlags = holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        String titleColor, prepositionColor;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            titleColor = "<font color=\"#fa6800\">";
            prepositionColor = "<font color=\"#9c9c9c\">";
        } else {
            titleColor = "<font color=\"#404040\">";
            prepositionColor = "<font color=\"#707070\">";
        }
        
        title.append("<b>").append(titleColor).append(media.getTitle()).append("</font>").append("</b>");
        
        if (credit instanceof CastCredit) {
            CastCredit castCredit = (CastCredit) credit;
            gig = castCredit.getCharacter();
            preposition = new StringBuilder(" as ");
            
            if (credit.getMediaType().equals(Credit.MEDIA_TYPE_TV)) {
                int episodeCount = castCredit.getEpisodeCount();
                
                preposition.insert(0, " (" + episodeCount + " " + getEpisodePostFix(episodeCount) + ")");
            }
        } else {
            CrewCredit crewCredit = (CrewCredit) credit;
            gig = crewCredit.getJob();
            preposition = new StringBuilder(" ... ");
        }
        preposition.insert(0, prepositionColor).append("</font>");
        
        result.append(title);
        if (gig != null && gig.length() > 0) {
            result.append(preposition).append(gig);
        }
        
        holder.tvYear.setText(year);
        if (position > 0 && !year.equals("\u2014")) {
            holder.tvYear.setVisibility(View.INVISIBLE);
        }
        holder.tvCredit.setText(Html.fromHtml(result.toString()));
        holder.layoutCreditItem.setOnClickListener(v -> MovieList.getInstance().getBaseMovie(new Movie(media.getId(), media.getTitle()), onMovieClickCallback));
    }
    
    @Override
    public int getItemCount() {
        return creditList.size();
    }
    
    private String getValidYear(int year) {
        return year == Short.MAX_VALUE ? "\u2014" : String.valueOf(year);
    }
    
    private String getEpisodePostFix(int episodeCount) {
        return episodeCount > 1 ? "episodes" : "episode";
    }
    
    public static class CreditViewHolder extends RecyclerView.ViewHolder {
        
        ConstraintLayout layoutCreditItem;
        TextView tvYear, tvCredit;
        
        public CreditViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCreditItem = itemView.findViewById(R.id.layout_credit_item);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvCredit = itemView.findViewById(R.id.tv_credit);
        }
    }
}
