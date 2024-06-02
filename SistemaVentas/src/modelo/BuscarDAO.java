package modelo;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author joelr
 */
public class BuscarDAO {

    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL

    public DefaultTableModel buscarClientes(String buscar) {
        // Se define los nombres de las columnas de la tabla
        String[] nombresColumnas = {"ID", "DNI/NIF", "NOMBRE", "TELÉFONO", "DIRECCIÓN", "CORREO", "RAZON SOCIAL"};
        // Array para almacenar los registros de cada fila
        Object[] registros = new Object[7];
        // Inicializa el modelo de la tabla con los nombres de las columnas
        DefaultTableModel modelo = new DefaultTableModel(null, nombresColumnas);
        // Consulta SQL para buscar clientes por id, dni, nombre
        String sql = "SELECT * FROM clientes WHERE id LIKE '%" + buscar + "%' OR dni LIKE '%" + buscar + "%' OR nombre LIKE '%" + buscar + "%'OR telefono LIKE '%" + buscar + "%'";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            rs = ps.executeQuery();// Ejecuta la consulta SQL

            // Recorre los campos de la tabla
            while (rs.next()) {
                // Asigna valores a cada columna del array de registros
                registros[0] = rs.getInt("id"); // ID del cliente
                registros[1] = rs.getString("dni"); // DNI del cliente
                registros[2] = rs.getString("nombre"); // Nombre del cliente
                registros[3] = rs.getString("telefono"); // Teléfono del cliente
                registros[4] = rs.getString("direccion"); // Dirección del cliente
                registros[5] = rs.getString("correo"); // Correo del cliente
                registros[6] = rs.getString("razon"); // Razón social del cliente

                // Añade la fila al modelo de la tabla
                modelo.addRow(registros);
            }
        } catch (SQLException e) {
            // Muestra un mensaje de error si hay problemas con la conexión
            JOptionPane.showMessageDialog(null, "Error al conectar. " + e.getMessage());
        } finally {
            // Cierra los recursos utilizados en la consulta
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        // Devuelve el modelo de la tabla con los registros encontrados
        return modelo;
    }

    public DefaultTableModel buscarProveedores(String buscar) {
        // Se define los nombres de las columnas de la tabla
        String[] nombresColumnas = {"ID", "NIF", "NOMBRE", "TELÉFONO", "DIRECCIÓN", "CORREO", "RAZON SOCIAL"};
        // Array para almacenar los registros de cada fila
        Object[] registros = new Object[7];
        // Inicializa el modelo de la tabla con los nombres de las columnas
        DefaultTableModel modelo = new DefaultTableModel(null, nombresColumnas);
        // Consulta SQL para buscar proveedores por id, dni, nombre
        String sql = "SELECT * FROM proveedor WHERE id LIKE '%" + buscar + "%' OR nif LIKE '%" + buscar + "%' OR nombre LIKE '%" + buscar + "%'OR telefono LIKE '%" + buscar + "%'";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            rs = ps.executeQuery();// Ejecuta la consulta SQL

            // Recorre los campos de la tabla
            while (rs.next()) {
                // Asigna valores a cada columna del array de registros
                registros[0] = rs.getInt("id"); // ID del proveedor
                registros[1] = rs.getString("nif"); // DNI del proveedor
                registros[2] = rs.getString("nombre"); // Nombre del proveedor
                registros[3] = rs.getString("telefono"); // Teléfono del proveedor
                registros[4] = rs.getString("direccion"); // Dirección del proveedor
                registros[5] = rs.getString("correo"); // Correo del proveedor
                registros[6] = rs.getString("razon"); // Razón social del proveedor

                // Añade la fila al modelo de la tabla
                modelo.addRow(registros);
            }
        } catch (SQLException e) {
            // Muestra un mensaje de error si hay problemas con la conexión
            JOptionPane.showMessageDialog(null, "Error al conectar. " + e.getMessage());
        } finally {
            // Cierra los recursos utilizados en la consulta
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        // Devuelve el modelo de la tabla con los registros encontrados
        return modelo;
    }

    public DefaultTableModel buscarProductos(String buscar) {
        // Se define los nombres de las columnas de la tabla
        String[] nombresColumnas = {"ID", "CÓDIGO", "DESCRIPCIÓN", "CANTIDAD", "PRECIO", "PROVEEDOR"};
        // Array para almacenar los registros de cada fila
        Object[] registros = new Object[6];
        // Inicializa el modelo de la tabla con los nombres de las columnas
        DefaultTableModel modelo = new DefaultTableModel(null, nombresColumnas);
        // Consulta SQL para buscar clientes por id, dni, nombre
        String sql = "SELECT * FROM productos WHERE id LIKE '%" + buscar + "%' OR codigo LIKE '%" + buscar + "%' OR nombre LIKE '%" + buscar + "%'";
        try {
            con = cn.getConnection();// Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql);// Prepara la sentencia SQL
            rs = ps.executeQuery();// Ejecuta la consulta SQL

            // Recorre los campos de la tabla
            while (rs.next()) {
                // Asigna valores a cada columna del array de registros
                registros[0] = rs.getInt("id"); // ID del producto
                registros[1] = rs.getString("codigo"); // Codigo del producto
                registros[2] = rs.getString("nombre"); // Nombre del producto
                registros[3] = rs.getString("stock"); // Stock del producto
                registros[4] = rs.getString("precio"); // Precio del producto
                registros[5] = rs.getString("proveedor"); // Proveedor del producto

                // Añade la fila al modelo de la tabla
                modelo.addRow(registros);
            }
        } catch (SQLException e) {
            // Muestra un mensaje de error si hay problemas con la conexión
            JOptionPane.showMessageDialog(null, "Error al conectar. " + e.getMessage());
        } finally {
            // Cierra los recursos utilizados en la consulta
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        // Devuelve el modelo de la tabla con los registros encontrados
        return modelo;
    }
}
