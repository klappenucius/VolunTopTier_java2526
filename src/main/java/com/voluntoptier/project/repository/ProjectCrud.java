package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Data access for the {@code projects} table.
 */
@Repository
public non-sealed class ProjectCrud implements Crud {

    private static final String INSERT_SQL =
            "INSERT INTO projects (name, totalHours, startDate, endDate, volunteersNeeded, address_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM projects WHERE id = ?";
    private static final String UPDATE_SQL =
            "UPDATE projects SET name = ?, totalHours = ?, startDate = ?, endDate = ?, volunteersNeeded = ?, "
                    + "address_id = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM projects WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final AddressCrud addressCrud;

    /**
     * @param jdbcTemplate Spring's JDBC helper, injected automatically
     * @param addressCrud  used to load each project's address
     */
    public ProjectCrud(JdbcTemplate jdbcTemplate, AddressCrud addressCrud) {
        this.jdbcTemplate = jdbcTemplate;
        this.addressCrud = addressCrud;
    }

    @Override
    public DBitem add(DBitem item) {
        if (!(item instanceof Project project)) {
            throw new IllegalArgumentException("Expected a Project, got: " + item.getClass());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepStmt = connection.prepareStatement(INSERT_SQL, new String[]{"ID"});
            prepStmt.setString(1, project.getName());
            prepStmt.setInt(2, project.getTotalHours());
            prepStmt.setObject(3, project.getStartDate());
            prepStmt.setObject(4, project.getEndDate());
            prepStmt.setInt(5, project.getVolunteersNeeded());
            prepStmt.setObject(6, addressIdOf(project));
            return prepStmt;
        }, keyHolder);

        project.setId(Objects.requireNonNull(keyHolder.getKey(), "No id returned for new project").intValue());
        return project;
    }

    /**
     * @return the project, or {@code null} if there is none with this id
     */
    @Override
    public DBitem getById(int id) {
        List<Project> results = jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean update(DBitem item) {
        if (!(item instanceof Project project)) {
            throw new IllegalArgumentException("Expected a Project, got: " + item.getClass());
        }

        int affectedRows = jdbcTemplate.update(UPDATE_SQL,
                project.getName(), project.getTotalHours(), project.getStartDate(), project.getEndDate(),
                project.getVolunteersNeeded(), addressIdOf(project), project.getId());

        if (affectedRows == 0) {
            throw new NoSuchElementException("Update failed, no project found with id: " + project.getId());
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    private static Integer addressIdOf(Project project) {
        return project.getAddress() == null ? null : project.getAddress().getId();
    }

    private Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer addressId = rs.getObject("address_id", Integer.class);
        Address address = addressId == null ? null : (Address) addressCrud.getById(addressId);

        return new Project.ProjectBuilder(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("totalHours"),
                rs.getObject("startDate", LocalDate.class),
                rs.getObject("endDate", LocalDate.class),
                rs.getInt("volunteersNeeded"))
                .address(address)
                .build();
    }
}
