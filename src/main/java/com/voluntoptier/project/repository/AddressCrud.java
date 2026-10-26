package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.DBitem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Data access for the {@code addresses} table.
 */
@Repository
public non-sealed class AddressCrud implements Crud {

    private static final String INSERT_SQL =
            "INSERT INTO addresses (street, houseNumber, postalCode, city, country) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM addresses WHERE id = ?";
    private static final String SELECT_SAME_SQL =
            "SELECT * FROM addresses WHERE street = ? AND houseNumber = ? AND postalCode = ? AND city = ? AND country = ?";
    private static final String UPDATE_SQL =
            "UPDATE addresses SET street = ?, houseNumber = ?, postalCode = ?, city = ?, country = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM addresses WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate Spring's JDBC helper, injected automatically
     */
    public AddressCrud(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DBitem add(DBitem item) {
        if (!(item instanceof Address address)) {
            throw new IllegalArgumentException("Expected an Address, got: " + item.getClass());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepStmt = connection.prepareStatement(INSERT_SQL, new String[]{"ID"});
            prepStmt.setString(1, address.getStreet());
            prepStmt.setString(2, address.getHouseNumber());
            prepStmt.setString(3, address.getPostalCode());
            prepStmt.setString(4, address.getCity());
            prepStmt.setString(5, address.getCountry());
            return prepStmt;
        }, keyHolder);

        address.setId(Objects.requireNonNull(keyHolder.getKey(), "No id returned for new address").intValue());
        return address;
    }

    /**
     * @return the address, or {@code null} if there is none with this id
     */
    @Override
    public DBitem getById(int id) {
        List<Address> results = jdbcTemplate.query(SELECT_BY_ID_SQL, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean update(DBitem item) {
        if (!(item instanceof Address address)) {
            throw new IllegalArgumentException("Expected an Address, got: " + item.getClass());
        }

        int affectedRows = jdbcTemplate.update(UPDATE_SQL,
                address.getStreet(), address.getHouseNumber(), address.getPostalCode(),
                address.getCity(), address.getCountry(), address.getId());

        if (affectedRows == 0) {
            throw new NoSuchElementException("Update failed, no address found with id: " + address.getId());
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    /**
     * Looks for a saved address with exactly the same fields.
     *
     * @param incomingAddress the address to look for
     * @return the saved address (with its id and the existing flag set), or the incoming address
     *         unchanged if no such address is saved yet
     */
    public Address isExisting(Address incomingAddress) {
        List<Address> results = jdbcTemplate.query(SELECT_SAME_SQL, this::mapRow,
                incomingAddress.getStreet(), incomingAddress.getHouseNumber(), incomingAddress.getPostalCode(),
                incomingAddress.getCity(), incomingAddress.getCountry());

        if (results.isEmpty()) {
            return incomingAddress;
        }
        Address existing = results.get(0);
        existing.setExistingFlag(true);
        return existing;
    }

    private Address mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Address(rs.getInt("id"),
                rs.getString("street"),
                rs.getString("houseNumber"),
                rs.getString("postalCode"),
                rs.getString("city"),
                rs.getString("country"));
    }
}
