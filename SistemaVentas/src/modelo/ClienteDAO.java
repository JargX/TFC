package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joelr
 *
 * Clase que maneja las operaciones CRUD para los Clientes en la base de datos.
 */
public class ClienteDAO {

    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL

    /**
     * Método para registrar un nuevo cliente en la base de datos.
     *
     * @param cl Objeto Cliente que contiene los datos del cliente a registrar.
     * @return true si el cliente fue registrado exitosamente, false en caso
     * contrario.
     */
    public boolean RegistarCliente(Cliente cl) {

        String sql = "INSERT INTO clientes (dni, nombre, telefono, direccion, correo, razon) VALUES (?,?,?,?,?,?)";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            // Establece los parametros de la sentencia SQL
            ps.setString(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setString(5, cl.getCorreo());
            ps.setString(6, cl.getRazon());
            // Ejecuta la sentencia SQL
            ps.execute();
            return true;//Devuelve verdadero si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;//Devuelve falso si la ejecución fue errónea
        } finally {
            try {
                con.close();//Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Método para obtener la lista de todos los clientes de la base de datos.
     *
     * @return Lista de objetos Cliente.
     */
    public List ListaClientes() {
        List<Cliente> ListaCl = new ArrayList();
        String sql = "SELECT * FROM clientes";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            rs = ps.executeQuery();// Ejecuta la consulta SQL
            while (rs.next()) {
                Cliente cl = new Cliente();// Nuevo objeto Cliente
                // Establece los atributos del objeto Cliente con los datos obtenidos de la base de datos
                cl.setId(rs.getInt("id"));
                cl.setDni(rs.getString("DNI"));
                cl.setNombre(rs.getString("nombre"));
                cl.setTelefono(rs.getInt("telefono"));
                cl.setDireccion(rs.getString("direccion"));
                cl.setCorreo(rs.getString("correo"));
                cl.setRazon(rs.getString("razon"));
                ListaCl.add(cl);// Añade el cliente a la lista
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return ListaCl;// Devuelve la lista de clientes
    }

    /**
     * Método para eliminar un cliente de la base de datos.
     *
     * @param id Identificador del cliente a eliminar.
     * @return true si el cliente fue eliminado exitosamente, false en caso
     * contrario.
     */
    public boolean EliminarCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setInt(1, id); // Establece el parámetro de la sentencia SQL
            ps.execute(); // Ejecuta la consulta SQL
            return true; // Devuelve verdadero si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false; // Devuelve falso si la ejecución fue errónea
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
     * Método para modificar los datos de un cliente en la base de datos.
     *
     * @param cl Objeto Cliente que contiene los nuevos datos del cliente.
     * @return true si los datos del cliente fueron modificados exitosamente,
     * false en caso contrario.
     */
    public boolean ModificarCliente(Cliente cl) {
        
        String sql = "UPDATE clientes SET dni=?, nombre=?, telefono=?, direccion=?, correo=?, razon=? WHERE id=?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establecer los parámetros de la sentencia SQL
            ps.setString(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setString(5, cl.getCorreo());
            ps.setString(6, cl.getRazon());
            ps.setInt(7, cl.getId());
            ps.execute(); // Ejecuta la consulta SQL
            return true; // Devuelve verdadero si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false; // Devuelve falso si la ejecución fue errónea
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
     * Método para buscar un cliente en la base de datos por su DNI.
     *
     * @param dni DNI del cliente a buscar.
     * @return Objeto Cliente que contiene los datos del cliente encontrado.
     */
    public Cliente BuscarCliente(String dni) {
        Cliente cl = new Cliente();
        String sql = "SELECT * FROM clientes WHERE dni = ?";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            ps.setString(1, dni);// Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery();// Ejecuta la consulta SQL y obtiene los resultados
            if (rs.next()) {
                // Establece los atributos del objeto Cliente con los datos obtenidos de la base de datos
                cl.setId(rs.getInt("id"));
                cl.setNombre(rs.getString("nombre"));
                cl.setTelefono(rs.getInt("telefono"));
                cl.setDireccion(rs.getString("direccion"));
                cl.setRazon(rs.getString("razon"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return cl;// Devuelve el objeto Cliente
    }
}
