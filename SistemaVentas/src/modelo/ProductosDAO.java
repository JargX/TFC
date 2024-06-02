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
 * Clase para manejar las operaciones CRUD relacionadas con productos en la base
 * de datos.
 */
public class ProductosDAO {

    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL

    /**
     * Método para registrar un nuevo producto en la base de datos.
     *
     * @param pro Objeto Productos que contiene los datos del producto a
     * registrar.
     * @return true si el producto fue registrado exitosamente, false en caso
     * contrario.
     */
    public boolean RegistarProducto(Productos pro) {

        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio) VALUES (?,?,?,?,?)";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.execute(); // Ejecuta la sentencia SQL
            return true;// Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Método para consultar y llenar un JComboBox con los nombres de los
     * proveedores.
     *
     * @param proveedor JComboBox donde se añadirán los nombres de los
     * proveedores.
     */
    public void ConsultarProveedor(JComboBox proveedor) {

        String sql = "SELECT nombre from proveedor";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados

            while (rs.next()) {
                proveedor.addItem(rs.getString("nombre"));// Agrega cada proveedor al JComboBox
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

    /**
     * Método para obtener el ID de un proveedor basado en su nombre.
     *
     * @param nombreProveedor Nombre del proveedor.
     * @return ID del proveedor.
     */
    public int obtenerIdProveedor(String nombreProveedor) {

        String sql = "SELECT id FROM proveedor WHERE nombre = ?";
        int idProveedor = 0;
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setString(1, nombreProveedor); // Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                idProveedor = rs.getInt("id");// Obtiene el ID del proveedor
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return idProveedor;// Devuelve el ID del proveedor
    }

    /**
     * Método para obtener el nombre de un proveedor basado en su ID.
     *
     * @param proveedorId ID del proveedor.
     * @return Nombre del proveedor.
     */
    public String obtenerNombreProveedor(String proveedorId) {

        String sql = "SELECT nombre FROM proveedor WHERE id = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setString(1, proveedorId); // Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                return rs.getString("nombre");// Retorna el nombre del proveedor
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return null;// Devuelve null si no se encuentra el proveedor
    }

    /**
     * Método para obtener la lista de todos los productos de la base de datos.
     *
     * @return Lista de objetos Productos.
     */
    public List ListaProductos() {

        List<Productos> ListaPro = new ArrayList();// Lista para almacenar los productos
        String sql = "SELECT * FROM productos";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            while (rs.next()) {
                Productos pro = new Productos();// Crea un nuevo objet Productos
                // Establece los atributos del objeto Productos con los datos obtenidos de la base de datos
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getInt("proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setPrecio(rs.getDouble("precio"));
                ListaPro.add(pro);// Añade el objeto Productos a la lista
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
        return ListaPro;// Devuelve la lista de productos
    }

    /**
     * Método para eliminar un producto de la base de datos.
     *
     * @param id Identificador del producto a eliminar.
     * @return true si el producto fue eliminado exitosamente, false en caso
     * contrario.
     */
    public boolean EliminarProducto(int id) {

        String sql = "DELETE FROM productos WHERE id = ?";
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
                    ps.close();// Cierra el PreparedStatement
                }
                if (con != null) {
                    con.close(); // Cierra la conexión
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /**
     * Método para modificar los datos de un producto en la base de datos.
     *
     * @param pro Objeto Productos que contiene los nuevos datos del producto.
     * @return true si los datos del producto fueron modificados exitosamente,
     * false en caso contrario.
     */
    public boolean ModificarProductos(Productos pro) {

        String sql = "UPDATE productos SET codigo=?, nombre=?, stock=?, precio=? WHERE id=?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getStock());
            ps.setDouble(4, pro.getPrecio());
            ps.setInt(5, pro.getId());
            ps.executeUpdate();// Ejecuta la sentencia SQL
            return true;// Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /**
     * Método para obtener el nombre del proveedor actual de un producto.
     *
     * @param idProducto Identificador del producto.
     * @return Nombre del proveedor actual.
     */
    public String obtenerProveedorActual(int idProducto) {

        String proveedorActual = "";
        String sql = "SELECT proveedor.nombre FROM productos "
                + "INNER JOIN proveedor ON productos.proveedor = proveedor.id "
                + "WHERE productos.id = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setInt(1, idProducto); // Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                proveedorActual = rs.getString("nombre");// Obtiene el nombre del proveedor
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (rs != null) {
                    rs.close(); // Cierra el ResultSet
                }
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
        return proveedorActual;// Devuelve el nombre del proveedor actual.
    }

    /**
     * Método para buscar un producto en la base de datos por su código.
     *
     * @param codigo Código del producto a buscar.
     * @return Objeto Productos con los datos del producto encontrado.
     */
    public Productos BuscarProductos(String codigo) {

        Productos producto = new Productos();// Crea un nuevo objeto Productos
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setString(1, codigo); // Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                // Establece los atributos del objeto Productos con los datos obtenidos de la base de datos
                producto.setNombre(rs.getString("nombre"));
                producto.setStock(rs.getInt("stock"));
                producto.setPrecio(rs.getDouble("precio"));
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
        return producto;// Devuelve el objeto Productos con los datos del producto encontrado
    }

    /**
     * Método para buscar los datos de configuración en la base de datos.
     *
     * @return Objeto Config con los datos de configuración encontrados.
     */
    public Config BuscarDatos() {

        Config config = new Config();// Crea un nuevo objeto Config
        String sql = "SELECT * FROM config";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                // Establece los atributos del objeto Config con los datos obtenidos de la base de datos
                config.setId(rs.getInt("id"));
                config.setNif(rs.getString("nif"));
                config.setNombre(rs.getString("nombre"));
                config.setTelefono(rs.getInt("telefono"));
                config.setDireccion(rs.getString("direccion"));
                config.setMensaje(rs.getString("mensaje"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();// Cierra la conexión a la base de datos.
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return config;// Devuelve el objeto Config con los datos de configuración encontrados
    }

    /**
     * Método para modificar los datos de configuración en la base de datos.
     *
     * @param config Objeto Config que contiene los nuevos datos de
     * configuración.
     * @return true si los datos de configuración fueron modificados
     * exitosamente, false en caso contrario.
     */
    public boolean ModificarDatos(Config config) {

        String sql = "UPDATE config SET nif=?, nombre=?, telefono=?, direccion=?, mensaje=? WHERE id=?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, config.getNif());
            ps.setString(2, config.getNombre());
            ps.setInt(3, config.getTelefono());
            ps.setString(4, config.getDireccion());
            ps.setString(5, config.getMensaje());
            ps.setInt(6, config.getId());
            ps.executeUpdate();// Ejecuta la sentencia SQL
            return true; // Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                if (ps != null) {
                    ps.close();// Cierra el PreparedStatement
                }
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }
}
