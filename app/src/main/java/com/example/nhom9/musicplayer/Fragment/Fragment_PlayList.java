package com.example.nhom9.musicplayer.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.nhom9.musicplayer.Adapter.PlayListAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
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

    private void fabAddPlaylistOnClick(View view) {
        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams")
        View enterTitleDialog = inflater.inflate(R.layout.layout_dialog, null);

        tilTitle = enterTitleDialog.findViewById(R.id.tilTitle);
        edtTitle = enterTitleDialog.findViewById(R.id.edtTitle);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        dialogBuilder.setTitle("Enter playlist's name");
        dialogBuilder.setView(enterTitleDialog);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNegativeButton("Cancel", this::dialogOnNegativeButtonClick);
        dialogBuilder.setPositiveButton("Save", this::dialogOnPositiveButtonClick);

        dialogBuilder.show();
    }

    private void dialogOnPositiveButtonClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    private void dialogOnNegativeButtonClick(DialogInterface dialogInterface, int i) {
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
