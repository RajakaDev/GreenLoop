package database;

import model.DeliveryAgent;
import java.sql.*;
import java.util.ArrayList;

public class DeliveryAgentDB {

    public boolean addAgent(DeliveryAgent agent) {
        String sql = "INSERT INTO delivery_agents(name, email, phone, vehicle_no, vehicle_type) VALUES(?,?,?,?,?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, agent.getName());
            pst.setString(2, agent.getEmail());
            pst.setString(3, agent.getPhone());
            pst.setString(4, agent.getVehicleNo());
            pst.setString(5, agent.getVehicleType());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<DeliveryAgent> getAllAgents() {
        ArrayList<DeliveryAgent> agents = new ArrayList<>();
        String sql = "SELECT * FROM delivery_agents";

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                agents.add(new DeliveryAgent(
                        rs.getInt("agent_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("vehicle_no"),
                        rs.getString("vehicle_type")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return agents;
    }

    public boolean deleteAgent(int agentId) {
        String sql = "DELETE FROM delivery_agents WHERE agent_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, agentId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}