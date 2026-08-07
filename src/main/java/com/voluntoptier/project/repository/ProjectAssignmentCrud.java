package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.*;

import java.sql.*;

public final class ProjectAssignmentCrud implements Crud{
    private final Connection connection;
    private final UserCrud userCrud;
    private final ProjectCrud projectCrud;

    public ProjectAssignmentCrud(Connection connection, UserCrud userCrud, ProjectCrud projectCrud) {
        this.connection = connection;
        this.userCrud = userCrud;
        this.projectCrud = projectCrud;
    }

    public DBitem add(DBitem item) {
        if (!(item instanceof ProjectAssignment projectAssignment)) {
            throw new IllegalArgumentException("Expected a ProjectAssignment, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO projectAssignments (assignedDate, assignedTime, assignedBy_id, project_id, user_id, hoursNeeded, approvedHours, pendingApproval) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            prepStmt.setDate(1, Date.valueOf(projectAssignment.getDate()));
            prepStmt.setTime(2, Time.valueOf(projectAssignment.getTime()));
            prepStmt.setInt(3, projectAssignment.getAssignedby().getId());
            prepStmt.setInt(4, projectAssignment.getProject().getId());
            prepStmt.setInt(5, projectAssignment.getUser().getId());
            prepStmt.setInt(6, projectAssignment.getExpectedHours());
            prepStmt.setInt(7, projectAssignment.getHoursWorked().approvedHours());
            prepStmt.setInt(8, projectAssignment.getHoursWorked().pendingApproval());

            int affectedRows = prepStmt.executeUpdate();

            //if (affectedRows > 0) {
            //    System.out.println("Incentive added successfully");
            //}

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    projectAssignment.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating project assignment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding project assignment: " + e.getMessage(), e);
        }

        return projectAssignment;

    };

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM  projectAssignments WHERE id = ?";
        ProjectAssignment resultProjectAssignment = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try (ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {

                    User assignedBy = (User) userCrud.getById(selectResults.getInt("assignedBy_id"));
                    User user = (User) userCrud.getById(selectResults.getInt("user_id"));
                    Project project = (Project) projectCrud.getById(selectResults.getInt("project_id"));

                    HoursWorked hoursWorked = new HoursWorked(
                            selectResults.getInt("approvedHours"),
                            selectResults.getInt("pendingApproval")
                    );

                    resultProjectAssignment = new ProjectAssignment(selectResults.getInt("id"),
                            selectResults.getDate("assignedDate").toLocalDate(),
                            selectResults.getTime("assignedTime").toLocalTime(),
                            assignedBy,
                            project,
                            user,
                            selectResults.getInt("hoursNeeded"),
                            hoursWorked
                    );
                } else {
                    throw new RuntimeException("Creating project assignment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project assignment: " + e.getMessage(), e);
        }

        return resultProjectAssignment;

    };

    public boolean update(DBitem item){
        // u ovoj funkciji se samo prepisuju sve vrijednosti postojećeg objekta u bazu;
        // stvarna promjena vrijednosti odradit će se u drugim funkcijama

        if (!(item instanceof ProjectAssignment projectAssignment)) {
            throw new IllegalArgumentException("Expected a ProjectAssignment, got: " + item.getClass());
        }

        String updateSql = "UPDATE projectAssignments SET date = ?, time = ?, project_id = ?, user_id = ?, assignedBy_id = ?, hoursNeeded = ?, approvedHours = ?, pendingApproval = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {
            prepStmt.setDate(1, Date.valueOf(projectAssignment.getDate()));
            prepStmt.setTime(2, Time.valueOf(projectAssignment.getTime()));
            prepStmt.setInt(3, projectAssignment.getProject().getId());
            prepStmt.setInt(4, projectAssignment.getUser().getId());
            prepStmt.setInt(5, projectAssignment.getAssignedby().getId());
            prepStmt.setInt(6, projectAssignment.getExpectedHours());

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no project assignment found with id: " + projectAssignment.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project assignment: " + e.getMessage(), e);
        }

        return true;
    }

    public boolean delete(int id) {

        String deleteSql = "DELETE FROM projectAssignments WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {

            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no project assignment found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project assignment: " + e.getMessage(), e);
        }
    }
}
