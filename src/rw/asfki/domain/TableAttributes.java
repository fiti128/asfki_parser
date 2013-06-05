package rw.asfki.domain;

/**
 * @author Yanusheusky S.
 *
 */
public class TableAttributes {
	private String tableNameAttribute;
	private String schemaNameAttribute;
	
	private TableAttributes(String tableNameAttribute,
			String schemaNameAttribute) {
		this.tableNameAttribute = tableNameAttribute;
		this.schemaNameAttribute = schemaNameAttribute;
	}
	
	public static TableAttributes getInstance(String tableNameAttribute,
			String schemaNameAttribute) {
		return new TableAttributes(tableNameAttribute, schemaNameAttribute);
	}
	
	
	public String getTableNameAttribute() {
		return tableNameAttribute;
	}
	public void setTableNameAttribute(String tableNameAttribute) {
		this.tableNameAttribute = tableNameAttribute;
	}
	public String getSchemaNameAttribute() {
		return schemaNameAttribute;
	}
	public void setSchemaNameAttribute(String schemaNameAttribute) {
		this.schemaNameAttribute = schemaNameAttribute;
	}
	
}
