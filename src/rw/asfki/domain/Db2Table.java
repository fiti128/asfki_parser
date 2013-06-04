package rw.asfki.domain;

import java.util.List;

public class Db2Table {
	
	private String name;
	private String schema;
	private List<Db2Column> columns;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public List<Db2Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Db2Column> columns) {
		this.columns = columns;
	}
	
	@Override
	public String toString() {
	
		return "\"" + schema + "\".\"" + name + "\"";
	}
	
	
}
