package com.finance.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.finance.database.DatabaseService;
import com.finance.model.Gelir;
import com.finance.model.Gider;

public class GelirGiderGrafikFrame extends JFrame {
    public GelirGiderGrafikFrame(DatabaseService dbService) {
        setTitle("Gelir-Gider Grafiği");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Verileri ay bazında gruplayarak ekle
        List<Gelir> gelirler = dbService.getAllGelirler();
        List<Gider> giderler = dbService.getAllGiderler();

        // Ay isimleri Türkçe
        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        double[] aylikGelir = new double[12];
        double[] aylikGider = new double[12];

        for (Gelir gelir : gelirler) {
            int ay = gelir.getTarih().getMonthValue();
            if (ay >= 1 && ay <= 12) {
                aylikGelir[ay - 1] += gelir.getTutar();
            }
        }
        for (Gider gider : giderler) {
            int ay = gider.getTarih().getMonthValue();
            if (ay >= 1 && ay <= 12) {
                aylikGider[ay - 1] += gider.getTutar();
            }
        }

        for (int i = 0; i < 12; i++) {
            dataset.addValue(aylikGelir[i], "Gelir", aylar[i]);
            dataset.addValue(aylikGider[i], "Gider", aylar[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Aylık Gelir-Gider Grafiği",
                "Ay",
                "Tutar (₺)",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(780, 540));
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }
} 