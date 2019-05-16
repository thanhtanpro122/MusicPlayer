package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.Context;
import android.database.Cursor;

import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.CaSi;

import java.io.IOException;
import java.util.ArrayList;

public class CaSiService extends DbHelper {
    public CaSiService(Context context) throws IOException {
        super(context);
    }
    public String getSongArtist(String idSong) {
        String query = "SELECT Name " +
                "FROM Artist, Song_Artist " +
                "WHERE ID = ID_Artist AND ID_Song = ?";
        Cursor cursor = database.rawQuery(query, new String[]{idSong});
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    public ArrayList<CaSi> getAll() {
        ArrayList<CaSi> caSis = new ArrayList<>();

        String query = "SELECT * FROM Artist";
        Cursor cursor = database.rawQuery(query, null);

        CaSi caSi;

        while(cursor.moveToNext()){
            caSi = new CaSi();
            caSi.setIdCaSi(cursor.getInt(0));
            caSi.setTenCaSi(cursor.getString(1));

            caSis.add(caSi);
        }

        cursor.close();
        return caSis;
    }


    public CaSi getOne(Object... keys) {
        return null;
    }


    public void add(CaSi artist) {

    }


    public void delete(Object... keys) {

    }


    public void edit(CaSi oldEntity, CaSi newEntity) {

    }

    public ArrayList<BaiHat> getAllSongOfArtist(int idArtist) {
        ArrayList<BaiHat> songs = new ArrayList<>();

//        String query = "select * from Song_Artist, Song where ID_Artist="+idArtist+" and Song.ID=Song_Artist.ID_Song";
        String query = "select * from BaiHat";
        Cursor cursor = database.rawQuery(query, null);
        BaiHat song;
        while (cursor.moveToNext()) {
            song = new BaiHat();
            song.setIdBaiHat(cursor.getInt(0));
            song.setTenBaiHat(cursor.getString(1));
            song.setIdCasi(cursor.getInt(2));
            song.setTenTacGia(cursor.getString(3));
            song.setUrlBaiHat(cursor.getString(4));
            song.setHinhAnh(cursor.getBlob(5));

            songs.add(song);
        }

        cursor.close();
        return songs;
    }
    public String layTenCaSi(int idCaSi) {
        String query = "SELECT TenCaSi FROM CaSi WHERE IdCaSi = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(idCaSi)});
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }
}
