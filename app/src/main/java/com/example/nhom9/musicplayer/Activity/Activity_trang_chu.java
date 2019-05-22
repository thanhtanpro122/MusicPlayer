package com.example.nhom9.musicplayer.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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

    SeekBar collapseSeekbar;
    ImageButton btnPlay;
    ImageView profileImg;
    TextView nameSong,nameSinger;
    LinearLayout musicBar;
    FrameLayout frameDisplay;

    private MediaPlayerService player;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    Handler handler;
    Runnable myRunnable;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        handler = new Handler(getMainLooper());


        loadFragment(new Fragment_List_BaiHat());

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        btnPlay = (ImageButton) findViewById(R.id.btn_play_collapse);
        profileImg=(ImageView) findViewById(R.id.profile_image);
        nameSong=(TextView) findViewById(R.id.txt_name_song);
        nameSinger=(TextView) findViewById(R.id.txt_name_singer);
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
                if(player!= null){
                    player.setSeekTo(seekBar.getProgress());
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player!=null){
                    if (player.btnPlayStopClick()) {
                        btnPlay.setImageResource(R.drawable.iconplay);
                    } else {
                        btnPlay.setImageResource(R.drawable.iconpause);
                    }
                }
            }
        });

        musicBar = (LinearLayout) findViewById(R.id.musicBar);
        frameDisplay = (FrameLayout) findViewById(R.id.frame_container);
        setHideMusicBar(true);
        musicBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Activity_play_nhac.comingBaiHat != null){
                    Intent intent = new Intent(getApplicationContext(), Activity_play_nhac.class);
                    startActivity(intent);
                }
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


    public void setHideMusicBar(boolean flag){
        if(flag){
            musicBar.setVisibility(View.GONE);
        }
        else {
            musicBar.setVisibility(View.VISIBLE);
        }
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
            
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Activity_play_nhac.binder != null){
            player = Activity_play_nhac.binder.getService();
            UpdateTimeSong();
        }
    }

    private void UpdateTimeSong() {
        setHideMusicBar(false);
        SetTimeTotal();
        if(player != null){
            if(myRunnable == null){
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        collapseSeekbar.setProgress(player.getCurrentPosition());
                        nameSong.setText(player.getCurrentBaiHat().getTenBaiHat());
                        nameSinger.setText(player.getCaSiService().layTenCaSi(player.getCurrentBaiHat().getIdCasi()));


                        Bitmap imgbitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image5);
                        if(player.getCurrentBaiHat().getHinhAnh()!=null){
                            imgbitmap = BitmapFactory.decodeByteArray(player.getCurrentBaiHat().getHinhAnh(), 0, player.getCurrentBaiHat().getHinhAnh().length); //replace with medias albumArt
                        }

                        profileImg.setImageBitmap(imgbitmap);

                        if (!player.getMediaPlayerState()) {
                            btnPlay.setImageResource(R.drawable.iconplay);
                        } else {
                            btnPlay.setImageResource(R.drawable.iconpause);
                        }
                        handler.postDelayed(this, 100);
                    }
                };

                handler.post(myRunnable);
            }
        }

    }

    public void SetTimeTotal() {
        //Gán max của skSong = thoi gian phát
        collapseSeekbar.setMax(player.getDuration());
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
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
