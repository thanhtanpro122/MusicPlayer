package com.example.nhom9.musicplayer.Model;

public class CaSi {
    private int idCaSi;
    private String tenCaSi;

    public CaSi() {
    }

    public CaSi(int idCaSi, String tenCaSi) {
        this.idCaSi = idCaSi;
        this.tenCaSi = tenCaSi;
    }

    public int getIdCaSi() {
        return idCaSi;
    }

    public void setIdCaSi(int idCaSi) {
        this.idCaSi = idCaSi;
    }

    public String getTenCaSi() {
        return tenCaSi;
    }

    public void setTenCaSi(String tenCaSi) {
        this.tenCaSi = tenCaSi;
    }
}
