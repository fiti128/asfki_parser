package rw.asfki.domain;

public class Db2FileLoadProps implements Cloneable, Comparable<Db2FileLoadProps> {
	private String absPathToFile;
	private String delimeter;
	private String absPathToLogFile;
	private String schema;
	private String table;

	public Db2FileLoadProps(){}
	
	@Override
	public Db2FileLoadProps clone() throws CloneNotSupportedException {
		Db2FileLoadProps df = (Db2FileLoadProps) super.clone();
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
	public int compareTo(Db2FileLoadProps o) {
		return this.table.compareTo(o.getTable());
	}



}
