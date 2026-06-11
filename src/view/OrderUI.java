package view;

import database.ClientDB;
import database.OrderDB;
import database.ProductDB;
import model.Client;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OrderUI extends JFrame {

    JComboBox<String> cmbClient;
    JComboBox<String> cmbProduct;
    JTextField txtQuantity;
    JLabel lblTotal;

    ArrayList<Client> clients;
    ArrayList<Product> products;

    ClientDB clientDB = new ClientDB();
    ProductDB productDB = new ProductDB();
    OrderDB orderDB = new OrderDB();

    public OrderUI() {
        setTitle("GreenLoop - Process Client Orders");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        cmbClient = new JComboBox<>();
        cmbProduct = new JComboBox<>();
        txtQuantity = new JTextField();
        lblTotal = new JLabel("Rs. 0.00");

        JButton btnCalculate = new JButton("Calculate Total");
        JButton btnSave = new JButton("Save Order");

        panel.add(new JLabel("Select Client"));
        panel.add(cmbClient);

        panel.add(new JLabel("Select Product"));
        panel.add(cmbProduct);

        panel.add(new JLabel("Quantity"));
        panel.add(txtQuantity);

        panel.add(new JLabel("Total Amount"));
        panel.add(lblTotal);

        panel.add(btnCalculate);
        panel.add(btnSave);

        add(panel);

        loadClients();
        loadProducts();

        btnCalculate.addActionListener(e -> calculateTotal());
        btnSave.addActionListener(e -> saveOrder());
    }

    private void loadClients() {
        clients = clientDB.getAllClients();

        for (Client c : clients) {
            cmbClient.addItem(c.getClientId() + " - " + c.getName());
        }
    }

    private void loadProducts() {
        products = productDB.getAllProducts();

        for (Product p : products) {
            cmbProduct.addItem(
                    p.getProductId() + " - " + p.getName() +
                            " | Stock: " + p.getQuantity() +
                            " | Rs. " + p.getPrice()
            );
        }
    }

    private void calculateTotal() {
        if (cmbProduct.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Select a product");
            return;
        }

        String qtyText = txtQuantity.getText().trim();

        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter quantity");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyText);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0");
                return;
            }

            Product product = products.get(cmbProduct.getSelectedIndex());

            if (quantity > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Not enough stock available");
                return;
            }

            double total = product.getPrice() * quantity;
            lblTotal.setText("Rs. " + total);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
        }
    }

    private void saveOrder() {
        if (cmbClient.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Select a client");
            return;
        }

        if (cmbProduct.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Select a product");
            return;
        }

        String qtyText = txtQuantity.getText().trim();

        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter quantity");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyText);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0");
                return;
            }

            Client client = clients.get(cmbClient.getSelectedIndex());
            Product product = products.get(cmbProduct.getSelectedIndex());

            if (quantity > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Not enough stock available");
                return;
            }

            if (orderDB.createOrder(client.getClientId(), product.getProductId(), quantity)) {
                JOptionPane.showMessageDialog(this, "Order Saved Successfully");

                txtQuantity.setText("");
                lblTotal.setText("Rs. 0.00");

                cmbProduct.removeAllItems();
                loadProducts();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
        }
    }

    public static void main(String[] args) {
        new OrderUI().setVisible(true);
    }
}