package rw.asfki.domain;

public class Db2File implements Cloneable, Comparable<Db2File> {
	private String absPathToFile;
	private String delimeter;
	private String absPathToLogFile;
	private String schema;
	private String table;

	public Db2File(){}
	
	@Override
	public Db2File clone() throws CloneNotSupportedException {
		Db2File df = (Db2File) super.clone();
		return df;
	}
	
	public String getAbsPathToFile() {
		return absPathToFile;
	}

	public void setAbsPathToFile(String absPathToFile) {
		this.absPathToFile = absPathToFile;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public String getAbsPathToLogFile() {
		return absPathToLogFile;
	}

	public void setAbsPathToLogFile(String absPathToLogFile) {
		this.absPathToLogFile = absPathToLogFile;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	@Override
	public int compareTo(Db2File o) {
		return this.table.compareTo(o.getTable());
	}



}
