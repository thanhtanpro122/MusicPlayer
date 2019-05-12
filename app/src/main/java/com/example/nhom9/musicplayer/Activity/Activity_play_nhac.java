package com.example.nhom9.musicplayer.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class Activity_play_nhac extends AppCompatActivity {

    TextView txtTime, txtTotalTime;
    SeekBar seekBar;
    ImageButton btnRandom, btnPreview, btnPlay, btnNext, btnRepeat;
    BaiHat baiHat;

    ArrayList<BaiHat> arraySongs;

    int position = 0;
    MediaPlayer mediaPlayer;

    Animation animation;

    boolean repeat = false;
    boolean checkrandom = false;
    boolean next = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_nhac);
        AnhXa();
        AddSong();

        animation = AnimationUtils.loadAnimation(this, R.anim.disc_routate);

        baiHat = (BaiHat) getIntent().getSerializableExtra("song");

        for (int i = 0; i < arraySongs.size(); i++) {
            if (arraySongs.get(i).getIdBaiHat() == baiHat.getIdBaiHat()) {
                position = i;
                break;
            }
        }
        Toolbar toolbar_play_nhac = findViewById(R.id.toolbar_play_nhac);
        setSupportActionBar(toolbar_play_nhac);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(baiHat.getTenBaiHat());

        toolbar_play_nhac.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        XuLyCacNut();
    }
    private void XuLyCacNut(){
        KhoiTaoMediaPlayer();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    // nếu đang hát
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.iconplay);
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.iconpause);
                }
                SetTimeTotal();
                UpdateTimeSong();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                if (position > arraySongs.size() - 1) {
                    position = 0;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                baiHat = arraySongs.get(position);
                getSupportActionBar().setTitle(baiHat.getTenBaiHat());
                KhoiTaoMediaPlayer();
                btnPlay.setImageResource(R.drawable.iconpause);
                SetTimeTotal();
                mediaPlayer.start();
                UpdateTimeSong();
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position--;
                if (position < 0) {
                    position = arraySongs.size() - 1;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                baiHat = arraySongs.get(position);
                getSupportActionBar().setTitle(baiHat.getTenBaiHat());
                KhoiTaoMediaPlayer();
                btnPlay.setImageResource(R.drawable.iconpause);
                SetTimeTotal();
                mediaPlayer.start();
                UpdateTimeSong();
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRepeat.getTag().equals('0')){
                    mediaPlayer.setLooping(true);
                    btnRepeat.setImageResource(R.drawable.iconsyned);
                    btnRepeat.setTag('1');
                }
                else {
                    mediaPlayer.setLooping(false);
                    btnRepeat.setImageResource(R.drawable.iconrepeat);
                    btnRepeat.setTag('0');
                }

            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRandom.getTag().equals('0')){
                    btnRandom.setImageResource(R.drawable.iconshuffled);
                    btnRandom.setTag('1');
                }
                else {
                    btnRandom.setImageResource(R.drawable.iconsuffle);
                    btnRandom.setTag('0');
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer == null) {
                    return;
                }

                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                txtTime.setText(dinhDangGio.format(mediaPlayer.getCurrentPosition()));

                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                //Kiểm tra thời gian bài hát  nếu kết thúc thì chuyển tiếp
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer.isLooping()) {
                            return;
                        }

                        position++;
                        if (position > arraySongs.size() - 1) {
                            position = 0;
                        }

                        if (btnRandom.getTag().equals("1")) {
                            Random random = new Random();
                            position = random.nextInt(arraySongs.size());
                        }

                        baiHat = arraySongs.get(position);
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        KhoiTaoMediaPlayer();
//                        if (btnRepeat.getTag().equals('1')){
//                            btnRepeat.setImageResource(R.drawable.iconrepeat);
//                            btnRepeat.setTag('0');
//                        }
                        btnPlay.setImageResource(R.drawable.iconpause);
                        SetTimeTotal();
                        mediaPlayer.start();
                        UpdateTimeSong();
                    }
                });

                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private void SetTimeTotal() {
        SimpleDateFormat dinhDanggio = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(dinhDanggio.format(mediaPlayer.getDuration()));
        //Gán max của skSong = thoi gian phát
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void KhoiTaoMediaPlayer() {
        mediaPlayer = MediaPlayer.create(Activity_play_nhac.this, Uri.parse(baiHat.getUrlBaiHat()));
        getSupportActionBar().setTitle(baiHat.getTenBaiHat());
    }

    private void AddSong() {
        try {
            BaiHatService service = new BaiHatService(getApplicationContext());
            arraySongs = service.layDanhSachBaiHat();
        } catch (Exception ignored) {
        }
    }

    private void AnhXa() {
        txtTime = (TextView) findViewById(R.id.txt_time_song);
        txtTotalTime = (TextView) findViewById(R.id.txt_total_time_song);


        seekBar = (SeekBar) findViewById(R.id.seekbar_song);

        btnPreview = (ImageButton) findViewById(R.id.btn_preview);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnRandom=(ImageButton) findViewById(R.id.btn_ngaunhien);
        btnRepeat=(ImageButton) findViewById(R.id.btn_repeat);
    }

}
