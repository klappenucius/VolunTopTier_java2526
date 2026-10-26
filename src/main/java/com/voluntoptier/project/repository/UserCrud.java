package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.entities.Role;
import com.voluntoptier.project.entities.User;
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
 * Data access for the {@code users} table.
 */
@Repository
public non-sealed class UserCrud implements Crud {

    private static final String INSERT_SQL =
            "INSERT INTO users (firstName, lastName, username, oib, dateOfBirth, email, address_id, role) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String UPDATE_SQL =
            "UPDATE users SET firstName = ?, lastName = ?, username = ?, oib = ?, dateOfBirth = ?, email = ?, "
                    + "address_id = ?, role = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM users WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final AddressCrud addressCrud;

    /**
     * @param jdbcTemplate Spring's JDBC helper, injected automatically
     * @param addressCrud  used to load each user's address
     */
    public UserCrud(JdbcTemplate jdbcTemplate, AddressCrud addressCrud) {
        this.jdbcTemplate = jdbcTemplate;
        this.addressCrud = addressCrud;
    }

    @Override
    public DBitem add(DBitem item) {
        if (!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepStmt = connection.prepareStatement(INSERT_SQL, new String[]{"ID"});
            prepStmt.setString(1, user.getFirstName());
            prepStmt.setString(2, user.getLastName());
            prepStmt.setString(3, user.getUsername());
            prepStmt.setString(4, user.getOib());
            prepStmt.setObject(5, user.getDateOfBirth());
            prepStmt.setString(6, user.getEmail());
            prepStmt.setObject(7, addressIdOf(user));
            prepStmt.setString(8, user.getRole().name());
            return prepStmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey(), "No id returned for new user").intValue());
        return user;
    }

    /**
     * @return the user, or {@code null} if there is none with this id
     */
    @Override
    public DBitem getById(int id) {
        List<User> results = jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * @param username the login name to look for
     * @return the user, or {@code null} if there is none with this username
     */
    public User fetchByUsername(String username) {
        List<User> results = jdbcTemplate.query(SELECT_BY_USERNAME_SQL, this::mapRow, username);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean update(DBitem item) {
        if (!(item instanceof User user)) {
            throw new IllegalArgumentException("Expected a User, got: " + item.getClass());
        }

        int affectedRows = jdbcTemplate.update(UPDATE_SQL,
                user.getFirstName(), user.getLastName(), user.getUsername(), user.getOib(),
                user.getDateOfBirth(), user.getEmail(), addressIdOf(user), user.getRole().name(), user.getId());

        if (affectedRows == 0) {
            throw new NoSuchElementException("Update failed, no user found with id: " + user.getId());
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    private static Integer addressIdOf(User user) {
        return user.getAddress() == null ? null : user.getAddress().getId();
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer addressId = rs.getObject("address_id", Integer.class);
        Address address = addressId == null ? null : (Address) addressCrud.getById(addressId);

        return new User(
                rs.getInt("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("username"),
                rs.getString("oib"),
                rs.getObject("dateOfBirth", LocalDate.class),
                address,
                rs.getString("email"),
                Role.valueOf(rs.getString("role")));
    }
}