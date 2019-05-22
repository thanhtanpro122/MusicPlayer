package com.example.nhom9.musicplayer.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.nhom9.musicplayer.Adapter.SongPlaylistAdapter;
import com.example.nhom9.musicplayer.Common.Consts;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.Fragment.list_bai_hat_playlist;
import com.example.nhom9.musicplayer.Fragment.Fragment_Playlist_List_BaiHat_;

import java.util.ArrayList;

public class Activity_playlist_baihat extends AppCompatActivity {
    private TextView txtNamePlaylist;
    private Button btnPlayAll;

    private SongPlaylistAdapter adapter;

    private PlayListService service;
    public static PlayList playList;
    private ArrayList<BaiHat> baiHats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_baihat);
        playList = (PlayList) getIntent().getSerializableExtra(Consts.PLAY_LIST);

        txtNamePlaylist = findViewById(R.id.txtName_Playlist);

        txtNamePlaylist.setText(playList.getTenPlayList());

        btnPlayAll = findViewById(R.id.btn_play_all);
        loadFragment(new Fragment_Playlist_List_BaiHat_());
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<BaiHat> lstSong = service.getSongList(String.valueOf(playList.getIdPlayList()));
                    Intent playIntent = new Intent(getApplicationContext(), Activity_play_nhac.class);

                    if(Activity_play_nhac.comingBaiHat != null){
                        Activity_play_nhac.comingBaiHat = lstSong.get(0);
                    }
                    if(Activity_play_nhac.currentPlayList != null){
                        Activity_play_nhac.currentPlayList = lstSong ;
                    }
                    if(Activity_play_nhac.comingBaiHat == null && Activity_play_nhac.currentPlayList==null){
                        playIntent.putExtra(Consts.PLAY_LIST, lstSong);
                        playIntent.putExtra(Consts.SONG_EXTRA, lstSong.get(0));
                    }


//        playIntent.putExtra(Consts.SONGS_EXTRA, lstSong);
//        playIntent.putExtra(Consts.SONG_EXTRA, lstSong.get(0));
//        playIntent.putExtra(Consts.SONG_POSITION_EXTRA, 0);

                    startActivity(playIntent);
                }
            });
        Toolbar toolbar_play_nhac = findViewById(R.id.toolbar_back_playlist);
        setSupportActionBar(toolbar_play_nhac);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_play_nhac.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Activity_trang_chu.class);
                startActivity(intent);
            }
        });
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_playlist,fragment );
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
