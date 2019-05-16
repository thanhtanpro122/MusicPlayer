package com.example.nhom9.musicplayer.Model;

import java.io.Serializable;
import java.util.Date;

public class PlayList implements Serializable {
    private int idPlayList;
    private String tenPlayList;
    private Date ngayTao;
    private byte[] hinhAnh;

    public PlayList() {
    }

    public PlayList(int idPlayList, String tenPlayList, Date ngayTao, byte[] hinhAnh) {
        this.idPlayList = idPlayList;
        this.tenPlayList = tenPlayList;
        this.ngayTao = ngayTao;
        this.hinhAnh = hinhAnh;
    }

    public int getIdPlayList() {
        return idPlayList;
    }

    public void setIdPlayList(int idPlayList) {
        this.idPlayList = idPlayList;
    }

    public String getTenPlayList() {
        return tenPlayList;
    }

    public void setTenPlayList(String tenPlayList) {
        this.tenPlayList = tenPlayList;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public byte[] getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
