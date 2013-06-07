package rw.asfki.sax;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2Table;
import rw.asfki.domain.RemoteFileConfig;

public class TableMetaDataRetriever extends DefaultHandler {
	protected static Logger logger = Logger.getLogger("Service");
	private List<Db2Table> tableList;
	private RemoteFileConfig config;
	private String string;
	private Db2Table db2Table;
	private Db2Column db2Column;
	private ArrayList<Db2Column> columnlist;
	
	private TableMetaDataRetriever(List<Db2Table> tableList, RemoteFileConfig config) {
		this.tableList = tableList;
		this.config = config;
	}
	
	public static TableMetaDataRetriever getInstance(List<Db2Table> tableList, RemoteFileConfig config) {
		return new TableMetaDataRetriever(tableList,config);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase(config.getTableTag())) {
			db2Table = new Db2Table();
			db2Table.setName(attributes.getValue(
					config.getTableAttributes().
					getTableNameAttribute()));
			db2Table.setSchema(attributes.getValue(
					config.getTableAttributes().
					getSchemaNameAttribute()));
		}
		if (qName.equalsIgnoreCase(config.getColumnsRootTag())) {
			columnlist = new ArrayList<Db2Column>();
		}
		if (qName.equalsIgnoreCase(config.getColumnTag())) {
			
			db2Column = new Db2Column();
			db2Column.setDataType(attributes.getValue(
					config.getColumnAttributes().
					getTypeAttribute()));
			if (db2Column.getDataType() == Types.CHAR || db2Column.getDataType() == Types.VARCHAR) {
				String size = attributes.getValue(
						config.getColumnAttributes().
						getSizeAttribute());
				if (size != null) {
					db2Column.setSize(Integer.valueOf(size).intValue());
				}
			}
			String nullable = attributes.getValue(
					config.getColumnAttributes().
					getNullableAttribute());
			nullable = (nullable == null) ? "0" : nullable;
			db2Column.setNullable(Integer.valueOf(nullable).intValue());
			if (db2Column.getDataType() == Types.DECIMAL) {
				String decimalDigits = attributes.getValue(
						config.getColumnAttributes().
						getDecimalScaleAttribute());
				int decimalDigitsInt = (decimalDigits == null)? 5 : Integer.valueOf(decimalDigits).intValue();
				db2Column.setDecimalDigits(decimalDigitsInt);
				String decimalPrecision = attributes.getValue(
						config.getColumnAttributes().
						getDecimalPrecisionAttribute());
				int decimalPrecisionInt = (decimalPrecision == null)? 15: Integer.valueOf(decimalPrecision).intValue();
				db2Column.setDecimalPrecision(decimalPrecisionInt);
				
			}
			
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase(config.getColumnTag())) {
			if (string != null) {
				db2Column.setName(string);
				columnlist.add(db2Column);
			}
		}
		
		if (qName.equalsIgnoreCase(config.getColumnsRootTag())) {
			db2Table.setColumns(columnlist);
			tableList.add(db2Table);
			throw new ExpectedSaxException("Sax loaded metadata of " + db2Table.getName());
		}
	
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		string = new String(ch, start, length);
	}

}
