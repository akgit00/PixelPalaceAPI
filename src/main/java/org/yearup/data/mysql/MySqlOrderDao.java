package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    //constructor receives a DataSource from Spring and passes it to the base class
    public MySqlOrderDao(DataSource ds) {
        super(ds);
    }

    @Override
    public int createOrder(Profile profile, ShoppingCart cart) {
        BigDecimal total = cart.getTotal();

        //get the current date for the order record
        LocalDateTime today = LocalDateTime.now();

        int orderId = 0;

        try (
                //open a connection to the MySQL database
                Connection c = ds.getConnection();
                //prepare SQL statement to insert a new order
                PreparedStatement q = c.prepareStatement("""
                        INSERT INTO Orders 
                        (user_id, date, address, city, state, zip, shipping_amount)
                        VALUES (?,?,?,?,?,?,?)
                           """, Statement.RETURN_GENERATED_KEYS)) {

            //bind user and shipping details to the prepared statement
            q.setInt(1, profile.getUserId());
            q.setTimestamp(2, Timestamp.valueOf((today)));
            q.setString(3, profile.getAddress());
            q.setString(4, profile.getCity());
            q.setString(5, profile.getState());
            q.setString(6, profile.getZip());

            //set the total cost of the cart as the shipping amount
            q.setBigDecimal(7, total);

            //execute insert to create the order record
            q.executeUpdate();

            try (
                    ResultSet r = q.getGeneratedKeys()) {
                //check if keys were generated
                if (!r.next()) {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
                //Store generated key in first column (order_id) in our orderId int
                orderId = r.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error checking out " + e);
        }
        return orderId;
    }

    @Override
    public void addOrderToDatabase(int orderId, ShoppingCartItem item ){
        try(Connection c = ds.getConnection();
            PreparedStatement q = c.prepareStatement("""
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                VALUES (?,?,?,?,?)
                """) ){
            q.setInt(1, orderId);
            q.setInt(2, item.getProductId());
            q.setBigDecimal(3,item.getLineTotal());
            q.setInt(4, item.getQuantity());
            q.setBigDecimal(5, item.getDiscountPercent());

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error adding to database " + e);
        }
    }

    @Override
    public void updateStock(int productId, int quantity) {
        try(Connection c = ds.getConnection();
            PreparedStatement q = c.prepareStatement("""
                UPDATE products
                SET stock = stock - ?
                WHERE product_id = ?
                AND stock >= ?
                """)){
            q.setInt(1, quantity);
            q.setInt(2, productId);
            q.setInt(3, quantity);

            int rows = q.executeUpdate();

            if(rows == 0){
                throw new RuntimeException("Were all out of product # " + productId);
            }

        }catch(SQLException e){
            System.out.println("Error updating stock " + e);
        }
    }
}