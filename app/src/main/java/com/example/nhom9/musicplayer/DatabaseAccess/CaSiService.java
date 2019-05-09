package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.Context;
import android.database.Cursor;

import java.io.IOException;

public class CaSiService extends DbHelper {
    public CaSiService(Context context) throws IOException {
        super(context);
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
