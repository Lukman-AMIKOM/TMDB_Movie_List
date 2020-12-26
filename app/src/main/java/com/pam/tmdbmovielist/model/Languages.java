package com.pam.tmdbmovielist.model;

import android.content.Context;

import com.pam.tmdbmovielist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class Languages {
    
    public static StringBuilder getOriginalLanguage(Context context, String iso) {
        StringBuilder originalLanguage = new StringBuilder();
        
        InputStream inputStream = context.getResources().openRawResource(R.raw.languages);
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        
        try {
            JSONArray languages = new JSONArray(jsonString);
            
            for (int i = 0; i < languages.length(); i++) {
                JSONObject obj = languages.getJSONObject(i);
                String isoName = obj.getString("iso_639_1");
                
                if (isoName.equalsIgnoreCase(iso)) {
                    originalLanguage.append(obj.getString("english_name"));
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return originalLanguage;
    }
}
