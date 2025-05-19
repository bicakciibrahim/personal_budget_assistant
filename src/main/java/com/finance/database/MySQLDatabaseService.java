package com.finance.database;

import com.finance.model.*;
import java.sql.*;
import java.util.*;
import java.sql.Date;
import com.finance.model.Rapor;
import javax.swing.JOptionPane;

public class MySQLDatabaseService implements DatabaseService {
    private final DatabaseManager dbManager;

    public MySQLDatabaseService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public List<GelirKategorisi> getAllGelirKategorileri() {
        List<GelirKategorisi> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad, olusturma_tarihi FROM gelir_kategorileri ORDER BY ad")) {
            while (rs.next()) {
                GelirKategorisi k = new GelirKategorisi();
                k.setId(String.valueOf(rs.getInt("id")));
                k.setAd(rs.getString("ad"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addGelirKategorisi(GelirKategorisi kategori) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO gelir_kategorileri (ad) VALUES (?)")) {
            ps.setString(1, kategori.getAd());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGelirKategorisi(GelirKategorisi kategori) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE gelir_kategorileri SET ad=? WHERE id=?")) {
            ps.setString(1, kategori.getAd());
            ps.setInt(2, Integer.parseInt(kategori.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGelirKategorisi(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gelir_kategorileri WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<GiderKategorisi> getAllGiderKategorileri() {
        List<GiderKategorisi> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad, olusturma_tarihi FROM gider_kategorileri ORDER BY ad")) {
            while (rs.next()) {
                GiderKategorisi k = new GiderKategorisi();
                k.setId(String.valueOf(rs.getInt("id")));
                k.setAd(rs.getString("ad"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addGiderKategorisi(GiderKategorisi kategori) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO gider_kategorileri (ad) VALUES (?)")) {
            ps.setString(1, kategori.getAd());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGiderKategorisi(GiderKategorisi kategori) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE gider_kategorileri SET ad=? WHERE id=?")) {
            ps.setString(1, kategori.getAd());
            ps.setInt(2, Integer.parseInt(kategori.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGiderKategorisi(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gider_kategorileri WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Gelir> getAllGelirler() {
        List<Gelir> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, kategori_id, tutar, aciklama, tarih, olusturma_tarihi FROM gelirler ORDER BY tarih DESC")) {
            while (rs.next()) {
                Gelir gelir = new Gelir();
                gelir.setId(rs.getInt("id"));
                gelir.setKategoriId(String.valueOf(rs.getInt("kategori_id")));
                gelir.setTutar(rs.getDouble("tutar"));
                gelir.setAciklama(rs.getString("aciklama"));
                gelir.setTarih(rs.getDate("tarih").toLocalDate());
                list.add(gelir);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addGelir(Gelir gelir) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO gelirler (kategori_id, tutar, aciklama, tarih) VALUES (?, ?, ?, ?)"
             )) {
            ps.setInt(1, Integer.parseInt(gelir.getKategoriId()));
            ps.setDouble(2, gelir.getTutar());
            ps.setString(3, gelir.getAciklama());
            ps.setDate(4, Date.valueOf(gelir.getTarih()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGelir(Gelir gelir) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE gelirler SET kategori_id=?, tutar=?, aciklama=?, tarih=? WHERE id=?"
             )) {
            ps.setInt(1, Integer.parseInt(gelir.getKategoriId()));
            ps.setDouble(2, gelir.getTutar());
            ps.setString(3, gelir.getAciklama());
            ps.setDate(4, Date.valueOf(gelir.getTarih()));
            ps.setInt(5, gelir.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGelir(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gelirler WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Gider> getAllGiderler() {
        List<Gider> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, kategori_id, tutar, aciklama, tarih, olusturma_tarihi FROM giderler ORDER BY tarih DESC")) {
            while (rs.next()) {
                Gider gider = new Gider();
                gider.setId(rs.getInt("id"));
                gider.setKategoriId(String.valueOf(rs.getInt("kategori_id")));
                gider.setTutar(rs.getDouble("tutar"));
                gider.setAciklama(rs.getString("aciklama"));
                gider.setTarih(rs.getDate("tarih").toLocalDate());
                list.add(gider);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addGider(Gider gider) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO giderler (kategori_id, tutar, aciklama, tarih) VALUES (?, ?, ?, ?)"
             )) {
            ps.setInt(1, Integer.parseInt(gider.getKategoriId()));
            ps.setDouble(2, gider.getTutar());
            ps.setString(3, gider.getAciklama());
            ps.setDate(4, Date.valueOf(gider.getTarih()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGider(Gider gider) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE giderler SET kategori_id=?, tutar=?, aciklama=?, tarih=? WHERE id=?"
             )) {
            ps.setInt(1, Integer.parseInt(gider.getKategoriId()));
            ps.setDouble(2, gider.getTutar());
            ps.setString(3, gider.getAciklama());
            ps.setDate(4, Date.valueOf(gider.getTarih()));
            ps.setInt(5, gider.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGider(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM giderler WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AylikButce> getAllAylikButceler() {
        List<AylikButce> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ay, yil, tutar, olusturma_tarihi FROM butce ORDER BY yil DESC, ay DESC")) {
            while (rs.next()) {
                AylikButce butce = new AylikButce();
                butce.setId(rs.getInt("id"));
                butce.setAy(rs.getInt("ay"));
                butce.setYil(rs.getInt("yil"));
                butce.setTutar(rs.getDouble("tutar"));
                list.add(butce);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addAylikButce(AylikButce butce) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO butce (ay, yil, tutar) VALUES (?, ?, ?)"
             )) {
            ps.setInt(1, butce.getAy());
            ps.setInt(2, butce.getYil());
            ps.setDouble(3, butce.getTutar());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAylikButce(AylikButce butce) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE butce SET ay=?, yil=?, tutar=? WHERE id=?"
             )) {
            ps.setInt(1, butce.getAy());
            ps.setInt(2, butce.getYil());
            ps.setDouble(3, butce.getTutar());
            ps.setInt(4, butce.getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Bütçe güncellenemedi: Kayıt bulunamadı.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Bütçe güncellenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void deleteAylikButce(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM butce WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TasarrufHedefi> getAllTasarrufHedefleri() {
        List<TasarrufHedefi> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad, hedef_tutar, birikim_tutar, olusturma_tarihi FROM tasarruf_hedefleri ORDER BY id DESC")) {
            while (rs.next()) {
                TasarrufHedefi hedef = new TasarrufHedefi();
                hedef.setId(rs.getInt("id"));
                hedef.setAd(rs.getString("ad"));
                hedef.setHedefTutar(rs.getDouble("hedef_tutar"));
                hedef.setBirikimTutar(rs.getDouble("birikim_tutar"));
                list.add(hedef);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addTasarrufHedefi(TasarrufHedefi hedef) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO tasarruf_hedefleri (ad, hedef_tutar, birikim_tutar) VALUES (?, ?, ?)"
             )) {
            ps.setString(1, hedef.getAd());
            ps.setDouble(2, hedef.getHedefTutar());
            ps.setDouble(3, hedef.getBirikimTutar());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTasarrufHedefi(TasarrufHedefi hedef) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE tasarruf_hedefleri SET ad=?, hedef_tutar=?, birikim_tutar=? WHERE id=?"
             )) {
            ps.setString(1, hedef.getAd());
            ps.setDouble(2, hedef.getHedefTutar());
            ps.setDouble(3, hedef.getBirikimTutar());
            ps.setInt(4, hedef.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTasarrufHedefi(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tasarruf_hedefleri WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Not> getAllNotlar() {
        List<Not> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, baslik, icerik, olusturma_tarihi FROM notlar ORDER BY id DESC")) {
            while (rs.next()) {
                Not not = new Not();
                not.setId(rs.getInt("id"));
                not.setBaslik(rs.getString("baslik"));
                not.setIcerik(rs.getString("icerik"));
                list.add(not);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addNot(Not not) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO notlar (baslik, icerik) VALUES (?, ?)"
             )) {
            ps.setString(1, not.getBaslik());
            ps.setString(2, not.getIcerik());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNot(Not not) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE notlar SET baslik=?, icerik=? WHERE id=?"
             )) {
            ps.setString(1, not.getBaslik());
            ps.setString(2, not.getIcerik());
            ps.setInt(3, not.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteNot(int id) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM notlar WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMySQLTables() {
        try (Statement stmt = dbManager.getMySQLConnection().createStatement()) {
            // ... mevcut tablolar ...
            // Raporlar tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS raporlar (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ay INT," +
                    "yil INT," +
                    "gelir DECIMAL(10,2)," +
                    "gider DECIMAL(10,2)," +
                    "bakiye DECIMAL(10,2)," +
                    "olusturma_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRapor(int ay, int yil, double gelir, double gider, double bakiye) {
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO raporlar (ay, yil, gelir, gider, bakiye) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, ay);
            ps.setInt(2, yil);
            ps.setDouble(3, gelir);
            ps.setDouble(4, gider);
            ps.setDouble(5, bakiye);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Rapor> getAllRaporlar() {
        List<Rapor> list = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM raporlar ORDER BY yil DESC, ay DESC, olusturma_tarihi DESC")) {
            while (rs.next()) {
                Rapor rapor = new Rapor();
                rapor.setId(rs.getInt("id"));
                rapor.setAy(rs.getInt("ay"));
                rapor.setYil(rs.getInt("yil"));
                rapor.setGelir(rs.getDouble("gelir"));
                rapor.setGider(rs.getDouble("gider"));
                rapor.setBakiye(rs.getDouble("bakiye"));
                rapor.setOlusturmaTarihi(rs.getTimestamp("olusturma_tarihi").toLocalDateTime());
                list.add(rapor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
} 