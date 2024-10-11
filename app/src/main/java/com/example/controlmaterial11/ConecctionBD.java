package com.example.controlmaterial11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConecctionBD {

    // Cambia estos valores con los detalles de tu servidor SQL Server
    private static final String URL = "jdbc:sqlserver://10.10.1.111;databaseName=JMAS_REPORTES_MATERIAL";
    private static final String USER = "NATA";
    private static final String PASSWORD = "123123";

    // Método para obtener la conexión
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Cargar el driver de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Conectarse a la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error: Driver JDBC no encontrado");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: No se pudo conectar a la base de datos");
        }
        return connection;
    }

    // Método para cerrar la conexión
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: No se pudo cerrar la conexión");
            }
        }
    }
}
