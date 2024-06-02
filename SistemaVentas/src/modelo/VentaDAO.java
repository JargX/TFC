package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joelr
 *
 * Clase para manejar las operaciones CRUD relacionadas con ventas en la base de
 * datos.
 */
public class VentaDAO {

    // Instancia de la clase conexión
    conexion cn = new conexion();
    Connection con;// Objeto para manejar la conexión a la base de datos
    PreparedStatement ps;// Objeto para manejar las sentencias SQL precompiladas
    ResultSet rs;// Objeto para almacenar los resultados de las consultas SQL
    int r;// Variable para almacenar resultados numéricos

    /**
     * Método para registrar una nueva venta en la base de datos.
     *
     * @param v Objeto Venta que contiene los datos de la venta a registrar.
     * @return El ID de la venta generada, o 0 en caso de error.
     */
    public int RegistrarVenta(Venta v) {
        String sql = "INSERT INTO ventas (cliente, vendedor, total) VALUES (?,?,?)";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Prepara la sentencia SQL y solicita las claves generadas
            // Establece los parámetros de la sentencia SQL
            ps.setInt(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.executeUpdate();// Ejecuta la sentencia SQL
            rs = ps.getGeneratedKeys();// Obtiene las claves generadas
            if (rs.next()) {
                return rs.getInt(1); // Retornar el ID generado
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return 0;// Devuelve 0 en caso de error
    }

    /**
     * Método para registrar el detalle de una venta en la base de datos.
     *
     * @param dv Objeto Detalle que contiene los datos del detalle de la venta a
     * registrar.
     * @return 1 si el detalle fue registrado exitosamente, 0 en caso contrario.
     */
    public int RegistrarDetalle(Detalle dv) {
        String sql = "INSERT INTO detalle (cod_producto, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setString(1, dv.getCod_producto());
            ps.setInt(2, dv.getCantidad());
            ps.setDouble(3, dv.getPrecio());
            ps.setInt(4, dv.getId_venta());
            ps.executeUpdate();// Ejecuta la sentencia SQL
        } catch (SQLException e) {
            System.out.println(e.toString());
            return 0;// Devuelve 0 en caso de error
        } finally {
            try {
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return 1;// Devuelve 0 en caso de error
    }

    /**
     * Método para obtener el ID de la última venta registrada en la base de
     * datos.
     *
     * @return El ID de la última venta registrada.
     */
    public int IdVenta() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            if (rs.next()) {
                id = rs.getInt(1);// Obtiene el ID de la venta
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return id;// Devuelve el ID de la venta
    }

    /**
     * Método para actualizar el stock de un producto en la base de datos.
     *
     * @param cantidad Nueva cantidad de stock.
     * @param cod Código del producto.
     * @return true si el stock fue actualizado exitosamente, false en caso
     * contrario.
     */
    public boolean ActualizarStock(int cantidad, String cod) {
        String sql = "UPDATE productos SET stock = ? WHERE codigo = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            // Establece los parámetros de la sentencia SQL
            ps.setInt(1, cantidad);
            ps.setString(2, cod);
            ps.execute();// Ejecuta la sentencia SQL
            return true;// Devuelve true si la ejecución fue exitosa
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;// Devuelve false en caso de error
        } finally {
            try {
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Método para obtener la lista de ventas de un vendedor específico.
     *
     * @param nombreVendedor Nombre del vendedor.
     * @return Lista de objetos Venta que contiene las ventas del vendedor
     * especificado.
     */
    public List<Venta> ListaVentas(String nombreVendedor) {
        List<Venta> ListaVenta = new ArrayList<>();// Lista para almacenar las ventas
        String sql = "SELECT * FROM ventas WHERE vendedor = ?";
        try {
            con = cn.getConnection(); // Obtiene la conexión a la base de datos
            ps = con.prepareStatement(sql); // Prepara la sentencia SQL
            ps.setString(1, nombreVendedor); // Establece el parámetro de la sentencia SQL
            rs = ps.executeQuery(); // Ejecuta la consulta y obtiene los resultados
            while (rs.next()) {
                Venta venta = new Venta();// Crea un nuevo objeto Venta
                // Establece los atributos del objeto Venta con los datos obtenidos de la base de datos
                venta.setId(rs.getInt("id"));
                venta.setCliente(rs.getInt("cliente"));
                venta.setVendedor(rs.getString("vendedor"));
                venta.setTotal(rs.getInt("total"));
                ListaVenta.add(venta);// Añade el objeto Venta a la lista
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();// Cierra la conexión a la base de datos
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return ListaVenta;// Devuelve la lista de ventas
    }
}
