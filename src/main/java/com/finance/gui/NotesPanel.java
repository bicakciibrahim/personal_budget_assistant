package com.finance.gui;

import com.finance.database.DatabaseService;
import com.finance.model.Not;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotesPanel extends JPanel {
    private final DatabaseService dbService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField baslikField;
    private final JTextArea icerikArea;
    private final JButton addButton;
    private final JButton deleteButton;

    public NotesPanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel baslikPanel = new JPanel(new BorderLayout(5, 0));
        baslikPanel.add(new JLabel("Başlık:"), BorderLayout.WEST);
        baslikField = new JTextField();
        baslikPanel.add(baslikField, BorderLayout.CENTER);
        inputPanel.add(baslikPanel);

        JPanel icerikPanel = new JPanel(new BorderLayout(5, 0));
        icerikPanel.add(new JLabel("İçerik:"), BorderLayout.WEST);
        icerikArea = new JTextArea(3, 20);
        icerikArea.setLineWrap(true);
        icerikArea.setWrapStyleWord(true);
        JScrollPane icerikScroll = new JScrollPane(icerikArea);
        icerikPanel.add(icerikScroll, BorderLayout.CENTER);
        inputPanel.add(icerikPanel);

        formPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Ekle");
        deleteButton = new JButton("Sil");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Başlık", "İçerik"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> addNot());
        deleteButton.addActionListener(e -> deleteNot());

        // Load initial data
        refreshData();
    }

    private void addNot() {
        String baslik = baslikField.getText().trim();
        String icerik = icerikArea.getText().trim();

        if (baslik.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen not başlığını giriniz.");
            return;
        }

        try {
            Not not = new Not();
            not.setBaslik(baslik);
            not.setIcerik(icerik);

            dbService.addNot(not);
            clearForm();
            refreshData();
            JOptionPane.showMessageDialog(this, "Not başarıyla eklendi.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata oluştu: " + e.getMessage());
        }
    }

    private void deleteNot() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek notu seçiniz.");
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Seçili notu silmek istediğinizden emin misiniz?",
            "Silme Onayı",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbService.deleteNot(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Not başarıyla silindi.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        baslikField.setText("");
        icerikArea.setText("");
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Not> notlar = dbService.getAllNotlar();
        for (Not not : notlar) {
            tableModel.addRow(new Object[]{
                not.getId(),
                not.getBaslik(),
                not.getIcerik()
            });
        }
    }
} 