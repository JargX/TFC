package vista;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.BuscarDAO;
import modelo.Cliente;
import modelo.ClienteDAO;
import modelo.Config;
import modelo.Detalle;
import modelo.Productos;
import modelo.ProductosDAO;
import modelo.Proveedor;
import modelo.ProveedorDAO;
import modelo.TextPrompt;
import modelo.Validaciones;
import modelo.Venta;
import modelo.VentaDAO;
import modelo.login;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import reportes.Excel;
import reportes.Excel2;
import reportes.Excel3;

/**
 *
 * @author joelr
 */
public class SistemaPrincipal extends javax.swing.JFrame {

    /**
     * Creación del Sistema Principal de Ventas.
     */
    // Variables y objetos necesarios para el funcionamiento del Sistema
    Cliente cl = new Cliente();
    ClienteDAO cliente = new ClienteDAO();
    Proveedor pr = new Proveedor();
    ProveedorDAO proveedor = new ProveedorDAO();
    Productos pro = new Productos();
    ProductosDAO producto = new ProductosDAO();
    Venta v = new Venta();
    VentaDAO venta = new VentaDAO();
    Detalle dv = new Detalle();
    Config config = new Config();
    Validaciones val = new Validaciones();
    DefaultTableModel modelo = new DefaultTableModel();
    DefaultTableModel modelo2 = new DefaultTableModel();
    int item;
    double totalPagar = 0.00;

    // Constructor del Sistema Principal
    public SistemaPrincipal() {
        initComponents();// Inicializa los componentes del formulario
        this.setLocationRelativeTo(null);// Centra la ventana en la pantalla
    }

    // Constructor del Sistema Principal pasando el parametro adm de la clase login para comprobar si el usuario es administrador o vendedor dentro del sistema.
    public SistemaPrincipal(login adm) {
        initComponents();// Inicializa los componentes del formulario
        this.setLocationRelativeTo(null);// Centra la ventana en la pantalla
         setIconImage(new ImageIcon(getClass().getResource("/img/cup-cake.png")).getImage());

        // Oculta elementos clave dentro del sistema
        txtIdCliente.setVisible(false);
        txtIdProveedor.setVisible(false);
        txtIdProducto.setVisible(false);
        txtTelefonoCV.setVisible(false);
        txtDireccionCV.setVisible(false);
        txtRazonCV.setVisible(false);
        txtIdClienteCV.setVisible(false);
        txtIdConfig.setVisible(false);
        AutoCompleteDecorator.decorate(cbxProveedorProducto);// Autocompleta al escribir dentro del JComboBox
        producto.ConsultarProveedor(cbxProveedorProducto);
        TextPrompt phcliente = new TextPrompt("Buscar DNI, Nombre o Teléfono", txtBuscarCliente);
        TextPrompt phproveedor = new TextPrompt("Buscar NIF, Nombre o Teléfono", txtBuscarProveedor);
        TextPrompt phproducto = new TextPrompt("Buscar Código o Descripción", txtBuscarProducto);

        // Rellena los campos del tab Configuración
        listarConfig();

        // Hace la comprobación de que rol cumple el usuario dentro del sistema y limita el uso de la aplicacion ocultando botones o funciones
        switch (adm.getRol()) {
            case "Vendedor":
                txtNombreVendedor.setText(adm.getNombre());// Rellena el textField con el nombre del usuario que haya iniciado sesión
                btnProveedores.setVisible(false);
                btnDarAlta.setVisible(false);
                btnGuardarProducto.setVisible(false);
                btnActualizarProducto.setVisible(false);
                btnEliminarProducto.setVisible(false);
                btnProveedores.setVisible(false);
                btnFormInsertarProductos.setVisible(false);
                break;
            case "Almacén":

                txtNombreVendedor.setText(adm.getNombre());
                limpiarTabla();
                listarProductos();
                limpiarProducto();
                jTabbedPane1.setSelectedIndex(3);// Selecciona la pestaña con el índice 3 y lo muestra
                btnDarAlta.setVisible(false);
                btnGuardarCliente.setVisible(false);
                btnActualizarCliente.setVisible(false);
                btnEliminarCliente.setVisible(false);
                btnClientes.setVisible(false);
                btnVentaNueva.setVisible(false);
                btnVentas.setVisible(false);
                btnConfiguracion.setVisible(false);
                break;
            case "Administrador":
                txtNombreVendedor.setText(adm.getNombre());
                break;
        }
    }

    /**
     * MÉTODOS PARA LISTAR DENTRO DE LAS TABLAS O TEXTFIELDS
     */
    public void listarClientes() {
        List<Cliente> ListarCl = cliente.ListaClientes();
        modelo = (DefaultTableModel) tablaClientes.getModel();
        modelo.setRowCount(0); // Limpia todas las filas existentes
        Object[] ob = new Object[7];

        for (Cliente cl : ListarCl) {
            ob[0] = cl.getId();
            ob[1] = cl.getDni();
            ob[2] = cl.getNombre();
            ob[3] = cl.getTelefono();
            ob[4] = cl.getDireccion();
            ob[5] = cl.getCorreo();
            ob[6] = cl.getRazon();
            modelo.addRow(ob);
        }
        tablaClientes.setModel(modelo);
    }

    public void listarProveedor() {
        List<Proveedor> ListarPr = proveedor.ListaProveedores();// Obtiene y almacena la lista de Provedores
        modelo = (DefaultTableModel) tablaProveedores.getModel();// Obtiene el modelo para poder añadir luego las filas a la tabla
        modelo.setRowCount(0); // Limpia todas las filas existentes
        Object[] ob = new Object[7];// Array para almacenar los datos del proveedor

        // Recorre la lista de Proveedores y añade filas a la tabla del sistema
        for (int i = 0; i < ListarPr.size(); i++) {
            ob[0] = ListarPr.get(i).getId();
            ob[1] = ListarPr.get(i).getNif();
            ob[2] = ListarPr.get(i).getNombre();
            ob[3] = ListarPr.get(i).getTelefono();
            ob[4] = ListarPr.get(i).getDireccion();
            ob[5] = ListarPr.get(i).getCorreo();
            ob[6] = ListarPr.get(i).getRazon();
            modelo.addRow(ob);
        }
        tablaProveedores.setModel(modelo);// Establece el modelo a la tabla de Proveedores
    }

    public void listarProductos() {
        List<Productos> ListarPro = producto.ListaProductos();// Obtiene y almacena la lista de Productos
        modelo = (DefaultTableModel) tablaProductos.getModel();// Obtiene el modelo para poder añadir luego las filas a la tabla
        modelo.setRowCount(0); // Limpia todas las filas existentes
        Object[] ob = new Object[6];// Array para almacenar los datos del producto

        // Recorre la lista de Productos y añade filas a la tabla del sistema
        for (int i = 0; i < ListarPro.size(); i++) {
            ob[0] = ListarPro.get(i).getId();
            ob[1] = ListarPro.get(i).getCodigo();
            ob[2] = ListarPro.get(i).getNombre();
            ob[3] = ListarPro.get(i).getStock();
            ob[4] = ListarPro.get(i).getPrecio();
            ob[5] = ListarPro.get(i).getProveedor();
            modelo.addRow(ob);
        }
        tablaProductos.setModel(modelo);// Establece el modelo a la tabla de Productos
    }

    public void listarConfig() {
        config = producto.BuscarDatos();// Busca los datos de la empresa en la tabla Config
        // Rellena los textFields con la información de la empresa
        txtIdConfig.setText("" + config.getId());
        txtNIFConfig.setText("" + config.getNif());
        txtNombreConfig.setText("" + config.getNombre());
        txtTelefonoConfig.setText("" + config.getTelefono());
        txtDireccionConfig.setText("" + config.getDireccion());
        txtMensajeConfig.setText("" + config.getMensaje());

    }

    public void listarVentas() {
        // Obtener el nombre del vendedor del textField
        String nombreVendedor = txtNombreVendedor.getText();

        // Obtener la lista de ventas del vendedor
        List<Venta> ListarVenta = venta.ListaVentas(nombreVendedor);
        modelo2 = (DefaultTableModel) tablaVentas.getModel();
        modelo2.setRowCount(0); // Limpiar la tabla antes de agregar las nuevas filas
        Object[] ob = new Object[4];

        // Recorrer la lista de ventas y añadir filas a la tabla
        for (int i = 0; i < ListarVenta.size(); i++) {
            ob[0] = ListarVenta.get(i).getId();
            ob[1] = ListarVenta.get(i).getCliente();
            ob[2] = ListarVenta.get(i).getVendedor();
            ob[3] = ListarVenta.get(i).getTotal();
            modelo2.addRow(ob);
        }
        tablaVentas.setModel(modelo2);
    }

    /**
     * METODOS PARA LIMPIAR LAS TABLAS Y PODER ACTUALIZARLAS LUEGO.
     */
    public void limpiarTabla() {
        modelo.setRowCount(0); // Elimina todas las filas del modelo de la tabla
    }

    public void limpiarTablaVenta() {
        for (int i = 0; i < modelo2.getRowCount(); i++) {
            modelo2.removeRow(i);
            i = i - 1;
        }
    }

    /**
     * METODOS PARA LIMPIAR LOS TEXTFIELDS DE CLIENTES, PROVEEDORES,PRODUCTOS Y
     * VENTAS.
     */
    public void limpiarCliente() {
        txtIdCliente.setText("");
        txtDNICliente.setText("");
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtIdCliente.setText("");
        txtDireccionCliente.setText("");
        txtCorreoCliente.setText("");
        txtRazonCliente.setText("");
    }

    public void limpiarProveedor() {
        txtIdProveedor.setText("");
        txtNIFProveedor.setText("");
        txtNombreProveedor.setText("");
        txtTelefonoProveedor.setText("");
        txtIdProveedor.setText("");
        txtDireccionProveedor.setText("");
        txtCorreoProveedor.setText("");
        txtRazonProveedor.setText("");
    }

    public void limpiarProducto() {
        txtIdProducto.setText("");
        txtCodigoProducto.setText("");
        cbxProveedorProducto.setSelectedItem(null);
        txtNombreProducto.setText("");
        txtCantidadProducto.setText("");
        txtPrecioProducto.setText("");
    }

