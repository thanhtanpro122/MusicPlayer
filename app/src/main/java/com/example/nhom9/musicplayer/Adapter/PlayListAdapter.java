package com.example.nhom9.musicplayer.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nhom9.musicplayer.DatabaseAccess.PlayListService;
import com.example.nhom9.musicplayer.Model.PlayList;
import com.example.nhom9.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {
    private Context context;
    private ArrayList<PlayList> playLists;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClick;
    private OnMoreItemClickListener onMoreItemClick;

    public PlayListAdapter(Context context, ArrayList<PlayList> playLists)
    {
        this.context = context;
        this.playLists=playLists;
        inflater = LayoutInflater.from(context);
    }

    public PlayListAdapter()
    {

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClick = onItemClickListener;
    }
    public void setOnMoreItemClick(OnMoreItemClickListener onMoreItemClick) {
        this.onMoreItemClick = onMoreItemClick;
    }



    @NonNull
    @Override
    //khi mới chạy lên đi vào hàm này
    //
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_playlist, viewGroup, false);
        return new PlayListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PlayListViewHolder playListViewHolder, int position) {


        try {
            PlayListService service= new PlayListService(context);
            PlayList playList = playLists.get(position);
            playListViewHolder.txtPlayListName.setText(playList.getTenPlayList());
            playListViewHolder.txtSongCount.setText(String.valueOf(service.getSongNumber(playList.getIdPlayList()))+" bài hát");
            playListViewHolder.parent.setOnClickListener(view ->{
                if(onItemClick!=null)
                {
                    onItemClick.onItemClick(view,playList);
                }
            });

            playListViewHolder.btnMore.setOnClickListener(view ->{
                btnMoreClick(view,playList);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void btnMoreClick(View view, PlayList playList) {
        android.support.v7.widget.PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            onMoreItemClick.onMoreItemClick(view, playList, item);
            return true;
        });
        popupMenu.inflate(R.menu.menu_more_playlist);
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    class PlayListViewHolder extends RecyclerView.ViewHolder {

        View parent;
        TextView txtPlayListName;
        TextView txtSongCount;
        ImageButton btnMore;

        public PlayListViewHolder(@NonNull View itemView)
        {

            super(itemView);
            parent = itemView.findViewById(R.id.lyt_parent);
            txtPlayListName = itemView.findViewById(R.id.txt_song_name);
            txtSongCount = itemView.findViewById(R.id.txt_artist);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, PlayList playList);
    }

    public interface OnMoreItemClickListener {
        void onMoreItemClick(View view, PlayList playList, MenuItem item);
    }


}
