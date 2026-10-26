package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.HoursWorked;
import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.entities.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Data access for the {@code projectAssignments} table (which volunteer works on which project).
 */
@Repository
public non-sealed class ProjectAssignmentCrud implements Crud {

    private static final String INSERT_SQL =
            "INSERT INTO projectAssignments (project_id, user_id, assignedBy_id, assignedDate, assignedTime, "
                    + "hoursNeeded, approvedHours, pendingApproval, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM projectAssignments WHERE id = ?";
    private static final String SELECT_BY_USER_SQL = "SELECT * FROM projectAssignments WHERE user_id = ?";
    private static final String SELECT_BY_USER_AND_PROJECT_SQL =
            "SELECT * FROM projectAssignments WHERE user_id = ? AND project_id = ?";
    private static final String SUM_HOURS_BY_USER_SQL =
            "SELECT COALESCE(SUM(approvedHours), 0) AS approved, COALESCE(SUM(pendingApproval), 0) AS pending "
                    + "FROM projectAssignments WHERE user_id = ?";
    private static final String UPDATE_SQL =
            "UPDATE projectAssignments SET project_id = ?, user_id = ?, assignedBy_id = ?, assignedDate = ?, "
                    + "assignedTime = ?, hoursNeeded = ?, approvedHours = ?, pendingApproval = ?, isActive = ? "
                    + "WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM projectAssignments WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final UserCrud userCrud;
    private final ProjectCrud projectCrud;

    /**
     * @param jdbcTemplate Spring's JDBC helper, injected automatically
     * @param userCrud     used to load the volunteer and the person who assigned them
     * @param projectCrud  used to load the project
     */
    public ProjectAssignmentCrud(JdbcTemplate jdbcTemplate, UserCrud userCrud, ProjectCrud projectCrud) {
        this.jdbcTemplate = jdbcTemplate;
        this.userCrud = userCrud;
        this.projectCrud = projectCrud;
    }

    @Override
    public DBitem add(DBitem item) {
        if (!(item instanceof ProjectAssignment pa)) {
            throw new IllegalArgumentException("Expected a ProjectAssignment, got: " + item.getClass());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepStmt = connection.prepareStatement(INSERT_SQL, new String[]{"ID"});
            prepStmt.setInt(1, pa.getProject().getId());
            prepStmt.setInt(2, pa.getUser().getId());
            prepStmt.setInt(3, pa.getAssignedby().getId());
            prepStmt.setObject(4, pa.getDate());
            prepStmt.setObject(5, pa.getTime());
            prepStmt.setInt(6, pa.getExpectedHours());
            prepStmt.setInt(7, pa.getHoursWorked().approvedHours());
            prepStmt.setInt(8, pa.getHoursWorked().pendingApproval());
            prepStmt.setBoolean(9, pa.isActive());
            return prepStmt;
        }, keyHolder);

        pa.setId(Objects.requireNonNull(keyHolder.getKey(), "No id returned for new assignment").intValue());
        return pa;
    }

    /**
     * @return the assignment, or {@code null} if there is none with this id
     */
    @Override
    public DBitem getById(int id) {
        List<ProjectAssignment> results = jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * @param userId the volunteer's id
     * @return all assignments of this volunteer, active and inactive
     */
    public List<ProjectAssignment> getByUserId(int userId) {
        return jdbcTemplate.query(SELECT_BY_USER_SQL, this::mapRow, userId);
    }

    /**
     * @return the assignment of this volunteer to this project, or {@code null} if there is none
     */
    public ProjectAssignment fetchByUserAndProject(User user, Project project) {
        List<ProjectAssignment> results =
                jdbcTemplate.query(SELECT_BY_USER_AND_PROJECT_SQL, this::mapRow, user.getId(), project.getId());
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * @param userId the volunteer's id
     * @return the volunteer's approved and pending hours summed over all their assignments
     */
    public HoursWorked getTotalHoursForUser(int userId) {
        return jdbcTemplate.queryForObject(SUM_HOURS_BY_USER_SQL,
                (rs, rowNum) -> new HoursWorked(rs.getInt("approved"), rs.getInt("pending")), userId);
    }

    @Override
    public boolean update(DBitem item) {
        if (!(item instanceof ProjectAssignment pa)) {
            throw new IllegalArgumentException("Expected a ProjectAssignment, got: " + item.getClass());
        }

        int affectedRows = jdbcTemplate.update(UPDATE_SQL,
                pa.getProject().getId(), pa.getUser().getId(), pa.getAssignedby().getId(),
                pa.getDate(), pa.getTime(), pa.getExpectedHours(),
                pa.getHoursWorked().approvedHours(), pa.getHoursWorked().pendingApproval(),
                pa.isActive(), pa.getId());

        if (affectedRows == 0) {
            throw new NoSuchElementException("Update failed, no project assignment found with id: " + pa.getId());
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    private ProjectAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
        HoursWorked hoursWorked = new HoursWorked(rs.getInt("approvedHours"), rs.getInt("pendingApproval"));

        return new ProjectAssignment(
                rs.getInt("id"),
                rs.getObject("assignedDate", LocalDate.class),
                rs.getObject("assignedTime", LocalTime.class),
                (User) userCrud.getById(rs.getInt("assignedBy_id")),
                (Project) projectCrud.getById(rs.getInt("project_id")),
                (User) userCrud.getById(rs.getInt("user_id")),
                rs.getInt("hoursNeeded"),
                hoursWorked,
                rs.getBoolean("isActive"));
    }
}