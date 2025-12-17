package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile update(int userId, Profile profile) {
        try(
                Connection c = ds.getConnection();
                PreparedStatement q = c.prepareStatement("""
                        UPDATE 
                            Profiles
                        SET 
                            first_name = COALESCE(?, first_name),
                            last_name = COALESCE(?, last_name),
                            phone = COALESCE(?, phone),
                            email = COALESCE(?,email),
                            address = COALESCE(?, address),
                            city = COALESCE(?, city),
                            state = COALESCE(?, state),
                            zip = COALESCE(?,zip)
                        WHERE
                            user_id = ?
                        """)
        ){
            q.setString(1, profile.getFirstName());
            q.setString(2, profile.getLastName());
            q.setString(3, profile.getPhone());
            q.setString(4, profile.getEmail());
            q.setString(5, profile.getAddress());
            q.setString(6, profile.getCity());
            q.setString(7, profile.getState());
            q.setString(8, profile.getZip());
            q.setInt(9, userId);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error updating profile" + e);
        }
        return null;
    }

}
