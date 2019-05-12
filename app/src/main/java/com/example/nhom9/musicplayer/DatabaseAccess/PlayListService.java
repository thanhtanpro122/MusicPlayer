package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.nhom9.musicplayer.Model.PlayList;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlayListService extends DbHelper {

    public PlayListService(Context context) throws IOException {
        super(context);
    }
    public ArrayList<PlayList> getAll() {
        ArrayList<PlayList> playLists = new ArrayList<>();

        String query = "SELECT * FROM PLAYLIST";
        Cursor cursor = database.rawQuery(query, null);
        PlayList playList;
        while (cursor.moveToNext()) {
            playList = new PlayList();
            playList.setIdPlayList(cursor.getInt(0));
            playList.setTenPlayList(cursor.getString(1));


            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            playList.setNgayTao(date);
            playList.setHinhAnh(cursor.getBlob(3));

            playLists.add(playList);
        }

        cursor.close();

        return playLists;
    }
    public int getSongNumber(int id)
    {

        String query = "SELECT count(*) FROM PlayList_BaiHat WHERE IdPlayList = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        cursor.close();

        return 0;
    }

    public void add(PlayList playList) {
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));
        }
        ContentValues values = new ContentValues();
        values.put("ID",String.valueOf(System.currentTimeMillis()));
        values.put("Name", playList.getTenPlayList());
        values.put("CreateDate",dateFormat.format(new Date()));
        database.insert("PlayList",null,values);
    }

}
