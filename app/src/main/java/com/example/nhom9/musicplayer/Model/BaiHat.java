package com.example.nhom9.musicplayer.Model;

import android.app.Service;

import java.io.Serializable;

public class BaiHat implements Serializable {
    private int idBaiHat;
    private String tenBaiHat;
    private int idCasi;
    private String tenTacGia;
    private String urlBaiHat;

    public BaiHat(int idBaiHat, String tenBaiHat, int idCasi, String tenTacGia, String urlBaiHat) {
        this.idBaiHat = idBaiHat;
        this.tenBaiHat = tenBaiHat;
        this.idCasi = idCasi;
        this.tenTacGia = tenTacGia;
        this.urlBaiHat = urlBaiHat;
    }

    public BaiHat() {
    }

    public int getIdBaiHat() {
        return idBaiHat;
    }

    public void setIdBaiHat(int idBaiHat) {
        this.idBaiHat = idBaiHat;
    }

    public String getTenBaiHat() {
        return tenBaiHat;
    }

    public void setTenBaiHat(String tenBaiHat) {
        this.tenBaiHat = tenBaiHat;
    }

    public int getIdCasi() {
        return idCasi;
    }

    public void setIdCasi(int idCasi) {
        this.idCasi = idCasi;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public String getUrlBaiHat() {
        return urlBaiHat;
    }

    public void setUrlBaiHat(String urlBaiHat) {
        this.urlBaiHat = urlBaiHat;
    }
}
