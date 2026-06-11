package view;

import database.DeliveryAgentDB;
import model.DeliveryAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class DeliveryAgentUI extends JFrame {

    JTextField txtName, txtEmail, txtPhone, txtVehicleNo, txtVehicleType;
    JTable table;
    DefaultTableModel model;

    DeliveryAgentDB agentDB = new DeliveryAgentDB();

    public DeliveryAgentUI() {
        setTitle("GreenLoop - Delivery Agent Management");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtVehicleNo = new JTextField();
        txtVehicleType = new JTextField();

        formPanel.add(new JLabel("Agent Name"));
        formPanel.add(txtName);

        formPanel.add(new JLabel("Email"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Phone"));
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Vehicle No"));
        formPanel.add(txtVehicleNo);

        formPanel.add(new JLabel("Vehicle Type"));
        formPanel.add(txtVehicleType);

        JButton btnAdd = new JButton("Add Agent");
        JButton btnDelete = new JButton("Delete Agent");

        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Vehicle No");
        model.addColumn("Vehicle Type");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadAgents();

        btnAdd.addActionListener(e -> addAgent());
        btnDelete.addActionListener(e -> deleteAgent());
    }

    private void addAgent() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String vehicleNo = txtVehicleNo.getText().trim();
        String vehicleType = txtVehicleType.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agent name is required");
            return;
        }

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain 10 digits");
            return;
        }

        if (vehicleNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle number is required");
            return;
        }

        if (vehicleType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle type is required");
            return;
        }

        DeliveryAgent agent = new DeliveryAgent(name, email, phone, vehicleNo, vehicleType);

        if (agentDB.addAgent(agent)) {
            JOptionPane.showMessageDialog(this, "Delivery Agent Added Successfully");
            clearFields();
            loadAgents();
        }
    }

    private void deleteAgent() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Delivery Agent");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (agentDB.deleteAgent(id)) {
            JOptionPane.showMessageDialog(this, "Delivery Agent Deleted Successfully");
            loadAgents();
        }
    }

    private void loadAgents() {
        model.setRowCount(0);

        ArrayList<DeliveryAgent> agents = agentDB.getAllAgents();

        for (DeliveryAgent a : agents) {
            model.addRow(new Object[]{
                    a.getAgentId(),
                    a.getName(),
                    a.getEmail(),
                    a.getPhone(),
                    a.getVehicleNo(),
                    a.getVehicleType()
            });
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtVehicleNo.setText("");
        txtVehicleType.setText("");
    }

    public static void main(String[] args) {
        new DeliveryAgentUI().setVisible(true);
    }
}