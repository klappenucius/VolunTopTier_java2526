package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.entities.Worklog;
import com.voluntoptier.project.entities.WorklogStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Data access for the {@code worklogs} table.
 */
@Repository
public non-sealed class WorklogCrud implements Crud {

    private static final String INSERT_SQL =
            "INSERT INTO worklogs (projectAssignment_id, workDate, hours, description, status, submittedAt, "
                    + "approvedBy, resolvedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM worklogs WHERE id = ?";
    private static final String UPDATE_SQL =
            "UPDATE worklogs SET projectAssignment_id = ?, workDate = ?, hours = ?, description = ?, status = ?, "
                    + "submittedAt = ?, approvedBy = ?, resolvedAt = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM worklogs WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ProjectAssignmentCrud projectAssignmentCrud;

    /**
     * @param jdbcTemplate          Spring's JDBC helper, injected automatically
     * @param projectAssignmentCrud used to load the assignment each work log belongs to
     */
    public WorklogCrud(JdbcTemplate jdbcTemplate, ProjectAssignmentCrud projectAssignmentCrud) {
        this.jdbcTemplate = jdbcTemplate;
        this.projectAssignmentCrud = projectAssignmentCrud;
    }

    @Override
    public DBitem add(DBitem item) {
        if (!(item instanceof Worklog worklog)) {
            throw new IllegalArgumentException("Expected a Worklog, got: " + item.getClass());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepStmt = connection.prepareStatement(INSERT_SQL, new String[]{"ID"});
            prepStmt.setInt(1, worklog.getProjectAssignment().getId());
            prepStmt.setObject(2, worklog.getDate());
            prepStmt.setInt(3, worklog.getHours());
            prepStmt.setString(4, worklog.getDescription());
            prepStmt.setString(5, worklog.getStatus().name());
            prepStmt.setObject(6, worklog.getSubmittedAt());
            prepStmt.setString(7, worklog.getApprovedBy());
            prepStmt.setObject(8, worklog.getResolvedAt());
            return prepStmt;
        }, keyHolder);

        worklog.setId(Objects.requireNonNull(keyHolder.getKey(), "No id returned for new work log").intValue());
        return worklog;
    }

    /**
     * @return the work log, or {@code null} if there is none with this id
     */
    @Override
    public DBitem getById(int id) {
        List<Worklog> results = jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean update(DBitem item) {
        if (!(item instanceof Worklog worklog)) {
            throw new IllegalArgumentException("Expected a Worklog, got: " + item.getClass());
        }

        int affectedRows = jdbcTemplate.update(UPDATE_SQL,
                worklog.getProjectAssignment().getId(), worklog.getDate(), worklog.getHours(),
                worklog.getDescription(), worklog.getStatus().name(), worklog.getSubmittedAt(),
                worklog.getApprovedBy(), worklog.getResolvedAt(), worklog.getId());

        if (affectedRows == 0) {
            throw new NoSuchElementException("Update failed, no worklog found with id: " + worklog.getId());
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    private Worklog mapRow(ResultSet rs, int rowNum) throws SQLException {
        Worklog worklog = new Worklog(
                rs.getInt("id"),
                (ProjectAssignment) projectAssignmentCrud.getById(rs.getInt("projectAssignment_id")),
                rs.getObject("workDate", LocalDate.class),
                rs.getInt("hours"),
                rs.getString("description"),
                WorklogStatus.valueOf(rs.getString("status")));

        worklog.setSubmittedAt(rs.getObject("submittedAt", LocalDateTime.class));
        worklog.setApprovedBy(rs.getString("approvedBy"));
        worklog.setResolvedAt(rs.getObject("resolvedAt", LocalDateTime.class));
        return worklog;
    }
}







