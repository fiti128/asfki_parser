/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Строка для Jaxb2
 * 
 * @author Yanusheusky S.
 * @since 15.01.2013
 */
@XmlRootElement(name = "row")
public class Row {
	private int rowNum;
	private List<RowColumn> rowColumnList;
	
	public int getRowNum() {
		return rowNum;
	}
	@XmlAttribute(name = "num")
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public List<RowColumn> getRowColumnList() {
		return rowColumnList;
	}
	@XmlElement(name = "col")
	public void setRowColumnList(List<RowColumn> rowColumnList) {
		this.rowColumnList = rowColumnList;
	}
	
	
}
