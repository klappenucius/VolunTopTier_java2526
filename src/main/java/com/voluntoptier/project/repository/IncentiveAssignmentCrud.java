package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.utils.DatabaseUtil;

import java.io.IOException;
import java.sql.*;

public final class IncentiveAssignmentCrud implements Crud {
    private Connection connection;
    private final IncentiveCrud incentiveCrud;
    private final ProjectCrud projectCrud;
    private final UserCrud userCrud;

    public IncentiveAssignmentCrud(Connection connection, IncentiveCrud incentiveCrud, ProjectCrud projectCrud, UserCrud userCrud){
        this.connection = connection;
        this.incentiveCrud = incentiveCrud;
        this.projectCrud = projectCrud;
        this.userCrud = userCrud;
    }

    public DBitem add(DBitem item) {
        if (!(item instanceof IncentiveAssignment incentiveAssignment)) {
            throw new IllegalArgumentException("Expected an IncentiveAssgnment, got: " + item.getClass());
        }

        String insertSql = "INSERT INTO incentiveAssignments(assignedDate, assignedTime, assignedBy_id, project_id, incentive_id, hoursNeeded) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prepStmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            prepStmt.setDate(1, Date.valueOf(incentiveAssignment.getAssignmentDate()));
            prepStmt.setTime(2, Time.valueOf(incentiveAssignment.getAssignmentTime()));
            prepStmt.setInt(3, incentiveAssignment.getAssignedBy().getId());
            prepStmt.setInt(4, incentiveAssignment.getProject().getId());
            prepStmt.setInt(5, incentiveAssignment.getIncentive().getId());
            prepStmt.setInt(6, incentiveAssignment.getHoursNeeded());

            int affectedRows = prepStmt.executeUpdate();

            //if (affectedRows > 0) {
            //    System.out.println("Incentive added successfully");
            //}

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    incentiveAssignment.setId(generatedKeys.getInt(1));
                } else {
                    throw new RuntimeException("Creating incentive assignment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding incentive assignment: " + e.getMessage(), e);
        }

        return incentiveAssignment;

    };

    public DBitem getById(int id) {

        String selectSql = "SELECT * FROM  projectAssignments WHERE id = ?";
        IncentiveAssignment resultIncentiveAssignment = null;

        try (PreparedStatement prepStmt = connection.prepareStatement(selectSql)) {
            prepStmt.setInt(1, id);

            try (ResultSet selectResults = prepStmt.executeQuery()) {
                if (selectResults.next()) {

                    User assignedBy = (User) userCrud.getById(selectResults.getInt("assignedBy_id"));
                    Incentive incentive = (Incentive) incentiveCrud.getById(selectResults.getInt("user_id"));
                    Project project = (Project) projectCrud.getById(selectResults.getInt("project_id"));

                    HoursWorked hoursWorked = new HoursWorked(
                            selectResults.getInt("approvedHours"),
                            selectResults.getInt("pendingApproval")
                    );

                    resultIncentiveAssignment = new IncentiveAssignment(selectResults.getInt("id"),
                            selectResults.getDate("assignedDate").toLocalDate(),
                            selectResults.getTime("assignedTime").toLocalTime(),
                            incentive,
                            project,
                            assignedBy,
                            selectResults.getInt("hoursNeeded")
                    );
                } else {
                    throw new RuntimeException("Creating incentive assignment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching incentive assignment: " + e.getMessage(), e);
        }

        return resultIncentiveAssignment;

    };

    public boolean update(DBitem item){
        // u ovoj funkciji se samo prepisuju sve vrijednosti postojećeg objekta u bazu;
        // stvarna promjena vrijednosti odradit će se u drugim funkcijama

        if (!(item instanceof IncentiveAssignment incentiveAssignment)) {
            throw new IllegalArgumentException("Expected an IncentiveAssignment, got: " + item.getClass());
        }

        String updateSql = "UPDATE incentiveAssignments SET incentive_id = ?, project_id = ?, assignedBy_id = ?, hoursNeeded = ?, assignedDate = ?, assignedTime = ? WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(updateSql)) {
            prepStmt.setInt(1, incentiveAssignment.getIncentive().getId());
            prepStmt.setInt(2, incentiveAssignment.getProject().getId());
            prepStmt.setInt(3, incentiveAssignment.getAssignedBy().getId());
            prepStmt.setInt(6, incentiveAssignment.getHoursNeeded());
            prepStmt.setDate(2, Date.valueOf(incentiveAssignment.getAssignmentDate()));
            prepStmt.setTime(2, Time.valueOf(incentiveAssignment.getAssignmentTime()));

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no project assignment found with id: " + incentiveAssignment.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project assignment: " + e.getMessage(), e);
        }

        return true;
    }

    public boolean delete(int id) {

        String deleteSql = "DELETE FROM incentiveAssignments WHERE id = ?";

        try (PreparedStatement prepStmt = connection.prepareStatement(deleteSql)) {

            prepStmt.setInt(1, id);

            int affectedRows = prepStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed, no incentive assignment found with id: " + id);
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting incentive assignment: " + e.getMessage(), e);
        }
    }
}
