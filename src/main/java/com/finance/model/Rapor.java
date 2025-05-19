package com.finance.model;

import java.time.LocalDateTime;

public class Rapor {
    private int id;
    private int ay;
    private int yil;
    private double gelir;
    private double gider;
    private double bakiye;
    private LocalDateTime olusturmaTarihi;

    public Rapor() {}

    public Rapor(int id, int ay, int yil, double gelir, double gider, double bakiye, LocalDateTime olusturmaTarihi) {
        this.id = id;
        this.ay = ay;
        this.yil = yil;
        this.gelir = gelir;
        this.gider = gider;
        this.bakiye = bakiye;
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAy() { return ay; }
    public void setAy(int ay) { this.ay = ay; }
    public int getYil() { return yil; }
    public void setYil(int yil) { this.yil = yil; }
    public double getGelir() { return gelir; }
    public void setGelir(double gelir) { this.gelir = gelir; }
    public double getGider() { return gider; }
    public void setGider(double gider) { this.gider = gider; }
    public double getBakiye() { return bakiye; }
    public void setBakiye(double bakiye) { this.bakiye = bakiye; }
    public LocalDateTime getOlusturmaTarihi() { return olusturmaTarihi; }
    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) { this.olusturmaTarihi = olusturmaTarihi; }
} 