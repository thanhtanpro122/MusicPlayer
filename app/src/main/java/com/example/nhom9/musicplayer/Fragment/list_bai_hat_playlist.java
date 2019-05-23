package com.example.nhom9.musicplayer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Activity.Activity_playlist_baihat;
import com.example.nhom9.musicplayer.Adapter.PlayListAdapter;
import com.example.nhom9.musicplayer.Adapter.SongPlaylistAdapter;
import com.example.nhom9.musicplayer.Common.Consts;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class list_bai_hat_playlist extends Fragment {

    private static list_bai_hat_playlist Instance;
    public list_bai_hat_playlist(){}
    public static list_bai_hat_playlist getInstance(){
        if(Instance == null)
        {
            Instance = new list_bai_hat_playlist();
        }
        return Instance;
    }

    public RecyclerView rclSongPlaylist;
    public SongPlaylistAdapter adapter;
    private PlayListService service;
    private PlayList playList;
    private ArrayList<BaiHat> baiHats;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_bai_hat_playlist,container,false );
        rclSongPlaylist = root.findViewById(R.id.recycler_play_baihat_playlist);
        rclSongPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        rclSongPlaylist.setHasFixedSize(true);
        playList = Activity_playlist_baihat.playList;
        try {
            service = new PlayListService(getContext());
            baiHats = service.getSongList(String.valueOf(playList.getIdPlayList()));

            adapter = new SongPlaylistAdapter(getContext(), baiHats);
            adapter.setOnItemClick(new SongPlaylistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, BaiHat song) {
                    ArrayList<BaiHat> lstSong = service.getSongList(String.valueOf(playList.getIdPlayList()));
                    Intent intent = new Intent(getContext(), Activity_play_nhac.class);
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
                        case R.id.menu_item_play:
                            Intent intent = new Intent(getContext(), Activity_play_nhac.class);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }


    private void Delete(BaiHat baiHat){
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        confirmDelete.setTitle("Xóa bài hát khỏi Playlist");
        confirmDelete.setMessage("Bạn có chắc chắn muốn xóa bài hát: "+ baiHat.getTenBaiHat() + " ra khỏi '"+playList.getTenPlayList() +"' ? ");
        confirmDelete.setPositiveButton("yes", (dialogInterface, i) -> {
            service.deleteSongFromPlaylist(playList.getIdPlayList(),baiHat.getIdBaiHat());
            baiHats.clear();
            baiHats.addAll(service.getSongList(String.valueOf(playList.getIdPlayList())));
            adapter.notifyDataSetChanged();
            rclSongPlaylist.setAdapter(adapter);
            Fragment_PlayList.getInstance().recyclerView.setAdapter(Fragment_PlayList.getInstance().adapter);
        });
        confirmDelete.setNegativeButton("Hủy bỏ", (dialogInterface, i) -> dialogInterface.dismiss());
        confirmDelete.show();
    }
}
