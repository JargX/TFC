package modelo;

import java.sql.*;
/**
 *
 * @author joelr

* Esta clase contiene un metodo que obtiene los datos de un usuario de la base de datos,
* para luego ser usados en otra clase.
*/
public class loginDAO {
    
    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL
    
    /**
     * Método para obtener los datos de un usuario en la base de datos mediante su correo y contraseña.
     * @param correo El correo del usuario.
     * @param contrasena La contraseña del usuario.
     * @return Un objeto login que contiene los datos del usuario.
     */
    public login log(String correo, String contrasena){
        
        login l = new login();// Crea un nuevo objeto login
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?";
        
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            rs=ps.executeQuery();// Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                // Establece los atributos del objeto login con los datos obtenidos de la base de datos
                l.setId(rs.getInt("id"));
                l.setNombre(rs.getString("nombre"));
                l.setCorreo(rs.getString("correo"));
                l.setContrasena(rs.getString("contrasena"));
                l.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return l;// Devuelve el objeto login
    }
    
    /**
     * Método para registrar un nuevo usuario en la base de datos.
     * @param reg Un objeto login que contiene los datos del usuario a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean Registrar(login reg){
        String sql = "INSERT INTO usuarios (nombre, correo, contrasena, rol) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, reg.getNombre());
            ps.setString(2, reg.getCorreo());
            ps.setString(3, reg.getContrasena());
            ps.setString(4, reg.getRol());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        }finally{
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
}
