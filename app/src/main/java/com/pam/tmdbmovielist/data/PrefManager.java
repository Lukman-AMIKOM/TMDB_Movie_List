package com.pam.tmdbmovielist.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context, String prefName) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public void setDarkMode(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }
    
    public boolean getDarkMode(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    
    public void removePref() {
        editor.clear().commit();
    }
}
