package com.example.learntoprogram;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Connor Sedwick on 3/21/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditTextPreference textColorPref = (EditTextPreference)findPreference(getString(R.string.pref_text_color_key));
        textColorPref.setSummary(textColorPref.getText());

        EditTextPreference themePref = (EditTextPreference)findPreference(getString(R.string.pref_theme_key));
        themePref.setSummary(themePref.getText());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_text_color_key))) {
            EditTextPreference textColorPref = (EditTextPreference)findPreference(key);
            textColorPref.setSummary(textColorPref.getText());

        }
        /*if (key.equals(getString(R.string.pref_theme_key))) {
            EditTextPreference themePref = (EditTextPreference)findPreference(key);
            themePref.setSummary(themePref.getText());
        }
        if (key.equals(getString(R.string.pref_filter_switch_key))) {
        }*/
    }
    @Override
    public void onResume() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
