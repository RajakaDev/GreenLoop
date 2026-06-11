package view;

import javax.swing.*;
import java.awt.*;

public class DashboardUI extends JFrame {

    public DashboardUI() {
        setTitle("GreenLoop - Eco Packaging Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("GreenLoop Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        mainPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));

        JButton btnProducts = new JButton("Product Catalogue");
        JButton btnClients = new JButton("Clients");
        JButton btnInventory = new JButton("Inventory");
        JButton btnAgents = new JButton("Delivery Agents");
        JButton btnOrders = new JButton("Process Orders");
        JButton btnDeliveries = new JButton("Delivery Assignment");
        JButton btnReports = new JButton("Reports");
        JButton btnExit = new JButton("Exit");

        buttonPanel.add(btnProducts);
        buttonPanel.add(btnClients);
        buttonPanel.add(btnInventory);
        buttonPanel.add(btnAgents);
        buttonPanel.add(btnOrders);
        buttonPanel.add(btnDeliveries);
        buttonPanel.add(btnReports);
        buttonPanel.add(btnExit);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        btnProducts.addActionListener(e -> new ProductUI().setVisible(true));
        btnClients.addActionListener(e -> new ClientUI().setVisible(true));
        btnInventory.addActionListener(e -> new InventoryUI().setVisible(true));
        btnAgents.addActionListener(e -> new DeliveryAgentUI().setVisible(true));
        btnOrders.addActionListener(e -> new OrderUI().setVisible(true));
        btnDeliveries.addActionListener(e -> new DeliveryUI().setVisible(true));
        btnReports.addActionListener(e -> new ReportUI().setVisible(true));
        btnExit.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new DashboardUI().setVisible(true);
    }
}