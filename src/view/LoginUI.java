package view;

import database.UserDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginUI extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_DARK    = new Color(0x1A2E22);
    private static final Color C_ACCENT  = new Color(0x4CAF78);
    private static final Color C_BG      = new Color(0xF8FAF9);
    private static final Color C_CARD    = Color.WHITE;
    private static final Color C_BORDER  = new Color(0xDDE8E2);
    private static final Color C_MUTED   = new Color(0x6B8070);
    private static final Color C_WHITE   = Color.WHITE;

    JTextField     txtUsername;
    JPasswordField txtPassword;
    UserDB         userDB = new UserDB();

    public LoginUI() {
        setTitle("GreenLoop - Login");
        setSize(860, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // root: two columns
        JPanel root = new JPanel(new GridLayout(1, 2));
        setContentPane(root);

        // ── LEFT: branding ────────────────────────────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_DARK, getWidth(), getHeight(), new Color(0x0F1F14));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_ACCENT);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth() - 100, getHeight() - 120, 240, 240);
                g2.dispose();
            }
        };
        left.setLayout(new GridBagLayout());

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(0, 40, 0, 40));

        // green circle "logo"
        JPanel logoCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ACCENT); g2.fillOval(0, 0, 60, 60);
                g2.setColor(C_WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("G", (60 - fm.stringWidth("G")) / 2, (60 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(60, 60));
        logoCircle.setMaximumSize(new Dimension(60, 60));
        logoCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("GreenLoop");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 30));
        appName.setForeground(C_WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html>Eco Packaging<br>Management System</html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(0x8BB89A));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2E4A36));
        sep.setMaximumSize(new Dimension(140, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(Box.createVerticalGlue());
        brand.add(logoCircle);
        brand.add(Box.createVerticalStrut(18));
        brand.add(appName);
        brand.add(Box.createVerticalStrut(8));
        brand.add(tagline);
        brand.add(Box.createVerticalStrut(24));
        brand.add(sep);
        brand.add(Box.createVerticalGlue());
        left.add(brand);

        // ── RIGHT: form ───────────────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(C_BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,12)); g2.fillRoundRect(2,3,getWidth()-4,getHeight()-2,14,14);
                g2.setColor(C_CARD); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-2,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setPreferredSize(new Dimension(330, 360));

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(C_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your admin account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = styledField();
        txtPassword = styledPasswordField();

        JButton btnLogin = styledButton("Sign In", C_ACCENT, new Color(0x3D9B68));
        JButton btnExit  = outlineButton("Exit");

        for (JComponent c : new JComponent[]{txtUsername, txtPassword, btnLogin, btnExit}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        }

        card.add(heading);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));
        card.add(fieldLabel("Username")); card.add(Box.createVerticalStrut(5));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("Password")); card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(24));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnExit);

        right.add(card);
        root.add(left);
        root.add(right);

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (username.isEmpty()) { JOptionPane.showMessageDialog(this, "Username is required"); return; }
        if (password.isEmpty()) { JOptionPane.showMessageDialog(this, "Password is required");  return; }
        if (userDB.login(username, password)) {
            new DashboardUI().setVisible(true); dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }

    // ── Shared widget helpers (self-contained, no external theme class) ────────
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBackground(C_CARD); f.setForeground(C_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        return f;
    }
    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBackground(C_CARD); f.setForeground(C_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        return f;
    }
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private JButton styledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(C_WHITE); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(C_ACCENT); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}