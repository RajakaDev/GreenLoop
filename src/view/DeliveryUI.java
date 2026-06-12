package view;

import database.DeliveryAgentDB;
import database.DeliveryDB;
import database.DBConnection;
import model.DeliveryAgent;
import serviceLayer.EmailService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DeliveryUI extends JFrame {

    private static final Color C_DARK    = new Color(0x1A2E22);
    private static final Color C_ACCENT  = new Color(0x4CAF78);
    private static final Color C_AMBER   = new Color(0xE8A642);
    private static final Color C_BLUE    = new Color(0x2980B9);
    private static final Color C_BG      = new Color(0xF8FAF9);
    private static final Color C_CARD    = Color.WHITE;
    private static final Color C_BORDER  = new Color(0xDDE8E2);
    private static final Color C_MUTED   = new Color(0x6B8070);
    private static final Color C_TEXT    = new Color(0x1A2E22);
    private static final Color C_WHITE   = Color.WHITE;
    private static final Color C_ROW_ALT = new Color(0xF0F7F3);
    private static final Color C_ROW_SEL = new Color(0xC8E6D2);

    JComboBox<String> cmbOrder;
    JComboBox<String> cmbAgent;
    JComboBox<String> cmbStatus;
    JTextField txtDate;
    JTable table;
    DefaultTableModel model;

    ArrayList<Integer>       orderIds = new ArrayList<>();
    ArrayList<DeliveryAgent> agents;

    DeliveryAgentDB agentDB    = new DeliveryAgentDB();
    DeliveryDB      deliveryDB = new DeliveryDB();

    public DeliveryUI() {
        setTitle("GreenLoop - Delivery Assignment");
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
        body.add(buildFormCard(),   BorderLayout.WEST);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        loadOrders();
        loadAgents();
        loadDeliveries();
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK); bar.setBorder(new EmptyBorder(16,28,16,28));
        JLabel title = new JLabel("Delivery Assignment");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(C_WHITE);
        JLabel sub = new JLabel("Assign orders to delivery agents and track status");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(0x8BB89A));
        JPanel g = new JPanel(); g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.add(title); g.add(sub); bar.add(g, BorderLayout.WEST); return bar;
    }

    private JPanel buildFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(290, 0));

        JLabel heading = new JLabel("Assign Delivery");
        heading.setFont(new Font("Segoe UI",Font.BOLD,15));
        heading.setForeground(C_TEXT); heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbOrder  = styledCombo();
        cmbAgent  = styledCombo();
        txtDate   = styledField(); txtDate.setText("2026-06-11");
        cmbStatus = styledCombo("Assigned", "Dispatched", "Delivered");

        JButton btnAssign = filledButton("Assign Delivery", C_ACCENT, new Color(0x3D9B68));
        JButton btnUpdate = filledButton("Update Status",   C_BLUE,   new Color(0x206090));

        for (JComponent c : new JComponent[]{cmbOrder,cmbAgent,txtDate,cmbStatus,btnAssign,btnUpdate}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        }

        // divider
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading); card.add(Box.createVerticalStrut(16));
        card.add(fieldRow("Select Order",  cmbOrder));  card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Select Agent",  cmbAgent));  card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Delivery Date (YYYY-MM-DD)", txtDate)); card.add(Box.createVerticalStrut(10));
        card.add(btnAssign); card.add(Box.createVerticalStrut(20));
        card.add(sep); card.add(Box.createVerticalStrut(16));

        JLabel heading2 = new JLabel("Update Status");
        heading2.setFont(new Font("Segoe UI",Font.BOLD,13));
        heading2.setForeground(C_TEXT); heading2.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading2); card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Delivery Status", cmbStatus)); card.add(Box.createVerticalStrut(10));
        card.add(btnUpdate);

        btnAssign.addActionListener(e -> assignDelivery());
        btnUpdate.addActionListener(e -> updateStatus());
        return card;
    }

    private JPanel buildTablePanel() {
        model = new DefaultTableModel(
                new String[]{"Delivery ID","Order ID","Client","Agent","Date","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row%2==0 ? C_CARD : C_ROW_ALT);
                else c.setBackground(C_ROW_SEL);
                // colour status column
                if (col == 5 && !isRowSelected(row)) {
                    Object val = model.getValueAt(row,5);
                    if ("Delivered".equals(val))   c.setForeground(C_ACCENT);
                    else if ("Dispatched".equals(val)) c.setForeground(C_BLUE);
                    else                               c.setForeground(C_AMBER);
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

        JLabel heading = new JLabel("All Deliveries");
        heading.setFont(new Font("Segoe UI",Font.BOLD,15)); heading.setForeground(C_TEXT);
        heading.setBorder(new EmptyBorder(0,0,10,0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Logic (unchanged) ─────────────────────────────────────────────────────
    private void loadOrders() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT order_id FROM orders");
            while (rs.next()) {
                int id = rs.getInt("order_id");
                orderIds.add(id); cmbOrder.addItem("Order #" + id);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadAgents() {
        agents = agentDB.getAllAgents();
        for (DeliveryAgent a : agents) cmbAgent.addItem(a.getAgentId() + " - " + a.getName());
    }

    private void assignDelivery() {
        if (cmbOrder.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this,"Select an order"); return; }
        if (cmbAgent.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this,"Select an agent"); return; }
        String date = txtDate.getText().trim();
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) { JOptionPane.showMessageDialog(this,"Date must be YYYY-MM-DD"); return; }
        int orderId = orderIds.get(cmbOrder.getSelectedIndex());
        int agentId = agents.get(cmbAgent.getSelectedIndex()).getAgentId();
        if (deliveryDB.assignDelivery(orderId, agentId, date)) {
            DeliveryAgent agent = agents.get(cmbAgent.getSelectedIndex());
            EmailService.sendEmail(agent.getEmail(), "New Delivery Assigned",
                    "Dear " + agent.getName() + ",\n\nA new delivery has been assigned to you.\nOrder ID: " + orderId + "\nDelivery Date: " + date + "\n\nPlease check the GreenLoop system for more details.");
            JOptionPane.showMessageDialog(this,"Delivery Assigned Successfully");
            loadDeliveries();
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this,"Select a delivery row"); return; }
        int deliveryId = Integer.parseInt(model.getValueAt(row,0).toString());
        String status = cmbStatus.getSelectedItem().toString();
        if (deliveryDB.updateDeliveryStatus(deliveryId, status)) {
            if (status.equals("Dispatched")) {
                String[] client = deliveryDB.getClientDetailsByDeliveryId(deliveryId);
                if (client != null)
                    EmailService.sendEmail(client[1], "Your GreenLoop Order Has Been Dispatched",
                            "Dear " + client[0] + ",\n\nYour order has been dispatched.\nOrder ID: " + client[2] + "\n\nThank you for choosing GreenLoop.");
            }
            JOptionPane.showMessageDialog(this,"Delivery Status Updated");
            loadDeliveries();
        }
    }

    private void loadDeliveries() {
        model.setRowCount(0);
        ArrayList<String[]> deliveries = deliveryDB.getAllDeliveries();
        for (String[] d : deliveries) model.addRow(d);
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
        p.setOpaque(false); p.setBorder(new EmptyBorder(20,20,20,20)); return p;
    }
    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(); row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
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
    private JComboBox<String> styledCombo(String... items) {
        JComboBox<String> cb = items.length > 0 ? new JComboBox<>(items) : new JComboBox<>();
        cb.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cb.setBackground(C_CARD); cb.setForeground(C_TEXT);
        cb.setBorder(BorderFactory.createLineBorder(C_BORDER,1)); return cb;
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
        SwingUtilities.invokeLater(() -> new DeliveryUI().setVisible(true));
    }
}