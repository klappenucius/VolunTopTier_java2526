package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Incentive;
import com.voluntoptier.project.entities.Project;

import java.time.LocalDate;
import java.sql.Date;
import java.sql.*;

public final class ProjectCrud implements Crud{

    private final Connection connection;

    public ProjectCrud(Connection connection) {
        this.connection = connection;
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
                    resultProject = new Project (selectResults.getInt("id"),
                            selectResults.getString("name"),
                            )
                } else {
                    throw new RuntimeException("Creating incentive failed, no ID obtained.");
                }
            }
        }
    }
}
