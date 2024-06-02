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
     *
     * @param evt Evento de teclado.
     */
    public void soloTexto(KeyEvent evt) {
        char car = evt.getKeyChar(); // Obtiene el caracter escrito
        // Verifica si el carácter no es una letra (minúscula o mayúscula), letra con tilde, espacio o tecla de retroceso
        if (!Character.isLetter(car) && car != ' ' && car != (char) KeyEvent.VK_BACK_SPACE) {
            evt.consume(); // No deja que el caracter sea introducido
        }
    }

    /**
     * Método para permitir solo entrada de números enteros.
     *
     * @param evt Evento de teclado.
     */
    public void soloNumeros(KeyEvent evt) {
        char car = evt.getKeyChar();// Obtiene el caracter escrito
        // Verifica si el carácter no es un número o tecla de retroceso
        if ((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();// No deja que el caracter sea introducido
        }
    }

    /**
     * Método para permitir solo entrada de números decimales en un JTextField.
     *
     * @param evt Evento de teclado.
     * @param textField JTextField en el que se realiza la entrada.
     */
    public void soloNumerosDecimales(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();// Obtiene el caracter escrito
        if ((car < '0' || car > '9') && textField.getText().contains(".") && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();// No deja que el caracter sea introducido
        } else if ((car < '0' || car > '9') && (car != '.') && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();// No deja que el caracter sea introducido
        }
    }
}
