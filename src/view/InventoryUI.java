package view;

import database.ProductDB;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class InventoryUI extends JFrame {

    private static final Color C_DARK    = new Color(0x1A2E22);
    private static final Color C_ACCENT  = new Color(0x4CAF78);
    private static final Color C_AMBER   = new Color(0xE8A642);
    private static final Color C_BG      = new Color(0xF8FAF9);
    private static final Color C_CARD    = Color.WHITE;
    private static final Color C_BORDER  = new Color(0xDDE8E2);
    private static final Color C_MUTED   = new Color(0x6B8070);
    private static final Color C_TEXT    = new Color(0x1A2E22);
    private static final Color C_WHITE   = Color.WHITE;
    private static final Color C_ROW_ALT = new Color(0xF0F7F3);
    private static final Color C_ROW_SEL = new Color(0xC8E6D2);

    JTable table;
    DefaultTableModel model;
    JTextField txtStockIn;
    ProductDB productDB = new ProductDB();

    public InventoryUI() {
        setTitle("GreenLoop - Inventory Management");
        setSize(950, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));
        body.add(buildControlCard(), BorderLayout.NORTH);
        body.add(buildTablePanel(),  BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        loadInventory();
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK); bar.setBorder(new EmptyBorder(16,28,16,28));
        JLabel title = new JLabel("Inventory Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); title.setForeground(C_WHITE);
        JLabel sub = new JLabel("Monitor stock levels and add incoming stock");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12)); sub.setForeground(new Color(0x8BB89A));
        JPanel g = new JPanel(); g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.add(title); g.add(sub); bar.add(g, BorderLayout.WEST); return bar;
    }

    private JPanel buildControlCard() {
        JPanel card = roundCard();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel lbl = new JLabel("Stock In Quantity");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(C_MUTED);

        txtStockIn = styledField();
        txtStockIn.setPreferredSize(new Dimension(160, 34));

        JButton btnStockIn = filledButton("+ Stock In", C_ACCENT, new Color(0x3D9B68));
        JButton btnRefresh = outlineButton("↻  Refresh");

        btnStockIn.setPreferredSize(new Dimension(130, 34));
        btnRefresh.setPreferredSize(new Dimension(110, 34));

        card.add(lbl); card.add(txtStockIn); card.add(btnStockIn); card.add(btnRefresh);

        btnStockIn.addActionListener(e -> stockIn());
        btnRefresh.addActionListener(e -> loadInventory());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false); wrap.setBorder(new EmptyBorder(0,0,12,0));
        wrap.add(card); return wrap;
    }

    private JPanel buildTablePanel() {
        model = new DefaultTableModel(
                new String[]{"Product ID","Product Name","Qty On Hand","Reorder Level","Stock Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row%2==0 ? C_CARD : C_ROW_ALT);
                else c.setBackground(C_ROW_SEL);
                // colour the Status column
                if (col == 4 && !isRowSelected(row)) {
                    Object val = model.getValueAt(row, 4);
                    if ("LOW STOCK".equals(val)) c.setForeground(C_AMBER);
                    else c.setForeground(C_ACCENT);
                } else { c.setForeground(C_TEXT); }
                return c;
            }
        };
        table.setFont(new Font("Segoe UI",Font.PLAIN,12)); table.setRowHeight(34);
        table.setGridColor(C_BORDER); table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0,1));
        table.setSelectionBackground(C_ROW_SEL); table.setSelectionForeground(C_TEXT);
        table.setBackground(C_CARD); table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        table.getTableHeader().setBackground(C_DARK); table.getTableHeader().setForeground(C_WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0,38));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER,1));
        sp.getViewport().setBackground(C_CARD);

        JLabel heading = new JLabel("Stock Overview");
        heading.setFont(new Font("Segoe UI",Font.BOLD,15)); heading.setForeground(C_TEXT);
        heading.setBorder(new EmptyBorder(0,0,10,0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void loadInventory() {
        model.setRowCount(0);
        ArrayList<Product> products = productDB.getAllProducts();
        for (Product p : products) {
            String status = (p.getQuantity() <= p.getReorderLevel()) ? "LOW STOCK" : "AVAILABLE";
            model.addRow(new Object[]{p.getProductId(),p.getName(),p.getQuantity(),p.getReorderLevel(),status});
        }
    }

    private void stockIn() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
        String stockText = txtStockIn.getText().trim();
        if (stockText.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter stock quantity"); return; }
        try {
            int qty = Integer.parseInt(stockText);
            if (qty <= 0) { JOptionPane.showMessageDialog(this,"Stock quantity must be greater than 0"); return; }
            int productId = Integer.parseInt(model.getValueAt(row,0).toString());
            if (productDB.stockInProduct(productId, qty)) {
                JOptionPane.showMessageDialog(this,"Stock Updated Successfully");
                txtStockIn.setText(""); loadInventory();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Invalid stock quantity"); }
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
        p.setOpaque(false); p.setBorder(new EmptyBorder(10,16,10,16)); return p;
    }
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI",Font.PLAIN,12));
        f.setBackground(C_CARD); f.setForeground(C_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER,1,true), new EmptyBorder(4,10,4,10)));
        return f;
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
        SwingUtilities.invokeLater(() -> new InventoryUI().setVisible(true));
    }
}