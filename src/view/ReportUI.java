package view;

import database.ReportDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ReportUI extends JFrame {

    JTextField txtMonth;
    JLabel lblRevenue;

    JTable table;
    DefaultTableModel model;

    ReportDB reportDB = new ReportDB();

    public ReportUI() {
        setTitle("GreenLoop - Sales & Inventory Reports");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        txtMonth = new JTextField("2026-06");
        lblRevenue = new JLabel("Revenue: Rs. 0.00");

        JButton btnRevenue = new JButton("Generate Revenue");
        JButton btnLowStock = new JButton("Show Low Stock");

        topPanel.add(new JLabel("Month YYYY-MM"));
        topPanel.add(txtMonth);
        topPanel.add(btnRevenue);

        topPanel.add(new JLabel("Monthly Revenue"));
        topPanel.add(lblRevenue);
        topPanel.add(btnLowStock);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Quantity");
        model.addColumn("Reorder Level");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRevenue.addActionListener(e -> generateRevenue());
        btnLowStock.addActionListener(e -> loadLowStock());
    }

    private void generateRevenue() {
        String month = txtMonth.getText().trim();

        if (!month.matches("\\d{4}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Month format must be YYYY-MM");
            return;
        }

        double revenue = reportDB.getMonthlyRevenue(month);
        lblRevenue.setText("Revenue: Rs. " + revenue);
    }

    private void loadLowStock() {
        model.setRowCount(0);

        ArrayList<String[]> list = reportDB.getLowStockProducts();

        for (String[] row : list) {
            model.addRow(row);
        }

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No low stock products found");
        }
    }

    public static void main(String[] args) {
        new ReportUI().setVisible(true);
    }
}