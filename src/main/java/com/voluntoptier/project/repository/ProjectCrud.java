package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Project;

import java.sql.*;

public final class ProjectCrud implements Crud{

    private final Connection connection;
    private final AddressCrud addressCrud;

    public ProjectCrud(Connection connection, AddressCrud addressCrud) {
        this.connection = connection;
        this.addressCrud = addressCrud;
    }

    public DBitem add (DBitem item) {
        if (!(item instanceof Project project)) {
            throw new IllegalArgumentException("Expected a Project, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO projects (name, totalHours, startDate, endDate, volunteersNeeded, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql)) {
            prepStmt.setString(1, project.getName());
            prepStmt.setInt(2, project.getTotalHours());
            prepStmt.setDate(3, Date.valueOf(project.getStartDate()));
            prepStmt.setDate(4, Date.valueOf(project.getEndDate()));
            prepStmt.setInt(5, project.getVolunteersNeeded());
            prepStmt.setInt(6, project.getAddress().getId());

            int affectedRows = prepStmt.executeUpdate();

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating project failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding project: " + e.getMessage(), e);
        }

        return project;
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
                    throw new RuntimeException("Creating incentive failed, no ID obtained.");
                    }
                }
            }  catch (SQLException e) {
            throw new RuntimeException("Error adding project: " + e.getMessage(), e);
        }
        return resultProject;
    }

    public boolean update (DBitem item) {

        if(!(item instanceof Project project)) {
            throw new IllegalArgumentException("Expected a Project, got: " + item.getClass());
        }

        String updateSql = "UPDATE projects SET name = ?, totalHours = ?, hoursWorked = ?, startDate = ?, endDate = ?, volunteersNeeded = ?, address_id = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {

            prepStmt.setString(1, project.getName());
            prepStmt.setInt(2, project.getTotalHours());
            prepStmt.setInt(3, project.getHoursWorked());
            prepStmt.setDate(4, Date.valueOf(project.getStartDate()));
            prepStmt.setDate(5, Date.valueOf(project.getEndDate()));
            prepStmt.setInt(6, project.getVolunteersNeeded());
            prepStmt.setInt(7, project.getAddress().getId());
            prepStmt.setInt(8, project.getId());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no project found with id: " + project.getId());
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project: " + e.getMessage(), e);
        }
    }

    public boolean delete (int id) {

        String deleteSql = "DELETE FROM projects WHERE id = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {
            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if(affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no address found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting address: " + e.getMessage(), e);
        }
    }
}
