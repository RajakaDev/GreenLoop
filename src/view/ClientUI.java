package view;

import database.ClientDB;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ClientUI extends JFrame {

    JTextField txtName, txtEmail, txtPhone, txtAddress;
    JTable table;
    DefaultTableModel model;

    ClientDB clientDB = new ClientDB();

    public ClientUI() {
        setTitle("GreenLoop - Client Management");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();

        formPanel.add(new JLabel("Client Name"));
        formPanel.add(txtName);

        formPanel.add(new JLabel("Email"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Phone"));
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Address"));
        formPanel.add(txtAddress);

        JButton btnAdd = new JButton("Add Client");
        JButton btnDelete = new JButton("Delete Client");

        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Address");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadClients();

        btnAdd.addActionListener(e -> addClient());
        btnDelete.addActionListener(e -> deleteClient());
    }

    private void addClient() {

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Client name is required");
            return;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address");
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain 10 digits");
            return;
        }

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required");
            return;
        }

        Client client = new Client(name, email, phone, address);

        if (clientDB.addClient(client)) {
            JOptionPane.showMessageDialog(this, "Client Added Successfully");
            clearFields();
            loadClients();
        }
    }

    private void deleteClient() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Client");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (clientDB.deleteClient(id)) {
            JOptionPane.showMessageDialog(this, "Client Deleted Successfully");
            loadClients();
        }
    }

    private void loadClients() {
        model.setRowCount(0);

        ArrayList<Client> clients = clientDB.getAllClients();

        for (Client c : clients) {
            model.addRow(new Object[]{
                    c.getClientId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress()
            });
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        new ClientUI().setVisible(true);
    }
}