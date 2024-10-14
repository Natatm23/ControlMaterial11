package com.example.controlmaterial11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConecctionBD {

    private static final String IP = "10.10.1.111";
    private static final String USER = "NATA";
    private static final String PASSWORD = "123123";
    private static final String DATABASE = "JMAS_REPORTES_MATERIAL";
    // Intenta agregar sslProtocol para controlar SSL
    private static final String URL = "jdbc:sqlserver://" + IP + ";databaseName=" + DATABASE + ";encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2";
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String TAG = "ConnectionBD";

    // Método para obtener la conexión
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Cargar el controlador JDBC
            Class.forName(DRIVER_CLASS);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver JDBC no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: No se pudo conectar a la base de datos. Detalles:");
            System.out.println("Estado SQL: " + e.getSQLState());
            System.out.println("Código de error: " + e.getErrorCode());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
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
