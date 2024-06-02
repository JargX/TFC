package modelo;

import java.sql.*;

/**
 *
 * @author joelr
 * 
 * Clase para manejar la conexión a la base de datos MySQL.
 */
public class conexion {
    
    Connection con;// Objeto para manejar la conexión a la base de datos
    
    /**
     * Método para obtener la conexión a la base de datos.
     * 
     * @return la conexión a la base de datos si se establece correctamente, de lo contrario, retorna null.
     */
    public Connection getConnection(){
        try {
            // URL de la base de datos 
            String BBDD = "jdbc:mysql://u0b5swa9oa1q8iay:kenEh3FgGd7u005LBBI2@bvmznhqquwqce17tm0cd-mysql.services.clever-cloud.com:3306/bvmznhqquwqce17tm0cd";
            // Establece la conexión usando la URL, el usuario y la contraseña
            con = DriverManager.getConnection(BBDD,"u0b5swa9oa1q8iay","kenEh3FgGd7u005LBBI2");
            return con;// Devuelve la conexión si se establece correctamente
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;// Devuelve null si no se pudo establecer la conexión
    }
}
