package com.finance.console;

import com.finance.database.DatabaseManager;
import com.finance.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void start() {
        while (true) {
            System.out.println("\n=== Finans Yönetim Sistemi ===");
            System.out.println("1. Gelirler");
            System.out.println("2. Giderler");
            System.out.println("3. Kategoriler");
            System.out.println("4. Çıkış");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: gelirMenu(); break;
                case 2: giderMenu(); break;
                case 3: kategoriMenu(); break;
                case 4: System.out.println("Çıkılıyor..."); return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    private void gelirMenu() {
        while (true) {
            System.out.println("\n--- Gelirler ---");
            System.out.println("1. Gelirleri Listele");
            System.out.println("2. Gelir Ekle");
            System.out.println("3. Gelir Sil");
            System.out.println("0. Geri");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: listGelirler(); break;
                case 2: addGelir(); break;
                case 3: deleteGelir(); break;
                case 0: return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    private void giderMenu() {
        while (true) {
            System.out.println("\n--- Giderler ---");
            System.out.println("1. Giderleri Listele");
            System.out.println("2. Gider Ekle");
            System.out.println("3. Gider Sil");
            System.out.println("0. Geri");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: listGiderler(); break;
                case 2: addGider(); break;
                case 3: deleteGider(); break;
                case 0: return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    private void kategoriMenu() {
        while (true) {
            System.out.println("\n--- Kategoriler ---");
            System.out.println("1. Gelir Kategorileri");
            System.out.println("2. Gider Kategorileri");
            System.out.println("0. Geri");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: gelirKategoriMenu(); break;
                case 2: giderKategoriMenu(); break;
                case 0: return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    private void gelirKategoriMenu() {
        while (true) {
            System.out.println("\n--- Gelir Kategorileri ---");
            System.out.println("1. Listele");
            System.out.println("2. Ekle");
            System.out.println("3. Sil");
            System.out.println("0. Geri");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: listGelirKategorileri(); break;
                case 2: addGelirKategori(); break;
                case 3: deleteGelirKategori(); break;
                case 0: return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    private void giderKategoriMenu() {
        while (true) {
            System.out.println("\n--- Gider Kategorileri ---");
            System.out.println("1. Listele");
            System.out.println("2. Ekle");
            System.out.println("3. Sil");
            System.out.println("0. Geri");
            System.out.print("Seçiminiz: ");
            int secim = getIntInput();
            switch (secim) {
                case 1: listGiderKategorileri(); break;
                case 2: addGiderKategori(); break;
                case 3: deleteGiderKategori(); break;
                case 0: return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }

    // --- Gelir/Gider/Kategori İşlemleri ---
    private void listGelirler() {
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT g.id, k.ad, g.tutar, g.aciklama, g.tarih FROM gelirler g LEFT JOIN gelir_kategorileri k ON g.kategori_id = k.id ORDER BY g.tarih DESC")) {
            System.out.println("ID | Kategori | Tutar | Açıklama | Tarih");
            while (rs.next()) {
                System.out.printf("%d | %s | %.2f | %s | %s\n",
                        rs.getInt("id"), rs.getString("ad"), rs.getDouble("tutar"), rs.getString("aciklama"), rs.getDate("tarih").toLocalDate().format(dateFormatter));
            }
        } catch (SQLException e) {
            System.out.println("Gelirler listelenemedi: " + e.getMessage());
        }
    }

    private void addGelir() {
        int kategoriId = selectGelirKategori();
        if (kategoriId == -1) return;
        System.out.print("Tutar: ");
        double tutar = getDoubleInput();
        System.out.print("Açıklama: ");
        String aciklama = scanner.nextLine();
        System.out.print("Tarih (GG.AA.YYYY): ");
        LocalDate tarih = getDateInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO gelirler (kategori_id, tutar, aciklama, tarih) VALUES (?, ?, ?, ?)");) {
            ps.setInt(1, kategoriId);
            ps.setDouble(2, tutar);
            ps.setString(3, aciklama);
            ps.setDate(4, Date.valueOf(tarih));
            ps.executeUpdate();
            System.out.println("Gelir eklendi.");
        } catch (SQLException e) {
            System.out.println("Gelir eklenemedi: " + e.getMessage());
        }
    }

    private void deleteGelir() {
        listGelirler();
        System.out.print("Silinecek Gelir ID: ");
        int id = getIntInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gelirler WHERE id = ?")) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected > 0) System.out.println("Silindi.");
            else System.out.println("Kayıt bulunamadı.");
        } catch (SQLException e) {
            System.out.println("Silinemedi: " + e.getMessage());
        }
    }

    private void listGiderler() {
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT g.id, k.ad, g.tutar, g.aciklama, g.tarih FROM giderler g LEFT JOIN gider_kategorileri k ON g.kategori_id = k.id ORDER BY g.tarih DESC")) {
            System.out.println("ID | Kategori | Tutar | Açıklama | Tarih");
            while (rs.next()) {
                System.out.printf("%d | %s | %.2f | %s | %s\n",
                        rs.getInt("id"), rs.getString("ad"), rs.getDouble("tutar"), rs.getString("aciklama"), rs.getDate("tarih").toLocalDate().format(dateFormatter));
            }
        } catch (SQLException e) {
            System.out.println("Giderler listelenemedi: " + e.getMessage());
        }
    }

    private void addGider() {
        int kategoriId = selectGiderKategori();
        if (kategoriId == -1) return;
        System.out.print("Tutar: ");
        double tutar = getDoubleInput();
        System.out.print("Açıklama: ");
        String aciklama = scanner.nextLine();
        System.out.print("Tarih (GG.AA.YYYY): ");
        LocalDate tarih = getDateInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO giderler (kategori_id, tutar, aciklama, tarih) VALUES (?, ?, ?, ?)");) {
            ps.setInt(1, kategoriId);
            ps.setDouble(2, tutar);
            ps.setString(3, aciklama);
            ps.setDate(4, Date.valueOf(tarih));
            ps.executeUpdate();
            System.out.println("Gider eklendi.");
        } catch (SQLException e) {
            System.out.println("Gider eklenemedi: " + e.getMessage());
        }
    }

    private void deleteGider() {
        listGiderler();
        System.out.print("Silinecek Gider ID: ");
        int id = getIntInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM giderler WHERE id = ?")) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected > 0) System.out.println("Silindi.");
            else System.out.println("Kayıt bulunamadı.");
        } catch (SQLException e) {
            System.out.println("Silinemedi: " + e.getMessage());
        }
    }

    private void listGelirKategorileri() {
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad FROM gelir_kategorileri ORDER BY ad")) {
            System.out.println("ID | Ad");
            while (rs.next()) {
                System.out.printf("%d | %s\n", rs.getInt("id"), rs.getString("ad"));
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler listelenemedi: " + e.getMessage());
        }
    }

    private void addGelirKategori() {
        System.out.print("Kategori Adı: ");
        String ad = scanner.nextLine();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO gelir_kategorileri (ad) VALUES (?)")) {
            ps.setString(1, ad);
            ps.executeUpdate();
            System.out.println("Kategori eklendi.");
        } catch (SQLException e) {
            System.out.println("Kategori eklenemedi: " + e.getMessage());
        }
    }

    private void deleteGelirKategori() {
        listGelirKategorileri();
        System.out.print("Silinecek Kategori ID: ");
        int id = getIntInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gelir_kategorileri WHERE id = ?")) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected > 0) System.out.println("Silindi.");
            else System.out.println("Kayıt bulunamadı.");
        } catch (SQLException e) {
            System.out.println("Silinemedi: " + e.getMessage());
        }
    }

    private void listGiderKategorileri() {
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad FROM gider_kategorileri ORDER BY ad")) {
            System.out.println("ID | Ad");
            while (rs.next()) {
                System.out.printf("%d | %s\n", rs.getInt("id"), rs.getString("ad"));
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler listelenemedi: " + e.getMessage());
        }
    }

    private void addGiderKategori() {
        System.out.print("Kategori Adı: ");
        String ad = scanner.nextLine();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO gider_kategorileri (ad) VALUES (?)")) {
            ps.setString(1, ad);
            ps.executeUpdate();
            System.out.println("Kategori eklendi.");
        } catch (SQLException e) {
            System.out.println("Kategori eklenemedi: " + e.getMessage());
        }
    }

    private void deleteGiderKategori() {
        listGiderKategorileri();
        System.out.print("Silinecek Kategori ID: ");
        int id = getIntInput();
        try (Connection conn = dbManager.getMySQLConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM gider_kategorileri WHERE id = ?")) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected > 0) System.out.println("Silindi.");
            else System.out.println("Kayıt bulunamadı.");
        } catch (SQLException e) {
            System.out.println("Silinemedi: " + e.getMessage());
        }
    }

    // --- Yardımcılar ---
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Lütfen geçerli bir sayı girin: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.print("Lütfen geçerli bir sayı girin: ");
            scanner.next();
        }
        double val = scanner.nextDouble();
        scanner.nextLine();
        return val;
    }

    private LocalDate getDateInput() {
        while (true) {
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (Exception e) {
                System.out.print("Tarih formatı yanlış! (GG.AA.YYYY): ");
            }
        }
    }

    private int selectGelirKategori() {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad FROM gelir_kategorileri ORDER BY ad")) {
            int i = 1;
            while (rs.next()) {
                System.out.printf("%d. %s\n", i, rs.getString("ad"));
                ids.add(rs.getInt("id"));
                i++;
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler listelenemedi: " + e.getMessage());
            return -1;
        }
        if (ids.isEmpty()) {
            System.out.println("Önce kategori ekleyin!");
            return -1;
        }
        System.out.print("Kategori seç (numara): ");
        int secim = getIntInput();
        if (secim < 1 || secim > ids.size()) {
            System.out.println("Geçersiz seçim!");
            return -1;
        }
        return ids.get(secim - 1);
    }

    private int selectGiderKategori() {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, ad FROM gider_kategorileri ORDER BY ad")) {
            int i = 1;
            while (rs.next()) {
                System.out.printf("%d. %s\n", i, rs.getString("ad"));
                ids.add(rs.getInt("id"));
                i++;
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler listelenemedi: " + e.getMessage());
            return -1;
        }
        if (ids.isEmpty()) {
            System.out.println("Önce kategori ekleyin!");
            return -1;
        }
        System.out.print("Kategori seç (numara): ");
        int secim = getIntInput();
        if (secim < 1 || secim > ids.size()) {
            System.out.println("Geçersiz seçim!");
            return -1;
        }
        return ids.get(secim - 1);
    }
} 