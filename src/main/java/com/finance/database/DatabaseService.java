package com.finance.database;

import com.finance.model.*;
import java.util.List;

public interface DatabaseService {
    // Gelir Kategorileri
    List<GelirKategorisi> getAllGelirKategorileri();
    void addGelirKategorisi(GelirKategorisi kategori);
    void updateGelirKategorisi(GelirKategorisi kategori);
    void deleteGelirKategorisi(String id);

    // Gelirler
    List<Gelir> getAllGelirler();
    void addGelir(Gelir gelir);
    void updateGelir(Gelir gelir);
    void deleteGelir(String id);

    // Gider Kategorileri
    List<GiderKategorisi> getAllGiderKategorileri();
    void addGiderKategorisi(GiderKategorisi kategori);
    void updateGiderKategorisi(GiderKategorisi kategori);
    void deleteGiderKategorisi(String id);

    // Giderler
    List<Gider> getAllGiderler();
    void addGider(Gider gider);
    void updateGider(Gider gider);
    void deleteGider(String id);

    // Bütçe
    List<AylikButce> getAllAylikButceler();
    void addAylikButce(AylikButce butce);
    void updateAylikButce(AylikButce butce);
    void deleteAylikButce(String id);

    // Tasarruf Hedefleri
    List<TasarrufHedefi> getAllTasarrufHedefleri();
    void addTasarrufHedefi(TasarrufHedefi hedef);
    void updateTasarrufHedefi(TasarrufHedefi hedef);
    void deleteTasarrufHedefi(String id);

    // Notlar
    List<Not> getAllNotlar();
    void addNot(Not not);
    void updateNot(Not not);
    void deleteNot(String id);

    // Raporlar
    void addRapor(int ay, int yil, double gelir, double gider, double bakiye);
    java.util.List<com.finance.model.Rapor> getAllRaporlar();
} 