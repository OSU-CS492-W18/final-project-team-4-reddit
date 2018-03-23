package com.example.learntoprogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Connor Sedwick on 3/21/2018.
 */

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString(getString(R.string.pref_theme_key), getString(R.string.pref_theme_default_value));
        switch (themeName) {
            case "Blue":
                this.setTheme(R.style.Blue);
                break;
            case "Dark":
                setTheme(R.style.Dark);
                break;
            case "AppTheme":
                this.setTheme(R.style.AppTheme);
                break;
            default:
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
