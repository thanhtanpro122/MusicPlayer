package com.example.nhom9.musicplayer.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Adapter.PlayListAdapter;
import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.utils.Tools;
import com.example.nhom9.musicplayer.widget.SpacingItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class Fragment_PlayList extends Fragment {
    private EditText edtTitle;
    private TextInputLayout tilTitle;
    PlayListService service;
    PlayListAdapter adapter;
    ArrayList<PlayList> playLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment__play_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rcl_playlist);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            recyclerView.addItemDecoration(new SpacingItemDecoration(2,
                    Tools.dpToPx(Objects.requireNonNull(getContext()), 4), true));
        }

        recyclerView.setHasFixedSize(true);

        FloatingActionButton fabAddPlaylist = root.findViewById(R.id.fab_add_playlist);
        fabAddPlaylist.setOnClickListener(this::fabAddPlaylistOnClick);

        try {
            service = new PlayListService(getContext());
            playLists = service.getAll();

            adapter = new PlayListAdapter(getContext(), playLists);
            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return root;
    }

    private void init(){
        adapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PlayList playList, int position) {

            }

//            @Override
//            public void onClick(View view, BaiHat baiHat, int pos) {
//                Intent intent = new Intent(getContext(), Activity_play_nhac.class);
//                intent.putExtra("song", baiHat);
//
//                startActivity(intent);
//            }
        });
        adapter.setOnMoreItemClick(new PlayListAdapter.OnMoreItemClickListener() {
            @Override
            public void onMoreItemClick(View view, PlayList playList, MenuItem item) {

            }

//            @Override
//            public void onMoreItemClick(View view, BaiHat song, MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_item_play:
//                        Intent intent = new Intent(getContext(), Activity_play_nhac.class);
//                        intent.putExtra("song", song);
//
//                        startActivity(intent);
//                        break;
//                    case R.id.menu_item_them_playlist:
//                        // showPopup();
//                        break;
//                    case R.id.menu_item_rename:
//
//                        break;
//                    case R.id.menu_item_delete:
//
//                        break;
//                }
//            }
        });
    }

    private void fabAddPlaylistOnClick(View view) {
        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams")
        View enterTitleDialog = inflater.inflate(R.layout.layout_dialog, null);

        tilTitle = enterTitleDialog.findViewById(R.id.tilTitle);
        edtTitle = enterTitleDialog.findViewById(R.id.edtTitle);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogBuilder.setTitle("Mời nhập tên playlist");
        dialogBuilder.setView(enterTitleDialog);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Lưu", this::dialogOnPositiveButtonClick);
        dialogBuilder.setNegativeButton("Trở về", this::dialogOnNegativeButtonClick);


        dialogBuilder.show();
    }

    private void dialogOnNegativeButtonClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    private void dialogOnPositiveButtonClick(DialogInterface dialogInterface, int i) {
        String title = edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            tilTitle.setError("The title cannot be empty");
            return;
        }
        tilTitle.setErrorEnabled(false);
        PlayList playList = new PlayList();
        playList.setTenPlayList(title);
        service.add(playList);
        playLists.clear();
        playLists.addAll(service.getAll());
        adapter.notifyDataSetChanged();

    }



}
