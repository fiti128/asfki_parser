package rw.asfki.domain;

public class RemoteFileConfig {
	private String tableTag;
	private String columnsRootTag;
	private String columnTag;
	
	private TableAttributes tableAttributes;
	private ColumnAttributes columnAttributes;
	
	private RemoteFileConfig(String tableTag, TableAttributes tableAttributes,
			String columnRootTag, String columnTag, ColumnAttributes columnAttributes){
		this.tableTag = tableTag;
		this.columnsRootTag = columnRootTag;
		this.columnTag = columnTag;
		this.tableAttributes = tableAttributes;
		this.columnAttributes = columnAttributes;
	}
	
	public static RemoteFileConfig getInstance(String tableTag, TableAttributes tableAttributes,
			String columnRootTag, String columnTag, ColumnAttributes columnAttributes) {
		return new RemoteFileConfig(tableTag, tableAttributes, columnRootTag, columnTag, columnAttributes);
	}
	
	public String getTableTag() {
		return tableTag;
	}
	public void setTableTag(String tableTag) {
		this.tableTag = tableTag;
	}
	public String getColumnsRootTag() {
		return columnsRootTag;
	}
	public void setColumnsRootTag(String columnsRootTag) {
		this.columnsRootTag = columnsRootTag;
	}
	public String getColumnTag() {
		return columnTag;
	}
	public void setColumnTag(String columnTag) {
		this.columnTag = columnTag;
	}
	public TableAttributes getTableAttributes() {
		return tableAttributes;
	}
	public void setTableAttributes(TableAttributes tableAttributes) {
		this.tableAttributes = tableAttributes;
	}
	public ColumnAttributes getColumnAttributes() {
		return columnAttributes;
	}
	public void setColumnAttributes(ColumnAttributes columnAttributes) {
		this.columnAttributes = columnAttributes;
	}
	
	

	
}
