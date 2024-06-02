package modelo;

/**
 *
 * @author joelr
 */
public class Productos {
    public int id;
    public String codigo;
    public String nombre;
    public int proveedor;
    private String proveedorProducto;
    public int stock;
    public double precio;

    public Productos() {
    }

    public Productos(int id, String codigo, String nombre, int proveedor, String provedorProducto, int stock, double precio) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.proveedorProducto = provedorProducto;
        this.stock = stock;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getProveedor() {
        return proveedor;
    }

    public void setProveedor(int proveedor) {
        this.proveedor = proveedor;
    }
    
    public String getProveedorProducto() {
        return proveedorProducto;
    }

    public void setProveedorProducto(String proveedorProducto) {
        this.proveedorProducto = proveedorProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
