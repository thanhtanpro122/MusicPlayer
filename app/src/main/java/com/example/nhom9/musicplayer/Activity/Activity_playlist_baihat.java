package com.example.nhom9.musicplayer.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.nhom9.musicplayer.Adapter.SongPlaylistAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.common.Consts;

import java.io.IOException;
import java.util.ArrayList;

public class Activity_playlist_baihat extends AppCompatActivity {
    private TextView txtNamePlaylist;
    private RecyclerView rclSongPlaylist;
    private ArrayList<BaiHat> playLists;
    private SongPlaylistAdapter adapter;
    private PlayListService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_baihat);
        PlayList playList = (PlayList) getIntent().getSerializableExtra(Consts.PLAY_LIST);

        txtNamePlaylist = findViewById(R.id.txtName_Playlist);
        rclSongPlaylist = findViewById(R.id.rcl_song_Playlist);
        rclSongPlaylist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rclSongPlaylist.setHasFixedSize(true);

        try {
            service = new PlayListService(getApplicationContext());
            playLists = service.getSongList(String.valueOf(playList.getIdPlayList()));

            adapter = new SongPlaylistAdapter(getApplicationContext(), playLists);

            rclSongPlaylist.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
