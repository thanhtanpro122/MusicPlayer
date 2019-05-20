package com.example.nhom9.musicplayer.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.Adapter.SongPlaylistAdapter;
import com.example.nhom9.musicplayer.Common.Consts;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Fragment.Fragment_List_BaiHat;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.Service.MediaPlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_playlist_baihat extends AppCompatActivity {
    private TextView txtNamePlaylist;
    private RecyclerView rclSongPlaylist;
    private Button btnPlayAll;

    private SongPlaylistAdapter adapter;

    private PlayListService service;
    private PlayList playList;
    private ArrayList<BaiHat> baiHats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_baihat);
        playList = (PlayList) getIntent().getSerializableExtra(Consts.PLAY_LIST);

        txtNamePlaylist = findViewById(R.id.txtName_Playlist);
        rclSongPlaylist = findViewById(R.id.rcl_song_Playlist);
        rclSongPlaylist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rclSongPlaylist.setHasFixedSize(true);
        txtNamePlaylist.setText(playList.getTenPlayList());

        btnPlayAll = findViewById(R.id.btn_play_all);


        try {
            service = new PlayListService(getApplicationContext());
            baiHats = service.getSongList(String.valueOf(playList.getIdPlayList()));

            adapter = new SongPlaylistAdapter(getApplicationContext(), baiHats);
            adapter.setOnItemClick(new SongPlaylistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, BaiHat song) {
                    ArrayList<BaiHat> lstSong = service.getSongList(String.valueOf(playList.getIdPlayList()));
                    Intent intent = new Intent(getApplicationContext(), Activity_play_nhac.class);
                    if(Activity_play_nhac.comingBaiHat != null){
                        Activity_play_nhac.comingBaiHat = song;
                    }
                    if(Activity_play_nhac.currentPlayList != null){
                        Activity_play_nhac.currentPlayList = lstSong ;
                    }
                    if(Activity_play_nhac.currentPlayList == null && Activity_play_nhac.comingBaiHat == null){
                        intent.putExtra(Consts.PLAY_LIST, lstSong);
                        intent.putExtra(Consts.SONG_EXTRA, song);
                    }

                    startActivity(intent);
                }
            });
            adapter.setOnMoreItemClick(new SongPlaylistAdapter.OnMoreItemClickListener() {
                @Override
                public void onMoreItemClick(View view, BaiHat song, MenuItem item) {
                    ArrayList<BaiHat> lstSong = service.getSongList(String.valueOf(playList.getIdPlayList()));
                    switch (item.getItemId()) {
                        case R.id.action_play:
                            Intent intent = new Intent(getApplicationContext(), Activity_play_nhac.class);
                            if(Activity_play_nhac.comingBaiHat != null){
                                Activity_play_nhac.comingBaiHat = song;
                            }
                            if(Activity_play_nhac.currentPlayList != null){
                                Activity_play_nhac.currentPlayList = lstSong ;
                            }
                            if(Activity_play_nhac.currentPlayList == null && Activity_play_nhac.comingBaiHat == null){
                                intent.putExtra(Consts.PLAY_LIST, lstSong);
                                intent.putExtra(Consts.SONG_EXTRA, song);
                            }
                            startActivity(intent);
                            break;
                        case R.id.action_remove:
                            Delete(song);
                            break;
                    }
                }
            });

            rclSongPlaylist.setAdapter(adapter);


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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Delete(BaiHat baiHat){
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(Objects.requireNonNull(getApplicationContext()));
        confirmDelete.setTitle("Xóa bài hát khỏi Playlist");
        confirmDelete.setMessage("Bạn có chắc chắn muốn xóa bài hát: "+ baiHat.getTenBaiHat() + " ra khỏi '"+playList.getTenPlayList() +"' ? ");
        confirmDelete.setPositiveButton("yes", (dialogInterface, i) -> {
            service.deleteSongFromPlaylist(playList.getIdPlayList(),baiHat.getIdBaiHat());
            baiHats.clear();
            baiHats.addAll(service.getSongList(String.valueOf(playList.getIdPlayList())));
            adapter.notifyDataSetChanged();

        });
        confirmDelete.setNegativeButton("Hủy bỏ", (dialogInterface, i) -> dialogInterface.dismiss());
        confirmDelete.show();
    }

}
