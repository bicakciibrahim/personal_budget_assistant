package com.finance.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.finance.database.DatabaseService;
import com.finance.model.Gelir;
import com.finance.model.Gider;
import com.finance.model.Rapor;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.JComboBox;
import java.time.Month;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final DatabaseService dbService;
    private final DefaultTableModel monthlyModel;
    private final DefaultTableModel yearlyModel;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;

    public ReportsPanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 15));

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        monthComboBox = new JComboBox<>();
        for (Month m : Month.values()) {
            monthComboBox.addItem(m.getDisplayName(TextStyle.FULL, new Locale("tr")));
        }
        yearComboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 10; y <= currentYear + 1; y++) {
            yearComboBox.addItem(y);
        }
        JButton monthlyReportBtn = new JButton("Aylık Raporu Oluştur");
        JButton yearlyReportBtn = new JButton("Yıllık Raporu Oluştur");
        buttonPanel.add(new JLabel("Ay: "));
        buttonPanel.add(monthComboBox);
        buttonPanel.add(new JLabel("Yıl: "));
        buttonPanel.add(yearComboBox);
        buttonPanel.add(monthlyReportBtn);
        buttonPanel.add(yearlyReportBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // Tablo modelleri ve sekmeler
        monthlyModel = new DefaultTableModel(new Object[]{"Dönem", "Toplam Gelir", "Toplam Gider", "Bakiye"}, 0);
        JTable monthlyTable = new JTable(monthlyModel);
        JScrollPane monthlyScroll = new JScrollPane(monthlyTable);
        JPanel monthlyPanel = new JPanel(new BorderLayout());
        monthlyPanel.add(monthlyScroll, BorderLayout.CENTER);

        yearlyModel = new DefaultTableModel(new Object[]{"Yıl", "Toplam Gelir", "Toplam Gider", "Bakiye"}, 0);
        JTable yearlyTable = new JTable(yearlyModel);
        JScrollPane yearlyScroll = new JScrollPane(yearlyTable);
        JPanel yearlyPanel = new JPanel(new BorderLayout());
        yearlyPanel.add(yearlyScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Aylık", monthlyPanel);
        tabbedPane.addTab("Yıllık", yearlyPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // Butonlara işlev ekle
        monthlyReportBtn.addActionListener(e -> {
            int month = monthComboBox.getSelectedIndex() + 1;
            int year = (Integer) yearComboBox.getSelectedItem();
            saveReportToDb(month, year);
            loadAllMonthlyReports();
        });
        yearlyReportBtn.addActionListener(e -> {
            int year = (Integer) yearComboBox.getSelectedItem();
            saveReportToDb(0, year); // 0: yıl bazlı rapor
            loadAllYearlyReports();
        });
        loadAllMonthlyReports();
        loadAllYearlyReports();
    }

    // Tüm geçmiş aylık raporları tabloya yükle
    private void loadAllMonthlyReports() {
        monthlyModel.setRowCount(0);
        List<Rapor> raporlar = dbService.getAllRaporlar();
        for (Rapor rapor : raporlar) {
            if (rapor.getAy() > 0) {
                String monthName = Month.of(rapor.getAy()).getDisplayName(TextStyle.FULL, new Locale("tr"));
                monthlyModel.addRow(new Object[]{
                    monthName + " " + rapor.getYil(),
                    String.format("%.2f ₺", rapor.getGelir()),
                    String.format("%.2f ₺", rapor.getGider()),
                    String.format("%.2f ₺", rapor.getBakiye())
                });
            }
        }
    }

    // Tüm geçmiş yıllık raporları tabloya yükle
    private void loadAllYearlyReports() {
        yearlyModel.setRowCount(0);
        List<Rapor> raporlar = dbService.getAllRaporlar();
        for (Rapor rapor : raporlar) {
            if (rapor.getAy() == 0) {
                yearlyModel.addRow(new Object[]{
                    rapor.getYil(),
                    String.format("%.2f ₺", rapor.getGelir()),
                    String.format("%.2f ₺", rapor.getGider()),
                    String.format("%.2f ₺", rapor.getBakiye())
                });
            }
        }
    }

    private void saveReportToDb(int month, int year) {
        double totalIncome = 0, totalExpense = 0;
        if (month > 0) {
            for (Gelir gelir : dbService.getAllGelirler()) {
                if (gelir.getTarih().getMonthValue() == month && gelir.getTarih().getYear() == year) {
                    totalIncome += gelir.getTutar();
                }
            }
            for (Gider gider : dbService.getAllGiderler()) {
                if (gider.getTarih().getMonthValue() == month && gider.getTarih().getYear() == year) {
                    totalExpense += gider.getTutar();
                }
            }
        } else {
            for (Gelir gelir : dbService.getAllGelirler()) {
                if (gelir.getTarih().getYear() == year) {
                    totalIncome += gelir.getTutar();
                }
            }
            for (Gider gider : dbService.getAllGiderler()) {
                if (gider.getTarih().getYear() == year) {
                    totalExpense += gider.getTutar();
                }
            }
        }
        double balance = totalIncome - totalExpense;
        dbService.addRapor(month, year, totalIncome, totalExpense, balance);
    }

    public void refreshData() {}
} 