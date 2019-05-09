package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QuetBaiHatService extends DbHelper {
    private static final String MP3_EXT = ".mp3";
    private static MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private static ArrayList<String> MEDIA_PATHs = new ArrayList<>();

    static {
        MEDIA_PATHs.add(Environment.getExternalStorageDirectory().getPath() + "/Music/");

        File[] fileList = new File("/storage/").listFiles();
        for (File file : fileList) {
            String musicPath = file.getAbsolutePath() + "/Music/";
            if (file.isDirectory() && new File(musicPath).exists() && file.canRead()){
                MEDIA_PATHs.add(musicPath);
            }
        }
    }

    public QuetBaiHatService(Context context) throws IOException {
        super(context);
    }

    public void scanAndSave() {
        for (String path : MEDIA_PATHs) {
            File home = new File(path);
            File[] files = home.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanAndSaveInDirectory(file);
                    } else if (file.getName().endsWith(MP3_EXT)) {
                        extractAndSaveFrom(file.getPath());
                    }
                }
            }
        }
    }

    private void extractAndSaveFrom(String path) {
        retriever.setDataSource(path);
        String query;
        ContentValues values = new ContentValues();
        File file = new File(path);
        String fileName = file.getName();
        String songName = fileName.replace(MP3_EXT, "");
        String filePath = file.getPath();

        int idCaSi = -1;
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist != null) {
            query = "SELECT COUNT(*) FROM CaSi WHERE TenCaSi = ?";
            Cursor cursor2 = database.rawQuery(query, new String[]{artist});
            if (cursor2.moveToNext() && cursor2.getInt(0) <= 0) {
                values.clear();
                values.put("TenCaSi", artist);
                idCaSi = (int) database.insert("CaSi", null, values);
            }
            cursor2.close();
        }

        String tacGia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);

        values.clear();
        values.put("TenBaiHat", songName);
        if (idCaSi != -1) {
            values.put("IdCaSi", idCaSi);
        }
        values.put("TenTacGia", tacGia);
        values.put("UrlBaiHat", path);

        database.insert("BaiHat", null, values);
    }

    private void scanAndSaveInDirectory(File directory) {
        if (directory == null) {
            return;
        }
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanAndSaveInDirectory(file);
                } else if (file.getName().endsWith(MP3_EXT)) {
                    extractAndSaveFrom(file.getPath());
                }
            }
        }
    }
}
