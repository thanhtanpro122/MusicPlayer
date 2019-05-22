package com.example.nhom9.musicplayer.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nhom9.musicplayer.DatabaseAccess.CaSiService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;


public class SongPlaylistAdapter extends RecyclerView.Adapter<SongPlaylistAdapter.SongViewHolder> {

    private Context context;
    private ArrayList<BaiHat> songs;
    private OnItemClickListener onItemClick;
    private OnMoreItemClickListener onMoreItemClick;
    private CaSiService caSiService;
    LayoutInflater inflater;

    public SongPlaylistAdapter(Context context, ArrayList<BaiHat> songs) throws IOException {
        this.context = context;
        this.songs = songs;
        caSiService = new CaSiService(context);
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnMoreItemClick(OnMoreItemClickListener onMoreItemClick) {
        this.onMoreItemClick = onMoreItemClick;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        try {
            View view = inflater.inflate(R.layout.row_list_baihat, null, false);
            return new SongViewHolder(view);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        try {
            BaiHat song = songs.get(position);

            byte[] songImg = song.getHinhAnh();
            if (songImg != null) {
                Bitmap image = BitmapFactory.decodeByteArray(songImg, 0, songImg.length);
                songViewHolder.imgSong.setImageBitmap(image);
            }

            String songName = song.getTenBaiHat();
            if (!songName.isEmpty()) {
                songViewHolder.txtName.setText(songName);
            }

            String artist = caSiService.layTenCaSi(song.getIdBaiHat());
            if (!artist.isEmpty()) {
                songViewHolder.txtArtist.setText(artist);
            }

            songViewHolder.lyt_parent.setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onItemClick(view, song);
                }
            });

            songViewHolder.btnMore.setOnClickListener(view -> {
                if (onMoreItemClick != null) {
                    onMoreButtonClick(view, song);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMoreButtonClick(View view, BaiHat song) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            onMoreItemClick.onMoreItemClick(view, song, item);
            return true;
        });
        popupMenu.inflate(R.menu.menu_song_playlist_more);
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, BaiHat song);
    }

    public interface OnMoreItemClickListener {
        void onMoreItemClick(View view, BaiHat song, MenuItem item);
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        CircularImageView imgSong;
        TextView txtName;
        TextView txtArtist;
        ImageButton btnMore;
        View lyt_parent;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                imgSong = itemView.findViewById(R.id.img_song);
                txtName = itemView.findViewById(R.id.txt_play_nhac_ten_baihat);
                txtArtist = itemView.findViewById(R.id.txt_play_nhac_ten_casi);
                btnMore = itemView.findViewById(R.id.btn_more);
                lyt_parent = itemView.findViewById(R.id.lyt_parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
