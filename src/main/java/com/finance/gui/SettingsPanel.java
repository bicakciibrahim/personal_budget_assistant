package com.finance.gui;

import com.finance.database.DatabaseService;
import com.finance.model.GelirKategorisi;
import com.finance.model.GiderKategorisi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final DatabaseService dbService;
    private IncomePanel incomePanel;
    private ExpensePanel expensePanel;
    private JTextField incomeCategoryField;
    private JTextField expenseCategoryField;
    private JTable incomeCategoryTable;
    private JTable expenseCategoryTable;
    private DefaultTableModel incomeCategoryModel;
    private DefaultTableModel expenseCategoryModel;
    private JButton addIncomeCategoryBtn;
    private JButton deleteIncomeCategoryBtn;
    private JButton addExpenseCategoryBtn;
    private JButton deleteExpenseCategoryBtn;

    public SettingsPanel(DatabaseService dbService) {
        this.dbService = dbService;
        setLayout(new BorderLayout());

        // Income Tab Panel
        JPanel incomeTabPanel = new JPanel(new BorderLayout());
        incomeCategoryModel = new DefaultTableModel(new Object[]{"ID", "Ad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incomeCategoryTable = new JTable(incomeCategoryModel);
        incomeCategoryTable.setRowHeight(28);
        incomeCategoryTable.setFont(new Font("Arial", Font.PLAIN, 15));
        incomeCategoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane incomeScroll = new JScrollPane(incomeCategoryTable);
        incomeTabPanel.add(incomeScroll, BorderLayout.CENTER);
        JPanel incomeAddPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        incomeCategoryField = new JTextField(15);
        incomeCategoryField.setFont(new Font("Arial", Font.PLAIN, 15));
        addIncomeCategoryBtn = new JButton("Ekle");
        deleteIncomeCategoryBtn = new JButton("Sil");
        incomeAddPanel.add(new JLabel("Yeni Kategori:"));
        incomeAddPanel.add(incomeCategoryField);
        incomeAddPanel.add(addIncomeCategoryBtn);
        incomeAddPanel.add(deleteIncomeCategoryBtn);
        incomeTabPanel.add(incomeAddPanel, BorderLayout.SOUTH);

        // Expense Tab Panel
        JPanel expenseTabPanel = new JPanel(new BorderLayout());
        expenseCategoryModel = new DefaultTableModel(new Object[]{"ID", "Ad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseCategoryTable = new JTable(expenseCategoryModel);
        expenseCategoryTable.setRowHeight(28);
        expenseCategoryTable.setFont(new Font("Arial", Font.PLAIN, 15));
        expenseCategoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane expenseScroll = new JScrollPane(expenseCategoryTable);
        expenseTabPanel.add(expenseScroll, BorderLayout.CENTER);
        JPanel expenseAddPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        expenseCategoryField = new JTextField(15);
        expenseCategoryField.setFont(new Font("Arial", Font.PLAIN, 15));
        addExpenseCategoryBtn = new JButton("Ekle");
        deleteExpenseCategoryBtn = new JButton("Sil");
        expenseAddPanel.add(new JLabel("Yeni Kategori:"));
        expenseAddPanel.add(expenseCategoryField);
        expenseAddPanel.add(addExpenseCategoryBtn);
        expenseAddPanel.add(deleteExpenseCategoryBtn);
        expenseTabPanel.add(expenseAddPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 15));
        tabbedPane.addTab("Gelir Kategorileri", incomeTabPanel);
        tabbedPane.addTab("Gider Kategorileri", expenseTabPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // --- Kategori işlemleri ---
        loadIncomeCategories();
        loadExpenseCategories();
        addIncomeCategoryBtn.addActionListener(e -> addIncomeCategory());
        deleteIncomeCategoryBtn.addActionListener(e -> deleteIncomeCategory());
        addExpenseCategoryBtn.addActionListener(e -> addExpenseCategory());
        deleteExpenseCategoryBtn.addActionListener(e -> deleteExpenseCategory());
    }

    public void setIncomePanel(IncomePanel panel) { this.incomePanel = panel; }
    public void setExpensePanel(ExpensePanel panel) { this.expensePanel = panel; }

    private void loadIncomeCategories() {
        incomeCategoryModel.setRowCount(0);
        for (GelirKategorisi kategori : dbService.getAllGelirKategorileri()) {
            incomeCategoryModel.addRow(new Object[]{kategori.getId(), kategori.getAd()});
        }
    }

    private void loadExpenseCategories() {
        expenseCategoryModel.setRowCount(0);
        for (GiderKategorisi kategori : dbService.getAllGiderKategorileri()) {
            expenseCategoryModel.addRow(new Object[]{kategori.getId(), kategori.getAd()});
        }
    }

    private void addIncomeCategory() {
        String ad = incomeCategoryField.getText().trim();
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kategori adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GelirKategorisi kategori = new GelirKategorisi();
        kategori.setAd(ad);
        dbService.addGelirKategorisi(kategori);
        loadIncomeCategories();
        incomeCategoryField.setText("");
        if (incomePanel != null) incomePanel.refreshData();
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    private void deleteIncomeCategory() {
        int selectedRow = incomeCategoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir kategori seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(incomeCategoryModel.getValueAt(selectedRow, 0).toString());
        dbService.deleteGelirKategorisi(id);
        loadIncomeCategories();
        if (incomePanel != null) incomePanel.refreshData();
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    private void addExpenseCategory() {
        String ad = expenseCategoryField.getText().trim();
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kategori adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GiderKategorisi kategori = new GiderKategorisi();
        kategori.setAd(ad);
        dbService.addGiderKategorisi(kategori);
        loadExpenseCategories();
        expenseCategoryField.setText("");
        if (expensePanel != null) expensePanel.refreshData();
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    private void deleteExpenseCategory() {
        int selectedRow = expenseCategoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir kategori seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(expenseCategoryModel.getValueAt(selectedRow, 0).toString());
        dbService.deleteGiderKategorisi(id);
        loadExpenseCategories();
        if (expensePanel != null) expensePanel.refreshData();
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    public void refreshData() {
        loadIncomeCategories();
        loadExpenseCategories();
    }
} 