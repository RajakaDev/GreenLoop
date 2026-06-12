package view;

import database.ClientDB;
import database.OrderDB;
import database.ProductDB;
import model.Client;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class OrderUI extends JFrame {

    private static final Color C_DARK   = new Color(0x1A2E22);
    private static final Color C_ACCENT = new Color(0x4CAF78);
    private static final Color C_BG     = new Color(0xF8FAF9);
    private static final Color C_CARD   = Color.WHITE;
    private static final Color C_BORDER = new Color(0xDDE8E2);
    private static final Color C_MUTED  = new Color(0x6B8070);
    private static final Color C_TEXT   = new Color(0x1A2E22);
    private static final Color C_WHITE  = Color.WHITE;

    JComboBox<String> cmbClient;
    JComboBox<String> cmbProduct;
    JTextField txtQuantity;
    JLabel lblTotal;

    ArrayList<Client>  clients;
    ArrayList<Product> products;

    ClientDB  clientDB  = new ClientDB();
    ProductDB productDB = new ProductDB();
    OrderDB   orderDB   = new OrderDB();

    public OrderUI() {
        setTitle("GreenLoop - Process Client Orders");
        setSize(700, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(24, 40, 24, 40));
        body.add(buildOrderCard());
        root.add(body, BorderLayout.CENTER);

        loadClients();
        loadProducts();
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK); bar.setBorder(new EmptyBorder(16,28,16,28));
        JLabel title = new JLabel("Process Client Orders");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(C_WHITE);
        JLabel sub = new JLabel("Create a new order for a client");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(0x8BB89A));
        JPanel g = new JPanel(); g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.add(title); g.add(sub); bar.add(g, BorderLayout.WEST); return bar;
    }

    private JPanel buildOrderCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 360));

        JLabel heading = new JLabel("New Order");
        heading.setFont(new Font("Segoe UI",Font.BOLD,16));
        heading.setForeground(C_TEXT); heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbClient  = styledCombo();
        cmbProduct = styledCombo();
        txtQuantity = styledField();

        lblTotal = new JLabel("Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI",Font.BOLD,18));
        lblTotal.setForeground(C_ACCENT);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCalc = outlineButton("Calculate Total");
        JButton btnSave = filledButton("Save Order", C_ACCENT, new Color(0x3D9B68));

        for (JComponent c : new JComponent[]{cmbClient,cmbProduct,txtQuantity,btnCalc,btnSave}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        }

        // total panel
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false); totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel totalLbl = new JLabel("Order Total");
        totalLbl.setFont(new Font("Segoe UI",Font.BOLD,11)); totalLbl.setForeground(C_MUTED);
        totalRow.add(totalLbl, BorderLayout.NORTH);
        totalRow.add(lblTotal, BorderLayout.CENTER);

        card.add(heading); card.add(Box.createVerticalStrut(20));
        card.add(fieldRow("Select Client",  cmbClient));  card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Select Product", cmbProduct)); card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Quantity",       txtQuantity));card.add(Box.createVerticalStrut(16));
        card.add(totalRow); card.add(Box.createVerticalStrut(20));
        card.add(btnCalc); card.add(Box.createVerticalStrut(10));
        card.add(btnSave);

        btnCalc.addActionListener(e -> calculateTotal());
        btnSave.addActionListener(e -> saveOrder());
        return card;
    }

    private void loadClients() {
        clients = clientDB.getAllClients();
        for (Client c : clients) cmbClient.addItem(c.getClientId() + " - " + c.getName());
    }

    private void loadProducts() {
        products = productDB.getAllProducts();
        for (Product p : products)
            cmbProduct.addItem(p.getProductId() + " - " + p.getName() + " | Stock: " + p.getQuantity() + " | Rs. " + p.getPrice());
    }

    private void calculateTotal() {
        if (cmbProduct.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter quantity"); return; }
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) { JOptionPane.showMessageDialog(this,"Quantity must be greater than 0"); return; }
            Product product = products.get(cmbProduct.getSelectedIndex());
            if (qty > product.getQuantity()) { JOptionPane.showMessageDialog(this,"Not enough stock available"); return; }
            double total = product.getPrice() * qty;
            lblTotal.setText("Rs. " + String.format("%.2f", total));
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Invalid quantity"); }
    }

    private void saveOrder() {
        if (cmbClient.getSelectedIndex() == -1)  { JOptionPane.showMessageDialog(this,"Select a client");  return; }
        if (cmbProduct.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter quantity"); return; }
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) { JOptionPane.showMessageDialog(this,"Quantity must be greater than 0"); return; }
            Client client   = clients.get(cmbClient.getSelectedIndex());
            Product product = products.get(cmbProduct.getSelectedIndex());
            if (qty > product.getQuantity()) { JOptionPane.showMessageDialog(this,"Not enough stock available"); return; }
            if (orderDB.createOrder(client.getClientId(), product.getProductId(), qty)) {
                JOptionPane.showMessageDialog(this,"Order Saved Successfully");
                txtQuantity.setText(""); lblTotal.setText("Rs. 0.00");
                cmbProduct.removeAllItems(); loadProducts();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Invalid quantity"); }
    }

    // ── Widget helpers ─────────────────────────────────────────────────────────
    private JPanel roundCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,10)); g2.fillRoundRect(2,3,getWidth()-4,getHeight()-2,14,14);
                g2.setColor(C_CARD); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-2,14,14);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(new EmptyBorder(28,28,28,28)); return p;
    }
    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(); row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11)); lbl.setForeground(C_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.add(lbl); row.add(Box.createVerticalStrut(4)); row.add(field); return row;
    }
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI",Font.PLAIN,12));
        f.setBackground(C_CARD); f.setForeground(C_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER,1,true), new EmptyBorder(4,10,4,10)));
        return f;
    }
    private JComboBox<String> styledCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cb.setBackground(C_CARD); cb.setForeground(C_TEXT);
        cb.setBorder(BorderFactory.createLineBorder(C_BORDER,1));
        return cb;
    }
    private JButton filledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI",Font.BOLD,12));
        btn.setForeground(C_WHITE); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }
    private JButton outlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xEEF6F1) : C_CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.setColor(C_ACCENT); g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI",Font.BOLD,12));
        btn.setForeground(C_ACCENT); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderUI().setVisible(true));
    }
}