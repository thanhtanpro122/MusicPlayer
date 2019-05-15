package com.example.nhom9.musicplayer.DatabaseAccess;

import android.content.Context;
import android.database.Cursor;

import com.example.nhom9.musicplayer.Model.BaiHat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

public class BaiHatService extends DbHelper {
    public BaiHatService(Context context) throws IOException {
        super(context);
    }

    public ArrayList<BaiHat> layDanhSachBaiHat() {
        ArrayList<BaiHat> dsbaihat = new ArrayList<>();

        String query = "SELECT * FROM BaiHat";
        Cursor cursor = database.rawQuery(query, null);

        BaiHat baiHat;
        while (cursor.moveToNext()) {
            baiHat = new BaiHat();
            baiHat.setIdBaiHat(cursor.getInt(0));
            baiHat.setTenBaiHat(cursor.getString(1));
            baiHat.setIdCasi(cursor.getInt(2));
            baiHat.setTenTacGia(cursor.getString(3));
            baiHat.setUrlBaiHat(cursor.getString(4));
            baiHat.setHinhAnh(cursor.getBlob(5));
            dsbaihat.add(baiHat);
        }
        cursor.close();

        return dsbaihat;
    }

    public BaiHat getOne(Object... keys) {
        return null;
    }


    @Deprecated
    public void add(BaiHat baiHat) {
    }


    @Deprecated
    public void delete(Object... keys) {
    }


    @Deprecated
    public void edit(BaiHat oldEntity, BaiHat newEntity) {
    }
}
