package com.example.diploma.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

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
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS imported_data (id SERIAL PRIMARY KEY, name VARCHAR(255), value VARCHAR(255))");
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
                statement.executeUpdate("CREATE TABLE imported_data (id SERIAL PRIMARY KEY, name VARCHAR(255), value VARCHAR(255))");
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
}

