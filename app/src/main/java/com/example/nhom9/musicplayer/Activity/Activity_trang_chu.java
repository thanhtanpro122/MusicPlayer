package com.example.nhom9.musicplayer.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nhom9.musicplayer.Adapter.ViewPageAdapter;
import com.example.nhom9.musicplayer.Fragment.Fragment_List_BaiHat;
import com.example.nhom9.musicplayer.Fragment.Fragment_PlayList;
import com.example.nhom9.musicplayer.R;

public class Activity_trang_chu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);
        ViewPageAdapter pageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        pageAdapter.addFragment("Playlists", new Fragment_PlayList());
        pageAdapter.addFragment("Bài Hát", new Fragment_List_BaiHat());


        TabLayout tabLayout = findViewById(R.id.tab_layout_library);
        ViewPager pgrMain = findViewById(R.id.pgrMain);

        pgrMain.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(pgrMain);

    }
}
