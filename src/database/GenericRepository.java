package database;

import services.AuditService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class GenericRepository<T> {
    protected Connection connection;

    public GenericRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Abstract methods to be implemented by concrete repositories
    protected abstract String getTableName();
    protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
    protected abstract PreparedStatement createInsertStatement(T entity) throws SQLException;
    protected abstract PreparedStatement createUpdateStatement(T entity) throws SQLException;

    public void create(T entity) throws SQLException {
        try (PreparedStatement statement = createInsertStatement(entity)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error creating entity: " + e.getMessage());
        }
        AuditService.getInstance().logAction("create " + getTableName());
    }

    public List<T> readAll() throws SQLException {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException("Error reading entities: " + e.getMessage());
        }

        AuditService.getInstance().logAction("read all " + getTableName());
        return entities;
    }
//
//    // Read by ID
//    public T readById(int id) throws SQLException {
//        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
//
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setInt(1, id);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                return mapResultSetToEntity(resultSet);
//            }
//        } catch (SQLException e) {
//            throw new SQLException("Error reading entity by ID: " + e.getMessage());
//        }
//
//        return null;
//    }

    public void update(T entity) throws SQLException {
        try (PreparedStatement statement = createUpdateStatement(entity)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating entity: " + e.getMessage());
        }
        AuditService.getInstance().logAction("update " + getTableName());
    }

    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting entity: " + e.getMessage());
        }

        AuditService.getInstance().logAction("delete " + getTableName());
    }
}