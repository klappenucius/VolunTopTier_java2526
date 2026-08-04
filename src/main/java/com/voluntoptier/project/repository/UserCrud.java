package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;

import java.sql.*;

public final class UserCrud implements Crud{

    private final Connection connection;
    private final AddressCrud addressCrud;

    public UserCrud(Connection connection, AddressCrud addressCrud) {
        this.connection = connection;
        this.addressCrud = addressCrud;
    }

    public DBitem add (DBitem item) {
        if (!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO projects (firstName, lastName, oib, email, hoursWorked, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {
            prepStmt.setString(1, user.getFirstName());
            prepStmt.setString(2, user.getLastName());
            prepStmt.setString(3, user.getOib());
            prepStmt.setString(4, user.getEmail());
            prepStmt.setInt(5, user.getTotalHoursWorked().approvedHours());
            prepStmt.setInt(6, user.getAddress().getId());

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

        String selectSql = "SELECT * FROM projects WHERE id = ?";
        Project resultProject = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try(ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {
                    Address address = (Address) addressCrud.getById(selectResults.getInt("address_id"));

                    resultProject = new Project.ProjectBuilder(
                            selectResults.getInt("id"),
                            selectResults.getString("name"),
                            selectResults.getInt("totalHours"),
                            selectResults.getDate("startDate").toLocalDate(),
                            selectResults.getDate("endDate").toLocalDate(),
                            selectResults.getInt("volunteersNeeded"))
                            .address(address)
                            .hoursWorked(selectResults.getInt("hoursWorked"))
                            .build();
                } else {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }
        return resultProject;
    }

    public boolean update (DBitem item) {

        if(!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        String updateSql = "UPDATE projects SET firstName = ?, lastName = ?, oib = ?, email = ?, hoursWorked = ?, address_id = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {

            prepStmt.setString(1, user.getFirstName());
            prepStmt.setString(2, user.getLastName());
            prepStmt.setString(3, user.getOib());
            prepStmt.setString(4, user.getEmail());
            prepStmt.setInt(5, user.getTotalHoursWorked().approvedHours());
            prepStmt.setInt(6, user.getAddress().getId());
            prepStmt.setInt(7, user.getId());

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
