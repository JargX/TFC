package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author joelr
 *
 * Clase que maneja las operaciones CRUD para los Proveedores en la base de
 * datos.
 */
public class ProveedorDAO {

    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL

    /**
     * Método para registrar un nuevo proveedor en la base de datos.
     *
     * @param pr Objeto Proveedor que contiene los datos del proveedor a
     * registrar.
     * @return true si el proveedor fue registrado exitosamente, false en caso
     * contrario.
     */
    public boolean RegistarProveedor(Proveedor pr) {

        String sql = "INSERT INTO proveedor (nif, nombre, telefono, direccion, correo, razon) VALUES (?,?,?,?,?,?)";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            // Establece los parametros de la sentencia SQL
            ps.setString(1, pr.getNif());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setString(5, pr.getCorreo());
            ps.setString(6, pr.getRazon());
            // Ejecuta la sentencia SQL
            ps.execute();
            return true;//Devuelve verdadero si la ejecución fue exitosa
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;// Devuelve false en caso de error.
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos.
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Método para obtener la lista de todos los proveedores de la base de
     * datos.
     *
     * @return Lista de objetos Proveedor.
     */
    public List ListaProveedores() {
        List<Proveedor> ListaPr = new ArrayList();// Lista para almacenar los proveedores
        String sql = "SELECT * FROM proveedor";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            while (rs.next()) {
                Proveedor pr = new Proveedor();// Crea un nuevo objeto Proveedor
                // Establece los atributos del objeto Proveedor con los datos obtenidos de la base de datos
                pr.setId(rs.getInt("id"));
                pr.setNif(rs.getString("nif"));
                pr.setNombre(rs.getString("nombre"));
                pr.setTelefono(rs.getInt("telefono"));
                pr.setDireccion(rs.getString("direccion"));
                pr.setCorreo(rs.getString("correo"));
                pr.setRazon(rs.getString("razon"));
                ListaPr.add(pr);// Añade el objeto Proveedor a la lista
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return ListaPr;// Devuelve la lista de proveedores
    }

    /**
     * Método para eliminar un proveedor de la base de datos.
     *
     * @param id Identificador del proveedor a eliminar.
     * @return true si el proveedor fue eliminado exitosamente, false en caso
     * contrario.
     */
    public boolean EliminarProveedor(int id) {

        String sql = "DELETE FROM proveedor WHERE id = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setInt(1, id); // Establece el parámetro de la sentencia SQL
            ps.execute(); // Ejecuta la sentencia SQL
            return true; // Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                if (ps != null) {
                    ps.close(); // Cierra el PreparedStatement
                }
                if (con != null) {
                    con.close(); // Cierra la conexión a la base de datos
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /**
     * Método para modificar los datos de un proveedor en la base de datos.
     *
     * @param pr Objeto Proveedor que contiene los nuevos datos del proveedor.
     * @return true si los datos del proveedor fueron modificados exitosamente,
     * false en caso contrario.
     */
    public boolean ModificarProveedor(Proveedor pr) {
        String sql = "UPDATE proveedor SET nif=?, nombre=?, telefono=?, direccion=?, correo=?, razon=? WHERE id=?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, pr.getNif());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setString(5, pr.getCorreo());
            ps.setString(6, pr.getRazon());
            ps.setInt(7, pr.getId());
            ps.execute();// Ejecuta la sentencia SQL
            return true;// Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                if (ps != null) {
                    ps.close(); // Cierra el PreparedStatement
                }
                if (con != null) {
                    con.close(); // Cierra la conexión a la base de datos
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /**
     * Método para actualizar la lista de proveedores en un JComboBox.
     *
     * @param proveedorComboBox JComboBox que contiene la lista de proveedores.
     */
    public void actualizarProveedores(JComboBox<String> proveedorComboBox) {
        proveedorComboBox.removeAllItems(); // Limpiar el ComboBox
        String sql = "SELECT nombre FROM proveedor";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            while (rs.next()) {
                proveedorComboBox.addItem(rs.getString("nombre")); // Agregar cada proveedor al ComboBox
            }
        } catch (SQLException e) {
            System.err.println(e.toString());
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
}
