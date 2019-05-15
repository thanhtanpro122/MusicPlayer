package com.example.nhom9.musicplayer.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.DatabaseAccess.QuetBaiHatService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_List_BaiHat extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private EditText edtTitle;
    private TextInputLayout tilTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__list__bai_hat, container, false);
        loadData(view);


//        FloatingActionButton fabAddSongs = view.findViewById(R.id.menu_item_rename);
//        fabAddSongs.setOnClickListener(this::fabAddSongOnClick);


//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        }
//        else {
//            loadData(view);
//        }
        return view;
    }
    private void loadData(View view) {

            RecyclerView rclbaiHat = view.findViewById(R.id.recycler_play_baihat);
            rclbaiHat.setLayoutManager(new LinearLayoutManager(getContext()));
            rclbaiHat.setHasFixedSize(true);

            try {
                BaiHatService baiHatService = new BaiHatService(getContext());

                PlayNhacAdapter adapter = new PlayNhacAdapter(getContext(), baiHatService.layDanhSachBaiHat());

                adapter.setOnItemClickListener(new PlayNhacAdapter.ItemClickListener() {
                    @Override
                    public void onClick(View view, BaiHat baiHat, int pos) {
                        Intent intent = new Intent(getContext(), Activity_play_nhac.class);
                        intent.putExtra("song", baiHat);

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

                                break;
                            case R.id.menu_item_delete:

                                break;
                        }
                    }
                });
                rclbaiHat.setAdapter(adapter);
            } catch (Exception ignored) {
            }

    }
//    private void fabAddSongOnClick(View view) {
//        LayoutInflater inflater = getLayoutInflater();
//
//        @SuppressLint("InflateParams")
//        View enterTitleDialog = inflater.inflate(R.layout.layout_rename_song, null);
//
//        tilTitle = enterTitleDialog.findViewById(R.id.tilTitle);
//        edtTitle = enterTitleDialog.findViewById(R.id.edtTitle);
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
//        dialogBuilder.setTitle("Đổi tên");
//        dialogBuilder.setView(enterTitleDialog);
//        dialogBuilder.setCancelable(true);
//        dialogBuilder.setPositiveButton("Lưu", this::dialogOnPositiveButtonClick);
//        dialogBuilder.setNegativeButton("Trở về", this::dialogOnNegativeButtonClick);
//
//
//        dialogBuilder.show();
//    }
//    private void dialogOnPositiveButtonClick(DialogInterface dialogInterface, int i) {
//        dialogInterface.dismiss();
//    }
//
//    private void dialogOnNegativeButtonClick(DialogInterface dialogInterface, int i) {
////        String title = edtTitle.getText().toString().trim();
////        if (title.isEmpty()) {
////            tilTitle.setError("The title cannot be empty");
////            return;
////        }
////        tilTitle.setErrorEnabled(false);
////        PlayList playList = new PlayList();
////        playList.setTenPlayList(title);
////        service.add(playList);
////        playLists.clear();
////        playLists.addAll(service.getAll());
////        adapter.notifyDataSetChanged();
//
//    }



}
