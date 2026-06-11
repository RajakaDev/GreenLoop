package view;

import database.ProductDB;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ProductUI extends JFrame {

    JTextField txtName;
    JTextField txtCategory;
    JTextField txtPrice;
    JTextField txtEcoRating;
    JTextField txtQuantity;
    JTextField txtReorderLevel;

    JTable table;
    DefaultTableModel model;

    ProductDB productDB = new ProductDB();

    public ProductUI() {

        setTitle("GreenLoop - Product Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(7,2,10,10));

        txtName = new JTextField();
        txtCategory = new JTextField();
        txtPrice = new JTextField();
        txtEcoRating = new JTextField();
        txtQuantity = new JTextField();
        txtReorderLevel = new JTextField();

        formPanel.add(new JLabel("Product Name"));
        formPanel.add(txtName);

        formPanel.add(new JLabel("Category"));
        formPanel.add(txtCategory);

        formPanel.add(new JLabel("Price"));
        formPanel.add(txtPrice);

        formPanel.add(new JLabel("Eco Rating"));
        formPanel.add(txtEcoRating);

        formPanel.add(new JLabel("Quantity"));
        formPanel.add(txtQuantity);

        formPanel.add(new JLabel("Reorder Level"));
        formPanel.add(txtReorderLevel);

        JButton btnAdd = new JButton("Add Product");
        JButton btnDelete = new JButton("Delete Product");

        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Price");
        model.addColumn("Eco Rating");
        model.addColumn("Quantity");
        model.addColumn("Reorder Level");

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        loadProducts();

        btnAdd.addActionListener(e -> addProduct());

        btnDelete.addActionListener(e -> deleteProduct());
    }

    private void addProduct() {

        try {

            Product product = new Product(
                    txtName.getText(),
                    txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtEcoRating.getText()),
                    Integer.parseInt(txtQuantity.getText()),
                    Integer.parseInt(txtReorderLevel.getText())
            );

            if(productDB.addProduct(product)) {

                JOptionPane.showMessageDialog(this,
                        "Product Added Successfully");

                clearFields();

                loadProducts();
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Invalid Data");
        }
    }

    private void deleteProduct() {

        int row = table.getSelectedRow();

        if(row == -1) {

            JOptionPane.showMessageDialog(this,
                    "Select a Product");

            return;
        }

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString()
        );

        if(productDB.deleteProduct(id)) {

            JOptionPane.showMessageDialog(this,
                    "Deleted Successfully");

            loadProducts();
        }
    }

    private void loadProducts() {

        model.setRowCount(0);

        ArrayList<Product> products =
                productDB.getAllProducts();

        for(Product p : products) {

            model.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getEcoRating(),
                    p.getQuantity(),
                    p.getReorderLevel()
            });
        }
    }

    private void clearFields() {

        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtEcoRating.setText("");
        txtQuantity.setText("");
        txtReorderLevel.setText("");
    }

    public static void main(String[] args) {

        new ProductUI().setVisible(true);
    }
}