package rw.asfki.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

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
