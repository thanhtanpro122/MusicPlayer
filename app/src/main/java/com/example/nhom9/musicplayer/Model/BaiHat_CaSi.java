package com.example.nhom9.musicplayer.Model;

public class BaiHat_CaSi {
    private int idBaiHat;
    private int idCaSi;

    public BaiHat_CaSi(int idBaiHat, int idCaSi) {
        this.idBaiHat = idBaiHat;
        this.idCaSi = idCaSi;
    }

    public BaiHat_CaSi() {
    }

    public int getIdBaiHat() {
        return idBaiHat;
    }

    public void setIdBaiHat(int idBaiHat) {
        this.idBaiHat = idBaiHat;
    }

    public int getIdCaSi() {
        return idCaSi;
    }

    public void setIdCaSi(int idCaSi) {
        this.idCaSi = idCaSi;
    }
}
