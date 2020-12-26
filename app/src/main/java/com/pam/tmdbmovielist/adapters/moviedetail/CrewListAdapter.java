package com.pam.tmdbmovielist.adapters.moviedetail;

import android.content.res.Configuration;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.interfaces.OnPersonClickCallback;
import com.pam.tmdbmovielist.model.Crew;
import com.pam.tmdbmovielist.ui.custom.LinkTouchMovementMethod;
import com.pam.tmdbmovielist.ui.custom.TouchableSpan;

import java.util.ArrayList;

public class CrewListAdapter extends RecyclerView.Adapter<CrewListAdapter.CrewViewHolder> {
    
    private final ArrayList<String> crewTitles;
    private final ArrayList<ArrayList<Crew>> crewList;
    private OnPersonClickCallback onPersonClickCallback;
    
    public CrewListAdapter(ArrayList<String> crewTitles, ArrayList<ArrayList<Crew>> crewList) {
        this.crewTitles = crewTitles;
        this.crewList = crewList;
    }
    
    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_row, parent, false);
        return new CrewViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        ArrayList<Crew> crews = crewList.get(position);
        if (!crews.isEmpty()) {
            holder.tvCrewTitle.setText(crewTitles.get(position));
            
            int nightModeFlags = holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            int colorId;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                colorId = Color.WHITE;
            } else {
                colorId = Color.parseColor("#404040");
            }
            
            int crewCount = crews.size();
            StringBuilder wholeString = new StringBuilder();
            ArrayList<String> clickableStrings = new ArrayList<>();
            ArrayList<TouchableSpan> clickableSpans = new ArrayList<>();
            for (int i = 0; i < crewCount; i++) {
                Crew crew = crews.get(i);
                
                wholeString.append(crew.getName());
                if (i < crewCount - 1) {
                    wholeString.append(", ");
                }
                
                clickableStrings.add(crew.getName());
                
                TouchableSpan clickableSpan = new TouchableSpan(colorId, Color.BLUE, Color.TRANSPARENT) {
                    @Override
                    public void onClick(@NonNull View widget) {
                        onPersonClickCallback.onPersonClicked(crew);
                    }
                };
                clickableSpans.add(clickableSpan);
            }
            
            setClickableString(wholeString.toString(), holder.tvCrewContent, clickableStrings, clickableSpans);
        }
    }
    
    @Override
    public int getItemCount() {
        return crewTitles.size();
    }
    
    public void setOnPersonClickCallback(OnPersonClickCallback onPersonClickCallback) {
        this.onPersonClickCallback = onPersonClickCallback;
    }
    
    private void setClickableString(String wholeString, TextView textView, ArrayList<String> clickableStrings, ArrayList<TouchableSpan> clickableSpans) {
        SpannableString spannableString = new SpannableString(wholeString);
        
        for (int i = 0; i < clickableStrings.size(); i++) {
            TouchableSpan clickableSpan = clickableSpans.get(i);
            String link = clickableStrings.get(i);
            
            int startIndexOfLink = wholeString.indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setMovementMethod(new LinkTouchMovementMethod());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }
    
    public static class CrewViewHolder extends RecyclerView.ViewHolder {
        
        TextView tvCrewTitle, tvCrewContent;
        
        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrewTitle = itemView.findViewById(R.id.tv_detail_title);
            tvCrewContent = itemView.findViewById(R.id.tv_detail_content);
        }
    }
}