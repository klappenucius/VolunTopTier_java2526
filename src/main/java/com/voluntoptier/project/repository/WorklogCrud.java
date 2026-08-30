package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.*;

import java.sql.*;
import java.time.LocalDate;

public final class WorklogCrud implements Crud{

    private final Connection connection;
    private final ProjectAssignmentCrud projectAssignmentCrud;

    public WorklogCrud(Connection connection, ProjectAssignmentCrud projectAssignmentCrud) {
        this.connection = connection;
        this.projectAssignmentCrud = projectAssignmentCrud;
    }

    public DBitem add(DBitem item) {
        if(!(item instanceof Worklog worklog)) {
            throw new IllegalArgumentException("Expected a Worklog, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO worklogs (projectAssignment_id, date, hours, description, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql,Statement.RETURN_GENERATED_KEYS)){

            prepStmt.setInt(1, worklog.getProjectAssignment().getId());
            prepStmt.setDate(2, Date.valueOf(worklog.getDate()));
            prepStmt.setInt(3, worklog.getHours());
            prepStmt.setString(4, worklog.getDescription());
            prepStmt.setString(5, worklog.getStatus().name());

            int affectedRows = prepStmt.executeUpdate();

            try(ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if(generatedKeys.next()){
                    worklog.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating project assignment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding worklog: " + e.getMessage(), e);
        }

        return worklog;
    }

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM worklogs WHERE id = ?";
        Worklog resultWorklog = null;

        try(PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {

            prepStmt.setInt(1, id);

            try (ResultSet selectResults = prepStmt.executeQuery()) {

                if (selectResults.next()) {

                    WorklogStatus worklogStatus = WorklogStatus.valueOf(selectResults.getString("status"));

                    resultWorklog = new Worklog(selectResults.getInt("id"),
                            (ProjectAssignment) projectAssignmentCrud.getById(selectResults.getInt("projectAssignmen")),
                            selectResults.getDate("date").toLocalDate(),
                            selectResults.getInt("hours"),
                            selectResults.getString("description"),
                            worklogStatus
                    );
                } else {
                    throw new RuntimeException("Creating worklog failed, no ID obtained.");
                }
            }
        return resultWorklog;
    } catch (SQLException e) {
            throw new RuntimeException("Error fetching worklog: " + e.getMessage(), e);
        }
    }

    public boolean update (DBitem item) {

        if (!(item instanceof Worklog worklog)) {
            throw new IllegalArgumentException("Expected a Worklog, got: " + item.getClass());
        }
// pa, date, hours, description, status
        String updateSql = "UPDATE worklogs SET projectAssignmentId = ?, date = ?, hours = ?, description =?, status = ?";

        try(PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {

            prepStmt.setInt(1, worklog.getId());
            prepStmt.setDate(2, Date.valueOf(worklog.getDate()));
            prepStmt.setInt(3, worklog.getHours());
            prepStmt.setString(4, worklog.getDescription());
            prepStmt.setString(5, worklog.getStatus().name());

            int affectedRows = prepStmt.executeUpdate();

            if(affectedRows == 0) {
                throw new RuntimeException("Update failed, no worklog found with id: " + worklog.getId());
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String deleteSql = "DELETE FROM worklogs WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {
            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no worklog found with id: " + id);
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting worklog: " + e.getMessage(), e);
        }
    }
}







