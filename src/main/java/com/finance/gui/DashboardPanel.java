package com.finance.gui;

import com.finance.database.DatabaseManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import com.finance.gui.GelirGiderGrafikFrame;
import com.finance.database.DatabaseService;

public class DashboardPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final JLabel totalIncomeLabel;
    private final JLabel totalExpenseLabel;
    private final JLabel balanceLabel;
    private final JLabel savingsLabel;
    private final JLabel budgetLabel;
    private final JTable recentTransactionsTable;
    private final DefaultTableModel tableModel;
    private ChartPanel chartPanel;

    public DashboardPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());

        // Özet kartları
        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toplam Gelir
        JPanel incomeCard = createSummaryCard("Toplam Gelir", "0.00 ₺", new Color(46, 204, 113));
        totalIncomeLabel = (JLabel) incomeCard.getComponent(1);
        summaryPanel.add(incomeCard);

        // Toplam Gider
        JPanel expenseCard = createSummaryCard("Toplam Gider", "0.00 ₺", new Color(231, 76, 60));
        totalExpenseLabel = (JLabel) expenseCard.getComponent(1);
        summaryPanel.add(expenseCard);

        // Bakiye
        JPanel balanceCard = createSummaryCard("Bakiye", "0.00 ₺", new Color(52, 152, 219));
        balanceLabel = (JLabel) balanceCard.getComponent(1);
        summaryPanel.add(balanceCard);

        // Birikim
        JPanel savingsCard = createSummaryCard("Birikim", "0.00 ₺", new Color(155, 89, 182));
        savingsLabel = (JLabel) savingsCard.getComponent(1);
        summaryPanel.add(savingsCard);

        // Aylık Bütçe
        JPanel budgetCard = createSummaryCard("Aylık Bütçe", "Tanımsız", new Color(241, 196, 15));
        budgetLabel = (JLabel) budgetCard.getComponent(1);
        summaryPanel.add(budgetCard);

        // Grafik
        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(Color.WHITE);
        chartCard.setBorder(BorderFactory.createTitledBorder("Gelir/Gider Grafiği"));
        JButton showChartButton = new JButton("Grafiği Göster");
        showChartButton.addActionListener(e -> showChartDialog());
        chartCard.add(showChartButton, BorderLayout.CENTER);
        summaryPanel.add(chartCard);

        // Son işlemler tablosu
        String[] columns = {"Tarih", "Tür", "Kategori", "Tutar", "Açıklama"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recentTransactionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);

        // Panel düzeni
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                summaryPanel, scrollPane);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);

        // Verileri yükle
        loadSummaryData();
        loadRecentTransactions();
        createChart();
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadSummaryData() {
        try (Connection conn = dbManager.getMySQLConnection()) {
            // Toplam gelir
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(tutar), 0) as toplam FROM gelirler")) {
                if (rs.next()) {
                    double totalIncome = rs.getDouble("toplam");
                    totalIncomeLabel.setText(String.format("%.2f ₺", totalIncome));
                }
            }

            // Toplam gider
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(tutar), 0) as toplam FROM giderler")) {
                if (rs.next()) {
                    double totalExpense = rs.getDouble("toplam");
                    totalExpenseLabel.setText(String.format("%.2f ₺", totalExpense));
                }
            }

            // Bakiye
            double totalIncome = parseDoubleSafe(totalIncomeLabel.getText(), "Toplam Gelir");
            double totalExpense = parseDoubleSafe(totalExpenseLabel.getText(), "Toplam Gider");
            double balance = totalIncome - totalExpense;
            balanceLabel.setText(String.format("%.2f ₺", balance));

            // Birikim (varsayılan olarak bakiyenin %20'si)
            double savings = balance * 0.20;
            savingsLabel.setText(String.format("%.2f ₺", savings));

            // Aylık Bütçe
            LocalDate today = LocalDate.now();
            try (PreparedStatement ps = conn.prepareStatement("SELECT tutar FROM butce WHERE ay = ? AND yil = ?")) {
                ps.setInt(1, today.getMonthValue());
                ps.setInt(2, today.getYear());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        double budget = rs.getDouble("tutar");
                        budgetLabel.setText(String.format("%.2f ₺", budget));
                    } else {
                        budgetLabel.setText("Tanımsız");
                    }
                }
            }

            // Grafiği güncelle
            updateChart();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Özet veriler yüklenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentTransactions() {
        tableModel.setRowCount(0);
        try (Connection conn = dbManager.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT g.tarih, 'Gelir' as tur, k.ad as kategori, g.tutar, g.aciklama " +
                     "FROM gelirler g LEFT JOIN gelir_kategorileri k ON g.kategori_id = k.id " +
                     "UNION ALL " +
                     "SELECT g.tarih, 'Gider' as tur, k.ad as kategori, g.tutar, g.aciklama " +
                     "FROM giderler g LEFT JOIN gider_kategorileri k ON g.kategori_id = k.id " +
                     "ORDER BY tarih DESC LIMIT 10")) {
            while (rs.next()) {
                Object[] row = {
                    rs.getDate("tarih"),
                    rs.getString("tur"),
                    rs.getString("kategori"),
                    String.format("%.2f ₺", rs.getDouble("tutar")),
                    rs.getString("aciklama")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Son işlemler yüklenirken hata oluştu: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createChart() {
        // Bu fonksiyon artık küçük grafik eklemeyecek, sadece buton olacak.
    }

    private void updateChart() {
        try (Connection conn = dbManager.getMySQLConnection()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Gelir kategorileri
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT k.ad as kategori, SUM(g.tutar) as toplam FROM gelirler g LEFT JOIN gelir_kategorileri k ON g.kategori_id = k.id GROUP BY k.ad")) {
                while (rs.next()) {
                    dataset.addValue(rs.getDouble("toplam"), "Gelir", rs.getString("kategori"));
                }
            }

            // Gider kategorileri
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT k.ad as kategori, SUM(g.tutar) as toplam FROM giderler g LEFT JOIN gider_kategorileri k ON g.kategori_id = k.id GROUP BY k.ad")) {
                while (rs.next()) {
                    dataset.addValue(rs.getDouble("toplam"), "Gider", rs.getString("kategori"));
                }
            }

            if (chartPanel != null) {
                JFreeChart chart = chartPanel.getChart();
                chart.getCategoryPlot().setDataset(dataset);
                chartPanel.repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double parseDoubleSafe(String value, String fieldName) {
        try {
            return Double.parseDouble(value.replace(" ₺", "").replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                fieldName + " değeri sayıya çevrilemedi: '" + value + "'\n0 olarak kabul edildi.",
                "Format Hatası",
                JOptionPane.WARNING_MESSAGE);
            return 0.0;
        }
    }

    public void refreshData() {
        loadSummaryData();
        loadRecentTransactions();
    }

    private void showChartDialog() {
        // Yeni ve büyük bir pencere aç
        DatabaseService dbService = dbManager.getDatabaseService();
        GelirGiderGrafikFrame grafikFrame = new GelirGiderGrafikFrame(dbService);
        grafikFrame.setVisible(true);
    }
} 