package modelo;
/**
 * @author joelr
 * 
 * Clase que maneja un "prompt" (mensaje) que se muestra sobre un componente de texto 
 * cuando el documento del campo de texto está vacío. La propiedad Show se utiliza para 
 * determinar la visibilidad del prompt.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * La clase TextPrompt mostrará un prompt sobre un componente de texto cuando 
 * el Document del campo de texto esté vacío. La propiedad Show se utiliza para 
 * determinar la visibilidad del prompt.
 *
 * La fuente y el color de primer plano del prompt por defecto serán los de las 
 * propiedades del componente de texto padre. Puedes cambiar las propiedades después 
 * de construir la clase.
 */
public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
	/**
	 * ID de serialización por defecto.
	 */
	private static final long serialVersionUID = 1L;

	// Enumeración para definir cuándo se mostrará el prompt
	public enum Show {
		ALWAYS, FOCUS_GAINED, FOCUS_LOST;
	}

	private JTextComponent component; // Componente de texto asociado al prompt
	private Document document; // Documento del componente de texto

	private Show show; // Propiedad que determina cuándo mostrar el prompt
	private boolean showPromptOnce; // Flag para mostrar el prompt solo una vez
	private int focusLost; // Contador de pérdidas de enfoque

	// Constructor que inicializa el prompt con el texto y el componente de texto
	public TextPrompt(String text, JTextComponent component) {
		this(text, component, Show.ALWAYS); // Llama al constructor con Show.ALWAYS por defecto
	}

	// Constructor que inicializa el prompt con el texto, el componente de texto y la propiedad Show
	public TextPrompt(String text, JTextComponent component, Show show) {
		this.component = component; // Asigna el componente de texto
		setShow(show); // Configura la propiedad Show
		document = component.getDocument(); // Obtiene el documento del componente de texto

		setText(text); // Establece el texto del prompt
		setFont(component.getFont()); // Establece la fuente del prompt
		setForeground(Color.gray); // Establece el color de primer plano del prompt
		setHorizontalAlignment(JLabel.LEADING); // Alinea el texto del prompt a la izquierda

		component.addFocusListener(this); // Añade el listener de enfoque al componente
		document.addDocumentListener(this); // Añade el listener de documento al documento

		component.setLayout(new BorderLayout()); // Establece el layout del componente
		component.add(this); // Añade el prompt al componente
		checkForPrompt(); // Comprueba si el prompt debe ser visible
	}

	/**
	 * Método conveniente para cambiar el valor alfa del color de primer plano actual 
	 * al valor especificado.
	 *
	 * @param alpha valor en el rango de 0 - 1.0.
	 */
	public void changeAlpha(float alpha) {
		changeAlpha((int) (alpha * 255)); // Convierte el valor a rango de 0 - 255 y llama al método sobrecargado
	}

	/**
	 * Método conveniente para cambiar el valor alfa del color de primer plano actual 
	 * al valor especificado.
	 *
	 * @param alpha valor en el rango de 0 - 255.
	 */
	public void changeAlpha(int alpha) {
		// Asegura que el valor alfa esté en el rango de 0 - 255
		alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

		Color foreground = getForeground(); // Obtiene el color de primer plano actual
		int red = foreground.getRed(); // Obtiene el valor rojo
		int green = foreground.getGreen(); // Obtiene el valor verde
		int blue = foreground.getBlue(); // Obtiene el valor azul

		Color withAlpha = new Color(red, green, blue, alpha); // Crea un nuevo color con el valor alfa
		super.setForeground(withAlpha); // Establece el color de primer plano con el nuevo valor alfa
	}

	/**
	 * Método conveniente para cambiar el estilo de la fuente actual. 
	 * Los valores de estilo se encuentran en la clase Font. Valores comunes 
	 * podrían ser: Font.BOLD, Font.ITALIC y Font.BOLD + Font.ITALIC.
	 *
	 * @param style valor que representa el nuevo estilo de la fuente.
	 */
	public void changeStyle(int style) {
		setFont(getFont().deriveFont(style)); // Cambia el estilo de la fuente actual
	}

	/**
	 * Obtiene la propiedad Show.
	 *
	 * @return la propiedad Show.
	 */
	public Show getShow() {
		return show;
	}

	/**
	 * Establece la propiedad Show para controlar cuándo se muestra el prompt. 
	 * Valores válidos son:
	 *
	 * Show.ALWAYS (por defecto) - siempre muestra el prompt 
	 * Show.FOCUS_GAINED - muestra el prompt cuando el componente gana enfoque 
	 * (y oculta el prompt cuando se pierde el enfoque) 
	 * Show.FOCUS_LOST - muestra el prompt cuando el componente pierde el enfoque 
	 * (y oculta el prompt cuando se gana el enfoque)
	 *
	 * @param show un valor válido de la enumeración Show.
	 */
	public void setShow(Show show) {
		this.show = show;
	}

	/**
	 * Obtiene la propiedad showPromptOnce.
	 *
	 * @return la propiedad showPromptOnce.
	 */
	public boolean getShowPromptOnce() {
		return showPromptOnce;
	}

	/**
	 * Muestra el prompt una vez. Una vez que el componente ha ganado/perdido el enfoque 
	 * una vez, el prompt no se mostrará nuevamente.
	 *
	 * @param showPromptOnce cuando es true, el prompt solo se mostrará una vez, de lo contrario, 
	 * se mostrará repetidamente.
	 */
	public void setShowPromptOnce(boolean showPromptOnce) {
		this.showPromptOnce = showPromptOnce;
	}

	/**
	 * Comprueba si el prompt debe ser visible o no. La visibilidad cambiará en 
	 * las actualizaciones al Document y en los cambios de enfoque.
	 */
	private void checkForPrompt() {
		// Texto ha sido ingresado, remueve el prompt
		if (document.getLength() > 0) {
			setVisible(false);
			return;
		}

		// El prompt ya se ha mostrado una vez, remuévelo
		if (showPromptOnce && focusLost > 0) {
			setVisible(false);
			return;
		}

		// Comprueba la propiedad Show y el enfoque del componente para determinar si 
		// el prompt debe mostrarse.
		if (component.hasFocus()) {
			if (show == Show.ALWAYS || show == Show.FOCUS_GAINED)
				setVisible(true);
			else
				setVisible(false);
		} else {
			if (show == Show.ALWAYS || show == Show.FOCUS_LOST)
				setVisible(true);
			else
				setVisible(false);
		}
	}

	// Implementa FocusListener

	public void focusGained(FocusEvent e) {
		checkForPrompt(); // Comprueba el prompt cuando el componente gana el enfoque
	}

	public void focusLost(FocusEvent e) {
		focusLost++; // Incrementa el contador de pérdidas de enfoque
		checkForPrompt(); // Comprueba el prompt cuando el componente pierde el enfoque
	}

	// Implementa DocumentListener

	public void insertUpdate(DocumentEvent e) {
		checkForPrompt(); // Comprueba el prompt cuando se inserta texto en el documento
	}

	public void removeUpdate(DocumentEvent e) {
		checkForPrompt(); // Comprueba el prompt cuando se remueve texto del documento
	}

	public void changedUpdate(DocumentEvent e) {
		// No se utiliza, pero es necesario implementar este método al implementar DocumentListener
	}
}
