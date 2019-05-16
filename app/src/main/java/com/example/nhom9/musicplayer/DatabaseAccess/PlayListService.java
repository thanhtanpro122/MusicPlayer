package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.nhom9.musicplayer.Model.BaiHat;
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
    public ArrayList<String> getName() {
        ArrayList<String> playLists = new ArrayList<>();

        String query = "SELECT TenPlayList FROM Playlist";
        Cursor cursor = database.rawQuery(query, null);
        PlayList playList;
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            playLists.add(name);
        }

        cursor.close();

        return playLists;
    }

    public ArrayList<BaiHat> getSongList(String ID)
    {
        ArrayList<BaiHat> songs = new ArrayList<>();

        String query = "SELECT * FROM BaiHat,PlayList_BaiHat WHERE IdBaiHat = IdBH AND IdPL = ?";
        Cursor cursor = database.rawQuery(query, new String[]{ID});
        BaiHat song;
        while (cursor.moveToNext()) {
            song = new BaiHat();
            song.setIdBaiHat(cursor.getInt(0));
            song.setTenTacGia(cursor.getString(1));
            song.setIdCasi(cursor.getInt(2));
            song.setTenTacGia(cursor.getString(3));
            song.setUrlBaiHat(cursor.getString(4));
            song.setHinhAnh(cursor.getBlob(5));

            songs.add(song);
        }
        cursor.close();

        return songs;

    }


    public int getSongNumber(int id)
    {

        String query = "SELECT count(*) FROM PlayList_BaiHat WHERE IdPL = ?";
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

    public  void deletePLaylist(String id)
    {
        try {
            String query = "DELETE FROM Playlist WHERE IdPlayList = ?";
            database.execSQL(query, new String[]{id});

            query = "DELETE FROM PlayList_BaiHat WHERE IdPL = ?";
            database.execSQL(query, new String[]{id});

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public  void rename(String id,String newname)
    {
        try {
            String query = "UPDATE Playlist SET Name = ? WHERE ID = ?";
            database.execSQL(query, new String[]{newname, id});

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void delete(Object... keys) {
        try {
            database.execSQL("DELETE FROM Playlist WHERE ID = ?", new String[]{(String) keys[0]});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
