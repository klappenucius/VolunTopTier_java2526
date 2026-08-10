package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.utils.DatabaseUtil;

import java.io.IOException;
import java.sql.*;

public final class AddressCrud implements Crud{

    private Connection connection;

    public AddressCrud(Connection connection){
        this.connection = connection;
    }

    public DBitem add (DBitem item) {

        if(!(item instanceof Address address)) {
            throw new IllegalArgumentException("Expected an Address, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO addresses (STREET, HOUSENUMBER, POSTALCODE, CITY, COUNTRY) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {
            prepStmt.setString(1, address.getStreet());
            prepStmt.setString(2, address.getHouseNumber());
            prepStmt.setString(3, address.getPostalCode());
            prepStmt.setString(4, address.getCity());
            prepStmt.setString(5, address.getCountry());

            int affectedRows = prepStmt.executeUpdate();

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    address.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating address failed, no ID obtained.");
                }
            }
            return address;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding address: " + e.getMessage(), e);
        }
    }

    public DBitem getById (int id) {

        String selectSql = "SELECT * FROM addresses WHERE id = ?";
        Address resultAddress = null;

        try(PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try (ResultSet selectResults = prepStmt.executeQuery()) {
                if(selectResults.next()) {
                    resultAddress = new Address(selectResults.getInt("id"),
                            selectResults.getString("street"),
                            selectResults.getString("houseNumber"),
                            selectResults.getString("postalCode"),
                            selectResults.getString("city"),
                            selectResults.getString("country"));
                } else {
                    throw new RuntimeException("Creating address failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching address: " + e.getMessage(), e);
        }

        return resultAddress;
    }

    public boolean update (DBitem item) {

        if(!(item instanceof Address address)) {
            throw new IllegalArgumentException("Expected an Address, got: " + item.getClass());
        }

        String updateSql = "UPDATE addresses SET street = ?, houseNumber = ?, postalCode = ?, city = ?, country = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {
            prepStmt.setString(1, address.getStreet());
            prepStmt.setString(2, address.getHouseNumber());
            prepStmt.setString(3, address.getPostalCode());
            prepStmt.setString(4, address.getCity());
            prepStmt.setString(5, address.getCountry());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no address found with id: " + address.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating address: " + e.getMessage(), e);
        }
        return true;
    }

    public boolean delete (int id) {

        String deleteSql = "DELETE FROM addresses WHERE id = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {
            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no address found with id: " + id);
            } else {
                return true;
        }
    } catch (SQLException e) {
            throw new RuntimeException("Error deleting address: " + e.getMessage(), e);
        }
    }
}
