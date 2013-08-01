package rw.asfki.domain;

import java.sql.Types;

public class Db2Column {

	private String name;
	private int dataType;
	private int size;
	private int decimalDigits;
	private int decimalPrecision;
	private int nullable;
	private int scale;
	
	private int sizeMultiplier;
	

	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public void setDataType(String stringType) {
			if (stringType.equalsIgnoreCase("INTEGER")) {
				this.dataType = Types.INTEGER;
			}
			if (stringType.equalsIgnoreCase("VARCHAR")) {
				this.dataType = Types.VARCHAR;
			}
			if (stringType.equalsIgnoreCase("CHAR")) {
				this.dataType = Types.CHAR;
			}
			if (stringType.equalsIgnoreCase("DATE")) {
				this.dataType = Types.DATE;
			}
			if (stringType.equalsIgnoreCase("TIMESTAMP")) {
				this.dataType = Types.TIMESTAMP;
			}
			if (stringType.equalsIgnoreCase("DECIMAL")) {
				this.dataType = Types.DECIMAL;
			}
			if (stringType.equalsIgnoreCase("SMALLINT")) {
				this.dataType = Types.SMALLINT;
			}
		}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getDecimalDigits() {
		return decimalDigits;
	}
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public int getDecimalPrecision() {
		return decimalPrecision;
	}
	public void setDecimalPrecision(int decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public int getNullable() {
		return nullable;
	}
	public void setNullable(int nullable) {
		this.nullable = nullable;
	}
	public int getSizeMultiplier() {
		return sizeMultiplier;
	}
	public void setSizeMultiplier(int sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dataType;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Db2Column other = (Db2Column) obj;
		if (dataType != other.dataType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		int columnSize = (sizeMultiplier == 0) ? size : size * sizeMultiplier;
		String nullableString = (nullable == 1) ? "": " not null";

//		nullableString = "";
		String printDetails = "";
		
		switch(dataType) {
			case Types.INTEGER: 
								printDetails = "INTEGER";
								break;
			case Types.DATE: 
								printDetails = "DATE";
								break;
			case Types.TIMESTAMP: 
								printDetails = "TIMESTAMP";
								break;
			case Types.SMALLINT: 
								printDetails = "SMALLINT";
								break;
				
									
			case Types.CHAR: 
								columnSize = (columnSize > 254) ? 254 : columnSize;
								printDetails = new StringBuilder().
									append("CHAR(").
									append(columnSize).
									append(")").toString();
								if (nullableString.length() > 1) {
									// TODO Db2 Load ignores 'with default'. It works only with inserts
//									nullableString = nullableString + " WITH DEFAULT ''";
									nullableString = "";
								}
								break;
			case Types.VARCHAR: 
								printDetails = new StringBuilder().
								append("VARCHAR(").
								append(columnSize).
								append(")").toString();
								if (nullableString.length() > 1) {
									// TODO Db2 Load ignores 'with default'. It works only with inserts
//									nullableString = nullableString + " WITH DEFAULT ''";
									nullableString = "";
								}
								break;
			case Types.DECIMAL: 
								printDetails = new StringBuilder().
								append("DECIMAL(").
								append(decimalPrecision).
								append(",").
								append(decimalDigits).
								append(")").toString();
								break;
			default:
								break;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" ").append(printDetails).append(nullableString);
		
		return sb.toString();
	}
	
	
	

	
}
