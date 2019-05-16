package com.example.nhom9.musicplayer.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhom9.musicplayer.Adapter.ViewPageAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.QuetBaiHatService;
import com.example.nhom9.musicplayer.Fragment.Fragment_List_BaiHat;
import com.example.nhom9.musicplayer.Fragment.Fragment_PlayList;
import com.example.nhom9.musicplayer.R;
public class Activity_trang_chu extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
//    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

//        setSupportActionBar(findViewById(R.id.action_bar));
        loadFragment(new Fragment_List_BaiHat());
//        actionBar = getSupportActionBar();
//        actionBar.setTitle("Tất cả bài hát");

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        toolbar.setTitle("Danh sách bài hát");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            loadData();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()){
                case R.id.action_home:
//                    actionBar.setTitle("Tất cả bài hát");
                    fragment = new Fragment_List_BaiHat();
                    loadFragment(fragment);
                    return true;
                case R.id.action_playlist:
//                    actionBar.setTitle("Playlist");
                    fragment = new Fragment_PlayList();
                    loadFragment(fragment);
                    return true;
                case R.id.action_nearby:
                    Toast.makeText(Activity_trang_chu.this, "Search", Toast.LENGTH_SHORT).show();
                    return true;
        }
            return false;
        }
    };

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadData() {
        SharedPreferences ref = getSharedPreferences("com.example.nhom9.musicplayer.Activity", MODE_PRIVATE);
        if (ref.getBoolean("first-run", true)) {
            try {
                QuetBaiHatService quetBaiHatService = new QuetBaiHatService(getApplicationContext());
                quetBaiHatService.scanAndSave();
                ref.edit().putBoolean("first-run", false).apply();
                loadData();
            } catch (Exception ignored) {
            }
        }else{
            Init();
        }
    }

    private void Init(){
        ViewPageAdapter pageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        pageAdapter.addFragment("Songs", new Fragment_List_BaiHat());
        pageAdapter.addFragment("Playlists", new Fragment_PlayList());



//        TabLayout tabLayout = findViewById(R.id.tab_layout_library);
//        ViewPager pgrMain = findViewById(R.id.pgrMain);

//        pgrMain.setAdapter(pageAdapter);
//        tabLayout.setupWithViewPager(pgrMain);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            }
        }
    }
}
