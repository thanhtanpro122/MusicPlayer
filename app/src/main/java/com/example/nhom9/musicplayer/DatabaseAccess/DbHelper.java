package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DbHelper {
    private Context context;
    private static final String DB_NAME = "media_player.sqlite";
    private static final String DB_PATH = "/databases/";
    SQLiteDatabase database;

    DbHelper(Context context) throws IOException {
        this.context = context;

        processCopyDatabase();
        database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    private void processCopyDatabase() throws IOException {
        File dbFile = context.getDatabasePath(DB_NAME);
        //dbFile.delete();
        if (!dbFile.exists()) {
            copyDatabaseFormAssets();
        }
    }

    private String getSavedPath() {
        return context.getApplicationInfo().dataDir + DB_PATH + DB_NAME;
    }

    private void copyDatabaseFormAssets() throws IOException {
        InputStream fileIn = context.getAssets().open("database/" + DB_NAME);

        File file = new File(context.getApplicationInfo().dataDir + DB_PATH);
        if (!file.exists() && !file.mkdir()) {
            throw new IOException("Cannot make folder " + file.getPath());
        }

        String fileOutName = getSavedPath();
        OutputStream fileOut = new FileOutputStream(fileOutName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileIn.read(buffer)) > 0) {
            fileOut.write(buffer, 0, length);
        }

        fileOut.flush();
        fileOut.close();
        fileIn.close();
    }
}
