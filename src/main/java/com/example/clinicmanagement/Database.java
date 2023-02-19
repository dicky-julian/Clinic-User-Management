package com.example.clinicmanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Database {
    private String LINK = "jdbc:mysql://localhost:3306/clinic_management";
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "";
    private Connection connection;

    Database() {
        try {
            connection = DriverManager.getConnection(LINK, USER_NAME, PASSWORD);
        } catch (Exception exception) {
            System.exit(-1);
        }
    }

    public ObservableList<User> getAll() throws SQLException {
        final String query = "SELECT * FROM user";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        ObservableList<User> users = FXCollections.observableArrayList();
        while (result.next()) {
            User user = new User(
                    result.getString("nama"),
                    result.getString("nik"),
                    result.getDate("tanggal_lahir").toLocalDate(),
                    result.getString("alamat")
            );
            users.add(user);
        }
        return users;
    }

    public User insert(User user) throws SQLException {
        final String query = "INSERT INTO user (nama, alamat, tanggal_lahir, nik) value (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getName());
        statement.setString(2, user.getAddress());
        statement.setDate(3, Date.valueOf(user.getBirthDate()));
        statement.setString(4, user.getNik());
        statement.executeUpdate();

        return user;
    }

    public User update(User user) throws SQLException {
        final String query = "UPDATE user SET nama = ?, alamat = ?, tanggal_lahir = ? WHERE nik = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getName());
        statement.setString(2, user.getAddress());
        statement.setDate(3, Date.valueOf(user.getBirthDate()));
        statement.setString(4, user.getNik());
        statement.executeUpdate();

        return user;
    }

    public User delete(User user) throws SQLException {
        final String query = "DELETE FROM user WHERE nik = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getNik());
        statement.executeUpdate();

        return user;
    }
}
