/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity.spisok;
/**
 *  Jaxb2 Root для локального списка
 *  @author Yanusheusky S.
 *  @since 27.02.2013
 */
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="table")
public class Root {
	
	private SpisokRow row;

	public SpisokRow getRow() {
		return row;
	}
	@XmlElement(name="row")
	public void setRow(SpisokRow row) {
		this.row = row;
	}
	
}
