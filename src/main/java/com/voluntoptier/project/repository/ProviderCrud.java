package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Provider;
import com.voluntoptier.project.utils.DatabaseUtil;

import java.io.IOException;
import java.sql.*;

public final class ProviderCrud implements Crud{
    private Connection connection;
    private final AddressCrud addressCrud;

    public ProviderCrud(Connection connection, AddressCrud addressCrud){
        this.connection = connection;
        this.addressCrud = addressCrud;
    }

    public DBitem add (DBitem item) {
        if (!(item instanceof Provider provider)) {
            throw new IllegalArgumentException("Expected a Provider, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO projects (name, oib, contact, address_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {
            prepStmt.setString(1, provider.getName());
            prepStmt.setString(2, provider.getOib());
            prepStmt.setString(3, provider.getContact());
            prepStmt.setInt(4, provider.getAddress().getId());

            int affectedRows = prepStmt.executeUpdate();

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    provider.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating provider failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding provider: " + e.getMessage(), e);
        }

        return provider;
    }

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM providers WHERE id = ?";
        Provider resultProvider = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try(ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {
                    Address address = (Address) addressCrud.getById(selectResults.getInt("address_id"));

                    resultProvider = new Provider(
                            selectResults.getInt("id"),
                            selectResults.getString("name"),
                            selectResults.getString("oib"),
                            selectResults.getString("contact"));
                } else {
                    throw new RuntimeException("Creating provider failed, no ID obtained.");
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Error adding provider: " + e.getMessage(), e);
        }
        return resultProvider;
    }

    public boolean update (DBitem item) {

        if(!(item instanceof Provider provider)) {
            throw new IllegalArgumentException("Expected a Provider, got: " + item.getClass());
        }

        String updateSql = "UPDATE projects SET name = ?, oib = ?, contact =?, address_id = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {

            prepStmt.setString(1, provider.getName());
            prepStmt.setString(2, provider.getOib());
            prepStmt.setString(3, provider.getContact());
            prepStmt.setInt(4, provider.getAddress().getId());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no provider found with id: " + provider.getId());
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating provider: " + e.getMessage(), e);
        }
    }

    public boolean delete (int id) {

        String deleteSql = "DELETE FROM providers WHERE id = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {
            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if(affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no provider found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting provider: " + e.getMessage(), e);
        }
    }
}
