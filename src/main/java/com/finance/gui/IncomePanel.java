package com.finance.gui;

import com.finance.database.DatabaseManager;
import com.finance.database.DatabaseService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.finance.model.GelirKategorisi;

public class IncomePanel extends JPanel {
    private final DatabaseService dbService;
    private final JTextField dateField;
    private final JTextField amountField;
    private final JTextField descriptionField;
    private final JComboBox<String> categoryCombo;
    private final JTable incomeTable;
    private final DefaultTableModel tableModel;
    private String currencySymbol = "₺";

    public IncomePanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tarih alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tarih:"), gbc);

        gbc.gridx = 1;
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        formPanel.add(dateField, gbc);

        // Tutar alanı
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tutar (" + currencySymbol +"):"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);

        // Kategori alanı
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Kategori:"), gbc);

        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        loadCategories();
        formPanel.add(categoryCombo, gbc);

        // Açıklama alanı
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Açıklama:"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ekle");
        JButton updateButton = new JButton("Güncelle");
        JButton deleteButton = new JButton("Sil");
        JButton clearButton = new JButton("Temizle");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Tablo
        String[] columns = {"ID", "Tarih", "Kategori", "Tutar", "Açıklama"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incomeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(incomeTable);

        // Panel düzeni
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                formPanel, scrollPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        // Event listeners
        addButton.addActionListener(e -> addIncome());
        updateButton.addActionListener(e -> updateIncome());
        deleteButton.addActionListener(e -> deleteIncome());
        clearButton.addActionListener(e -> clearForm());

        // Tablo çift tıklama olayı
        incomeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = incomeTable.getSelectedRow();
                    if (row != -1) {
                        dateField.setText(incomeTable.getValueAt(row, 1).toString());
                        categoryCombo.setSelectedItem(incomeTable.getValueAt(row, 2));
                        amountField.setText(incomeTable.getValueAt(row, 3).toString().replace(currencySymbol, ""));
                        descriptionField.setText(incomeTable.getValueAt(row, 4).toString());
                    }
                }
            }
        });

        // Input validation
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',') {
                    e.consume();
                }
            }
        });

        // Verileri yükle
        loadIncomeData();
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        for (GelirKategorisi kategori : dbService.getAllGelirKategorileri()) {
            categoryCombo.addItem(kategori.getAd());
        }
    }

    private void loadIncomeData() {
        tableModel.setRowCount(0);
        for (var gelir : dbService.getAllGelirler()) {
            tableModel.addRow(new Object[] {
                gelir.getId(),
                gelir.getTarih().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                getKategoriAdById(gelir.getKategoriId()),
                String.format("%.2f %s", gelir.getTutar(), currencySymbol),
                gelir.getAciklama()
            });
        }
    }

    private String getKategoriAdById(String kategoriId) {
        for (GelirKategorisi kategori : dbService.getAllGelirKategorileri()) {
            if (kategori.getId().equals(kategoriId)) return kategori.getAd();
        }
        return "";
    }

    private String getKategoriIdByAd(String ad) {
        for (GelirKategorisi kategori : dbService.getAllGelirKategorileri()) {
            if (kategori.getAd().equals(ad)) return kategori.getId();
        }
        return null;
    }

    private void addIncome() {
        try {
            LocalDate tarih = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            double tutar = Double.parseDouble(amountField.getText().replace(",", "."));
            String kategoriAd = (String) categoryCombo.getSelectedItem();
            String aciklama = descriptionField.getText();
            if (tutar <= 0) {
                JOptionPane.showMessageDialog(this, "Tutar 0'dan büyük olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String kategoriId = getKategoriIdByAd(kategoriAd);
            com.finance.model.Gelir gelir = new com.finance.model.Gelir();
            gelir.setKategoriId(kategoriId);
            gelir.setTutar(tutar);
            gelir.setAciklama(aciklama);
            gelir.setTarih(tarih);
            dbService.addGelir(gelir);
                JOptionPane.showMessageDialog(this, "Gelir başarıyla eklendi!");
                clearForm();
                loadIncomeData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gelir eklenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateIncome() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir gelir seçin!");
            return;
        }
        try {
            int id = (int) incomeTable.getValueAt(selectedRow, 0);
            LocalDate tarih = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            double tutar = Double.parseDouble(amountField.getText().replace(",", "."));
            String kategoriAd = (String) categoryCombo.getSelectedItem();
            String aciklama = descriptionField.getText();
            if (tutar <= 0) {
                JOptionPane.showMessageDialog(this, "Tutar 0'dan büyük olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String kategoriId = getKategoriIdByAd(kategoriAd);
            com.finance.model.Gelir gelir = new com.finance.model.Gelir();
            gelir.setId(id);
            gelir.setKategoriId(kategoriId);
            gelir.setTutar(tutar);
            gelir.setAciklama(aciklama);
            gelir.setTarih(tarih);
            dbService.updateGelir(gelir);
                JOptionPane.showMessageDialog(this, "Gelir başarıyla güncellendi!");
                clearForm();
                loadIncomeData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gelir güncellenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteIncome() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek bir gelir seçin!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçili geliri silmek istediğinizden emin misiniz?",
                "Silme Onayı",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) incomeTable.getValueAt(selectedRow, 0);
                dbService.deleteGelir(id);
                    JOptionPane.showMessageDialog(this, "Gelir başarıyla silindi!");
                    clearForm();
                    loadIncomeData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Gelir silinirken hata oluştu: " + e.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
        descriptionField.setText("");
        incomeTable.clearSelection();
    }

    public void refreshData() {
        loadCategories();
        loadIncomeData();
    }

    public void setCurrency(String currency) {
        switch (currency) {
            case "USD": currencySymbol = "$"; break;
            case "EUR": currencySymbol = "€"; break;
            default: currencySymbol = "₺";
        }
        loadIncomeData();
    }
} 