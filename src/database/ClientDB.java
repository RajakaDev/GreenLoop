package database;

import model.Client;
import java.sql.*;
import java.util.ArrayList;

public class ClientDB {

    public boolean addClient(Client client) {
        String sql = "INSERT INTO clients(name, email, phone, address) VALUES(?,?,?,?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, client.getName());
            pst.setString(2, client.getEmail());
            pst.setString(3, client.getPhone());
            pst.setString(4, client.getAddress());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("client_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clients;
    }

    public boolean deleteClient(int clientId) {
        String sql = "DELETE FROM clients WHERE client_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, clientId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean updateClient(Client client) {

        String sql = "UPDATE clients SET name=?, email=?, phone=?, address=? WHERE client_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, client.getName());
            pst.setString(2, client.getEmail());
            pst.setString(3, client.getPhone());
            pst.setString(4, client.getAddress());
            pst.setInt(5, client.getClientId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}