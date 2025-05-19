package com.finance.gui;

import com.finance.database.DatabaseService;
import com.finance.model.TasarrufHedefi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SavingsPanel extends JPanel {
    private final DatabaseService dbService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField nameField;
    private final JTextField targetField;
    private final JTextField savingsField;
    private final JButton addBtn;
    private final JButton updateBtn;
    private final JButton deleteBtn;

    public SavingsPanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID", "Hedef Adı", "Hedef Tutarı", "Birikim Tutarı"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        nameField = new JTextField(12);
        targetField = new JTextField(8);
        savingsField = new JTextField(8);
        addBtn = new JButton("Ekle");
        updateBtn = new JButton("Güncelle");
        deleteBtn = new JButton("Sil");
        addPanel.add(new JLabel("Hedef Adı:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Tutar:"));
        addPanel.add(targetField);
        addPanel.add(new JLabel("Birikim Tutarı:"));
        addPanel.add(savingsField);
        addPanel.add(addBtn);
        addPanel.add(updateBtn);
        addPanel.add(deleteBtn);
        add(addPanel, BorderLayout.SOUTH);

        // Event listeners
        addBtn.addActionListener(e -> addSavingsGoal());
        updateBtn.addActionListener(e -> updateSavingsGoal());
        deleteBtn.addActionListener(e -> deleteSavingsGoal());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());

        // Load initial data
        refreshData();
    }

    private void addSavingsGoal() {
        try {
            String name = nameField.getText().trim();
            double targetAmount = Double.parseDouble(targetField.getText().trim());
            double savingsAmount = 0.0;
            if (!savingsField.getText().trim().isEmpty()) {
                savingsAmount = Double.parseDouble(savingsField.getText().trim());
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen hedef adını giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            TasarrufHedefi hedef = new TasarrufHedefi();
            hedef.setAd(name);
            hedef.setHedefTutar(targetAmount);
            hedef.setBirikimTutar(savingsAmount);
            dbService.addTasarrufHedefi(hedef);
            refreshData();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir tutar giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSavingsGoal() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek hedefi seçiniz.");
            return;
        }
        try {
            int id = (int) table.getValueAt(selectedRow, 0);
            String name = nameField.getText().trim();
            double targetAmount = Double.parseDouble(targetField.getText().trim());
            double savingsAmount = 0.0;
            if (!savingsField.getText().trim().isEmpty()) {
                savingsAmount = Double.parseDouble(savingsField.getText().trim());
            } else {
                savingsAmount = (double) table.getValueAt(selectedRow, 3);
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen hedef adını giriniz.");
                return;
            }
            TasarrufHedefi hedef = new TasarrufHedefi();
            hedef.setId(id);
            hedef.setAd(name);
            hedef.setHedefTutar(targetAmount);
            hedef.setBirikimTutar(savingsAmount);
            dbService.updateTasarrufHedefi(hedef);
            clearForm();
            refreshData();
            JOptionPane.showMessageDialog(this, "Tasarruf hedefi başarıyla güncellendi.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli sayısal değerler giriniz.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata oluştu: " + e.getMessage());
        }
    }

    private void deleteSavingsGoal() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek hedefi seçiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        dbService.deleteTasarrufHedefi(id);
        refreshData();
    }

    private void clearForm() {
        nameField.setText("");
        targetField.setText("");
        savingsField.setText("");
    }

    private void fillFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            nameField.setText(table.getValueAt(selectedRow, 1).toString());
            targetField.setText(table.getValueAt(selectedRow, 2).toString().replace(" ₺", ""));
            savingsField.setText(table.getValueAt(selectedRow, 3).toString().replace(" ₺", ""));
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        for (TasarrufHedefi hedef : dbService.getAllTasarrufHedefleri()) {
            model.addRow(new Object[]{
                hedef.getId(),
                hedef.getAd(),
                String.format("%.2f ₺", hedef.getHedefTutar()),
                String.format("%.2f ₺", hedef.getBirikimTutar())
            });
        }
    }
} 