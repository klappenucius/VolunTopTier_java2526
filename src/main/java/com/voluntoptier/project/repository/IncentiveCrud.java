package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Incentive;

import java.sql.*;

public final class IncentiveCrud implements Crud{

    private final Connection connection;

    public IncentiveCrud(Connection connection) {
        this.connection = connection;
    }

    public DBitem add(DBitem item) {
        if (!(item instanceof Incentive incentive)) {
            throw new IllegalArgumentException("Expected an Incentive, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO incentives (name, description, provider_id) VALUES (?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {

            prepStmt.setString(1, incentive.getName());
            prepStmt.setString(2, incentive.getDescription());
            prepStmt.setString(3, incentive.getProvider().getName());

            int affectedRows = prepStmt.executeUpdate();

            //if (affectedRows > 0) {
            //    System.out.println("Incentive added successfully");
            //}

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    incentive.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating incentive failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding incentive: " + e.getMessage(), e);
        }

        return incentive;

    };

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM  incentives WHERE id = ?";
        Incentive resultIncentive = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try (ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {
                    resultIncentive = new Incentive(selectResults.getInt("id"),
                            selectResults.getString("name"),
                                    selectResults.getString("description"));
                } else {
                    throw new RuntimeException("Creating incentive failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching incentive: " + e.getMessage(), e);
        }

        return resultIncentive;

    };

    public boolean update(DBitem item){
        // u ovoj funkciji se samo prepisuju sve vrijednosti postojećeg objekta u bazu;
        // stvarna promjena vrijednosti odradit će se u drugim funkcijama

        if (!(item instanceof Incentive incentive)) {
            throw new IllegalArgumentException("Expected an Incentive, got: " + item.getClass());
        }

        String updateSql = "UPDATE incentives SET name = ?, description = ?, provider_id = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {
            prepStmt.setString(1, incentive.getName());
            prepStmt.setString(2, incentive.getDescription());
            prepStmt.setInt(3, incentive.getProvider().getId());
            prepStmt.setInt(4, incentive.getId());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no incentive found with id: " + incentive.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating incentive: " + e.getMessage(), e);
        }

        return true;
    }


    public boolean delete(int id) {

        String deleteSql = "DELETE FROM incentives WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {

            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no incentive found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting incentive: " + e.getMessage(), e);
        }
    }
}
