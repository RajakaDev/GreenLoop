package database;

import java.sql.*;
import java.util.ArrayList;

public class DeliveryDB {

    public boolean assignDelivery(int orderId, int agentId, String deliveryDate) {
        String sql = "INSERT INTO deliveries(order_id, agent_id, delivery_date, status) VALUES(?,?,?, 'Assigned')";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, orderId);
            pst.setInt(2, agentId);
            pst.setString(3, deliveryDate);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<String[]> getAllDeliveries() {
        ArrayList<String[]> list = new ArrayList<>();

        String sql = """
                SELECT d.delivery_id, d.order_id, c.name AS client_name,
                       a.name AS agent_name, d.delivery_date, d.status
                FROM deliveries d
                JOIN orders o ON d.order_id = o.order_id
                JOIN clients c ON o.client_id = c.client_id
                JOIN delivery_agents a ON d.agent_id = a.agent_id
                """;

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("delivery_id")),
                        String.valueOf(rs.getInt("order_id")),
                        rs.getString("client_name"),
                        rs.getString("agent_name"),
                        rs.getString("delivery_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateDeliveryStatus(int deliveryId, String status) {
        String sql = "UPDATE deliveries SET status=? WHERE delivery_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, status);
            pst.setInt(2, deliveryId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}