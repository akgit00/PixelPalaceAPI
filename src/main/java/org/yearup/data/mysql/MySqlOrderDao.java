package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    //constructor receives a DataSource from Spring and passes it to the base class
    public MySqlOrderDao(DataSource ds) {
        super(ds);
    }

    @Override
    public void createOrder(Profile profile, ShoppingCart cart) {
        BigDecimal total = cart.getTotal();

        //get the current date for the order record
        LocalDate today = LocalDate.now();

        try (
                //open a connection to the MySQL database
                Connection c = ds.getConnection();
                //prepare SQL statement to insert a new order
                PreparedStatement q = c.prepareStatement("""
                        INSERT INTO Orders 
                        (user_id, date, address, city, state, zip, shipping_amount)
                        VALUES (?,?,?,?,?,?,?)
                        """)
        ) {
            //bind user and shipping details to the prepared statement
            q.setInt(1, profile.getUserId());
            q.setDate(2, Date.valueOf(today));
            q.setString(3, profile.getAddress());
            q.setString(4, profile.getCity());
            q.setString(5, profile.getState());
            q.setString(6, profile.getZip());

            //set the total cost of the cart as the shipping amount
            q.setBigDecimal(7, total);

            //execute insert to create the order record
            q.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error checking out " + e);
        }
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
            System.out.println("Error adding to database");
        }
    }
}