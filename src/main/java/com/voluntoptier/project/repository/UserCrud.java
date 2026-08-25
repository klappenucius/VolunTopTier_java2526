package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.utils.DatabaseUtil;

import java.io.IOException;
import java.sql.*;

public final class UserCrud implements Crud{

    private Connection connection;
    private final AddressCrud addressCrud;
    private HoursWorked hoursWorked;

    public UserCrud(Connection connection, AddressCrud addressCrud){
        this.connection = connection;
        this.addressCrud = addressCrud;
    }

    public DBitem add (DBitem item) {
        if (!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO users (firstName, lastName, username, oib, dateOfBirth, email, address_id, role, approvedHours, pendingApproval) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {
            prepStmt.setString(1, user.getFirstName());
            prepStmt.setString(2, user.getLastName());
            prepStmt.setString(3, user.getUsername());
            prepStmt.setString(4, user.getOib());
            prepStmt.setDate(5, Date.valueOf(user.getDateOfBirth()));
            prepStmt.setString(6, user.getEmail());
            prepStmt.setInt(7, user.getAddress().getId());
            prepStmt.setString(8, user.getRole().name());
            prepStmt.setInt(9, user.getTotalHoursWorked().approvedHours());
            prepStmt.setInt(10, user.getTotalHoursWorked().pendingApproval());

            int affectedRows = prepStmt.executeUpdate();

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }

        return user;
    }

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM users WHERE id = ?";
        User resultUser = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try(ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {
                    Address address = (Address) addressCrud.getById(selectResults.getInt("address_id"));
                    HoursWorked hoursWokred = new HoursWorked(
                            selectResults.getInt("approvedHours"),
                            selectResults.getInt("pendingApproval")
                    );
                    Role role = Role.valueOf(selectResults.getString("role"));

                    resultUser = new User(
                            selectResults.getInt("id"),
                            selectResults.getString("firstName"),
                            selectResults.getString("lastName"),
                            selectResults.getString("username"),
                            selectResults.getString("oib"),
                            selectResults.getDate("dateOfBirth").toLocalDate(),
                            address,
                            selectResults.getString("email"),
                            role
                    );

                    resultUser.setTotalHoursWorked(hoursWorked);

                } else {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }
        return resultUser;
    }

    public User fetchByUsername(String username) {
        String selectSql = "SELECT * FROM users WHERE username=?";
        User resultUser = null;

        try(PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setString(1, username);

            try(ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {
                    Address address = (Address) addressCrud.getById(selectResults.getInt("address_id"));
                    HoursWorked hoursWokred = new HoursWorked(
                            selectResults.getInt("approvedHours"),
                            selectResults.getInt("pendingApproval")
                    );
                    Role role = Role.valueOf(selectResults.getString("role"));

                    resultUser = new User(
                            selectResults.getInt("id"),
                            selectResults.getString("firstName"),
                            selectResults.getString("lastName"),
                            selectResults.getString("username"),
                            selectResults.getString("oib"),
                            selectResults.getDate("dateOfBirth").toLocalDate(),
                            address,
                            selectResults.getString("email"),
                            role
                    );

                    resultUser.setTotalHoursWorked(hoursWorked);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }

        return resultUser;
    }

    public boolean update (DBitem item) {

        if(!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        String updateSql = "UPDATE users SET firstName = ?, lastName = ?, username = ?, oib = ?, dateOfBirth = ?, email = ?, address_id = ?, role = ?, approvedHours = ?, pendingApproval = ? WHERE id = ?";


        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {

            prepStmt.setString(1, user.getFirstName());
            prepStmt.setString(2, user.getLastName());
            prepStmt.setString(3, user.getUsername());
            prepStmt.setString(4, user.getOib());
            prepStmt.setDate(5, Date.valueOf(user.getDateOfBirth()));
            prepStmt.setString(6, user.getEmail());
            prepStmt.setInt(7, user.getAddress().getId());
            prepStmt.setString(8, user.getRole().name());
            prepStmt.setInt(9, user.getTotalHoursWorked().approvedHours());
            prepStmt.setInt(10, user.getTotalHoursWorked().pendingApproval());
            prepStmt.setInt(11, user.getId());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no user found with id: " + user.getId());
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    public boolean delete (int id) {

        String deleteSql = "DELETE FROM users WHERE id = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {
            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if(affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no user found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }
}
