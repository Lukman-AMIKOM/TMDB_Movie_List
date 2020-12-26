package com.pam.tmdbmovielist.model.credits;

import android.util.ArrayMap;

import com.pam.tmdbmovielist.model.Media;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.TreeMap;

public class CreditsManager {
    
    public static final String CREDIT_TYPE_CAST = "credit_type_cast";
    public static final String CREDIT_TYPE_CREW = "credit_type_crew";
    
    private final ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap;
    private final ArrayList<String> departments;
    private final MediaList mediaList;
    private final TreeMap<String, ArrayMap<String, Integer>> jobListMap;
    
    public CreditsManager(ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap,
                          MediaList mediaList, ArrayList<String> departments,
                          TreeMap<String, ArrayMap<String, Integer>> jobListMap) {
        this.creditMap = creditMap;
        this.mediaList = mediaList;
        this.departments = departments;
        this.jobListMap = jobListMap;
    }
    
    private TreeMap<Integer, ArrayList<Credit>> getCreditTreeMap() {
        return new TreeMap<>((year1, year2) -> -year1.compareTo(year2));
    }
    
    public void addCredits(JSONArray credits, String creditType, String mediaType) {
        try {
            for (int i = 0; i < credits.length(); i++) {
                JSONObject creditJsonObject = credits.getJSONObject(i);
                
                Credit credit;
                Media media;
                String department;
                int episodeCount = 0;
                
                String creditId = creditJsonObject.getString("credit_id");
                int id = creditJsonObject.getInt("id");
                String gig;
                
                int year;
                if (!mediaList.contains(id)) {
                    String title = null;
                    String releaseDate;
                    String posterPath = creditJsonObject.getString("poster_path");
                    int voteCount = creditJsonObject.getInt("vote_count");
                    float rating = (float) creditJsonObject.getDouble("vote_average");
                    
                    try {
                        if (mediaType.equalsIgnoreCase(Credit.MEDIA_TYPE_MOVIE)) {
                            title = creditJsonObject.getString("title");
                            releaseDate = creditJsonObject.getString("release_date");
                        } else {
                            title = creditJsonObject.getString("name");
                            releaseDate = creditJsonObject.getString("first_air_date");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        releaseDate = null;
                    }
    
                    media = new Media(id, title, posterPath, releaseDate, null,
                            null, voteCount, rating, false);
                    year = media.getYear();
                    mediaList.add(media);
                } else {
                    year = mediaList.getMedia(id).getYear();
                }
                
                if (mediaType.equalsIgnoreCase(Credit.MEDIA_TYPE_TV)) {
                    try {
                        episodeCount = creditJsonObject.getInt("episode_count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        episodeCount = -1;
                    }
                }
                
                if (creditType.equalsIgnoreCase(CREDIT_TYPE_CAST)) {
                    department = "Acting";
                    gig = "Actor";
                    String character = creditJsonObject.getString("character");
                    credit = new CastCredit(creditId, id, mediaType, episodeCount, character);
                } else {
                    department = creditJsonObject.getString("department");
                    String job = gig = creditJsonObject.getString("job");
                    credit = new CrewCredit(creditId, id, mediaType, episodeCount, department, job);
                }
                
                if (!departments.contains(department)) {
                    departments.add(department);
                }
                
                TreeMap<Integer, ArrayList<Credit>> creditTreeMap;
                if (creditMap.containsKey(department)) {
                    creditTreeMap = creditMap.get(department);
                } else {
                    creditTreeMap = getCreditTreeMap();
                }
                
                ArrayList<Credit> creditList;
                if (creditTreeMap != null) {
                    if (creditTreeMap.containsKey(year)) {
                        creditList = creditTreeMap.get(year);
                    } else {
                        creditList = new ArrayList<>();
                    }
                    
                    if (creditList != null) {
                        creditList.add(credit);
                    }
                    creditTreeMap.put(year, creditList);
                }
    
                creditMap.put(department, creditTreeMap);
                
                ArrayMap<String, Integer> jobMap;
                if (jobListMap.containsKey(department)) {
                    jobMap = jobListMap.get(department);
                    if (jobMap != null) {
                        if (jobMap.containsKey(gig)) {
                            jobMap.put(gig, jobMap.get(gig) + 1);
                        } else {
                            jobMap.put(gig, 1);
                        }
                    }
                } else {
                    jobMap = new ArrayMap<>();
                    jobMap.put(gig, 1);
                }
                jobListMap.put(department, jobMap);
            }
            
            Collections.sort(departments, (department1, department2) -> {
                int dpt1Members, dpt2Members;
                
                try {
                    dpt1Members = Objects.requireNonNull(creditMap.get(department1)).size();
                    dpt2Members = Objects.requireNonNull(creditMap.get(department2)).size();
                    
                    if (dpt1Members > dpt2Members) {
                        return -1;
                    } else if (dpt1Members < dpt2Members) {
                        return 1;
                    } else {
                        return Integer.compare(department1.compareTo(department2), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