    public void limpiarVenta() {
        txtCodigoVenta.setText("");
        txtDescripcionVenta.setText("");
        txtPrecioVentas.setText("");
        txtCantidadVentas.setText("");
        txtStockDisponibleVentas.setText("");
        txtIdClienteCV.setText("");
        txtDniNif.setText("");
        txtNombreVenta.setText("");
        txtDireccionCV.setText("");
        txtTelefonoCV.setText("");
        txtRazonCV.setText("");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnCerrarSesion = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        btnVentaNueva = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnConfiguracion = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnEliminarVenta = new javax.swing.JButton();
        txtCodigoVenta = new javax.swing.JTextField();
        txtDescripcionVenta = new javax.swing.JTextField();
        txtCantidadVentas = new javax.swing.JTextField();
        txtPrecioVentas = new javax.swing.JTextField();
        txtStockDisponibleVentas = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaVenta = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelTotal = new javax.swing.JLabel();
        txtDniNif = new javax.swing.JTextField();
        txtNombreVenta = new javax.swing.JTextField();
        btnRegistrarVenta = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnBuscarCodigoVenta = new javax.swing.JButton();
        btnBuscarDNIVenta = new javax.swing.JButton();
        txtTelefonoCV = new javax.swing.JTextField();
        txtDireccionCV = new javax.swing.JTextField();
        txtRazonCV = new javax.swing.JTextField();
        txtIdClienteCV = new javax.swing.JTextField();
        btnInsertarCantidad = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        txtDNICliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        txtTelefonoCliente = new javax.swing.JTextField();
        txtDireccionCliente = new javax.swing.JTextField();
        txtCorreoCliente = new javax.swing.JTextField();
        txtRazonCliente = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        btnGuardarCliente = new javax.swing.JButton();
        btnActualizarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        btnLimpiarCliente = new javax.swing.JButton();
        txtBuscarCliente = new javax.swing.JTextField();
        btnExportarExcelClientes = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtNIFProveedor = new javax.swing.JTextField();
        txtNombreProveedor = new javax.swing.JTextField();
        txtTelefonoProveedor = new javax.swing.JTextField();
        txtDireccionProveedor = new javax.swing.JTextField();
        txtCorreoProveedor = new javax.swing.JTextField();
        txtRazonProveedor = new javax.swing.JTextField();
        txtIdProveedor = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaProveedores = new javax.swing.JTable();
        btnGuardarProveedor = new javax.swing.JButton();
        btnActualizarProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        btnLimpiarProveedor = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        txtBuscarProveedor = new javax.swing.JTextField();
        btnExportarExcelProveedores = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        cbxProveedorProducto = new javax.swing.JComboBox<>();
        txtCodigoProducto = new javax.swing.JTextField();
        txtNombreProducto = new javax.swing.JTextField();
        txtCantidadProducto = new javax.swing.JTextField();
        txtPrecioProducto = new javax.swing.JTextField();
        txtIdProducto = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        btnGuardarProducto = new javax.swing.JButton();
        btnActualizarProducto = new javax.swing.JButton();
        btnEliminarProducto = new javax.swing.JButton();
        btnLimpiarProducto = new javax.swing.JButton();
        btnExportarExcelProductos = new javax.swing.JButton();
        txtBuscarProducto = new javax.swing.JTextField();
        btnFormInsertarProductos = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tablaVentas = new javax.swing.JTable();
        btnPDF = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtIdVentas = new javax.swing.JTextField();
        txtNombreVendedor = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtIdConfig = new javax.swing.JTextField();
        txtNIFConfig = new javax.swing.JTextField();
        txtNombreConfig = new javax.swing.JTextField();
        txtTelefonoConfig = new javax.swing.JTextField();
        txtDireccionConfig = new javax.swing.JTextField();
        txtMensajeConfig = new javax.swing.JTextField();
        btnActualizarConfig = new javax.swing.JButton();
        btnDarAlta = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SISTEMA PRINCIPAL");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(1900, 1000));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar-sesion.png"))); // NOI18N
        btnCerrarSesion.setToolTipText("Pulse para cerrar la sesión.");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1790, 0, 130, 70));

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setLayout(new java.awt.GridLayout(1, 0));

        btnVentaNueva.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnVentaNueva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ventas.png"))); // NOI18N
        btnVentaNueva.setText("VENTA NUEVA");
        btnVentaNueva.setToolTipText("Pulse para acceder al sistema de venta nueva.");
        btnVentaNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentaNuevaActionPerformed(evt);
            }
        });
        jPanel8.add(btnVentaNueva);

        btnClientes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cliente.png"))); // NOI18N
        btnClientes.setText("CLIENTES");
        btnClientes.setToolTipText("Pulse para acceder a los clientes.");
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        jPanel8.add(btnClientes);

        btnProveedores.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnProveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/proveedor.png"))); // NOI18N
        btnProveedores.setText("PROVEEDORES");
        btnProveedores.setToolTipText("Pulse para acceder a los proveedores.");
        btnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedoresActionPerformed(evt);
            }
        });
        jPanel8.add(btnProveedores);

        btnProductos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/productos.png"))); // NOI18N
        btnProductos.setText("PRODUCTOS");
        btnProductos.setToolTipText("Pulse para acceder a los productos.");
        btnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosActionPerformed(evt);
            }
        });
        jPanel8.add(btnProductos);

        btnVentas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/venta.png"))); // NOI18N
        btnVentas.setText("HISTORIAL");
        btnVentas.setToolTipText("Pulse para acceder al historial de ventas.");
        btnVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasActionPerformed(evt);
            }
        });
        jPanel8.add(btnVentas);

        btnConfiguracion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/configuracion.png"))); // NOI18N
        btnConfiguracion.setText("CONFIGURACIÓN");
        btnConfiguracion.setToolTipText("Pulse para acceder a la configuración.");
        btnConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionActionPerformed(evt);
            }
        });
        jPanel8.add(btnConfiguracion);

        getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1790, 70));

        jTabbedPane1.setBackground(new java.awt.Color(250, 239, 214));
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(250, 239, 214));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CÓDIGO");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 200, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DESCRIPCIÓN");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 30, 300, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CANTIDAD");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 30, 150, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PRECIO");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 30, 130, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("STOCK DISPONIBLE");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 30, 152, -1));

        btnEliminarVenta.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarVenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarVenta.setText("ELIMINAR PRODUCTO");
        btnEliminarVenta.setToolTipText("Botón Eliminar producto. Seleccione haciendo click un producto dentro de la lista para eliminarlo de la lista.");
        btnEliminarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarVentaActionPerformed(evt);
            }
        });
        jPanel2.add(btnEliminarVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(1600, 160, -1, -1));

        txtCodigoVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCodigoVenta.setToolTipText("Se introduce el código de un producto, tecla Enter o Botón Lupa para buscarlo y añadirlo para pasar a la cantidad de este. ");
        txtCodigoVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoVentaKeyPressed(evt);
            }
        });
        jPanel2.add(txtCodigoVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 200, 30));

        txtDescripcionVenta.setEditable(false);
        txtDescripcionVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDescripcionVenta.setToolTipText("Muestra la descripción del producto, no editable.");
        jPanel2.add(txtDescripcionVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 300, 30));

        txtCantidadVentas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCantidadVentas.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCantidadVentas.setToolTipText("Se introduce la cantidad del producto que se haya buscado,  tecla Enter o Botón Insertar para añadir esa cantidad del producto a la lista/venta.");
        txtCantidadVentas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadVentasKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadVentasKeyTyped(evt);
            }
        });
        jPanel2.add(txtCantidadVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 60, 150, 30));

        txtPrecioVentas.setEditable(false);
        txtPrecioVentas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPrecioVentas.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPrecioVentas.setToolTipText("Muestra el precio unitario del producto. No editable.");
        txtPrecioVentas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioVentasKeyTyped(evt);
            }
        });
        jPanel2.add(txtPrecioVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 60, 130, 30));

        txtStockDisponibleVentas.setEditable(false);
        txtStockDisponibleVentas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtStockDisponibleVentas.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtStockDisponibleVentas.setToolTipText("Muestra el stock disponible del producto. No editable.");
        jPanel2.add(txtStockDisponibleVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 60, 152, 30));

        tablaVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESCRIPCIÓN", "CANTIDAD", "PRECIO", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaVenta.setToolTipText("Tabla con los producto que seran vendidos.");
        tablaVenta.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tablaVenta);
        if (tablaVenta.getColumnModel().getColumnCount() > 0) {
            tablaVenta.getColumnModel().getColumn(0).setResizable(false);
            tablaVenta.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaVenta.getColumnModel().getColumn(1).setResizable(false);
            tablaVenta.getColumnModel().getColumn(1).setPreferredWidth(100);
            tablaVenta.getColumnModel().getColumn(2).setResizable(false);
            tablaVenta.getColumnModel().getColumn(2).setPreferredWidth(30);
            tablaVenta.getColumnModel().getColumn(3).setResizable(false);
            tablaVenta.getColumnModel().getColumn(3).setPreferredWidth(30);
            tablaVenta.getColumnModel().getColumn(4).setResizable(false);
            tablaVenta.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 1920, 625));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("DNI/NIF");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 144, 200, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("NOMBRE");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 140, 300, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel10.setText("TOTAL A PAGAR:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 160, -1, -1));

        labelTotal.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        labelTotal.setText("-------");
        jPanel2.add(labelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 160, -1, -1));

        txtDniNif.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDniNif.setToolTipText("Se introduce el DNI/NIF de un Cliente al que se le atribuya la venta.");
        txtDniNif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDniNifActionPerformed(evt);
            }
        });
        txtDniNif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDniNifKeyPressed(evt);
            }
        });
        jPanel2.add(txtDniNif, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 172, 200, 30));

        txtNombreVenta.setEditable(false);
        txtNombreVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNombreVenta.setToolTipText("Muestra el nombre del Cliente, no editable.");
        jPanel2.add(txtNombreVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 170, 300, 30));

        btnRegistrarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/flecha.png"))); // NOI18N
        btnRegistrarVenta.setToolTipText("Botón de generar venta. Cuando se hayan introducido todos los productos elegidos, la cantidad y el cliente al que se le atribuya la venta, pulsa el boton para generar el documento PDF con la informacion de la venta.");
        btnRegistrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarVentaActionPerformed(evt);
            }
        });
        jPanel2.add(btnRegistrarVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 160, 150, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("€");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1380, 160, -1, -1));

        btnBuscarCodigoVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/buscar.png"))); // NOI18N
        btnBuscarCodigoVenta.setToolTipText("Botón de buscar código de producto. Pulse para buscar una vez haya introducido el código de producto.");
        btnBuscarCodigoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCodigoVentaActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuscarCodigoVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 50, 50));

        btnBuscarDNIVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/buscar.png"))); // NOI18N
        btnBuscarDNIVenta.setToolTipText("Botón de buscar el DNI/NIF del cliente. Pulse para buscar una vez haya introducido el DNI/NIF de cliente.");
        btnBuscarDNIVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarDNIVentaActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuscarDNIVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 50, 50));

        txtTelefonoCV.setEditable(false);
        jPanel2.add(txtTelefonoCV, new org.netbeans.lib.awtextra.AbsoluteConstraints(1470, 110, 10, -1));

        txtDireccionCV.setEditable(false);
        txtDireccionCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDireccionCVActionPerformed(evt);
            }
        });
        jPanel2.add(txtDireccionCV, new org.netbeans.lib.awtextra.AbsoluteConstraints(1490, 110, 10, -1));

        txtRazonCV.setEditable(false);
        jPanel2.add(txtRazonCV, new org.netbeans.lib.awtextra.AbsoluteConstraints(1510, 110, 10, -1));

        txtIdClienteCV.setEditable(false);
        jPanel2.add(txtIdClienteCV, new org.netbeans.lib.awtextra.AbsoluteConstraints(1530, 110, 10, -1));

        btnInsertarCantidad.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnInsertarCantidad.setText("INSERTAR");
        btnInsertarCantidad.setToolTipText("Botón de insertar. La cantidad establecida del producto seleccionado se introduce en la tabla de la venta.");
        btnInsertarCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarCantidadActionPerformed(evt);
            }
        });
        jPanel2.add(btnInsertarCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 100, 150, -1));

        jTabbedPane1.addTab("Nueva Venta", jPanel2);

        jPanel3.setBackground(new java.awt.Color(250, 239, 214));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("BUSCAR");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 120, 40));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("NOMBRE:");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 275, 120, 40));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText("TELÉFONO:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 120, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText("DIRECCIÓN:");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 425, 120, 40));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel16.setText("CORREO:");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 120, 40));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel32.setText("RAZÓN:");
        jPanel3.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 575, 120, 40));

        txtIdCliente.setEditable(false);
        txtIdCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanel3.add(txtIdCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 35, 30));

        txtDNICliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDNICliente.setToolTipText("Se introduce o se muestra el DNI/NIF del cliente.");
        jPanel3.add(txtDNICliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 250, 40));

        txtNombreCliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNombreCliente.setToolTipText("Se introduce o se muestra el nombre del cliente.");
        txtNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyTyped(evt);
            }
        });
        jPanel3.add(txtNombreCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 275, 250, 40));

        txtTelefonoCliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtTelefonoCliente.setToolTipText("Se introduce o se muestra el teléfono del cliente.");
        txtTelefonoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoClienteKeyTyped(evt);
            }
        });
        jPanel3.add(txtTelefonoCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, 250, 40));

        txtDireccionCliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDireccionCliente.setToolTipText("Se introduce o se muestra el teléfono del cliente.");
        jPanel3.add(txtDireccionCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 425, 250, 40));

        txtCorreoCliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCorreoCliente.setToolTipText("Se introduce o se muestra el correo del cliente.");
        jPanel3.add(txtCorreoCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 500, 250, 40));

        txtRazonCliente.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtRazonCliente.setToolTipText("Se introduce o se muestra la razón social del cliente.");
        jPanel3.add(txtRazonCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 575, 250, 40));

        tablaClientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI/NIF", "NOMBRE", "TELÉFONO", "DIRECCIÓN", "CORREO", "RAZON SOCIAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaClientes.setToolTipText("Tabla clientes.");
        tablaClientes.setRowHeight(30);
        tablaClientes.getTableHeader().setReorderingAllowed(false);
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaClientes);
        if (tablaClientes.getColumnModel().getColumnCount() > 0) {
            tablaClientes.getColumnModel().getColumn(1).setResizable(false);
            tablaClientes.getColumnModel().getColumn(2).setResizable(false);
            tablaClientes.getColumnModel().getColumn(3).setResizable(false);
            tablaClientes.getColumnModel().getColumn(4).setResizable(false);
            tablaClientes.getColumnModel().getColumn(5).setResizable(false);
            tablaClientes.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 125, 1500, 800));

        btnGuardarCliente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGuardarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/guardar-el-archivo.png"))); // NOI18N
        btnGuardarCliente.setText("GUARDAR");
        btnGuardarCliente.setToolTipText("Boton Guardar. Haga clic con los campos llenos para guardar un nuevo cliente.");
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });
        jPanel3.add(btnGuardarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 680, 200, 50));

        btnActualizarCliente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnActualizarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/actualizar-flecha.png"))); // NOI18N
        btnActualizarCliente.setText("ACTUALIZAR");
        btnActualizarCliente.setToolTipText("Boton Actualizar. Seleccione un registro, haga los cambios deseados y haga clic con los campos llenos para actualizar un cliente.");
        btnActualizarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarClienteActionPerformed(evt);
            }
        });
        jPanel3.add(btnActualizarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 680, 200, 50));

        btnEliminarCliente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarCliente.setText("ELIMINAR");
        btnEliminarCliente.setToolTipText("Boton Eliminar. Seleccione un registro y  haga clic para eliminar un cliente.");
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });
        jPanel3.add(btnEliminarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 730, 200, 50));

        btnLimpiarCliente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-nuevo.png"))); // NOI18N
        btnLimpiarCliente.setText("LIMPIAR");
        btnLimpiarCliente.setToolTipText("Boton Limpiar. Limpia los campos para darle un nuevo uso.");
        btnLimpiarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarClienteActionPerformed(evt);
            }
        });
        jPanel3.add(btnLimpiarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 730, 200, 50));

        txtBuscarCliente.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtBuscarCliente.setToolTipText("Introduzca DNI, Nombre o Telefono para buscar el registro.");
        txtBuscarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyReleased(evt);
            }
        });
        jPanel3.add(txtBuscarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 35, 250, 40));

        btnExportarExcelClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportarExcelClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-excel.png"))); // NOI18N
        btnExportarExcelClientes.setText("EXPORTAR");
        btnExportarExcelClientes.setToolTipText("Boton Exportar. Haga clic para exportar la tabla a excel.");
        btnExportarExcelClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelClientesActionPerformed(evt);
            }
        });
        jPanel3.add(btnExportarExcelClientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(1550, 35, 200, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CLIENTES");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 100));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel39.setText("DNI/NIF:");
        jPanel3.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 120, 40));

        jTabbedPane1.addTab("Clientes", jPanel3);

        jPanel4.setBackground(new java.awt.Color(250, 239, 214));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel17.setText("CORREO:");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 120, 40));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel18.setText("NOMBRE:");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 275, 120, 40));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("NIF:");
        jPanel4.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 120, 40));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel20.setText("TELÉFONO:");
        jPanel4.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 120, 40));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel21.setText("DIRECCIÓN:");
        jPanel4.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 425, 120, 40));

        txtNIFProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNIFProveedor.setToolTipText("Se introduce o se muestra el NIF del proveedor.");
        jPanel4.add(txtNIFProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 250, 40));

        txtNombreProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNombreProveedor.setToolTipText("Se introduce o se muestra el nombre del proveedor.");
        txtNombreProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreProveedorKeyTyped(evt);
            }
        });
        jPanel4.add(txtNombreProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 275, 250, 40));

        txtTelefonoProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtTelefonoProveedor.setToolTipText("Se introduce o se muestra el teléfono del proveedor.");
        txtTelefonoProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoProveedorKeyTyped(evt);
            }
        });
        jPanel4.add(txtTelefonoProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, 250, 40));

        txtDireccionProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDireccionProveedor.setToolTipText("Se introduce o se muestra la dirección del proveedor.");
        jPanel4.add(txtDireccionProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 425, 250, 40));

        txtCorreoProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCorreoProveedor.setToolTipText("Se introduce o se muestra el correo del proveedor.");
        jPanel4.add(txtCorreoProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 500, 250, 40));

        txtRazonProveedor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtRazonProveedor.setToolTipText("Se introduce o se muestra la razón del proveedor.");
        jPanel4.add(txtRazonProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 575, 250, 40));

        txtIdProveedor.setEditable(false);
        jPanel4.add(txtIdProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 30, 30));

        tablaProveedores.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NIF", "NOMBRE", "TELÉFONO", "DIRECCIÓN", "CORREO", "RAZÓN SOCIAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProveedores.setToolTipText("Tabla proveedores.");
        tablaProveedores.setRowHeight(30);
        tablaProveedores.getTableHeader().setReorderingAllowed(false);
        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProveedoresMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaProveedores);
        if (tablaProveedores.getColumnModel().getColumnCount() > 0) {
            tablaProveedores.getColumnModel().getColumn(0).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(1).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(2).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(3).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(4).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(5).setResizable(false);
            tablaProveedores.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 125, 1500, 800));

        btnGuardarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGuardarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/guardar-el-archivo.png"))); // NOI18N
        btnGuardarProveedor.setText("GUARDAR");
        btnGuardarProveedor.setToolTipText("Boton Guardar. Haga clic con los campos llenos para guardar un nuevo proveedor.");
        btnGuardarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedorActionPerformed(evt);
            }
        });
        jPanel4.add(btnGuardarProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 680, 200, 50));

        btnActualizarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnActualizarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/actualizar-flecha.png"))); // NOI18N
        btnActualizarProveedor.setText("ACTUALIZAR");
        btnActualizarProveedor.setToolTipText("Boton Actualizar. Seleccione un registro, haga los cambios deseados y haga clic con los campos llenos para actualizar un proveedor.");
        btnActualizarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProveedorActionPerformed(evt);
            }
        });
        jPanel4.add(btnActualizarProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 680, 200, 50));

        btnEliminarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEliminarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarProveedor.setText("ELIMINAR");
        btnEliminarProveedor.setToolTipText("Boton Eliminar. Seleccione un registro y  haga clic para eliminar un proveedor.");
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });
        jPanel4.add(btnEliminarProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 730, 200, 50));

        btnLimpiarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-nuevo.png"))); // NOI18N
        btnLimpiarProveedor.setText("LIMPIAR");
        btnLimpiarProveedor.setToolTipText("Boton Limpiar. Limpia los campos para darle un nuevo uso.");
        btnLimpiarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarProveedorActionPerformed(evt);
            }
        });
        jPanel4.add(btnLimpiarProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 730, 200, 50));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel33.setText("RAZÓN:");
        jPanel4.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 575, 120, 40));

        txtBuscarProveedor.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtBuscarProveedor.setToolTipText("Introduzca NIF, Nombre o Telefono para buscar el registro.");
        txtBuscarProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProveedorKeyReleased(evt);
            }
        });
        jPanel4.add(txtBuscarProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 35, 250, 40));

        btnExportarExcelProveedores.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportarExcelProveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-excel.png"))); // NOI18N
        btnExportarExcelProveedores.setText("EXPORTAR");
        btnExportarExcelProveedores.setToolTipText("Boton Exportar. Haga clic para exportar la tabla a excel.");
        btnExportarExcelProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelProveedoresActionPerformed(evt);
            }
        });
        jPanel4.add(btnExportarExcelProveedores, new org.netbeans.lib.awtextra.AbsoluteConstraints(1550, 35, 200, 40));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel40.setText("BUSCAR");
        jPanel4.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 120, 40));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("PROVEEDORES");
        jPanel4.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 100));

        jTabbedPane1.addTab("Proveedores", jPanel4);

        jPanel5.setBackground(new java.awt.Color(250, 239, 214));
        jPanel5.setToolTipText("Tabla productos.");
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("CÓDIGO:");
        jPanel5.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 130, 40));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("DESCRIPCIÓN:");
        jPanel5.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 275, 130, 40));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("CANTIDAD:");
        jPanel5.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 130, 40));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel25.setText("PRECIO:");
        jPanel5.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 425, 130, 40));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel26.setText("PROVEEDOR:");
        jPanel5.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 130, 40));

        cbxProveedorProducto.setEditable(true);
        cbxProveedorProducto.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cbxProveedorProducto.setToolTipText("Se introduce o se muestra el proveedor del producto.");
        jPanel5.add(cbxProveedorProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 500, 250, 40));

        txtCodigoProducto.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCodigoProducto.setToolTipText("Se introduce o se muestra el código del producto.");
        jPanel5.add(txtCodigoProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 250, 40));

        txtNombreProducto.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNombreProducto.setToolTipText("Se introduce o se muestra la descripción del producto.");
        jPanel5.add(txtNombreProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 275, 250, 40));

        txtCantidadProducto.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCantidadProducto.setToolTipText("Se introduce o se muestra la cantidad del producto.");
        txtCantidadProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadProductoKeyTyped(evt);
            }
        });
        jPanel5.add(txtCantidadProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, 250, 40));

        txtPrecioProducto.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPrecioProducto.setToolTipText("Se introduce o se muestra el precio del producto.");
        txtPrecioProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioProductoKeyTyped(evt);
            }
        });
        jPanel5.add(txtPrecioProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 425, 250, 40));

        txtIdProducto.setEditable(false);
        jPanel5.add(txtIdProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 30, 30));

        tablaProductos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CÓDIGO", "DESCRIPCIÓN", "CANTIDAD", "PRECIO", "PROVEEDOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProductos.setToolTipText("Tabla Productos.");
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setResizable(false);
            tablaProductos.getColumnModel().getColumn(1).setResizable(false);
            tablaProductos.getColumnModel().getColumn(2).setResizable(false);
            tablaProductos.getColumnModel().getColumn(3).setResizable(false);
            tablaProductos.getColumnModel().getColumn(4).setResizable(false);
            tablaProductos.getColumnModel().getColumn(5).setResizable(false);
        }

        jPanel5.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 125, 1500, 800));

        btnGuardarProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGuardarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/guardar-el-archivo.png"))); // NOI18N
        btnGuardarProducto.setText("GUARDAR");
        btnGuardarProducto.setToolTipText("Boton Guardar. Haga clic con los campos llenos para guardar un nuevo producto.");
        btnGuardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductoActionPerformed(evt);
            }
        });
        jPanel5.add(btnGuardarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 680, 200, 50));

        btnActualizarProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnActualizarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/actualizar-flecha.png"))); // NOI18N
        btnActualizarProducto.setText("ACTUALIZAR");
        btnActualizarProducto.setToolTipText("Boton Actualizar. Seleccione un registro, haga los cambios deseados y haga clic con los campos llenos para actualizar un producto.");
        btnActualizarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProductoActionPerformed(evt);
            }
        });
        jPanel5.add(btnActualizarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 680, 200, 50));

        btnEliminarProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEliminarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarProducto.setText("ELIMINAR");
        btnEliminarProducto.setToolTipText("Boton Eliminar. Seleccione un registro y haga clic para eliminar un producto.");
        btnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoActionPerformed(evt);
            }
        });
        jPanel5.add(btnEliminarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 730, 200, 50));

        btnLimpiarProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-nuevo.png"))); // NOI18N
        btnLimpiarProducto.setText("LIMPIAR");
        btnLimpiarProducto.setToolTipText("Boton Limpiar. Limpia los campos para darle un nuevo uso.");
        btnLimpiarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarProductoActionPerformed(evt);
            }
        });
        jPanel5.add(btnLimpiarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 730, 200, 50));

        btnExportarExcelProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportarExcelProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-excel.png"))); // NOI18N
        btnExportarExcelProductos.setText("EXPORTAR");
        btnExportarExcelProductos.setToolTipText("Boton Exportar. Haga clic para exportar la tabla a excel.");
        btnExportarExcelProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelProductosActionPerformed(evt);
            }
        });
        jPanel5.add(btnExportarExcelProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(1550, 35, 200, 40));

        txtBuscarProducto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtBuscarProducto.setToolTipText("Introduzca Código, Nombre para buscar el registro.");
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyReleased(evt);
            }
        });
        jPanel5.add(txtBuscarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 35, 250, 40));

        btnFormInsertarProductos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFormInsertarProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/agregarProducto.png"))); // NOI18N
        btnFormInsertarProductos.setToolTipText("Boton Añadir mas productos. Haga clic para abrir la ventana del fomulario de añadir productos.");
        btnFormInsertarProductos.setLabel("AÑADIR PRODUCTOS");
        btnFormInsertarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFormInsertarProductosActionPerformed(evt);
            }
        });
        jPanel5.add(btnFormInsertarProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 250, 40));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("PRODUCTOS");
        jPanel5.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 100));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel41.setText("BUSCAR");
        jPanel5.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 120, 40));

        jTabbedPane1.addTab("Productos", jPanel5);

        jPanel6.setBackground(new java.awt.Color(250, 239, 214));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CLIENTE", "VENDEDOR", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaVentas.setRowHeight(25);
        tablaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaVentasMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tablaVentas);
        if (tablaVentas.getColumnModel().getColumnCount() > 0) {
            tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(20);
            tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(60);
            tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(60);
        }

        jPanel6.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 268, 1920, 750));

        btnPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archivo-pdf.png"))); // NOI18N
        btnPDF.setToolTipText("Boton PDF. Seleccione una venta y haga clic para abrirla.");
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });
        jPanel6.add(btnPDF, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 180, 70, 70));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("HISTORIAL DE VENTAS");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 100));

        txtIdVentas.setEditable(false);
        txtIdVentas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtIdVentas.setToolTipText("");
        jPanel6.add(txtIdVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 220, 135, 30));

        txtNombreVendedor.setEditable(false);
        txtNombreVendedor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNombreVendedor.setToolTipText("Nombre del vendedor");
        jPanel6.add(txtNombreVendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1530, 220, 370, 30));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel37.setText("ID VENTA:");
        jPanel6.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 140, 30));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel38.setText("VENDEDOR:");
        jPanel6.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(1370, 220, 150, 30));

        jTabbedPane1.addTab("tab5", jPanel6);

        jPanel7.setBackground(new java.awt.Color(250, 239, 214));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("NIF");
        jPanel7.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 106, 290, 40));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("NOMBRE");
        jPanel7.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 110, 290, 40));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("TELÉFONO");
        jPanel7.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(1215, 110, 290, 40));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("DIRECCIÓN");
        jPanel7.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 246, 290, 40));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("MENSAJE");
        jPanel7.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 246, 290, 40));

        txtIdConfig.setEditable(false);
        txtIdConfig.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel7.add(txtIdConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 350, -1, -1));

        txtNIFConfig.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNIFConfig.setToolTipText("Se introduce o se muestra el NIF de la configuración.");
        jPanel7.add(txtNIFConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 146, 290, 40));

        txtNombreConfig.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNombreConfig.setToolTipText("Se introduce o se muestra el nombre de la configuración.");
        txtNombreConfig.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreConfigKeyTyped(evt);
            }
        });
        jPanel7.add(txtNombreConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 150, 290, 40));

        txtTelefonoConfig.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtTelefonoConfig.setToolTipText("Se introduce o se muestra el teléfono de la configuración.");
        txtTelefonoConfig.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoConfigKeyTyped(evt);
            }
        });
        jPanel7.add(txtTelefonoConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(1215, 150, 290, 40));

        txtDireccionConfig.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtDireccionConfig.setToolTipText("Se introduce o se muestra el dirección de la configuración.");
        jPanel7.add(txtDireccionConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 286, 290, 40));

        txtMensajeConfig.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtMensajeConfig.setToolTipText("Se introduce o se muestra el mensaje de la configuración.");
        jPanel7.add(txtMensajeConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 286, 290, 40));

        btnActualizarConfig.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnActualizarConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/actualizar-flecha.png"))); // NOI18N
        btnActualizarConfig.setText("ACTUALIZAR DATOS");
        btnActualizarConfig.setToolTipText("Boton Actualizar Datos. Modifique los campos deseados y haga clic para actualizar los datos.");
        btnActualizarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarConfigActionPerformed(evt);
            }
        });
        jPanel7.add(btnActualizarConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 500, 400, 60));

        btnDarAlta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDarAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/anadir.png"))); // NOI18N
        btnDarAlta.setText("DAR DE ALTA USUARIOS");
        btnDarAlta.setToolTipText("Boton Dar de Alta Usuarios. Solo para administradores, haga clic para abrir el registro de usuarios,");
        btnDarAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDarAltaActionPerformed(evt);
            }
        });
        jPanel7.add(btnDarAlta, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 30, 250, 50));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("CONFIGURACIÓN DE LA EMPRESA");
        jPanel7.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 100));

        jTabbedPane1.addTab("tab6", jPanel7);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 1920, 1050));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * BOTONES DEL MENU DE ARRIBA
     */
    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        limpiarTabla();
        listarProveedor();
        jTabbedPane1.setSelectedIndex(2);// Selecciona la pestaña con el índice 2 y lo muestra
    }//GEN-LAST:event_btnProveedoresActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
        limpiarTabla();
        listarProductos();
        // Actualizar el ComboBox de proveedores
        proveedor.actualizarProveedores(cbxProveedorProducto);
        limpiarProducto();
        jTabbedPane1.setSelectedIndex(3);// Selecciona la pestaña con el índice 3 y lo muestra
    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
        limpiarTablaVenta();
        listarVentas();
        jTabbedPane1.setSelectedIndex(4);// Selecciona la pestaña con el índice 4 y lo muestra
    }//GEN-LAST:event_btnVentasActionPerformed

    private void btnConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionActionPerformed
        listarConfig();
        jTabbedPane1.setSelectedIndex(5);// Selecciona la pestaña con el índice 5 y lo muestra
    }//GEN-LAST:event_btnConfiguracionActionPerformed

    /**
     * BOTONES CRUD, TABLA Y LIMPIEZA DE CLIENTE
     */
    private void btnGuardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteActionPerformed
        // Verifica que los campos no esten vacios
        if (!"".equals(txtDNICliente.getText()) && !"".equals(txtNombreCliente.getText()) && !"".equals(txtTelefonoCliente.getText()) && !"".equals(txtDireccionCliente.getText()) && !"".equals(txtCorreoCliente.getText()) && !"".equals(txtRazonCliente.getText())) {
            if (txtTelefonoCliente.getText().length() <= 9) { // Validar longitud del teléfono
                int telefono = Integer.parseInt(txtTelefonoCliente.getText());
                if (telefono <= 999999999) { // Validar que el teléfono no sea mayor a 999999999
                    String correo = txtCorreoCliente.getText();
                    if (validarCorreo(correo)) { // Validar el formato del correo electrónico
                        // Establece los datos del cliente
                        cl.setDni(txtDNICliente.getText());
                        cl.setNombre(txtNombreCliente.getText());
                        cl.setTelefono(telefono);
                        cl.setDireccion(txtDireccionCliente.getText());
                        cl.setCorreo(correo);
                        cl.setRazon(txtRazonCliente.getText());
                        // Registra el cliente en la base de datos
                        if (cliente.RegistarCliente(cl)) {
                            limpiarTabla();
                            listarClientes();
                            limpiarCliente();
                            JOptionPane.showMessageDialog(null, "Cliente registrado correctamente");
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al registrar el cliente, por favor revise los datos");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El formato del correo electrónico es inválido");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El número de teléfono no puede ser mayor a 999999999");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El número de teléfono debe tener 9 dígitos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos");
        }
    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        limpiarTabla();
        listarClientes();
        jTabbedPane1.setSelectedIndex(1);//Selecciona la pestaña con el índice 1 y lo muestra
    }//GEN-LAST:event_btnClientesActionPerformed

    private void tablaClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClientesMouseClicked
        // Obtiene la fila seleccionada en la tabla
        int fila = tablaClientes.rowAtPoint(evt.getPoint());
        // Rellena los campos con la información de la tabla de la fila seleccionada
        txtIdCliente.setText(tablaClientes.getValueAt(fila, 0).toString());
        txtDNICliente.setText(tablaClientes.getValueAt(fila, 1).toString());
        txtNombreCliente.setText(tablaClientes.getValueAt(fila, 2).toString());
        txtTelefonoCliente.setText(tablaClientes.getValueAt(fila, 3).toString());
        txtDireccionCliente.setText(tablaClientes.getValueAt(fila, 4).toString());
        txtCorreoCliente.setText(tablaClientes.getValueAt(fila, 5).toString());
        txtRazonCliente.setText(tablaClientes.getValueAt(fila, 6).toString());
    }//GEN-LAST:event_tablaClientesMouseClicked

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        // Verifica que el campo ID no este vacío
        if (!"".equals(txtIdCliente.getText())) {
            // Mensaje de confirmación para eliminar el cliente
            int pregunta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar el cliente?", "Eliminar Cliente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (pregunta == 0) {
                int id = Integer.parseInt(txtIdCliente.getText());
                // Elimina el cliente de la base de datos
                cliente.EliminarCliente(id);
                limpiarTabla();
                listarClientes();
                limpiarCliente();
            }
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnActualizarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClienteActionPerformed
        // Verifica que el campo ID no este vacío
        if ("".equals(txtIdCliente.getText())) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente.");
        } else {
            // Verifica que los campos no esten vacios
            if (!"".equals(txtDNICliente.getText()) && !"".equals(txtNombreCliente.getText()) && !"".equals(txtTelefonoCliente.getText()) && !"".equals(txtDireccionCliente.getText()) && !"".equals(txtCorreoCliente.getText()) && !"".equals(txtRazonCliente.getText())) {
                if (txtTelefonoCliente.getText().length() <= 9) {
                    int telefono = Integer.parseInt(txtTelefonoCliente.getText());
                    if (telefono <= 999999999) {// Validar que el teléfono no sea mayor a 999999999
                        String correo = txtCorreoCliente.getText();
                        if (validarCorreo(correo)) {// Validar el formato del correo electrónico
                            // Establece los nuevos datos del clientes
                            cl.setId(Integer.parseInt(txtIdCliente.getText()));
                            cl.setDni(txtDNICliente.getText());
                            cl.setNombre(txtNombreCliente.getText());
                            cl.setTelefono(telefono);
                            cl.setDireccion(txtDireccionCliente.getText());
                            cl.setCorreo(correo);
                            cl.setRazon(txtRazonCliente.getText());
                            if (cliente.ModificarCliente(cl)) { // Verifica si la modificación se realizó con éxito
                                limpiarTabla();
                                listarClientes();
                                limpiarCliente();
                                JOptionPane.showMessageDialog(null, "Cliente modificado correctamente");
                            } else {
                                JOptionPane.showMessageDialog(null, "Error al modificar el cliente");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "El formato del correo electrónico es inválido");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El número de teléfono no puede ser mayor a 999999999");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El número de teléfono debe tener 9 dígitos");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos");
            }
        }
    }//GEN-LAST:event_btnActualizarClienteActionPerformed

    private void btnLimpiarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarClienteActionPerformed
        limpiarCliente();
    }//GEN-LAST:event_btnLimpiarClienteActionPerformed

    /**
     * BOTONES CRUD, TABLA Y LIMPIEZA DE PROVEEDOR
     */
    private void btnGuardarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedorActionPerformed
        // Verifica que los campos no esten vacios
        if (!"".equals(txtNIFProveedor.getText()) && !"".equals(txtNombreProveedor.getText()) && !"".equals(txtTelefonoProveedor.getText()) && !"".equals(txtDireccionProveedor.getText()) && !"".equals(txtCorreoProveedor.getText()) && !"".equals(txtRazonProveedor.getText())) {
            if (txtTelefonoProveedor.getText().length() == 9) { // Validar longitud del teléfono
                int telefonopr = Integer.parseInt(txtTelefonoProveedor.getText());
                if (telefonopr <= 999999999) { // Validar que el teléfono no sea mayor a 999999999
                    String correopr = txtCorreoProveedor.getText();
                    if (validarCorreo(correopr)) { // Validar el formato del correo electrónico
                        // Establece los datos del proveedor
                        pr.setNif(txtNIFProveedor.getText());
                        pr.setNombre(txtNombreProveedor.getText());
                        pr.setTelefono(telefonopr);
                        pr.setDireccion(txtDireccionProveedor.getText());
                        pr.setCorreo(correopr);
                        pr.setRazon(txtRazonProveedor.getText());
                        // Registra el proveedor en la base de datos
                        boolean registrado = proveedor.RegistarProveedor(pr);
                        if (registrado) {
                            limpiarTabla();
                            listarProveedor();
                            limpiarProveedor();
                            // Actualizar el ComboBox de proveedores
                            proveedor.actualizarProveedores(cbxProveedorProducto); // cbxProveedorCliente debe ser el ComboBox correcto
                            JOptionPane.showMessageDialog(null, "Proveedor registrado correctamente");
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al registrar el proveedor");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El formato del correo electrónico es inválido");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El número de teléfono no puede ser mayor a 999999999");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El número de teléfono debe tener 9 dígitos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos");
        }
    }//GEN-LAST:event_btnGuardarProveedorActionPerformed

    private void btnLimpiarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarProveedorActionPerformed
        limpiarProveedor();
    }//GEN-LAST:event_btnLimpiarProveedorActionPerformed

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed
        // Verifica que el campo ID no este vacío
        if (!"".equals(txtIdProveedor.getText())) {
            // Mensaje de confirmación para eliminar el proveedor
            int pregunta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar el proveedor?", "Eliminar Proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (pregunta == 0) {
                int id = Integer.parseInt(txtIdProveedor.getText());
                // Elimina el proveedor de la base de datos
                proveedor.EliminarProveedor(id);
                limpiarTabla();
                listarProveedor();
                limpiarProveedor();
            }
        }
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed

    private void tablaProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProveedoresMouseClicked
        // Obtiene la fila seleccionada en la tabla
        int fila = tablaProveedores.rowAtPoint(evt.getPoint());
        // Rellena los campos con la información de la tabla de la fila seleccionada
        txtIdProveedor.setText(tablaProveedores.getValueAt(fila, 0).toString());
        txtNIFProveedor.setText(tablaProveedores.getValueAt(fila, 1).toString());
        txtNombreProveedor.setText(tablaProveedores.getValueAt(fila, 2).toString());
        txtTelefonoProveedor.setText(tablaProveedores.getValueAt(fila, 3).toString());
        txtDireccionProveedor.setText(tablaProveedores.getValueAt(fila, 4).toString());
        txtCorreoProveedor.setText(tablaProveedores.getValueAt(fila, 5).toString());
        txtRazonProveedor.setText(tablaProveedores.getValueAt(fila, 6).toString());
    }//GEN-LAST:event_tablaProveedoresMouseClicked

    private void btnActualizarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProveedorActionPerformed
        // Verifica que los campos no esten vacios
        if (!"".equals(txtNIFProveedor.getText()) && !"".equals(txtNombreProveedor.getText()) && !"".equals(txtTelefonoProveedor.getText()) && !"".equals(txtDireccionProveedor.getText()) && !"".equals(txtCorreoProveedor.getText()) && !"".equals(txtRazonProveedor.getText())) {
            if (txtTelefonoProveedor.getText().length() == 9) { // Validar longitud del teléfono
                int telefonopr = Integer.parseInt(txtTelefonoProveedor.getText());
                if (telefonopr <= 999999999) { // Validar que el teléfono no sea mayor a 999999999
                    String correopr = txtCorreoProveedor.getText();
                    if (validarCorreo(correopr)) { // Validar el formato del correo electrónico
                        // Establece los nuevos datos del proveedor
                        pr.setId(Integer.parseInt(txtIdProveedor.getText()));
                        pr.setNif(txtNIFProveedor.getText());
                        pr.setNombre(txtNombreProveedor.getText());
                        pr.setTelefono(telefonopr);
                        pr.setDireccion(txtDireccionProveedor.getText());
                        pr.setCorreo(correopr);
                        pr.setRazon(txtRazonProveedor.getText());
                        //Modifica y actualiza el proveedor
                        proveedor.ModificarProveedor(pr);
                        limpiarTabla();
                        listarProveedor();
                        limpiarProveedor();
                        JOptionPane.showMessageDialog(null, "Proveedor modificado correctamente");
                    } else {
                        JOptionPane.showMessageDialog(null, "El formato del correo electrónico es inválido");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El número de teléfono no puede ser mayor a 999999999");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El número de teléfono debe tener 9 dígitos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos");
        }
    }//GEN-LAST:event_btnActualizarProveedorActionPerformed

    private void btnGuardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductoActionPerformed
        // Verifica que los campos no estén vacíos
        if (!"".equals(txtCodigoProducto.getText()) && !"".equals(txtNombreProducto.getText()) && !"".equals(txtCantidadProducto.getText()) && cbxProveedorProducto.getSelectedItem() != null && !"".equals(txtPrecioProducto.getText())) {
            pro.setCodigo(txtCodigoProducto.getText());
            pro.setNombre(txtNombreProducto.getText());
            // Obtiene el proveedor seleccionado del JComboBox
            String proveedorSeleccionado = cbxProveedorProducto.getSelectedItem().toString();
            // Asignar el ID del proveedor al producto
            int proveedorId = producto.obtenerIdProveedor(proveedorSeleccionado);
            pro.setProveedor(proveedorId);
            pro.setStock(Integer.parseInt(txtCantidadProducto.getText()));
            pro.setPrecio(Double.parseDouble(txtPrecioProducto.getText()));
            // Registra el producto en la base de datos
            if (producto.RegistarProducto(pro)) {
                limpiarTabla();
                listarProductos();
                limpiarProducto();
                JOptionPane.showMessageDialog(null, "Producto registrado correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el producto");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos");
        }
    }//GEN-LAST:event_btnGuardarProductoActionPerformed

    private void btnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoActionPerformed
        // Verifica que el campo ID no este vacío
        if (!"".equals(txtIdProducto.getText())) {
            // Mensaje de confirmación para eliminar el producto
            int pregunta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar el producto?", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (pregunta == 0) {
                int id = Integer.parseInt(txtIdProducto.getText());
                // Elimina el producto de la base de datos
                producto.EliminarProducto(id);
                limpiarTabla();
                listarProductos();
                limpiarProducto();
            }
        }
    }//GEN-LAST:event_btnEliminarProductoActionPerformed

    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked
        // Obtiene la fila seleccionada en la tabla
        int fila = tablaProductos.rowAtPoint(evt.getPoint());
        // Rellena los campos con la información de la tabla de la fila seleccionada
        String id = tablaProductos.getValueAt(fila, 0).toString();
        String codigo = tablaProductos.getValueAt(fila, 1).toString();
        String nombre = tablaProductos.getValueAt(fila, 2).toString();
        String cantidad = tablaProductos.getValueAt(fila, 3).toString();
        String precio = tablaProductos.getValueAt(fila, 4).toString();
        String proveedorId = tablaProductos.getValueAt(fila, 5).toString(); // ID del proveedor
        // Establece los datos en los campos correspondientes
        txtIdProducto.setText(id);
        txtCodigoProducto.setText(codigo);
        txtNombreProducto.setText(nombre);
        txtCantidadProducto.setText(cantidad);
        txtPrecioProducto.setText(precio);
        // Obtiene el nombre del proveedor basado en su ID
        String proveedorNombre = producto.obtenerNombreProveedor(proveedorId);
        // Establece el nombre del proveedor en el JComboBox
        cbxProveedorProducto.setSelectedItem(proveedorNombre);
    }//GEN-LAST:event_tablaProductosMouseClicked

    private void btnActualizarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProductoActionPerformed
        // Verifica que los campos no esten vacíos
        if (!"".equals(txtCodigoProducto.getText()) || !"".equals(txtNombreProducto.getText()) || !"".equals(txtCantidadProducto.getText()) || cbxProveedorProducto.getSelectedItem() != null || !"".equals(txtPrecioProducto.getText())) {
            // Verifica que el campo ID no este vacío y si lo esta salta un mensaje
            if ("".equals(txtIdProducto.getText())) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto.");
            } else {
                // Establece los datos del producto
                int id = Integer.parseInt(txtIdProducto.getText());
                String codigo = txtCodigoProducto.getText();
                String nombre = txtNombreProducto.getText();
                int cantidad = Integer.parseInt(txtCantidadProducto.getText());
                double precio = Double.parseDouble(txtPrecioProducto.getText());

                pro.setId(id);
                pro.setCodigo(codigo);
                pro.setNombre(nombre);
                pro.setStock(cantidad);
                pro.setPrecio(precio);
                // Obtiene el proveedor seleccionado
                String proveedorSeleccionado = cbxProveedorProducto.getSelectedItem().toString();
                // Obtiene el proveedor actual
                String proveedorActual = producto.obtenerProveedorActual(id);
                //Verifica que el proveedor no haya cambiado
                if (!proveedorActual.equals(proveedorSeleccionado)) {
                    JOptionPane.showMessageDialog(null, "No puedes cambiar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Modifica el producto en la base de datos
                if (producto.ModificarProductos(pro)) {
                    JOptionPane.showMessageDialog(null, "Producto modificado correctamente.");
                    limpiarTabla();
                    listarProductos();
                    limpiarProducto();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar el producto.");
                }
            }
        }
    }//GEN-LAST:event_btnActualizarProductoActionPerformed

    private void btnExportarExcelProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelProductosActionPerformed
        // Exporta los datos a excel
        Excel.reporte();
    }//GEN-LAST:event_btnExportarExcelProductosActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked

    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void btnVentaNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentaNuevaActionPerformed
        limpiarTabla();
        jTabbedPane1.setSelectedIndex(0);//Selecciona la pestaña con el índice 0 y lo muestra
    }//GEN-LAST:event_btnVentaNuevaActionPerformed

    private void btnLimpiarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarProductoActionPerformed
        limpiarProducto();
    }//GEN-LAST:event_btnLimpiarProductoActionPerformed

    private void txtCodigoVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoVentaKeyPressed
        // Al presionar la tecla enter
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            // Comprueba que el código de venta no este vacío
            if (!"".equals(txtCodigoVenta.getText())) {
                String cod = txtCodigoVenta.getText();// Obtiene el código de venta para almacenarlo en la variable cod
                //Busca el producto por el código
                pro = producto.BuscarProductos(cod);
                // Si encuentra el nombre, rellena los campos con los datos del producto 
                if (pro.getNombre() != null) {
                    txtDescripcionVenta.setText("" + pro.getNombre());
                    txtPrecioVentas.setText("" + pro.getPrecio());
                    txtStockDisponibleVentas.setText("" + pro.getStock());
                    txtCantidadVentas.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
                } else {
                    limpiarVenta();
                    txtCodigoVenta.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
                    JOptionPane.showMessageDialog(null, "Código no encontrado.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ingrese el código de producto.");
                txtCodigoVenta.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
            }
        }
    }//GEN-LAST:event_txtCodigoVentaKeyPressed

    private void txtCantidadVentasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadVentasKeyPressed
        // Al presionar la tecla enter
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            AddCantidadVenta();
        }
    }//GEN-LAST:event_txtCantidadVentasKeyPressed

    private void btnEliminarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVentaActionPerformed
        // Verifica si hay productos añadidos a la tabla de ventas
        if (tablaVenta.getRowCount() > 0) {
            // Obtiene el índice de la fila seleccionada en la tabla
            int selectedRow = tablaVenta.getSelectedRow();
            // Verifica si se ha seleccionado una fila
            if (selectedRow != -1) {
                // Obtiene el modelo de la tabla (permite manipular los datos de la tabla)
                modelo = (DefaultTableModel) tablaVenta.getModel();
                // Elimina la fila seleccionada del modelo de la tabla
                modelo.removeRow(selectedRow);
                TotalPagar();
                txtCodigoVenta.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay productos añadidos a la venta.");
        }
    }//GEN-LAST:event_btnEliminarVentaActionPerformed

    private void btnBuscarCodigoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCodigoVentaActionPerformed
        // Comprueba que el código de venta no este vacío
        if (!"".equals(txtCodigoVenta.getText())) {
            String cod = txtCodigoVenta.getText(); // Obtiene el código de venta para almacenarlo en la variable cod
            //Busca el producto por el código
            pro = producto.BuscarProductos(cod);
            // Si encuentra el nombre, rellena los campos con los datos del producto
            if (pro.getNombre() != null) {
                txtDescripcionVenta.setText("" + pro.getNombre());
                txtPrecioVentas.setText("" + pro.getPrecio());
                txtStockDisponibleVentas.setText("" + pro.getStock());
                txtCantidadVentas.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
            } else {
                limpiarVenta();
                txtCodigoVenta.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
                JOptionPane.showMessageDialog(null, "Código no encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese el código de producto.");
            txtCodigoVenta.requestFocus();// Hace que el campo se ponga automaticamente para escribir en él
        }
    }//GEN-LAST:event_btnBuscarCodigoVentaActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        // Mensaje de confirmación para cerrar sesión
        int pregunta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pregunta == 0) {
            Login lg = new Login();
            lg.setVisible(true);// Muestra el formulario de Login
            dispose();// Cierra este formulario
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnBuscarDNIVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarDNIVentaActionPerformed
        buscarDNI();
    }//GEN-LAST:event_btnBuscarDNIVentaActionPerformed

    private void txtDniNifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniNifKeyPressed
        // Al presionar la tecla enter
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            buscarDNI();
        }
    }//GEN-LAST:event_txtDniNifKeyPressed

    private void btnRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarVentaActionPerformed
        // Si existe algun registro en la tabla, registra la venta
        if (tablaVenta.getRowCount() > 0) {
            // Verifica si el nombre del cliente no está vacío
            if (!"".equals(txtNombreVenta.getText())) {
                RegistrarVentaConDetalles();
                ActualizarStock();
                try {
                    pdf();
                } catch (DocumentException | IOException ex) {
                    Logger.getLogger(SistemaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                limpiarTablaVenta();
                limpiarVenta();
                labelTotal.setText("-------");// Resetea el total del precio
            } else {
                // Muestra un mensaje si no ha seleccionado un cliente
                JOptionPane.showMessageDialog(null, "Seleccione un cliente");
            }
        } else {
            // Muestra un mensaje si no hay producto añadidos en la venta
            JOptionPane.showMessageDialog(null, "No hay productos añadidos a la venta.");
        }
    }//GEN-LAST:event_btnRegistrarVentaActionPerformed

    private void btnInsertarCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarCantidadActionPerformed
        AddCantidadVenta();
    }//GEN-LAST:event_btnInsertarCantidadActionPerformed

    private void txtDniNifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniNifActionPerformed

    }//GEN-LAST:event_txtDniNifActionPerformed

    private void btnActualizarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarConfigActionPerformed
        // Verifica que los campos no esten vacíos
        if (!"".equals(txtNIFConfig.getText()) && !"".equals(txtNombreConfig.getText()) && !"".equals(txtTelefonoConfig.getText()) && !"".equals(txtDireccionConfig.getText()) && !"".equals(txtNIFConfig.getText()) && !"".equals(txtMensajeConfig.getText())) {
            if (txtTelefonoConfig.getText().length() == 9) { // Validar longitud del teléfono
                int telefonoconf = Integer.parseInt(txtTelefonoConfig.getText());
                if (telefonoconf <= 999999999) { // Validar que el teléfono no sea mayor a 999999999
                    // Establece los datos de la configuración
                    config.setId(Integer.parseInt(txtIdConfig.getText()));
                    config.setNif(txtNIFConfig.getText());
                    config.setNombre(txtNombreConfig.getText());
                    config.setTelefono(telefonoconf);
                    config.setDireccion(txtDireccionConfig.getText());
                    config.setMensaje(txtMensajeConfig.getText());
                    // Modifica los datos de configuración
                    producto.ModificarDatos(config);
                    listarConfig();
                    JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "El número de teléfono no puede ser mayor a 999999999.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El número de teléfono debe tener 9 dígitos.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campos vacíos o incorrectos.");
        }
    }//GEN-LAST:event_btnActualizarConfigActionPerformed

    private void txtCantidadVentasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadVentasKeyTyped
        // Valida que solo se ingresen números
        val.soloNumeros(evt);
    }//GEN-LAST:event_txtCantidadVentasKeyTyped

    private void txtPrecioVentasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioVentasKeyTyped
        // Valida que solo se ingresen números y decimales
        val.soloNumerosDecimales(evt, txtPrecioVentas);
    }//GEN-LAST:event_txtPrecioVentasKeyTyped

    private void txtNombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyTyped
        // Valida que solo se ingresen letras
        val.soloTexto(evt);
    }//GEN-LAST:event_txtNombreClienteKeyTyped

    private void txtTelefonoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoClienteKeyTyped
        // Valida que solo se ingresen números
        val.soloNumeros(evt);
    }//GEN-LAST:event_txtTelefonoClienteKeyTyped

    private void txtNombreProveedorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreProveedorKeyTyped
        // Valida que solo se ingresen letras
        val.soloTexto(evt);
    }//GEN-LAST:event_txtNombreProveedorKeyTyped

    private void txtTelefonoProveedorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoProveedorKeyTyped
        // Valida que solo se ingresen números
        val.soloNumeros(evt);
    }//GEN-LAST:event_txtTelefonoProveedorKeyTyped

    private void txtCantidadProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadProductoKeyTyped
        // Valida que solo se ingresen números
        val.soloNumeros(evt);
    }//GEN-LAST:event_txtCantidadProductoKeyTyped

    private void txtPrecioProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioProductoKeyTyped
        // Valida que solo se ingresen números y decimales
        val.soloNumerosDecimales(evt, txtPrecioProducto);
    }//GEN-LAST:event_txtPrecioProductoKeyTyped

    private void txtNombreConfigKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreConfigKeyTyped
        // Valida que solo se ingresen letras
        val.soloTexto(evt);
    }//GEN-LAST:event_txtNombreConfigKeyTyped

    private void txtTelefonoConfigKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoConfigKeyTyped
        // Valida que solo se ingresen números
        val.soloNumeros(evt);
    }//GEN-LAST:event_txtTelefonoConfigKeyTyped

    private void tablaVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaVentasMouseClicked
        // Obtiene la fila seleccionada al hacer click en la tabla
        int fila = tablaVentas.rowAtPoint(evt.getPoint());
        // Rellena el campo ID con el valor del registro
        txtIdVentas.setText((tablaVentas.getValueAt(fila, 0).toString()));
    }//GEN-LAST:event_tablaVentasMouseClicked

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        try {
            // Obtiene el ID de la venta y lo almacena en la variable id
            int id = Integer.parseInt(txtIdVentas.getText());
            //Abre el documento obteniendo el directorio de documentos del usuario
            String userHome = System.getProperty("user.home");
            String ruta = userHome + File.separator + "Documents" + File.separator + "venta" + id + ".pdf";
            File file = new File(ruta);
            Desktop.getDesktop().open(file);// Abre el archivo PDF
        } catch (IOException e) {
        }
    }//GEN-LAST:event_btnPDFActionPerformed

    private void btnDarAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDarAltaActionPerformed
        Registro registro = new Registro();// Crea una nueva instancia de la ventana de Registro
        registro.setVisible(true);// Muestra el formulario de Registro
    }//GEN-LAST:event_btnDarAltaActionPerformed

    private void txtBuscarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyReleased
        buscarCliente(txtBuscarCliente.getText());

    }//GEN-LAST:event_txtBuscarClienteKeyReleased

    private void txtBuscarProveedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProveedorKeyReleased
        buscarProveedor(txtBuscarProveedor.getText());
    }//GEN-LAST:event_txtBuscarProveedorKeyReleased

    private void txtDireccionCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionCVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionCVActionPerformed

    private void txtBuscarProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyReleased
        buscarProducto(txtBuscarProducto.getText());
    }//GEN-LAST:event_txtBuscarProductoKeyReleased

    private void btnExportarExcelClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelClientesActionPerformed
        Excel2.reporte();
    }//GEN-LAST:event_btnExportarExcelClientesActionPerformed

    private void btnExportarExcelProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelProveedoresActionPerformed
        Excel3.reporte();
    }//GEN-LAST:event_btnExportarExcelProveedoresActionPerformed

    private void btnFormInsertarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFormInsertarProductosActionPerformed
        jTabbedPane1.setSelectedIndex(2);// Selecciona la pestaña con el índice 2 y lo muestra
        InsertarProductos ip = new InsertarProductos();
        ip.setVisible(true);
    }//GEN-LAST:event_btnFormInsertarProductosActionPerformed

    private boolean validarCorreo(String correo) {
        // Expresión regular para validar un correo electrónico
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // Compila la expresión regular en un patrón
        Pattern pattern = Pattern.compile(regex);
        // Compara el correo dado con el patrón
        Matcher matcher = pattern.matcher(correo);
        // Retorna true si el correo coincide con el patrón, de lo contrario false
        return matcher.matches();
    }

    private void TotalPagar() {
        totalPagar = 0.00;// Variable del total
        // Obtiene el número de filas en la tabla
        int numFila = tablaVenta.getRowCount();
        // Recorre las filas que haya para obtener los precios e ir sumandolos
        for (int i = 0; i < numFila; i++) {
            double cal = Double.parseDouble(String.valueOf(tablaVenta.getModel().getValueAt(i, 4)));
            totalPagar = totalPagar + cal;
        }
        labelTotal.setText(String.format("%.2f", totalPagar));// Muestra el total en un label
    }

    /*private void RegistrarVenta() {
        int cliente = Integer.parseInt(txtIdClienteCV.getText());
        String vendedor = "Joel Andrés Rivas Galán";
        double tot = totalPagar;
        v.setCliente(cliente);
        v.setVendedor(vendedor);
        v.setTotal(tot);
        venta.RegistrarVenta(v);
    }*/
    private void RegistrarVentaConDetalles() {
        // Obtiene el ID de cliente, nombre del vendedor y el total a pagar
        int cliente = Integer.parseInt(txtIdClienteCV.getText());
        String vendedor = txtNombreVendedor.getText();
        double tot = totalPagar;
        // Establece los datos en el objeto de venta
        v.setCliente(cliente);
        v.setVendedor(vendedor);
        v.setTotal(tot);

        int idVenta = venta.RegistrarVenta(v); // Registrar la venta y obtener el ID generado

        // Si el ID es mayor a 0, recorre la tabla, obtiene el codigo, cantidad y precio, luego registra la venta
        if (idVenta > 0) {
            for (int i = 0; i < tablaVenta.getRowCount(); i++) {
                String cod = tablaVenta.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());
                double precio = Double.parseDouble(tablaVenta.getValueAt(i, 3).toString());
                // Establece los datos para el objeto de detalles de venta
                dv.setCod_producto(cod);
                dv.setCantidad(cantidad);
                dv.setPrecio(precio);
                dv.setId_venta(idVenta);
                // Registra los detalles de la venta
                venta.RegistrarDetalle(dv);
            }
        } else {
            System.out.println("Error al registrar la venta");
        }
    }

    private void AddCantidadVenta() {
        // Verifica que se haya introducido una cantidad
        if (!"".equals(txtCantidadVentas.getText())) {
            // Obtiene el código, la descripición, cantidad, precio, total y el stock, 
            String codigo = txtCodigoVenta.getText();
            String descripcion = txtDescripcionVenta.getText();
            int cantidad = Integer.parseInt(txtCantidadVentas.getText());
            double precio = Double.parseDouble(txtPrecioVentas.getText());
            double total = cantidad * precio;
            int stock = Integer.parseInt(txtStockDisponibleVentas.getText());
            // Verifica que haya suficiente stock
            if (stock >= cantidad) {
                if (cantidad > 0) {
                    item = item + 1;
                    modelo2 = (DefaultTableModel) tablaVenta.getModel();
                    // Verifica si el producto ya esta registrado
                    for (int i = 0; i < tablaVenta.getRowCount(); i++) {
                        if (tablaVenta.getValueAt(i, 1).equals(txtDescripcionVenta.getText())) {
                            JOptionPane.showMessageDialog(null, "El producto ya esta registrado.");
                            return;
                        }
                    }
                    // Lista con los datos del producto
                    ArrayList lista = new ArrayList();
                    lista.add(item);
                    lista.add(codigo);
                    lista.add(descripcion);
                    lista.add(cantidad);
                    lista.add(precio);
                    lista.add(total);
                    // Un array de objetos con los datos del producto, luego los añade a la tabla
                    Object[] O = new Object[5];
                    O[0] = lista.get(1);
                    O[1] = lista.get(2);
                    O[2] = lista.get(3);
                    O[3] = lista.get(4);
                    O[4] = lista.get(5);
                    modelo2.addRow(O);
                    tablaVenta.setModel(modelo2);
                    // Calcula el total y limpia los campos de venta 
                    TotalPagar();
                    limpiarVenta();
                    txtCodigoVenta.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "La cantidad no puede ser 0");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Stock no disponible");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese Cantidad.");
        }
    }

    private void buscarDNI() {
        // Verifica si se ha ingresado DNI/NIF
        if (!"".equals(txtDniNif.getText())) {
            String dni = txtDniNif.getText();
            cl = cliente.BuscarCliente(dni);// Busca el cliente por DNI/NIF
            // Si encuentra el cliente, llena los campos de venta
            if (cl.getNombre() != null) {
                txtIdClienteCV.setText("" + cl.getId());
                txtNombreVenta.setText("" + cl.getNombre());
                txtTelefonoCV.setText("" + cl.getTelefono());
                txtDireccionCV.setText("" + cl.getDireccion());
                txtRazonCV.setText("" + cl.getRazon());
            } else {
                JOptionPane.showMessageDialog(null, "El cliente no existe.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Introduzca el DNI/NIF del cliente");
        }
    }

    private void ActualizarStock() {
        // Recorre la tabla para obtener los datos para actualizar el stock
        for (int i = 0; i < tablaVenta.getRowCount(); i++) {
            String cod = tablaVenta.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());
            pro = producto.BuscarProductos(cod);
            int stockActual = pro.getStock() - cantidad;
            // Actualiza el stock en la base de datos
            venta.ActualizarStock(stockActual, cod);
        }
    }

    private void pdf() throws DocumentException, IOException {
        try {
            // La ruta de salida para el archivo de venta*.pdf
            int id = venta.IdVenta();// Obtiene el ID de venta
            String userHome = System.getProperty("user.home");// Obtiene el directorio del usuario
            String ruta = userHome + File.separator + "Documents" + File.separator + "venta" + id + ".pdf";// La ruta donde se guardara el archivo
            File file = new File(ruta);
            // Flujo de salida para el archivo
            FileOutputStream archivo = new FileOutputStream(file);
            Document doc = new Document();// Crea un objeto Document
            PdfWriter.getInstance(doc, archivo);// Se vincula el archivo con el doc
            doc.open();// Abre el documento para añadir contenido

            // Obtiene la imagen desde la carpeta ./img
            InputStream imageStream = getClass().getResourceAsStream("/img/Sweetswithlove.png");
            Image img = Image.getInstance(imageStream.readAllBytes());
            img.scaleToFit(100, 100); // Ajusta el tamaño de la imagen

            // Crea la fecha para añadirla al documento 
            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);// Crea una fuente en negrita
            fecha.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            Date date = new Date();
            fecha.add("Factura:" + id + "\n" + "Fecha: " + new SimpleDateFormat("dd-MM-yyyy").format(date) + "\n\n");// Añade el ID al numero de factura y la fecha 

            // Crea el encabezado del documento 
            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);// Establece el ancho del encabezado al 100%
            encabezado.getDefaultCell().setBorder(0);// Elimina los bordes/margenes

            // Array para definir el ancho de las columnas
            float[] columnaEncabezado = new float[]{20f, 30f, 70f, 40f};
            encabezado.setWidths(columnaEncabezado);
            encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);// Alinea la tabla a la izquierda

            // Añade la imagen al encabezado
            encabezado.addCell(img);

            //Obtiene los datos de configuración
            String nif = txtNIFConfig.getText();
            String nombre = txtNombreConfig.getText();
            String telefono = txtTelefonoConfig.getText();
            String direccion = txtDireccionConfig.getText();
            String razon = txtMensajeConfig.getText();

            //Añade la información de la configuración y la fecha al encabezado
            encabezado.addCell("");
            encabezado.addCell("NIF: " + nif + "\nNombre: " + nombre + "\nTeléfono: " + telefono + "\nDirección: " + direccion + "\nMensaje: " + razon);
            encabezado.addCell(fecha);
            doc.add(encabezado);// Añade el encabezado al documento

            // Detalle del cliente y el parrafo nuevo para la información del cliente
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            cli.add("CLIENTE" + "\n\n");
            doc.add(cli);

            // Crea la tabla con la información del cliente
            PdfPTable tablaCli = new PdfPTable(4);
            tablaCli.setWidthPercentage(100);// Establece el ancho del encabezado al 100%
            tablaCli.getDefaultCell().setBorder(0);// Elimina los bordes/margenes

            // Array para definir el ancho de las columnas
            float[] columnaCli = new float[]{20f, 50f, 30f, 40f};
            tablaCli.setWidths(columnaCli);

            // Crea las celdas con los titulos para añadir la información del cliente
            PdfPCell cl1 = new PdfPCell(new Phrase("DNI/NIF", negrita));
            PdfPCell cl2 = new PdfPCell(new Phrase("NOMBRE", negrita));
            PdfPCell cl3 = new PdfPCell(new Phrase("TELÉFONO", negrita));
            PdfPCell cl4 = new PdfPCell(new Phrase("DIRECCIÓN", negrita));
            // Elimina los bordes
            cl1.setBorder(0);
            cl2.setBorder(0);
            cl3.setBorder(0);
            cl4.setBorder(0);
            // Establece el color de fondo en Gris Claro
            cl1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            // Añade las celdas a la tabla
            tablaCli.addCell(cl1);
            tablaCli.addCell(cl2);
            tablaCli.addCell(cl3);
            tablaCli.addCell(cl4);
            // Añade los datos del cliente a la tabla
            tablaCli.addCell(txtDniNif.getText());
            tablaCli.addCell(txtNombreVenta.getText());
            tablaCli.addCell(txtTelefonoCV.getText());
            tablaCli.addCell(txtDireccionCV.getText());
            // Añade la tabla del cliente al documento
            doc.add(tablaCli);

            // Detalles del producto
            Paragraph pro = new Paragraph();
            pro.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            pro.add("INFORMACIÓN DE LOS PRODUCTOS" + "\n\n");
            doc.add(pro);

            // Crea la tabla de detalles
            PdfPTable tablaPro = new PdfPTable(5);
            tablaPro.setWidthPercentage(100);// Establece el ancho del encabezado al 100%
            tablaPro.getDefaultCell().setBorder(0);// Elimina los bordes/margenes
            // Array para definir el ancho de las columnas
            float[] columnaPro = new float[]{15f, 35f, 20f, 15f, 15f};
            tablaPro.setWidths(columnaPro);

            // Crea las celdas con los titulos para añadir la información del producto
            PdfPCell pr1 = new PdfPCell(new Phrase("CANTIDAD", negrita));
            PdfPCell pr2 = new PdfPCell(new Phrase("DESCRIPCIÓN", negrita));
            PdfPCell pr3 = new PdfPCell(new Phrase("PRECIO UNIDAD", negrita));
            PdfPCell pr4 = new PdfPCell(new Phrase("PRECIO TOTAL", negrita));
            PdfPCell pr5 = new PdfPCell(new Phrase("IVA (10%)", negrita));
            // Elimina los bordes
            pr1.setBorder(0);
            pr2.setBorder(0);
            pr3.setBorder(0);
            pr4.setBorder(0);
            pr5.setBorder(0);
            // Establece el color de fondo en Gris Claro
            pr1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pr2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pr3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pr4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pr5.setBackgroundColor(BaseColor.LIGHT_GRAY);
            // Añade las celdas a la tabla
            tablaPro.addCell(pr1);
            tablaPro.addCell(pr2);
            tablaPro.addCell(pr3);
            tablaPro.addCell(pr4);
            tablaPro.addCell(pr5);

            // Inicializa el total de IVA
            double totalIVA = 0;

            // Añade los datos del cliente a la tabla, recorriendo la tabla de venta por cada producto y los añade
            for (int i = 0; i < tablaVenta.getRowCount(); i++) {
                String cantidad = tablaVenta.getValueAt(i, 2).toString();
                String descripcion = tablaVenta.getValueAt(i, 1).toString();
                double precioUnidad = Double.parseDouble(tablaVenta.getValueAt(i, 3).toString());
                double precioTotal = Double.parseDouble(tablaVenta.getValueAt(i, 4).toString());
                double iva = precioTotal * 0.10;
                totalIVA += iva;

                tablaPro.addCell(cantidad);
                tablaPro.addCell(descripcion);
                tablaPro.addCell(String.format("%.2f", precioUnidad));
                tablaPro.addCell(String.format("%.2f", precioTotal));
                tablaPro.addCell(String.format("%.2f", iva));
            }
            // Añade la tabla al documento
            doc.add(tablaPro);

            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            info.add("Total IVA: " + String.format("%.2f", totalIVA) + " €\n");
            info.add("Total a pagar: " + String.format("%.2f", totalPagar + totalIVA) + " €");// Añade una nueva linea con el total a pagar
            info.setAlignment(Element.ALIGN_RIGHT);// Alinea la tabla a la derecha
            doc.add(info);

            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            firma.add("FIRMA\n\n");// Añade una nueva linea para que al imprimir sea el hueco donde firmar
            firma.add("------------------------------");
            firma.setAlignment(Element.ALIGN_CENTER);// Alinea la tabla en el centro
            doc.add(firma);// Añade el hueco de la firma al documento

            Paragraph msj = new Paragraph();
            msj.add(Chunk.NEWLINE);// Añade una nueva linea en el documento
            msj.add("¡Gracias por comprar en nuestra tienda!");
            msj.setAlignment(Element.ALIGN_CENTER);// Alinea la tabla en el centro
            doc.add(msj); // Añade el mensaje de agradecimiento

            // Cierra el documento y el archivo
            doc.close();
            archivo.close();
            // Abre el archivo PDF
            Desktop.getDesktop().open(file);
            JOptionPane.showMessageDialog(null, "PDF generado en: " + ruta);// Muestra la ruta en un mensaje

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el archivo.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    public void buscarCliente(String buscar) {
        BuscarDAO Buscar = new BuscarDAO();
        DefaultTableModel modelo = Buscar.buscarClientes(buscar);
        tablaClientes.setModel(modelo);
    }

    public void buscarProveedor(String buscar) {
        BuscarDAO Buscar = new BuscarDAO();
        DefaultTableModel modelo = Buscar.buscarProveedores(buscar);
        tablaProveedores.setModel(modelo);
    }

    public void buscarProducto(String buscar) {
        BuscarDAO Buscar = new BuscarDAO();
        DefaultTableModel modelo = Buscar.buscarProductos(buscar);
        tablaProductos.setModel(modelo);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SistemaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SistemaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SistemaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SistemaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SistemaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarCliente;
    private javax.swing.JButton btnActualizarConfig;
    private javax.swing.JButton btnActualizarProducto;
    private javax.swing.JButton btnActualizarProveedor;
    private javax.swing.JButton btnBuscarCodigoVenta;
    private javax.swing.JButton btnBuscarDNIVenta;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnConfiguracion;
    private javax.swing.JButton btnDarAlta;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarProducto;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnEliminarVenta;
    private javax.swing.JButton btnExportarExcelClientes;
    private javax.swing.JButton btnExportarExcelProductos;
    private javax.swing.JButton btnExportarExcelProveedores;
    private javax.swing.JButton btnFormInsertarProductos;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnGuardarProducto;
    private javax.swing.JButton btnGuardarProveedor;
    private javax.swing.JButton btnInsertarCantidad;
    private javax.swing.JButton btnLimpiarCliente;
    private javax.swing.JButton btnLimpiarProducto;
    private javax.swing.JButton btnLimpiarProveedor;
    private javax.swing.JButton btnPDF;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnRegistrarVenta;
    private javax.swing.JButton btnVentaNueva;
    private javax.swing.JButton btnVentas;
    private javax.swing.JComboBox<String> cbxProveedorProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTable tablaProveedores;
    private javax.swing.JTable tablaVenta;
    private javax.swing.JTable tablaVentas;
    private javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtBuscarProveedor;
    private javax.swing.JTextField txtCantidadProducto;
    private javax.swing.JTextField txtCantidadVentas;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtCodigoVenta;
    private javax.swing.JTextField txtCorreoCliente;
    private javax.swing.JTextField txtCorreoProveedor;
    private javax.swing.JTextField txtDNICliente;
    private javax.swing.JTextField txtDescripcionVenta;
    private javax.swing.JTextField txtDireccionCV;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtDireccionConfig;
    private javax.swing.JTextField txtDireccionProveedor;
    private javax.swing.JTextField txtDniNif;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdClienteCV;
    private javax.swing.JTextField txtIdConfig;
    private javax.swing.JTextField txtIdProducto;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtIdVentas;
    private javax.swing.JTextField txtMensajeConfig;
    private javax.swing.JTextField txtNIFConfig;
    private javax.swing.JTextField txtNIFProveedor;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreConfig;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtNombreVendedor;
    private javax.swing.JTextField txtNombreVenta;
    private javax.swing.JTextField txtPrecioProducto;
    private javax.swing.JTextField txtPrecioVentas;
    private javax.swing.JTextField txtRazonCV;
    private javax.swing.JTextField txtRazonCliente;
    private javax.swing.JTextField txtRazonProveedor;
    private javax.swing.JTextField txtStockDisponibleVentas;
    private javax.swing.JTextField txtTelefonoCV;
    private javax.swing.JTextField txtTelefonoCliente;
    private javax.swing.JTextField txtTelefonoConfig;
    private javax.swing.JTextField txtTelefonoProveedor;
    // End of variables declaration//GEN-END:variables
}
