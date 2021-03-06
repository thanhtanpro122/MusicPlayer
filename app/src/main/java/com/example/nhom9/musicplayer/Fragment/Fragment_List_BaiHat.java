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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.Common.Consts;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.DatabaseAccess.QuetBaiHatService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;
import com.example.nhom9.musicplayer.Service.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_List_BaiHat extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private EditText edtTitle;
    private TextInputLayout tilTitle;

    BaiHatService baiHatService;
    private ArrayList<BaiHat> baiHats;
    private  ArrayList<PlayList> playLists;
    private PlayListService playlistService;
    PlayNhacAdapter adapter;
    MediaPlayerService player;

//    public static BaiHat selectedSong;

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
                baiHatService = new BaiHatService(getContext());
                playlistService= new PlayListService(getContext());

                baiHats = baiHatService.layDanhSachBaiHat();
                playLists=playlistService.getAll();

                adapter = new PlayNhacAdapter(getContext(), baiHats);

                adapter.setOnItemClickListener(new PlayNhacAdapter.ItemClickListener() {
                    @Override
                    public void onClick(View view, BaiHat baiHat) {
                        Intent intent = new Intent(getContext(), Activity_play_nhac.class);
                        if(Activity_play_nhac.comingBaiHat != null){
                            Activity_play_nhac.comingBaiHat = baiHat;
                        }
                        if(Activity_play_nhac.currentPlayList != null){
                            Activity_play_nhac.currentPlayList = baiHatService.layDanhSachBaiHat() ;
                        }
                        if(Activity_play_nhac.comingBaiHat == null && Activity_play_nhac.currentPlayList==null){
                            intent.putExtra(Consts.PLAY_LIST, baiHatService.layDanhSachBaiHat());
                            intent.putExtra(Consts.SONG_EXTRA, baiHat);
                        }
                        startActivity(intent);
                    }
                });
                adapter.setOnMoreItemClick(new PlayNhacAdapter.OnMoreItemClickListener() {
                    @Override
                    public void onMoreItemClick(View view, BaiHat song, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_play:
                                Intent intent = new Intent(getContext(), Activity_play_nhac.class);
                                if(Activity_play_nhac.comingBaiHat != null){
                                    Activity_play_nhac.comingBaiHat = song;
                                }
                                if(Activity_play_nhac.currentPlayList != null){
                                    Activity_play_nhac.currentPlayList = baiHatService.layDanhSachBaiHat() ;
                                }
                                if(Activity_play_nhac.comingBaiHat == null && Activity_play_nhac.currentPlayList==null){
                                    intent.putExtra(Consts.PLAY_LIST, baiHatService.layDanhSachBaiHat());
                                    intent.putExtra(Consts.SONG_EXTRA, song);
                                }
                                startActivity(intent);
                                break;
                            case R.id.menu_item_them_playlist:
                                showPopup(song);
                                break;
                            case R.id.menu_item_rename:
                                Rename(song);
                                break;
                            case R.id.menu_item_delete:
                                Delete(song);
                                break;
                        }
                    }
                });
                rclbaiHat.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void Delete(BaiHat baiHat){
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        confirmDelete.setTitle("Xóa bài hát");
        confirmDelete.setMessage("Bạn có chắc chắn muốn xóa bài hát: "+ baiHat.getTenBaiHat() + "?");
        confirmDelete.setPositiveButton("yes", (dialogInterface, i) -> {
            baiHatService.deleteID(baiHat.getIdBaiHat());
            baiHats.clear();
            ArrayList<BaiHat> listBaiHat = baiHatService.layDanhSachBaiHat();
            baiHats.addAll(listBaiHat);
            //Cập nhật lại danh sách bài hát
            MediaPlayerService player = Activity_play_nhac.binder.getService();
            player.updateListBaiHat(listBaiHat);
            //
            adapter.notifyDataSetChanged();
        });
        confirmDelete.setNegativeButton("Hủy bỏ", (dialogInterface, i) -> dialogInterface.dismiss());
        confirmDelete.show();
    }
    private void Rename(BaiHat baiHat){
        AlertDialog.Builder renameDialog= new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        renameDialog.setTitle("Đổi tên bài hát");
        renameDialog.setMessage("Nhập tên khác : "+ baiHat.getTenBaiHat());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view= inflater.inflate(R.layout.layout_rename_song, null);
        EditText editTenBaiHat= view.findViewById(R.id.edtTitle);
        editTenBaiHat.setText(baiHat.getTenBaiHat());
        renameDialog.setView(view);
        renameDialog.setPositiveButton("Lưu", (dialogInterface, i) -> {
            BaiHat baiHatMoi = new BaiHat();
            baiHatMoi.setTenBaiHat(editTenBaiHat.getText().toString());
            baiHatService.edit(baiHat, baiHatMoi);
            baiHats.clear();
            ArrayList<BaiHat> listBaiHat = baiHatService.layDanhSachBaiHat();
            baiHats.addAll(listBaiHat);
            //Cập nhật lại danh sách bài hát
            MediaPlayerService player = Activity_play_nhac.binder.getService();
            player.updateListBaiHat(listBaiHat);
            //
            adapter.notifyDataSetChanged();

        });
        renameDialog.setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());
        renameDialog.show();
    }
    private void showPopup(BaiHat song) {

        List<String> PlaylistName = new ArrayList<String>();
        try
        {
            for (PlayList playlist : playLists)
            {
                PlaylistName.add(playlist.getTenPlayList());
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //Create sequence of items
        final CharSequence[] ListName = PlaylistName.toArray(new String[PlaylistName.size()]);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Mời bạn chọn Playlist:");
        dialogBuilder.setItems(ListName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                try{
                    playlistService.addPlaylist_BaiHat(playLists.get(item).getIdPlayList(),song.getIdBaiHat());
                    if(Activity_play_nhac.binder!=null){
                        player = Activity_play_nhac.binder.getService();
                        player.updateListBaiHat(playlistService.getSongList(String.valueOf(playLists.get(item).getIdPlayList())));
                    }
                    int count  = playlistService.getSongNumber(playLists.get(item).getIdPlayList());
                }catch (Exception e){
                    Toast.makeText(getContext(),"Bạn đã có bài hát này trong playlist",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }




}
