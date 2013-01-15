package ru.retbansk.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
