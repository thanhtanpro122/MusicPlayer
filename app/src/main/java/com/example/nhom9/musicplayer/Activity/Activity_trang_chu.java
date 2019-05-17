package com.example.nhom9.musicplayer.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.nhom9.musicplayer.Adapter.ViewPageAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.QuetBaiHatService;
import com.example.nhom9.musicplayer.Fragment.Fragment_List_BaiHat;
import com.example.nhom9.musicplayer.Fragment.Fragment_PlayList;
import com.example.nhom9.musicplayer.Fragment.Fragment_Search_Song;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.Service.MediaPlayerService;


public class Activity_trang_chu extends AppCompatActivity {

    private final String TAG = "Activity_trang_chu";

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.nhom9.musicplayer.PlayNewAudio";

    private MediaPlayerService player;

    SeekBar collapseSeekbar;
    ImageButton btnPlay;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        loadFragment(new Fragment_List_BaiHat());

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnPlay = (ImageButton) findViewById(R.id.btn_play_collapse);
        collapseSeekbar = (SeekBar)findViewById(R.id.seekbar_song_collapse);
        collapseSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.setSeekTo(seekBar.getProgress());
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            loadData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Activity_play_nhac.binder != null){
            player = Activity_play_nhac.binder.getService();
            SetTimeTotal();
            UpdateTimeSong();
        }
    }

    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStart: "+player.getCurrentPosition());
                collapseSeekbar.setProgress(player.getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    public void SetTimeTotal() {
        //Gán max của skSong = thoi gian phát
        collapseSeekbar.setMax(player.getDuration());
//        Activity_trang_chu.collapseSeekbar.setProgress(player.getDuration());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()){
                case R.id.action_home:
//                  actionBar.setTitle("Tất cả bài hát");
                    fragment = new Fragment_List_BaiHat();
                    loadFragment(fragment);
                    return true;
                case R.id.action_playlist:
                    fragment = new Fragment_PlayList();
                    loadFragment(fragment);
                    return true;
                case R.id.action_nearby:
//                    actionBar.setTitle("Tìm kiếm bài hát");
                    fragment=new Fragment_Search_Song();
                    loadFragment(fragment);
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            }
        }
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        stopService(playerIntent);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i=new Intent(this,Activity_trang_chu.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
