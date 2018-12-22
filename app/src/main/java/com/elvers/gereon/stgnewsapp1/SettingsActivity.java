package com.elvers.gereon.stgnewsapp1;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Activity that manages the settings displayed within the App
 *
 * @author Gereon Elvers
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Since it is best practice to manage settings through a SettingsFragment utilizing a preferences.xml file,
     * this Activity just displays the fragment in a Layout matching the rest of App.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(R.string.settings_string);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_ll, new SettingsFragment()).commit();


    }
}