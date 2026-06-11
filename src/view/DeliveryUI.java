package view;

import database.DeliveryAgentDB;
import database.DeliveryDB;
import database.DBConnection;
import model.DeliveryAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DeliveryUI extends JFrame {

    JComboBox<String> cmbOrder;
    JComboBox<String> cmbAgent;
    JComboBox<String> cmbStatus;
    JTextField txtDate;

    JTable table;
    DefaultTableModel model;

    ArrayList<Integer> orderIds = new ArrayList<>();
    ArrayList<DeliveryAgent> agents;

    DeliveryAgentDB agentDB = new DeliveryAgentDB();
    DeliveryDB deliveryDB = new DeliveryDB();

    public DeliveryUI() {
        setTitle("GreenLoop - Delivery Assignment");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        cmbOrder = new JComboBox<>();
        cmbAgent = new JComboBox<>();
        txtDate = new JTextField("2026-06-11");
        cmbStatus = new JComboBox<>(new String[]{"Assigned", "Dispatched", "Delivered"});

        JButton btnAssign = new JButton("Assign Delivery");
        JButton btnUpdateStatus = new JButton("Update Status");

        panel.add(new JLabel("Select Order"));
        panel.add(cmbOrder);

        panel.add(new JLabel("Select Agent"));
        panel.add(cmbAgent);

        panel.add(new JLabel("Delivery Date"));
        panel.add(txtDate);

        panel.add(new JLabel("Delivery Status"));
        panel.add(cmbStatus);

        panel.add(btnAssign);
        panel.add(btnUpdateStatus);

        add(panel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("Delivery ID");
        model.addColumn("Order ID");
        model.addColumn("Client");
        model.addColumn("Agent");
        model.addColumn("Date");
        model.addColumn("Status");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadOrders();
        loadAgents();
        loadDeliveries();

        btnAssign.addActionListener(e -> assignDelivery());
        btnUpdateStatus.addActionListener(e -> updateStatus());
    }

    private void loadOrders() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT order_id FROM orders");

            while (rs.next()) {
                int id = rs.getInt("order_id");
                orderIds.add(id);
                cmbOrder.addItem("Order #" + id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAgents() {
        agents = agentDB.getAllAgents();

        for (DeliveryAgent a : agents) {
            cmbAgent.addItem(a.getAgentId() + " - " + a.getName());
        }
    }

    private void assignDelivery() {
        if (cmbOrder.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Select an order");
            return;
        }

        if (cmbAgent.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Select an agent");
            return;
        }

        String date = txtDate.getText().trim();

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Date must be YYYY-MM-DD");
            return;
        }

        int orderId = orderIds.get(cmbOrder.getSelectedIndex());
        int agentId = agents.get(cmbAgent.getSelectedIndex()).getAgentId();

        if (deliveryDB.assignDelivery(orderId, agentId, date)) {
            JOptionPane.showMessageDialog(this, "Delivery Assigned Successfully");
            loadDeliveries();
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a delivery row");
            return;
        }

        int deliveryId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String status = cmbStatus.getSelectedItem().toString();

        if (deliveryDB.updateDeliveryStatus(deliveryId, status)) {
            JOptionPane.showMessageDialog(this, "Delivery Status Updated");
            loadDeliveries();
        }
    }

    private void loadDeliveries() {
        model.setRowCount(0);

        ArrayList<String[]> deliveries = deliveryDB.getAllDeliveries();

        for (String[] d : deliveries) {
            model.addRow(d);
        }
    }

    public static void main(String[] args) {
        new DeliveryUI().setVisible(true);
    }
}