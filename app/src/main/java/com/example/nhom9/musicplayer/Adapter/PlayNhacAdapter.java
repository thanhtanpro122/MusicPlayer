package com.example.nhom9.musicplayer.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.example.nhom9.musicplayer.DatabaseAccess.CaSiService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;

import java.util.ArrayList;

public class PlayNhacAdapter extends RecyclerView.Adapter<PlayNhacAdapter.ViewHolder> {

    Context context;
    ArrayList<BaiHat> mangbaihat;
    CaSiService caSiService;
    ItemClickListener onItemClickListener;

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PlayNhacAdapter(Context context, ArrayList<BaiHat> mangbaihat) {
        this.context = context;
        this.mangbaihat = mangbaihat;
        try {
            caSiService = new CaSiService(context);
        } catch (Exception ignored) {
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        try{
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.row_list_baihat, viewGroup, false);
            return new ViewHolder(view);
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final BaiHat baihat = mangbaihat.get(i);
        viewHolder.txtIndex.setText(i +1 + "");
        viewHolder.txtTencasi.setText(caSiService.layTenCaSi(baihat.getIdCasi()));
        viewHolder.txtTenbaihat.setText(baihat.getTenBaiHat());
        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(view, baihat, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangbaihat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIndex, txtTenbaihat, txtTencasi;
        View parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtIndex = itemView.findViewById(R.id.txt_nhac_index);
            txtTenbaihat = itemView.findViewById(R.id.txt_play_nhac_ten_baihat);
            txtTencasi = itemView.findViewById(R.id.txt_play_nhac_ten_casi);
        }
    }

    public interface ItemClickListener {
        void onClick(View view, BaiHat baiHat, int pos);
    }
}