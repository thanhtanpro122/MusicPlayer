package com.example.nhom9.musicplayer.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhom9.musicplayer.Common.Consts;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.Fragment.Fragment_List_BaiHat;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.Service.MediaPlayerService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_play_nhac extends AppCompatActivity {

    private final String TAG = "Activity_play_nhac";

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.nhom9.musicplayer.PlayNewAudio";

    public static MediaPlayerService.LocalBinder binder;

    public MediaPlayerService player;

    public static boolean serviceBound = false;

    TextView txtTime, txtTotalTime, txtSongName, txtSingerName;
    SeekBar seekBar;
    ImageButton btnRandom, btnPreview, btnPlay, btnNext, btnRepeat;
    CircleImageView profileImg;

    public int indexBaiHat;

    Animation animation;

    public static ArrayList<BaiHat> currentPlayList;
    public static BaiHat comingBaiHat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_nhac);
        AnhXa();
//        AddSong();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        animation = AnimationUtils.loadAnimation(this, R.anim.disc_routate);


        Toolbar toolbar_play_nhac = findViewById(R.id.toolbar_play_nhac);
        setSupportActionBar(toolbar_play_nhac);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_play_nhac.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Activity_trang_chu.class);
                startActivity(intent);
            }
        });
//        XuLyCacNut();
//        SetTimeTotal();
//        UpdateTimeSong();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                comingBaiHat = (BaiHat) extras.getSerializable(Consts.SONG_EXTRA);
                currentPlayList = (ArrayList<BaiHat>) extras.getSerializable(Consts.PLAY_LIST);
            }
        } else {
            comingBaiHat = (BaiHat) savedInstanceState.getSerializable(Consts.SONG_EXTRA);
            currentPlayList = (ArrayList<BaiHat>) savedInstanceState.getSerializable(Consts.PLAY_LIST);
        }

//        setUpService();
        XuLyCacNut();
//        setUpScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpService();
        setUpScreen();
        if(player!= null){
            player.updateListBaiHat(currentPlayList);
            if (!player.isCurrentSong(comingBaiHat)) {
                indexBaiHat = player.setSongIndex(comingBaiHat.getIdBaiHat());
                comingBaiHat = player.getCurrentBaiHat();
                setUpScreen();
            }
        }

        Log.i("Activity_play_nhac","OnStart");
    }
