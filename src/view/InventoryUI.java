package view;

import database.ProductDB;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class InventoryUI extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField txtStockIn;

    ProductDB productDB = new ProductDB();

    public InventoryUI() {
        setTitle("GreenLoop - Inventory Management");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        txtStockIn = new JTextField();

        JButton btnStockIn = new JButton("Stock In");
        JButton btnRefresh = new JButton("Refresh");

        topPanel.add(new JLabel("Stock In Quantity"));
        topPanel.add(txtStockIn);
        topPanel.add(btnStockIn);
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();

        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Quantity On Hand");
        model.addColumn("Reorder Level");
        model.addColumn("Stock Status");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadInventory();

        btnStockIn.addActionListener(e -> stockIn());
        btnRefresh.addActionListener(e -> loadInventory());
    }

    private void loadInventory() {
        model.setRowCount(0);

        ArrayList<Product> products = productDB.getAllProducts();

        for (Product p : products) {
            String status;

            if (p.getQuantity() <= p.getReorderLevel()) {
                status = "LOW STOCK";
            } else {
                status = "AVAILABLE";
            }

            model.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getQuantity(),
                    p.getReorderLevel(),
                    status
            });
        }
    }

    private void stockIn() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product");
            return;
        }

        String stockText = txtStockIn.getText().trim();

        if (stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter stock quantity");
            return;
        }

        try {
            int quantityToAdd = Integer.parseInt(stockText);

            if (quantityToAdd <= 0) {
                JOptionPane.showMessageDialog(this, "Stock quantity must be greater than 0");
                return;
            }

            int productId = Integer.parseInt(model.getValueAt(row, 0).toString());

            if (productDB.stockInProduct(productId, quantityToAdd)) {
                JOptionPane.showMessageDialog(this, "Stock Updated Successfully");
                txtStockIn.setText("");
                loadInventory();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid stock quantity");
        }
    }

    public static void main(String[] args) {
        new InventoryUI().setVisible(true);
    }
}