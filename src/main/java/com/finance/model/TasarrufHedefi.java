package com.finance.model;

import java.time.LocalDateTime;

public class TasarrufHedefi {
    private int id;
    private String ad;
    private double hedefTutar;
    private double birikimTutar;
    private LocalDateTime olusturmaTarihi;

    public TasarrufHedefi() {
        this.olusturmaTarihi = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public double getHedefTutar() {
        return hedefTutar;
    }

    public void setHedefTutar(double hedefTutar) {
        this.hedefTutar = hedefTutar;
    }

    public double getBirikimTutar() {
        return birikimTutar;
    }

    public void setBirikimTutar(double birikimTutar) {
        this.birikimTutar = birikimTutar;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
} 