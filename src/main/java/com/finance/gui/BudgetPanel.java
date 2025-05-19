package com.finance.gui;

import com.finance.database.DatabaseService;
import com.finance.model.AylikButce;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class BudgetPanel extends JPanel {
    private final DatabaseService dbService;
    private final JTextField monthField;
    private final JTextField yearField;
    private final JTextField amountField;
    private final JTable budgetTable;
    private final DefaultTableModel tableModel;
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton clearButton;

    public BudgetPanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ay alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Ay:"), gbc);

        gbc.gridx = 1;
        monthField = new JTextField(10);
        formPanel.add(monthField, gbc);

        // Yıl alanı
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Yıl:"), gbc);

        gbc.gridx = 1;
        yearField = new JTextField(10);
        formPanel.add(yearField, gbc);

        // Tutar alanı
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tutar (₺):"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Ekle");
        updateButton = new JButton("Güncelle");
        deleteButton = new JButton("Sil");
        clearButton = new JButton("Temizle");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Tablo
        String[] columns = {"ID", "Ay", "Yıl", "Tutar"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(budgetTable);

        // Panel düzeni
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                formPanel, scrollPane);
        splitPane.setDividerLocation(150);

        add(splitPane, BorderLayout.CENTER);

        // Event listeners
        addButton.addActionListener(e -> addBudget());
        updateButton.addActionListener(e -> updateBudget());
        deleteButton.addActionListener(e -> deleteBudget());
        clearButton.addActionListener(e -> clearForm());

        // Tablo çift tıklama olayı
        budgetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = budgetTable.getSelectedRow();
                    if (row != -1) {
                        monthField.setText(budgetTable.getValueAt(row, 1).toString());
                        yearField.setText(budgetTable.getValueAt(row, 2).toString());
                        amountField.setText(budgetTable.getValueAt(row, 3).toString().replace(" ₺", ""));
                    }
                }
            }
        });

        // Input validation
        monthField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || monthField.getText().length() >= 2) {
                    e.consume();
                }
            }
        });

        yearField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || yearField.getText().length() >= 4) {
                    e.consume();
                }
            }
        });

        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',') {
                    e.consume();
                }
            }
        });

        // Otomatik ay/yıl doldurma
        LocalDate today = LocalDate.now();
        monthField.setText(String.valueOf(today.getMonthValue()));
        yearField.setText(String.valueOf(today.getYear()));

        // Verileri yükle
        loadBudgetData();
    }

    private void addBudget() {
        int ay = Integer.parseInt(monthField.getText());
        int yil = Integer.parseInt(yearField.getText());
        double tutar = Double.parseDouble(amountField.getText());
        // Tekil kayıt kontrolü
        for (AylikButce butce : dbService.getAllAylikButceler()) {
            if (butce.getAy() == ay && butce.getYil() == yil) {
                JOptionPane.showMessageDialog(this, "Bu ay ve yıl için zaten bir bütçe tanımlanmış!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        AylikButce yeniButce = new AylikButce();
        yeniButce.setAy(ay);
        yeniButce.setYil(yil);
        yeniButce.setTutar(tutar);
        dbService.addAylikButce(yeniButce);
        loadBudgetData();
    }

    private void updateBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek bir bütçe seçin!");
            return;
        }

        try {
            int id = (int) budgetTable.getValueAt(selectedRow, 0);
            String ayStr = monthField.getText();
            int ay;
            try {
                ay = Integer.parseInt(ayStr);
            } catch (NumberFormatException e) {
                try {
                    ay = turkceAyiSayiyaCevir(ayStr);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Geçersiz ay değeri!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            int yil = Integer.parseInt(yearField.getText());
            double tutar = Double.parseDouble(amountField.getText().replace(" ₺", "").replace(",", ".").trim());

            if (ay < 1 || ay > 12) {
                JOptionPane.showMessageDialog(this, "Ay 1-12 arasında olmalıdır", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (yil < 2000 || yil > 2100) {
                JOptionPane.showMessageDialog(this, "Geçerli bir yıl giriniz", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tutar <= 0) {
                JOptionPane.showMessageDialog(this, "Tutar 0'dan büyük olmalıdır", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tekil kayıt kontrolü (kendi ID'si hariç)
            for (AylikButce butce : dbService.getAllAylikButceler()) {
                if (butce.getAy() == ay && butce.getYil() == yil && butce.getId() != id) {
                    JOptionPane.showMessageDialog(this, "Bu ay ve yıl için zaten bir bütçe tanımlanmış!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            AylikButce guncelButce = new AylikButce();
            guncelButce.setId(id);
            guncelButce.setAy(ay);
            guncelButce.setYil(yil);
            guncelButce.setTutar(tutar);
            dbService.updateAylikButce(guncelButce);
            loadBudgetData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Bütçe başarıyla güncellendi", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli sayısal değerler girin", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Bütçe güncellenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek bir bütçe seçin!");
            return;
        }
        int id = (int) budgetTable.getValueAt(selectedRow, 0);
        dbService.deleteAylikButce(id);
        loadBudgetData();
    }

    private void clearForm() {
        monthField.setText(String.valueOf(LocalDate.now().getMonthValue()));
        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        amountField.setText("");
        budgetTable.clearSelection();
    }

    private void loadBudgetData() {
        tableModel.setRowCount(0);
        for (AylikButce butce : dbService.getAllAylikButceler()) {
            String ayAdi = Month.of(butce.getAy()).getDisplayName(TextStyle.FULL, new Locale("tr"));
            tableModel.addRow(new Object[] {
                butce.getId(),
                ayAdi,
                butce.getYil(),
                String.format("%.2f ₺", butce.getTutar())
            });
        }
    }

    public void refreshData() {
        loadBudgetData();
    }

    // Türkçe ay isimlerini sayıya çeviren yardımcı fonksiyon
    private int turkceAyiSayiyaCevir(String ayAdi) {
        switch (ayAdi.trim().toLowerCase()) {
            case "ocak": return 1;
            case "şubat": return 2;
            case "mart": return 3;
            case "nisan": return 4;
            case "mayıs": return 5;
            case "haziran": return 6;
            case "temmuz": return 7;
            case "ağustos": return 8;
            case "eylül": return 9;
            case "ekim": return 10;
            case "kasım": return 11;
            case "aralık": return 12;
            default: throw new IllegalArgumentException("Geçersiz ay adı: " + ayAdi);
        }
    }
} 