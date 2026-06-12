package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardDB {

    private int getCount(String tableName) {

        try {

            Connection conn =
                    DBConnection.getConnection();

            Statement st =
                    conn.createStatement();

            ResultSet rs =
                    st.executeQuery(
                            "SELECT COUNT(*) FROM " + tableName
                    );

            if(rs.next()) {
                return rs.getInt(1);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getProductCount() {
        return getCount("products");
    }

    public int getClientCount() {
        return getCount("clients");
    }

    public int getOrderCount() {
        return getCount("orders");
    }

    public int getAgentCount() {
        return getCount("delivery_agents");
    }
}