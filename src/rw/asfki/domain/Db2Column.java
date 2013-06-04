package rw.asfki.domain;

import java.sql.Types;

public class Db2Column {

	private String name;
	private int dataType;
	private int size;
	private int decimalDigits;
	private int nullable;
	
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
	public String toString() {
		int columnSize = (sizeMultiplier == 0) ? size : size * sizeMultiplier;
		String nullableString = (nullable == 1) ? "": " not null";
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
								printDetails = new StringBuilder().
									append("CHAR(").
									append(columnSize).
									append(")").toString();
								break;
			case Types.VARCHAR: 
								printDetails = new StringBuilder().
								append("VARCHAR(").
								append(columnSize).
								append(")").toString();
								break;
			case Types.DECIMAL: 
								printDetails = new StringBuilder().
								append("DECIMAL(").
								append(decimalDigits).
								append(")").toString();
								break;
			default:
								break;
		}
		printDetails = printDetails + nullableString;
		
		return printDetails;
	}
	
	

	
}
