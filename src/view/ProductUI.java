package view;

import database.ProductDB;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ProductUI extends JFrame {

    private static final Color C_DARK = new Color(0x1A2E22);
    private static final Color C_ACCENT = new Color(0x4CAF78);
    private static final Color C_DANGER = new Color(0xD94F4F);
    private static final Color C_BG = new Color(0xF8FAF9);
    private static final Color C_CARD = Color.WHITE;
    private static final Color C_BORDER = new Color(0xDDE8E2);
    private static final Color C_MUTED = new Color(0x6B8070);
    private static final Color C_TEXT = new Color(0x1A2E22);
    private static final Color C_WHITE = Color.WHITE;
    private static final Color C_ROW_ALT = new Color(0xF0F7F3);
    private static final Color C_ROW_SEL = new Color(0xC8E6D2);

    JTextField txtSearch;
    JTextField txtName, txtCategory, txtPrice, txtEcoRating, txtQuantity, txtReorderLevel;

    JTable table;
    DefaultTableModel model;

    ProductDB productDB = new ProductDB();

    public ProductUI() {
        setTitle("GreenLoop - Product Management");
        setSize(1100, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));

        body.add(buildFormCard(), BorderLayout.WEST);
        body.add(buildTablePanel(), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        loadProducts();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK);
        bar.setBorder(new EmptyBorder(16, 28, 16, 28));

        JLabel title = new JLabel("Product Catalogue");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_WHITE);

        JLabel sub = new JLabel("Manage eco-packaging products, pricing and stock");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x8BB89A));

        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        group.add(title);
        group.add(sub);

        bar.add(group, BorderLayout.WEST);

        return bar;
    }

    private JPanel buildFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 0));

        JLabel heading = new JLabel("Product Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(C_TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName = styledField();
        txtCategory = styledField();
        txtPrice = styledField();
        txtEcoRating = styledField();
        txtQuantity = styledField();
        txtReorderLevel = styledField();

        JButton btnAdd = filledButton("Add Product", C_ACCENT, new Color(0x3D9B68));
        JButton btnUpdate = filledButton("Update Product", new Color(0x2980B9), new Color(0x206090));
        JButton btnDelete = filledButton("Delete Product", C_DANGER, new Color(0xBF3A3A));

        card.add(heading);
        card.add(Box.createVerticalStrut(16));

        card.add(fieldRow("Product Name", txtName));
        card.add(Box.createVerticalStrut(8));

        card.add(fieldRow("Category", txtCategory));
        card.add(Box.createVerticalStrut(8));

        card.add(fieldRow("Price (Rs.)", txtPrice));
        card.add(Box.createVerticalStrut(8));

        card.add(fieldRow("Eco Rating (1-5)", txtEcoRating));
        card.add(Box.createVerticalStrut(8));

        card.add(fieldRow("Quantity", txtQuantity));
        card.add(Box.createVerticalStrut(8));

        card.add(fieldRow("Reorder Level", txtReorderLevel));
        card.add(Box.createVerticalStrut(18));

        card.add(btnAdd);
        card.add(Box.createVerticalStrut(8));

        card.add(btnUpdate);
        card.add(Box.createVerticalStrut(8));

        card.add(btnDelete);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel heading = new JLabel("All Products");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(C_TEXT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);

        txtSearch = styledField();
        txtSearch.setPreferredSize(new Dimension(220, 32));

        JButton btnSearch = smallButton("Search");
        JButton btnShowAll = smallButton("Show All");

        searchPanel.add(new JLabel("Search Product"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        btnSearch.addActionListener(e -> searchProducts());
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadProducts();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Category", "Price", "Eco Rating", "Quantity", "Reorder Level"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = styledTable(model);
        JScrollPane scrollPane = styledScroll(table);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addProduct() {
        try {
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name is required");
                return;
            }

            if (category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category is required");
                return;
            }

            double price = Double.parseDouble(txtPrice.getText().trim());
            int ecoRating = Integer.parseInt(txtEcoRating.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0");
                return;
            }

            if (ecoRating < 1 || ecoRating > 5) {
                JOptionPane.showMessageDialog(this, "Eco rating must be between 1 and 5");
                return;
            }

            if (quantity < 0 || reorderLevel < 0) {
                JOptionPane.showMessageDialog(this, "Quantity and reorder level cannot be negative");
                return;
            }

            Product product = new Product(name, category, price, ecoRating, quantity, reorderLevel);

            if (productDB.addProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product Added Successfully");
                clearFields();
                loadProducts();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input values");
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Product to update");
            return;
        }

        try {
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();

            if (name.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and category are required");
                return;
            }

            double price = Double.parseDouble(txtPrice.getText().trim());
            int ecoRating = Integer.parseInt(txtEcoRating.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            if (price <= 0 || ecoRating < 1 || ecoRating > 5 || quantity < 0 || reorderLevel < 0) {
                JOptionPane.showMessageDialog(this, "Enter valid product details");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            Product product = new Product(id, name, category, price, ecoRating, quantity, reorderLevel);

            if (productDB.updateProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product Updated Successfully");
                clearFields();
                loadProducts();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input values");
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Product");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (productDB.deleteProduct(id)) {
            JOptionPane.showMessageDialog(this, "Product Deleted Successfully");
            clearFields();
            loadProducts();
        }
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        model.setRowCount(0);

        ArrayList<Product> products = productDB.searchProducts(keyword);

        for (Product p : products) {
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

        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found");
        }
    }

    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();

        if (row != -1) {
            txtName.setText(model.getValueAt(row, 1).toString());
            txtCategory.setText(model.getValueAt(row, 2).toString());
            txtPrice.setText(model.getValueAt(row, 3).toString());
            txtEcoRating.setText(model.getValueAt(row, 4).toString());
            txtQuantity.setText(model.getValueAt(row, 5).toString());
            txtReorderLevel.setText(model.getValueAt(row, 6).toString());
        }
    }

    private void loadProducts() {
        model.setRowCount(0);

        ArrayList<Product> products = productDB.getAllProducts();

        for (Product p : products) {
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

    private JPanel roundCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 2, 14, 14);

                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 2, 14, 14);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        return panel;
    }

    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(C_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        row.add(lbl);
        row.add(Box.createVerticalStrut(4));
        row.add(field);

        return row;
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(C_CARD);
        field.setForeground(C_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        return field;
    }

    private JButton filledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(C_WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        return btn;
    }

    private JButton smallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(C_ACCENT);
        btn.setForeground(C_WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JTable styledTable(DefaultTableModel tableModel) {
        JTable productTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);

                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_CARD : C_ROW_ALT);
                } else {
                    c.setBackground(C_ROW_SEL);
                }

                return c;
            }
        };

        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        productTable.setRowHeight(34);
        productTable.setGridColor(C_BORDER);
        productTable.setShowVerticalLines(false);
        productTable.setIntercellSpacing(new Dimension(0, 1));
        productTable.setSelectionBackground(C_ROW_SEL);
        productTable.setSelectionForeground(C_TEXT);
        productTable.setBackground(C_CARD);
        productTable.setFillsViewportHeight(true);

        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        productTable.getTableHeader().setBackground(C_DARK);
        productTable.getTableHeader().setForeground(C_WHITE);
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        return productTable;
    }

    private JScrollPane styledScroll(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        scrollPane.getViewport().setBackground(C_CARD);

        return scrollPane;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductUI().setVisible(true));
    }
}