package com.example.nhom9.musicplayer.Model;

public class PlayList_BaiHat {
    private int idPlayList;
    private int idBaiHat;

    public PlayList_BaiHat() {
    }

    public PlayList_BaiHat(int idPlayList, int idBaiHat) {
        this.idPlayList = idPlayList;
        this.idBaiHat = idBaiHat;
    }

    public int getIdPlayList() {
        return idPlayList;
    }

    public void setIdPlayList(int idPlayList) {
        this.idPlayList = idPlayList;
    }

    public int getIdBaiHat() {
        return idBaiHat;
    }

    public void setIdBaiHat(int idBaiHat) {
        this.idBaiHat = idBaiHat;
    }
}
