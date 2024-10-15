package com.example.controlmaterial11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConecctionBD {
    private static final String IP = "10.10.1.111"; // IP de la base de datos
    private static final String USER = "NATA"; // Usuario de la base de datos
    private static final String PASSWORD = "123123"; // Contraseña de la base de datos
    private static final String DATABASE = "JMAS_REPORTES_MATERIAL"; // Nombre de la base de datos
    private static final String DRIVER_CLASS = "net.sourceforge.jtds.jdbc.Driver"; // Controlador JDBC
    private static final String TAG = "ConnectionBD"; // Etiqueta para logs

    // Método para obtener la conexión
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Cargar el controlador
            Class.forName(DRIVER_CLASS);
            // Construir la cadena de conexión
            String connectionString = "jdbc:jtds:sqlserver://" + IP + ";databaseName=" + DATABASE + ";user=" + USER + ";password=" + PASSWORD + ";";
            // Establecer la conexión
            connection = DriverManager.getConnection(connectionString);
            System.out.println("Conexión establecida con éxito");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Controlador no encontrado. Asegúrate de que jTDS esté incluido en tus dependencias.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: No se pudo establecer la conexión con la base de datos");
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
                System.err.println("Error: No se pudo cerrar la conexión");
                e.printStackTrace();
            }
        }
    }
}
