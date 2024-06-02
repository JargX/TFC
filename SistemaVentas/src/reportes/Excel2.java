package reportes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.conexion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author joelr
 *
 * Clase para generar reportes en formato Excel utilizando Apache POI.
 */
public class Excel2 {

    /**
     * Método estático para generar un reporte de clientes en un archivo Excel.
     */
    public static void reporte() {
        Workbook book = new XSSFWorkbook(); // Crear un libro de trabajo Excel
        Sheet sheet = book.createSheet("Clientes"); // Crear una hoja llamada "Clientes"

        try {
            // Cargar el logo desde los recursos
            InputStream is = Excel.class.getResourceAsStream("/img/Logo150.png");
            if (is == null) {
                throw new IOException("Resource not found: /img/Logo150.png");
            }
            byte[] bytes = IOUtils.toByteArray(is); // Convertir el InputStream a un array de bytes
            int imgIndex = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG); // Agregar la imagen al libro
            is.close();

            CreationHelper help = book.getCreationHelper();
            Drawing<?> draw = sheet.createDrawingPatriarch();

            // Configurar la posición de la imagen en la hoja
            ClientAnchor anchor = help.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(1);
            Picture pict = draw.createPicture(anchor, imgIndex);
            pict.resize(1, 3); // Redimensionar la imagen

            // Estilo para el título del reporte
            CellStyle tituloEstilo = book.createCellStyle();
            tituloEstilo.setAlignment(HorizontalAlignment.CENTER);
            tituloEstilo.setVerticalAlignment(VerticalAlignment.CENTER);
            Font fuenteTitulo = book.createFont();
            fuenteTitulo.setFontName("Arial");
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            tituloEstilo.setFont(fuenteTitulo);

            // Crear fila y celda para el título
            Row filaTitulo = sheet.createRow(1);
            Cell celdaTitulo = filaTitulo.createCell(1);
            celdaTitulo.setCellStyle(tituloEstilo);
            celdaTitulo.setCellValue("Reporte de Clientes");

            // Unir celdas para el título
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 3));

            // Encabezados de las columnas
            String[] cabecera = new String[]{"DNI", "Nombre", "Teléfono", "Dirección", "Correo", "Razón"};

            // Estilo para los encabezados
            CellStyle headerStyle = book.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            Font font = book.createFont();
            font.setFontName("Arial");
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 12);
            headerStyle.setFont(font);

            // Crear fila y celdas para los encabezados
            Row filaEncabezados = sheet.createRow(4);

            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEncabezado = filaEncabezados.createCell(i);
                celdaEncabezado.setCellStyle(headerStyle);
                celdaEncabezado.setCellValue(cabecera[i]);
            }

            // Conectar a la base de datos y obtener los datos de los clientes
            conexion con = new conexion();
            PreparedStatement ps;
            ResultSet rs;
            Connection conn = con.getConnection();

            int numFilaDatos = 5; // Fila inicial para los datos

            // Estilo para las celdas de datos
            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderBottom(BorderStyle.THIN);

            ps = conn.prepareStatement("SELECT dni, nombre, telefono, direccion, correo, razon FROM clientes");
            rs = ps.executeQuery();

            int numCol = rs.getMetaData().getColumnCount();

            // Rellenar las filas con los datos obtenidos de la base de datos
            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);

                for (int a = 0; a < numCol; a++) {
                    Cell celdaDatos = filaDatos.createCell(a);
                    celdaDatos.setCellStyle(datosEstilo);
                    celdaDatos.setCellValue(rs.getString(a + 1));
                }

                numFilaDatos++;
            }

            // Ajustar el tamaño de las columnas automáticamente
            for (int i = 0; i < cabecera.length; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.setZoom(150); // Ajustar el zoom de la hoja

            // Definir el nombre y la ruta del archivo
            String fileName = "reporteClientes";
            String home = System.getProperty("user.home");
            File file = new File(home + "/Downloads/" + fileName + ".xlsx");

            // Guardar el libro de trabajo en un archivo
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                book.write(fileOut);
            }

            // Abrir el archivo automáticamente
            Desktop.getDesktop().open(file);

            // Mostrar un mensaje de éxito
            JOptionPane.showMessageDialog(null, "Reporte Generado");

        } catch (IOException | SQLException ex) {
            // Registrar cualquier excepción que ocurra
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
