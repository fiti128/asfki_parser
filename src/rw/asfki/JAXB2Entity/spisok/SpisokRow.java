/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity.spisok;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Jaxb2 Строка у списка
 * @author Yanusheusky S.
 * @since 27.02.2013
 */
@XmlRootElement(name="row")
public class SpisokRow {
	
	private List<SpisokColumn> column;

	public List<SpisokColumn> getColumn() {
		return column;
	}
	@XmlElement(name="col")
	public void setColumn(List<SpisokColumn> column) {
		this.column = column;
	}
	
}
