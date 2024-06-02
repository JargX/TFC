package modelo;

import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author joelr
 * 
 * Clase para realizar validaciones en componentes de interfaz gráfica.
 */
public class Validaciones {
    
    /**
     * Método para permitir solo entrada de texto.
     * @param evt Evento de teclado.
     */
    public void soloTexto(KeyEvent evt) {
        char car = evt.getKeyChar();
        if ((car < 'a' || car > 'z') && (car < 'A' || car > 'Z')
                && (car != (char) KeyEvent.VK_BACK_SPACE) && (car != (char) KeyEvent.VK_SPACE)) {
            evt.consume();
        }
    }
    
    /**
     * Método para permitir solo entrada de números enteros.
     * @param evt Evento de teclado.
     */
    public void soloNumeros(KeyEvent evt) {
        char car = evt.getKeyChar();
        if ((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        }
    }
 
    /**
     * Método para permitir solo entrada de números decimales en un JTextField.
     * @param evt Evento de teclado.
     * @param textField JTextField en el que se realiza la entrada.
     */
    public void soloNumerosDecimales(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();
        if ((car < '0' || car > '9') && textField.getText().contains(".") && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        } else if ((car < '0' || car > '9') && (car != '.') && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        }
    }
}
