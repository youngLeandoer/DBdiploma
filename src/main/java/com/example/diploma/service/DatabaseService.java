package com.example.diploma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getDatabaseList() {
        List<String> databaseList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "1488");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString("TABLE_CAT");
                databaseList.add(databaseName);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databaseList;
    }

    public boolean connectToDatabase(String database) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "postgres", "1488");
            System.out.println("Connected to database: " + database);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS imported_data (id SERIAL PRIMARY KEY, name VARCHAR(255), value TEXT)");
                System.out.println("Table 'imported_data' created successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getImportedData(String database) {
        List<Map<String, Object>> importedData = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "postgres", "1488");
            Statement statement = connection.createStatement();
            // Проверяем существование таблицы
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "imported_data", null);
            if (!tables.next()) {
                // Если таблицы не существует, создаем ее
                statement.executeUpdate("CREATE TABLE imported_data (id SERIAL PRIMARY KEY, name VARCHAR(255), value TEXT)");
                System.out.println("Table 'imported_data' created successfully");
            }
            tables.close();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM imported_data");

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
                importedData.add(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return importedData;
    }
    public void importDataToDatabase(String database, List<Map<String, String>> data) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "postgres", "1488");
            String tableName = "imported_data";

            // Проверяем, что у нас есть данные для вставки
            if (data.isEmpty()) {
                System.err.println("No data to insert.");
                return;
            }

            // Подготавливаем запрос
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + tableName + " (id, name, value) VALUES (?, ?, ?)");

            // Генерируем ID для импортируемой таблицы
            int id = generateNextId(connection, tableName);

            // Сериализуем данные из каждой таблицы и вставляем их в поле value
            ObjectMapper objectMapper = new ObjectMapper();
            String serializedData = objectMapper.writeValueAsString(data);

            // Вставляем данные
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, "table" + id); // Имя таблицы будет tableN, где N - ID
            preparedStatement.setString(3, serializedData);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private int generateNextId(Connection connection, String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM " + tableName);

        int maxId = 0;
        if (resultSet.next()) {
            maxId = resultSet.getInt(1);
        }

        resultSet.close();
        statement.close();

        return maxId + 1;
    }
}


