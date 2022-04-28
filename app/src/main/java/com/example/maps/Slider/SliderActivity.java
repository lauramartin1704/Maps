package com.example.maps.Slider;

import android.content.Context;
import android.os.Bundle;

import com.example.maps.MapsActivity;
import com.example.maps.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.example.maps.databinding.ActivitySliderBinding;

import java.util.ArrayList;

public class SliderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySliderBinding binding;
    ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        urls = getIntent().getExtras().getStringArrayList("urls");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_slider);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        ViewPager mPager = findViewById(R.id.vpager);
        mPager.setAdapter(new SlidingAdapter(getApplicationContext(), urls));

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_slider);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}