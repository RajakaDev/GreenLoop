package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import database.DashboardDB;

public class DashboardUI extends JFrame {

    private static final Color C_DARK    = new Color(0x1A2E22);
    private static final Color C_ACCENT  = new Color(0x4CAF78);
    private static final Color C_BG      = new Color(0xF8FAF9);
    private static final Color C_CARD    = Color.WHITE;
    private static final Color C_WHITE   = Color.WHITE;
    private static final Color C_MUTED   = new Color(0x6B8070);
    private static final Color C_TEXT    = new Color(0x1A2E22);
    private static final Color C_SIDEBAR_TEXT  = new Color(0xB0CDB8);
    private static final Color C_SIDEBAR_HOVER = new Color(0x2E4A36);

    private DashboardDB dashboardDB = new DashboardDB();

    public DashboardUI() {
        setTitle("GreenLoop - Eco Packaging Management System");
        setSize(960, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_DARK);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, C_DARK, 0, getHeight(), new Color(0x0F1F14));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_ACCENT);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.fillOval(-40, getHeight() - 160, 200, 200);
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(195, 0));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BorderLayout());

        // brand
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 18, 18, 18));

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ACCENT); g2.fillOval(0, 0, 36, 36);
                g2.setColor(C_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("G", (36 - fm.stringWidth("G"))/2, (36+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(36,36)); dot.setMaximumSize(new Dimension(36,36));
        dot.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel("GreenLoop");
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setForeground(C_WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(dot);
        brand.add(Box.createVerticalStrut(10));
        brand.add(name);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2E4A36));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // nav items
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(10, 0, 0, 0));

        String[][] items = {
                {"Products",   "📦"},
                {"Clients",    "👥"},
                {"Inventory",  "🗄"},
                {"Agents",     "🚚"},
                {"Orders",     "🧾"},
                {"Deliveries", "📍"},
                {"Reports",    "📊"},
        };
        for (String[] item : items) nav.add(navItem(item[1], item[0]));

        // exit at bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottom.setOpaque(false);
        JButton btnExit = sidebarButton("⏻  Exit");
        btnExit.addActionListener(e -> System.exit(0));
        bottom.add(btnExit);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(sep, BorderLayout.NORTH);
        center.add(nav, BorderLayout.CENTER);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(center, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel navItem(String icon, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)) {
            boolean h = false;
            { setPreferredSize(new Dimension(195, 40));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { h = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { h = false; repaint(); }
                    public void mouseClicked(MouseEvent e) { openModule(label); }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (h) { g2.setColor(C_SIDEBAR_HOVER);
                    g2.fill(new RoundRectangle2D.Float(8,2,getWidth()-16,getHeight()-4,8,8)); }
                g2.dispose(); super.paintComponent(g);
            }
        };
        item.setOpaque(false);

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel tx = new JLabel(label);
        tx.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tx.setForeground(C_SIDEBAR_TEXT);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { tx.setForeground(C_WHITE); }
            public void mouseExited (MouseEvent e) { tx.setForeground(C_SIDEBAR_TEXT); }
        });

        item.add(ic); item.add(tx);
        return item;
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(0x8BB89A));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── MAIN CONTENT ──────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);

        // top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(C_BG);
        topBar.setBorder(new EmptyBorder(22, 28, 10, 28));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(C_TEXT);

        JLabel sub = new JLabel("Select a module to get started");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_MUTED);

        JPanel tg = new JPanel();
        tg.setOpaque(false);
        tg.setLayout(new BoxLayout(tg, BoxLayout.Y_AXIS));
        tg.add(title);
        tg.add(sub);

        topBar.add(tg, BorderLayout.WEST);
        main.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 14, 14));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 28, 18, 28));

        statsPanel.add(createStatCard("Products", dashboardDB.getProductCount(), "📦"));
        statsPanel.add(createStatCard("Clients", dashboardDB.getClientCount(), "👥"));
        statsPanel.add(createStatCard("Orders", dashboardDB.getOrderCount(), "🧾"));
        statsPanel.add(createStatCard("Agents", dashboardDB.getAgentCount(), "🚚"));

        // module tiles
        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(6, 28, 28, 28));

        Object[][] modules = {
                {"📦", "Product Catalogue", "Manage products"},
                {"👥", "Clients", "Accounts & contacts"},
                {"🗄", "Inventory", "Stock & reorder levels"},
                {"🚚", "Delivery Agents", "Agent roster"},
                {"🧾", "Process Orders", "Create client orders"},
                {"📍", "Delivery Assignment", "Assign & track"},
                {"📊", "Reports", "Sales & inventory"},
                {"⏻", "Exit", "Close application"},
        };

        for (Object[] m : modules) {
            grid.add(moduleTile((String) m[0], (String) m[1], (String) m[2]));
        }

        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(grid, BorderLayout.CENTER);

        main.add(centerPanel, BorderLayout.CENTER);

        return main;
    }

    private JPanel moduleTile(String icon, String title, String subtitle) {
        JPanel tile = new JPanel() {
            boolean h = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { h = true;  repaint(); }
                public void mouseExited (MouseEvent e) { h = false; repaint(); }
                public void mouseClicked(MouseEvent e) {
                    if (title.equals("Exit")) System.exit(0);
                    else openModule(title);
                }
            });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0, h ? 20 : 10));
                g2.fillRoundRect(2, 3, getWidth()-4, getHeight()-4, 14, 14);
                g2.setColor(h ? new Color(0xF0FBF4) : C_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-2, 14, 14);
                if (h) {
                    g2.setColor(C_ACCENT);
                    g2.fillRoundRect(0, getHeight()-5, getWidth()-1, 6, 4, 4);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        tile.setOpaque(false);
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(new EmptyBorder(18,18,18,18));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        ic.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tl.setForeground(C_TEXT);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sl = new JLabel(subtitle);
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sl.setForeground(C_MUTED);
        sl.setAlignmentX(Component.LEFT_ALIGNMENT);

        tile.add(ic);
        tile.add(Box.createVerticalStrut(10));
        tile.add(tl);
        tile.add(Box.createVerticalStrut(3));
        tile.add(sl);
        return tile;
    }
    private JPanel createStatCard(String title, int value, String icon) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220,220,220)),
                        new EmptyBorder(15,15,15,15)
                )
        );

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValue);

        return card;
    }

    private void openModule(String label) {
        switch (label) {
            case "Products", "Product Catalogue" -> new ProductUI().setVisible(true);
            case "Clients"                        -> new ClientUI().setVisible(true);
            case "Inventory"                      -> new InventoryUI().setVisible(true);
            case "Agents", "Delivery Agents"      -> new DeliveryAgentUI().setVisible(true);
            case "Orders", "Process Orders"       -> new OrderUI().setVisible(true);
            case "Deliveries","Delivery Assignment"-> new DeliveryUI().setVisible(true);
            case "Reports"                        -> new ReportUI().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardUI().setVisible(true));
    }
}