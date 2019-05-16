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

    public ArrayList<BaiHat> timKiemBaiHat(String tenbaihat){
        ArrayList<BaiHat> dsBaiHat = new ArrayList<>();

        String query = "SELECT * FROM BaiHat where TenBaiHat like '%"+tenbaihat+"%'";
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
            dsBaiHat.add(baiHat);
        }
        cursor.close();

        return dsBaiHat;
    }

    @Deprecated
    public void add(BaiHat baiHat) {
    }


    public void delete(Object... keys) {
        if(keys !=null && keys.length>0)
        {
            String query = "DELETE FROM BaiHat WHERE IdBaiHat= ? ";
            database.execSQL(query, new String[]{(String)keys[0]});
        }
    }

    public  void deleteID(int id)
    {
        String query = "DELETE FROM BaiHat WHERE IdBaiHat= ? ";
        database.execSQL(query, new Integer[]{id});
    }


    public void edit(BaiHat oldEntity, BaiHat newEntity) {
        String query= "UPDATE BaiHat SET TenBaiHat = ? WHERE IdBaiHat= ?";
        database.execSQL(query, new String[]{newEntity.getTenBaiHat(),String.valueOf(oldEntity.getIdBaiHat())});
    }
}
