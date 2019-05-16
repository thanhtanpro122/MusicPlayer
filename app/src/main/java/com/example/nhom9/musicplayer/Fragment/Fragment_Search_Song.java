package com.example.nhom9.musicplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;

public class Fragment_Search_Song extends Fragment{
    BaiHatService baiHatService;
    private ArrayList<BaiHat> baiHats;
    PlayNhacAdapter adapter;
    RecyclerView recyclerView;
    android.widget.SearchView searchView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__search__song, container, false);
        searchView = (android.widget.SearchView)view.findViewById(R.id.search_box);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadDataTheoTen(recyclerView, s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadDataTheoTen(recyclerView, s);
                return false;
            }
        });

        recyclerView = view.findViewById(R.id.search_song_view);
        return view;
    }

    public void loadDataTheoTen(View view,String tenbaihat){
        recyclerView = view.findViewById(R.id.search_song_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        try {
            baiHatService = new BaiHatService(getContext());

            baiHats = baiHatService.timKiemBaiHat(tenbaihat);

            adapter = new PlayNhacAdapter(getContext(), baiHats);

            adapter.setOnItemClickListener(new PlayNhacAdapter.ItemClickListener() {
                @Override
                public void onClick(View view, BaiHat baiHat, int pos) {
                    Fragment_List_BaiHat.selectedSong = baiHat;
                    Intent intent = new Intent(getContext(), Activity_play_nhac.class);
                    startActivity(intent);
                }
            });
            adapter.setOnMoreItemClick(new PlayNhacAdapter.OnMoreItemClickListener() {
                @Override
                public void onMoreItemClick(View view, BaiHat song, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_play:
                            Intent intent = new Intent(getContext(), Activity_play_nhac.class);
                            intent.putExtra("song", song);

                            startActivity(intent);
                            break;
                        case R.id.menu_item_them_playlist:
                            // showPopup();
                            break;
                        case R.id.menu_item_rename:
//                            Rename(song);
                            break;
                        case R.id.menu_item_delete:
//                            Delete(song);
                            break;
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }catch (IOException ignored){

        }
    }

}
