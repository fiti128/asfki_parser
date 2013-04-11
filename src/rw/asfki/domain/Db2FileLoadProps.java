/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.domain;
/**
 *  Объект класса должен включать в себя всю 
 *  информацию необходимую для загружки
 *  файла в базу данных через процедуру db2load.
 *  Можно клонировать, использовать в Хэш и сортированных таблицах.
 *  
 * @author Yanusheusky S.
 * @since 27.02.2013
 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Db2FileLoadProps other = (Db2FileLoadProps) obj;
		if (absPathToFile == null) {
			if (other.absPathToFile != null)
				return false;
		} else if (!absPathToFile.equals(other.absPathToFile))
			return false;
		if (absPathToLogFile == null) {
			if (other.absPathToLogFile != null)
				return false;
		} else if (!absPathToLogFile.equals(other.absPathToLogFile))
			return false;
		if (delimeter == null) {
			if (other.delimeter != null)
				return false;
		} else if (!delimeter.equals(other.delimeter))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	

}
