package database;

import model.Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductDB {

    // Add Product
    public boolean addProduct(Product product) {

        String sql = "INSERT INTO products(name, category, price, eco_rating, quantity, reorder_level) VALUES(?,?,?,?,?,?)";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, product.getName());
            pst.setString(2, product.getCategory());
            pst.setDouble(3, product.getPrice());
            pst.setInt(4, product.getEcoRating());
            pst.setInt(5, product.getQuantity());
            pst.setInt(6, product.getReorderLevel());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get All Products
    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products";

        try {

            Connection conn = DBConnection.getConnection();

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("eco_rating"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level")
                );

                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    // Delete Product
    public boolean deleteProduct(int productId) {

        String sql = "DELETE FROM products WHERE product_id=?";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, productId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean stockInProduct(int productId, int quantityToAdd) {

        String sql =
                "UPDATE products " +
                        "SET quantity = quantity + ? " +
                        "WHERE product_id=?";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setInt(1, quantityToAdd);
            pst.setInt(2, productId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
    public boolean updateProduct(Product product) {

        String sql = "UPDATE products SET name=?, category=?, price=?, eco_rating=?, quantity=?, reorder_level=? WHERE product_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, product.getName());
            pst.setString(2, product.getCategory());
            pst.setDouble(3, product.getPrice());
            pst.setInt(4, product.getEcoRating());
            pst.setInt(5, product.getQuantity());
            pst.setInt(6, product.getReorderLevel());
            pst.setInt(7, product.getProductId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, productId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("eco_rating"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public ArrayList<Product> searchProducts(String keyword) {

        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();

            while(rs.next()) {

                products.add(
                        new Product(
                                rs.getInt("product_id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getDouble("price"),
                                rs.getInt("eco_rating"),
                                rs.getInt("quantity"),
                                rs.getInt("reorder_level")
                        )
                );
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return products;
    }

}