package com.pam.tmdbmovielist.model;

import android.util.ArrayMap;

import com.pam.tmdbmovielist.model.credits.Credit;
import com.pam.tmdbmovielist.model.credits.ExternalIds;
import com.pam.tmdbmovielist.model.credits.MediaList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class PersonDetail extends Person {
    
    private String birthday;
    private String deathday;
    private String placeOfBirth;
    private int gender;
    private String knownForDepartment;
    private String biography;
    private ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap;
    private ArrayList<String> departments;
    private TreeMap<String, ArrayMap<String, Integer>> jobListMap;
    private MediaList mediaList;
    private ExternalIds externalIds;
    private ArrayList<String> images;
    
    private ArrayList<String> knownForDepartments;
    private String[] knownForJobs;
    private int knownCredits = 0;
    
    public PersonDetail(int id, String name, String profilePath, String birthday, String deathday,
                        String placeOfBirth, int gender, String knownForDepartment, String biography,
                        ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap,
                        ArrayList<String> departments, TreeMap<String, ArrayMap<String, Integer>> jobListMap,
                        MediaList mediaList, ExternalIds externalIds, ArrayList<String> images) {
        super(id, name, profilePath);
        this.birthday = birthday;
        this.deathday = deathday;
        this.placeOfBirth = placeOfBirth;
        this.gender = gender;
        this.knownForDepartment = knownForDepartment;
        this.biography = biography;
        this.creditMap = creditMap;
        this.departments = departments;
        this.jobListMap = jobListMap;
        this.mediaList = mediaList;
        this.externalIds = externalIds;
        this.images = images;
        setKnownForDepartments();
        setKnownCredits();
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public String getDeathday() {
        return deathday;
    }
    
    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }
    
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
    
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }
    
    public int getGender() {
        return gender;
    }
    
    public String getGenderString() {
        return gender == 1 ? "Female" : "Male";
    }
    
    public void setGender(int gender) {
        this.gender = gender;
    }
    
    private String getKnownForActing() {
        return gender == 1 ? "Actress" : "Actor";
    }
    
    public String getKnownForDepartment() {
        return knownForDepartment;
    }
    
    public void setKnownForDepartment(String knownForDepartment) {
        this.knownForDepartment = knownForDepartment;
    }
    
    public void setKnownForDepartments() {
        knownForDepartments = new ArrayList<>();
        knownForDepartments.add(knownForDepartment);
        for (String department : departments) {
            if (knownForDepartments.size() < 3 && !knownForDepartments.contains(department)) {
                knownForDepartments.add(department);
            }
        }
        
        setKnownForJobs();
    }
    
    public String[] getKnownForJobs() {
        return knownForJobs;
    }
    
    public void setKnownForJobs() {
        knownForJobs = new String[knownForDepartments.size()];
        for (int i = 0; i < knownForDepartments.size(); i++) {
            String department = knownForDepartments.get(i);
            
            if (department.equals("Acting")) {
                knownForJobs[i] = getKnownForActing();
            } else {
                ArrayMap<String, Integer> jobMap = jobListMap.get(department);
                int biggest = 0;
                if (jobMap != null) {
                    for (Map.Entry<String, Integer> jobEntry : jobMap.entrySet()) {
                        if (jobEntry.getValue() > biggest) {
                            biggest = jobEntry.getValue();
                            if (!jobEntry.getKey().equals("Thanks")) {
                                knownForJobs[i] = jobEntry.getKey();
                            }
                        }
                    }
                }
            }
        }
    }
    
    public ArrayList<Media> getKnownForMediaList() {
        TreeMap<Integer, ArrayList<Credit>> treeCreditMap = creditMap.get(knownForDepartment);
        ArrayList<Credit> allCredit = new ArrayList<>();
        
        for (Map.Entry<Integer, ArrayList<Credit>> creditListEntry : treeCreditMap.entrySet()) {
            allCredit.addAll(creditListEntry.getValue());
        }
        
        ArrayList<Media> knownForMedia = new ArrayList<>();
        for (Credit credit : allCredit) {
            Media media = mediaList.getMedia(credit.getId());
            if (!knownForMedia.contains(media)) {
                knownForMedia.add(media);
            }
        }
        
        Collections.sort(knownForMedia, new Comparator<Media>() {
            @Override
            public int compare(Media media1, Media media2) {
                int vote1 = media1.getVoteCount();
                int vote2 = media2.getVoteCount();
                
                return Integer.compare(vote2, vote1);
            }
        });
    
        if (knownForMedia.size() > 10) {
            return new ArrayList<>(knownForMedia.subList(0, 10));
        } else {
            return knownForMedia;
        }
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> getCreditMap() {
        return creditMap;
    }
    
    public void setCreditMap(ArrayMap<String, TreeMap<Integer, ArrayList<Credit>>> creditMap) {
        this.creditMap = creditMap;
    }
    
    public ArrayList<String> getDepartments() {
        return departments;
    }
    
    public void setDepartments(ArrayList<String> departments) {
        this.departments = departments;
    }
    
    public TreeMap<String, ArrayMap<String, Integer>> getJobListMap() {
        return jobListMap;
    }
    
    public void setJobListMap(TreeMap<String, ArrayMap<String, Integer>> jobListMap) {
        this.jobListMap = jobListMap;
    }
    
    public MediaList getMediaList() {
        return mediaList;
    }
    
    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
    }
    
    public ExternalIds getExternalIds() {
        return externalIds;
    }
    
    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }
    
    public ArrayList<String> getImages() {
        return images;
    }
    
    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
    
    public int getKnownCredits() {
        return knownCredits;
    }
    
    public void setKnownCredits() {
        for (TreeMap<Integer, ArrayList<Credit>> creditTreeMap : creditMap.values()) {
            for (ArrayList<Credit> creditList : creditTreeMap.values()) {
                knownCredits += creditList.size();
            }
        }
    }
}
