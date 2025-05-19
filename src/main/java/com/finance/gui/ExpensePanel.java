package com.finance.gui;

import com.finance.database.DatabaseService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.finance.model.GiderKategorisi;
import com.finance.model.Gider;

public class ExpensePanel extends JPanel {
    private final DatabaseService dbService;
    private final JTextField dateField;
    private final JTextField amountField;
    private final JTextField descriptionField;
    private final JComboBox<String> categoryCombo;
    private final JTable expenseTable;
    private final DefaultTableModel tableModel;
    private String currencySymbol = "₺";

    public ExpensePanel(DatabaseService dbService) {
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
        expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);

        // Panel düzeni
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                formPanel, scrollPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        // Event listeners
        addButton.addActionListener(e -> addExpense());
        updateButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        clearButton.addActionListener(e -> clearForm());

        // Tablo çift tıklama olayı
        expenseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = expenseTable.getSelectedRow();
                    if (row != -1) {
                        dateField.setText(expenseTable.getValueAt(row, 1).toString());
                        categoryCombo.setSelectedItem(expenseTable.getValueAt(row, 2));
                        amountField.setText(expenseTable.getValueAt(row, 3).toString().replace(currencySymbol, ""));
                        descriptionField.setText(expenseTable.getValueAt(row, 4).toString());
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
        loadExpenseData();
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        for (GiderKategorisi kategori : dbService.getAllGiderKategorileri()) {
            categoryCombo.addItem(kategori.getAd());
        }
    }

    private void loadExpenseData() {
        tableModel.setRowCount(0);
        for (Gider gider : dbService.getAllGiderler()) {
            tableModel.addRow(new Object[] {
                gider.getId(),
                gider.getTarih().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                getKategoriAdById(String.valueOf(gider.getKategoriId())),
                String.format("%.2f %s", gider.getTutar(), currencySymbol),
                gider.getAciklama()
            });
            }
    }

    private String getKategoriAdById(String kategoriId) {
        for (GiderKategorisi kategori : dbService.getAllGiderKategorileri()) {
            if (kategori.getId().equals(kategoriId)) return kategori.getAd();
        }
        return "";
    }

    private void addExpense() {
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
            Gider gider = new Gider();
            gider.setKategoriId(kategoriId);
            gider.setTutar(tutar);
            gider.setAciklama(aciklama);
            gider.setTarih(tarih);
            dbService.addGider(gider);
                JOptionPane.showMessageDialog(this, "Gider başarıyla eklendi!");
                clearForm();
                loadExpenseData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gider eklenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir gider seçin!");
            return;
        }
        try {
            int id = (int) expenseTable.getValueAt(selectedRow, 0);
            LocalDate tarih = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            double tutar = Double.parseDouble(amountField.getText().replace(",", "."));
            String kategoriAd = (String) categoryCombo.getSelectedItem();
            String aciklama = descriptionField.getText();
            if (tutar <= 0) {
                JOptionPane.showMessageDialog(this, "Tutar 0'dan büyük olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String kategoriId = getKategoriIdByAd(kategoriAd);
            Gider gider = new Gider();
            gider.setId(id);
            gider.setKategoriId(kategoriId);
            gider.setTutar(tutar);
            gider.setAciklama(aciklama);
            gider.setTarih(tarih);
            dbService.updateGider(gider);
                JOptionPane.showMessageDialog(this, "Gider başarıyla güncellendi!");
                clearForm();
                loadExpenseData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gider güncellenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek bir gider seçin!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçili gideri silmek istediğinizden emin misiniz?",
                "Silme Onayı",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String id = expenseTable.getValueAt(selectedRow, 0).toString();
                dbService.deleteGider(id);
                JOptionPane.showMessageDialog(this, "Gider başarıyla silindi!");
                clearForm();
                loadExpenseData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Gider silinirken hata oluştu: " + e.getMessage(),
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
        expenseTable.clearSelection();
    }

    public void refreshData() {
        loadCategories();
        loadExpenseData();
    }

    public void setCurrency(String currency) {
        switch (currency) {
            case "USD": currencySymbol = "$"; break;
            case "EUR": currencySymbol = "€"; break;
            default: currencySymbol = "₺";
        }
        loadExpenseData();
    }

    private String getKategoriIdByAd(String ad) {
        for (GiderKategorisi kategori : dbService.getAllGiderKategorileri()) {
            if (kategori.getAd().equals(ad)) return kategori.getId();
        }
        return "";
    }
} 