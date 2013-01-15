package ru.retbansk.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Main domain class. Root for all xml files
 * @author ircm_yanusheusky
 * @since 15.01.2013
 */
@XmlRootElement(name="table")
public class XmlRoot {
	private Header header;
	private List<Row> rowsList;
	
	public Header getHeader() {
		return header;
	}
	@XmlElement(name="head")
	public void setHeader(Header header) {
		this.header = header;
	}
	public List<Row> getRowsList() {
		return rowsList;
	}
	@XmlElement(name="row")
	public void setRowsList(List<Row> rowsList) {
		this.rowsList = rowsList;
	}
	
	
}
