package database;

import java.sql.*;
import java.util.ArrayList;

public class ReportDB {

    public ArrayList<String[]> getLowStockProducts() {
        ArrayList<String[]> list = new ArrayList<>();

        String sql = "SELECT product_id, name, quantity, reorder_level FROM products WHERE quantity <= reorder_level";

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("product_id")),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("quantity")),
                        String.valueOf(rs.getInt("reorder_level"))
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public double getMonthlyRevenue(String month) {
        String sql = "SELECT SUM(total_amount) AS revenue FROM orders WHERE DATE_FORMAT(order_date, '%Y-%m') = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, month);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getDouble("revenue");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}