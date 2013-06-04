package rw.asfki.sax;

import org.xml.sax.SAXException;

/**
 * Именнованный SAXException. Не более того 
 * @author Yanusheusky S.
 * @since 04.06.2013
 */
public class ExpectedSaxException extends SAXException {
	public ExpectedSaxException() {
		super();
	}
	public ExpectedSaxException(String message) {
		super(message);
	}
	private static final long serialVersionUID = 1L;

}
