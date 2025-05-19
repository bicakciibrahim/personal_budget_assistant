package com.finance.gui;

import com.finance.database.DatabaseManager;
import com.finance.database.DatabaseService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JPanel contentPane;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DashboardPanel dashboardPanel;
    private IncomePanel incomePanel;
    private ExpensePanel expensePanel;
    private BudgetPanel budgetPanel;
    private SavingsPanel savingsPanel;
    private NotesPanel notesPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private DatabaseService dbService;

    public MainFrame() {
        setTitle("Kişisel Finans Yönetimi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        
        // Modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Veritabanı bağlantısını başlat
        try {
            dbService = DatabaseManager.getInstance().getDatabaseService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Veritabanı bağlantısı kurulamadı: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Ana panel
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // Sol menü paneli
        JPanel menuPanel = createMenuPanel();
        contentPane.add(menuPanel, BorderLayout.WEST);

        // Ana içerik paneli
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Panelleri oluştur
        createPanels();

        // Alt panel (durum çubuğu)
        JPanel statusPanel = createStatusPanel();
        contentPane.add(statusPanel, BorderLayout.SOUTH);

        // Pencere kapatıldığında bağlantıları kapat
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseManager.getInstance().closeConnections();
            }
        });

        // İlk paneli göster
        showPanel("DASHBOARD");
    }

    private void createPanels() {
        dashboardPanel = new DashboardPanel(DatabaseManager.getInstance());
        incomePanel = new IncomePanel(dbService);
        expensePanel = new ExpensePanel(dbService);
        budgetPanel = new BudgetPanel(dbService);
        savingsPanel = new SavingsPanel(dbService);
        notesPanel = new NotesPanel(dbService);
        reportsPanel = new ReportsPanel(dbService);
        settingsPanel = new SettingsPanel(dbService);
        settingsPanel.setIncomePanel(incomePanel);
        settingsPanel.setExpensePanel(expensePanel);

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(incomePanel, "INCOME");
        mainPanel.add(expensePanel, "EXPENSE");
        mainPanel.add(budgetPanel, "BUDGET");
        mainPanel.add(savingsPanel, "SAVINGS");
        mainPanel.add(notesPanel, "NOTES");
        mainPanel.add(reportsPanel, "REPORTS");
        mainPanel.add(settingsPanel, "SETTINGS");
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Menü başlığı
        JLabel titleLabel = new JLabel("Finans Yönetimi");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        menuPanel.add(titleLabel);

        // Menü butonları
        String[] menuItems = {
            "Gösterge Paneli",
            "Gelirler",
            "Giderler",
            "Bütçe",
            "Tasarruf",
            "Notlar",
            "Raporlar",
            "Ayarlar"
        };

        for (String item : menuItems) {
            JButton button = createMenuButton(item);
            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        return menuPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));

        button.addActionListener(e -> {
            switch (text) {
                case "Gösterge Paneli":
                    showPanel("DASHBOARD");
                    break;
                case "Gelirler":
                    showPanel("INCOME");
                    break;
                case "Giderler":
                    showPanel("EXPENSE");
                    break;
                case "Bütçe":
                    showPanel("BUDGET");
                    break;
                case "Tasarruf":
                    showPanel("SAVINGS");
                    break;
                case "Notlar":
                    showPanel("NOTES");
                    break;
                case "Raporlar":
                    showPanel("REPORTS");
                    break;
                case "Ayarlar":
                    showPanel("SETTINGS");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Bu özellik yakında!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        return button;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Hazır");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("v1.0");
        statusPanel.add(versionLabel, BorderLayout.EAST);

        return statusPanel;
    }

    private void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        if (panelName.equals("DASHBOARD")) {
            dashboardPanel.refreshData();
        } else if (panelName.equals("INCOME")) {
            incomePanel.refreshData();
        } else if (panelName.equals("EXPENSE")) {
            expensePanel.refreshData();
        } else if (panelName.equals("BUDGET")) {
            budgetPanel.refreshData();
        } else if (panelName.equals("SAVINGS")) {
            savingsPanel.refreshData();
        } else if (panelName.equals("NOTES")) {
            notesPanel.refreshData();
        } else if (panelName.equals("REPORTS")) {
            reportsPanel.refreshData();
        } else if (panelName.equals("SETTINGS")) {
            settingsPanel.refreshData();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 