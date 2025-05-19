package com.finance.model;

import java.time.LocalDateTime;

public class GiderKategorisi {
    private String id;
    private String ad;
    private LocalDateTime olusturmaTarihi;

    public GiderKategorisi() {
        this.olusturmaTarihi = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
} 