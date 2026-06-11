import database.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("GreenLoop Database Connected Successfully");
        } else {
            System.out.println("Database Connection Failed");
        }
    }
}
