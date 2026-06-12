package view;

import database.ClientDB;
import model.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ClientUI extends JFrame {

    private static final Color C_DARK   = new Color(0x1A2E22);
    private static final Color C_ACCENT = new Color(0x4CAF78);
    private static final Color C_DANGER = new Color(0xD94F4F);
    private static final Color C_BG     = new Color(0xF8FAF9);
    private static final Color C_CARD   = Color.WHITE;
    private static final Color C_BORDER = new Color(0xDDE8E2);
    private static final Color C_MUTED  = new Color(0x6B8070);
    private static final Color C_TEXT   = new Color(0x1A2E22);
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_ROW_ALT = new Color(0xF0F7F3);
    private static final Color C_ROW_SEL = new Color(0xC8E6D2);

    JTextField txtSearch;
    JTextField txtName, txtEmail, txtPhone, txtAddress;
    JTable table;
    DefaultTableModel model;
    ClientDB clientDB = new ClientDB();

    public ClientUI() {
        setTitle("GreenLoop - Client Management");
        setSize(1050, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);

        // ── Header bar ────────────────────────────────────────────────────────
        root.add(buildHeader(), BorderLayout.NORTH);

        // ── Body: form | table ────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 24, 24, 24));
        body.add(buildFormCard(), BorderLayout.WEST);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        loadClients();

        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable());
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK);
        bar.setBorder(new EmptyBorder(16, 28, 16, 28));
        JLabel title = new JLabel("Client Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_WHITE);
        JLabel sub = new JLabel("Add, edit and manage client accounts");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x8BB89A));
        JPanel g = new JPanel(); g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.add(title); g.add(sub);
        bar.add(g, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 0));

        JLabel heading = new JLabel("Client Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(C_TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName    = styledField(); txtEmail  = styledField();
        txtPhone   = styledField(); txtAddress = styledField();

        JButton btnAdd    = filledButton("Add Client",    C_ACCENT, new Color(0x3D9B68));
        JButton btnUpdate = filledButton("Update Client", new Color(0x2980B9), new Color(0x206090));
        JButton btnDelete = filledButton("Delete Client", C_DANGER, new Color(0xBF3A3A));

        for (JComponent c : new JComponent[]{txtName, txtEmail, txtPhone, txtAddress, btnAdd, btnUpdate, btnDelete}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        }

        card.add(heading);
        card.add(Box.createVerticalStrut(18));
        card.add(fieldRow("Client Name", txtName));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Email Address", txtEmail));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Phone Number", txtPhone));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Address", txtAddress));
        card.add(Box.createVerticalStrut(20));
        card.add(btnAdd);
        card.add(Box.createVerticalStrut(8));
        card.add(btnUpdate);
        card.add(Box.createVerticalStrut(8));
        card.add(btnDelete);

        btnAdd.addActionListener(e -> addClient());
        btnUpdate.addActionListener(e -> updateClient());
        btnDelete.addActionListener(e -> deleteClient());
        return card;
    }

    private JPanel buildTablePanel() {
        model = new DefaultTableModel(new String[]{"ID","Name","Email","Phone","Address"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = styledTable(model);
        JScrollPane sp = styledScroll(table);

        JLabel heading = new JLabel("All Clients");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(C_TEXT);

        txtSearch = styledField();
        txtSearch.setPreferredSize(new Dimension(220, 32));

        JButton btnSearch = smallButton("Search");
        JButton btnShowAll = smallButton("Show All");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);

        searchPanel.add(new JLabel("Search Client"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchClients());

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadClients();
        });

        return panel;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtName.setText(model.getValueAt(row, 1).toString());
            txtEmail.setText(model.getValueAt(row, 2).toString());
            txtPhone.setText(model.getValueAt(row, 3).toString());
            txtAddress.setText(model.getValueAt(row, 4).toString());
        }
    }

    private void addClient() {
        String name = txtName.getText().trim(), email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim(), address = txtAddress.getText().trim();
        if (name.isEmpty())  { JOptionPane.showMessageDialog(this,"Client name is required"); return; }
        if (email.isEmpty()) { JOptionPane.showMessageDialog(this,"Email is required");       return; }
        if (!email.contains("@") || !email.contains(".")) { JOptionPane.showMessageDialog(this,"Enter a valid email address"); return; }
        if (phone.isEmpty()) { JOptionPane.showMessageDialog(this,"Phone number is required"); return; }
        if (!phone.matches("\\d{10}")) { JOptionPane.showMessageDialog(this,"Phone number must contain 10 digits"); return; }
        if (address.isEmpty()) { JOptionPane.showMessageDialog(this,"Address is required"); return; }
        Client client = new Client(name, email, phone, address);
        if (clientDB.addClient(client)) {
            JOptionPane.showMessageDialog(this,"Client Added Successfully");
            clearFields(); loadClients();
        }
    }

    private void updateClient() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this,"Select a Client to update"); return; }
        String name = txtName.getText().trim(), email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim(), address = txtAddress.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this,"Client name is required"); return; }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) { JOptionPane.showMessageDialog(this,"Enter a valid email address"); return; }
        if (!phone.matches("\\d{10}")) { JOptionPane.showMessageDialog(this,"Phone number must contain 10 digits"); return; }
        if (address.isEmpty()) { JOptionPane.showMessageDialog(this,"Address is required"); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        Client client = new Client(id, name, email, phone, address);
        if (clientDB.updateClient(client)) {
            JOptionPane.showMessageDialog(this,"Client Updated Successfully");
            clearFields(); loadClients();
        }
    }

    private void deleteClient() {

        int row = table.getSelectedRow();

        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Client");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this client?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if(clientDB.deleteClient(id)) {
            JOptionPane.showMessageDialog(this, "Client Deleted Successfully");
            clearFields();
            loadClients();
        }
    }

    private void loadClients() {
        model.setRowCount(0);
        ArrayList<Client> clients = clientDB.getAllClients();
        for (Client c : clients)
            model.addRow(new Object[]{c.getClientId(),c.getName(),c.getEmail(),c.getPhone(),c.getAddress()});
    }
    private void searchClients() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadClients();
            return;
        }

        model.setRowCount(0);

        ArrayList<Client> clients = clientDB.searchClients(keyword);

        for (Client c : clients) {
            model.addRow(new Object[]{
                    c.getClientId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress()
            });
        }

        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clients found");
        }
    }

    private void clearFields() {
        txtName.setText(""); txtEmail.setText(""); txtPhone.setText(""); txtAddress.setText("");
    }

    // ── Widget helpers ────────────────────────────────────────────────────────
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
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        return p;
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
        row.add(lbl); row.add(Box.createVerticalStrut(4)); row.add(field);
        return row;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBackground(C_CARD); f.setForeground(C_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true), new EmptyBorder(4, 10, 4, 10)));
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(C_WHITE); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? C_CARD : C_ROW_ALT);
                else c.setBackground(C_ROW_SEL);
                return c;
            }
        };
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(34); t.setGridColor(C_BORDER);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(C_ROW_SEL); t.setSelectionForeground(C_TEXT);
        t.setBackground(C_CARD); t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(C_DARK);
        t.getTableHeader().setForeground(C_WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 38));
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(center);
        return t;
    }

    private JScrollPane styledScroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        sp.getViewport().setBackground(C_CARD);
        return sp;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientUI().setVisible(true));
    }
}