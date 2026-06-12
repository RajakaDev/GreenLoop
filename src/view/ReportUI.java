package view;

import database.ReportDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ReportUI extends JFrame {

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

    JTextField txtMonth;
    JLabel     lblRevenue;
    JTable     table;
    DefaultTableModel model;
    ReportDB   reportDB = new ReportDB();

    public ReportUI() {
        setTitle("GreenLoop - Sales & Inventory Reports");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));
        body.add(buildControlRow(), BorderLayout.NORTH);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK); bar.setBorder(new EmptyBorder(16,28,16,28));
        JLabel title = new JLabel("Sales & Inventory Reports");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(C_WHITE);
        JLabel sub = new JLabel("Monthly revenue and low-stock product overview");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(0x8BB89A));
        JPanel g = new JPanel(); g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.add(title); g.add(sub); bar.add(g, BorderLayout.WEST); return bar;
    }

    private JPanel buildControlRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        row.setOpaque(false);

        // Revenue card
        JPanel revCard = roundCard();
        revCard.setLayout(new BoxLayout(revCard, BoxLayout.Y_AXIS));
        revCard.setPreferredSize(new Dimension(320, 100));

        JLabel revHeading = new JLabel("Monthly Revenue");
        revHeading.setFont(new Font("Segoe UI",Font.BOLD,13));
        revHeading.setForeground(C_TEXT); revHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputRow.setOpaque(false);

        txtMonth = styledField(); txtMonth.setText("2026-06");
        txtMonth.setPreferredSize(new Dimension(120, 34));
        JButton btnRevenue = filledButton("Generate", C_ACCENT, new Color(0x3D9B68));
        btnRevenue.setPreferredSize(new Dimension(100, 34));
        inputRow.add(txtMonth); inputRow.add(btnRevenue);

        lblRevenue = new JLabel("Rs. 0.00");
        lblRevenue.setFont(new Font("Segoe UI",Font.BOLD,20));
        lblRevenue.setForeground(C_ACCENT); lblRevenue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        inputWrap.setOpaque(false); inputWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputWrap.add(inputRow);

        revCard.add(revHeading); revCard.add(Box.createVerticalStrut(8));
        revCard.add(inputWrap);  revCard.add(Box.createVerticalStrut(6));
        revCard.add(lblRevenue);

        // Low stock card
        JPanel stockCard = roundCard();
        stockCard.setLayout(new BoxLayout(stockCard, BoxLayout.Y_AXIS));
        stockCard.setPreferredSize(new Dimension(220, 100));

        JLabel stockHeading = new JLabel("Low Stock Alert");
        stockHeading.setFont(new Font("Segoe UI",Font.BOLD,13));
        stockHeading.setForeground(C_TEXT); stockHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stockSub = new JLabel("Products below reorder level");
        stockSub.setFont(new Font("Segoe UI",Font.PLAIN,11));
        stockSub.setForeground(C_MUTED); stockSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLowStock = filledButton("Show Low Stock", C_AMBER, new Color(0xC87F20));
        btnLowStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLowStock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        stockCard.add(stockHeading); stockCard.add(Box.createVerticalStrut(4));
        stockCard.add(stockSub);     stockCard.add(Box.createVerticalStrut(10));
        stockCard.add(btnLowStock);

        btnRevenue.addActionListener(e -> generateRevenue());
        btnLowStock.addActionListener(e -> loadLowStock());

        row.add(revCard);
        row.add(stockCard);
        return row;
    }

    private JPanel buildTablePanel() {
        model = new DefaultTableModel(
                new String[]{"Product ID","Product Name","Quantity","Reorder Level"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row%2==0 ? C_CARD : C_ROW_ALT);
                else c.setBackground(C_ROW_SEL);
                c.setForeground(C_TEXT); return c;
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

        JLabel heading = new JLabel("Low Stock Products");
        heading.setFont(new Font("Segoe UI",Font.BOLD,15)); heading.setForeground(C_TEXT);
        heading.setBorder(new EmptyBorder(0,0,10,0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void generateRevenue() {
        String month = txtMonth.getText().trim();
        if (!month.matches("\\d{4}-\\d{2}")) { JOptionPane.showMessageDialog(this,"Month format must be YYYY-MM"); return; }
        double revenue = reportDB.getMonthlyRevenue(month);
        lblRevenue.setText("Rs. " + String.format("%.2f", revenue));
    }

    private void loadLowStock() {
        model.setRowCount(0);
        ArrayList<String[]> list = reportDB.getLowStockProducts();
        for (String[] row : list) model.addRow(row);
        if (list.isEmpty()) JOptionPane.showMessageDialog(this,"No low stock products found");
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
        p.setOpaque(false); p.setBorder(new EmptyBorder(16,18,16,18)); return p;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportUI().setVisible(true));
    }
}