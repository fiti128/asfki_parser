/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
/**
 * Колонка в строке для Jaxb2
 * @author Yanusheusky S.
 * @since 15.01.2013
 */
@XmlRootElement(name = "col")
public class RowColumn {
	private int rowColumnNum;
	private String body;
	
	public int getRowColumnNum() {
		return rowColumnNum;
	}
	@XmlAttribute(name = "num")
	public void setRowColumnNum(int rowColumnNum) {
		this.rowColumnNum = rowColumnNum;
	}
	public String getBody() {
		return body;
	}
	@XmlValue
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
