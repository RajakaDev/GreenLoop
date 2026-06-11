package database;

import model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderDB {

    ProductDB productDB = new ProductDB();

    public boolean createOrder(int clientId, int productId, int quantity) {

        Product product = productDB.getProductById(productId);

        if (product == null) {
            return false;
        }

        if (quantity > product.getQuantity()) {
            return false;
        }

        double subtotal = product.getPrice() * quantity;

        try {
            Connection conn = DBConnection.getConnection();

            String orderSql = "INSERT INTO orders(client_id, order_date, total_amount, status) VALUES(?, CURDATE(), ?, 'Pending')";
            PreparedStatement orderPst = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);

            orderPst.setInt(1, clientId);
            orderPst.setDouble(2, subtotal);
            orderPst.executeUpdate();

            ResultSet rs = orderPst.getGeneratedKeys();
            int orderId = 0;

            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            String itemSql = "INSERT INTO order_items(order_id, product_id, quantity, price, subtotal) VALUES(?,?,?,?,?)";
            PreparedStatement itemPst = conn.prepareStatement(itemSql);

            itemPst.setInt(1, orderId);
            itemPst.setInt(2, productId);
            itemPst.setInt(3, quantity);
            itemPst.setDouble(4, product.getPrice());
            itemPst.setDouble(5, subtotal);
            itemPst.executeUpdate();

            String stockSql = "UPDATE products SET quantity = quantity - ? WHERE product_id=?";
            PreparedStatement stockPst = conn.prepareStatement(stockSql);

            stockPst.setInt(1, quantity);
            stockPst.setInt(2, productId);
            stockPst.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}