//
//    @Override
//    protected void onRestart() {
//        baiHat = (BaiHat) getIntent().getSerializableExtra("song");
//        if (!player.isCurrentSong(baiHat)) {
//            indexBaiHat = player.setSongIndex(baiHat.getIdBaiHat());
//        }
//        super.onRestart();
//    }

    private void setUpService(){
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            setUpScreen();
            Toast.makeText(Activity_play_nhac.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void setUpScreen(){
        if(player!= null){
            //getSupportActionBar().setTitle(player.getCurrentBaiHat().getTenBaiHat());

//            getSupportActionBar().setTitle("");
//            txtSongName.setText(player.getCurrentBaiHat().getTenBaiHat());
//            txtSingerName.setText(player.getCaSiService().layTenCaSi(player.getCurrentBaiHat().getIdCasi()));
//            Bitmap imgbitmap = BitmapFactory.decodeByteArray(player.getCurrentBaiHat().getHinhAnh(), 0, player.getCurrentBaiHat().getHinhAnh().length);
//            profileImg.setImageBitmap(imgbitmap);

            if(player.getMediaPlayerState()){
                btnPlay.setImageResource(R.drawable.iconpause);
            }else{
                btnPlay.setImageResource(R.drawable.iconplay);
            }
            if(player.isMediaPlayerLooping()){
                btnRepeat.setImageResource(R.drawable.iconsyned);
                btnRepeat.setTag('1');
            }else{
                btnRepeat.setImageResource(R.drawable.iconrepeat);
                btnRepeat.setTag('0');
            }
            if(player.isMediaPlayerRandom()){
                btnRandom.setImageResource(R.drawable.iconshuffled);
                btnRandom.setTag('1');
            }else{
                btnRandom.setImageResource(R.drawable.iconsuffle);
                btnRandom.setTag('0');
            }

//            SetTimeTotal();
            resetScreen();
            UpdateTimeSong();
        }
    }

    public void resetScreen(){
        if(player!= null){
            //getSupportActionBar().setTitle(player.getCurrentBaiHat().getTenBaiHat());
            getSupportActionBar().setTitle("");
            txtSongName.setText(player.getCurrentBaiHat().getTenBaiHat());
            txtSingerName.setText(player.getCaSiService().layTenCaSi(player.getCurrentBaiHat().getIdCasi()));
            Bitmap imgbitmap = BitmapFactory.decodeByteArray(player.getCurrentBaiHat().getHinhAnh(), 0, player.getCurrentBaiHat().getHinhAnh().length);
            profileImg.setImageBitmap(imgbitmap);
            SetTimeTotal();
        }
    }

    /**
     * Add the following methods to MainActivity to fix it.
     * All these methods do is save and restore the state of the serviceBound variable
     * and unbind the Service when a user closes the app.
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }
//CODE TODO
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "onKeyDown: back");
            Intent intent = new Intent(getApplicationContext(), Activity_trang_chu.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void XuLyCacNut(){
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.btnPlayStopClick()) {
                    // nếu đang hát
//                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.iconplay);
                } else {
//                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.iconpause);
                }
//                SetTimeTotal();
//                UpdateTimeSong();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.btnNextClick();
                btnPlay.setImageResource(R.drawable.iconpause);
                comingBaiHat = player.getCurrentBaiHat();
                indexBaiHat = player.getCurrentIndex();
                setUpScreen();
            }
        });
//
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.btnPreviousClick();
                btnPlay.setImageResource(R.drawable.iconpause);
                comingBaiHat = player.getCurrentBaiHat();
                indexBaiHat = player.getCurrentIndex();
                setUpScreen();
            }
        });
//
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRepeat.getTag().equals('0')){
                    player.setMediaPlayerLooping(true);
                    btnRepeat.setImageResource(R.drawable.iconsyned);
                    btnRepeat.setTag('1');
                    if(btnRandom.getTag().equals('1')){
                        btnRandom.setImageResource(R.drawable.iconsuffle);
                        player.setShuffled(false);
                        btnRandom.setTag('0');
                    }
                }
                else {
                    player.setMediaPlayerLooping(false);
                    btnRepeat.setImageResource(R.drawable.iconrepeat);
                    btnRepeat.setTag('0');
                }

            }
        });
//
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRandom.getTag().equals('0')){
                    btnRandom.setImageResource(R.drawable.iconshuffled);
                    player.setShuffled(true);
                    btnRandom.setTag('1');
                    if(btnRepeat.getTag().equals('1')){
                        player.setMediaPlayerLooping(false);
                        btnRepeat.setImageResource(R.drawable.iconrepeat);
                        btnRepeat.setTag('0');
                    }
                }
                else {
                    btnRandom.setImageResource(R.drawable.iconsuffle);
                    player.setShuffled(false);
                    btnRandom.setTag('0');
                }
            }
        });
//
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.setSeekTo(seekBar.getProgress());
//                player.setSeekTo(Activity_trang_chu.collapseSeekbar.getProgress());
            }
        });
    }

    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player.isMediaPlayerNull()) {
                    return;
                }
                if (!player.getMediaPlayerState()) {
                    btnPlay.setImageResource(R.drawable.iconplay);
                } else {
                    btnPlay.setImageResource(R.drawable.iconpause);
                }
                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                txtTime.setText(dinhDangGio.format(player.getCurrentPosition()));

                seekBar.setProgress(player.getCurrentPosition());
//                Activity_trang_chu.collapseSeekbar.setProgress(player.getCurrentPosition());

//                setUpScreen();
                resetScreen();

                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    public void SetTimeTotal() {
        SimpleDateFormat dinhDanggio = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(dinhDanggio.format(player.getDuration()));
        //Gán max của skSong = thoi gian phát
        seekBar.setMax(player.getDuration());
//        Activity_trang_chu.collapseSeekbar.setProgress(player.getDuration());
    }

//    private void KhoiTaoMediaPlayer() {
//        if (mediaPlayer != null) {
//            BaiHat song = (BaiHat) getIntent().getSerializableExtra("song");
//            if (!mediaPlayer.isPlaying()) {
//                baiHat = song;
//
//                for (int i = 0; i < arraySongs.size(); i++) {
//                    if (arraySongs.get(i).getIdBaiHat() == baiHat.getIdBaiHat()) {
//                        position = i;
//                        break;
//                    }
//                }
//                mediaPlayer.release();
//                mediaPlayer = MediaPlayer.create(Activity_play_nhac.this, Uri.parse(baiHat.getUrlBaiHat()));
//                mediaPlayer.start();
//                getSupportActionBar().setTitle(baiHat.getTenBaiHat());
//            } else {
//                if (baiHat.getIdBaiHat() != song.getIdBaiHat()) {
//                    baiHat = song;
//
//                    for (int i = 0; i < arraySongs.size(); i++) {
//                        if (arraySongs.get(i).getIdBaiHat() == baiHat.getIdBaiHat()) {
//                            position = i;
//                            break;
//                        }
//                    }
//                    mediaPlayer.release();
//                    mediaPlayer = MediaPlayer.create(Activity_play_nhac.this, Uri.parse(baiHat.getUrlBaiHat()));
//                    mediaPlayer.start();
//                    getSupportActionBar().setTitle(baiHat.getTenBaiHat());
//                } else {
//                    getSupportActionBar().setTitle(baiHat.getTenBaiHat());
//                }
//            }
//        } else {
//            baiHat = (BaiHat) getIntent().getSerializableExtra("song");
//
//            for (int i = 0; i < arraySongs.size(); i++) {
//                if (arraySongs.get(i).getIdBaiHat() == baiHat.getIdBaiHat()) {
//                    position = i;
//                    break;
//                }
//            }
//            mediaPlayer = MediaPlayer.create(Activity_play_nhac.this, Uri.parse(baiHat.getUrlBaiHat()));
//            mediaPlayer.start();
//            getSupportActionBar().setTitle(baiHat.getTenBaiHat());
//        }
//    }

//    private void AddSong() {
//        try {
//            BaiHatService service = new BaiHatService(getApplicationContext());
//            arraySongs = service.layDanhSachBaiHat();
//        } catch (Exception ignored) {
//        }
//    }

    private void AnhXa() {
        txtTime = (TextView) findViewById(R.id.txt_time_song);
        txtTotalTime = (TextView) findViewById(R.id.txt_total_time_song);


        seekBar = (SeekBar) findViewById(R.id.seekbar_song);

        btnPreview = (ImageButton) findViewById(R.id.btn_preview);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnRandom=(ImageButton) findViewById(R.id.btn_ngaunhien);
        btnRepeat=(ImageButton) findViewById(R.id.btn_repeat);
        txtSingerName=(TextView) findViewById(R.id.txt_name_singer);
        txtSongName=(TextView) findViewById(R.id.txt_name_song);
        profileImg=(CircleImageView) findViewById(R.id.profile_image);
    }






}
