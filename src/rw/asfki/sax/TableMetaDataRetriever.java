package rw.asfki.sax;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import rw.asfki.dao.InfoDao;
import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2Table;
import rw.asfki.domain.RemoteFileConfig;
import rw.asfki.error.ErrorManager;

public class TableMetaDataRetriever extends DefaultHandler {
	protected static Logger logger = Logger.getLogger(TableMetaDataRetriever.class);
	private RemoteFileConfig config;
	private String string;
	private Db2Table db2Table;
	private Db2Column db2Column;
	private ArrayList<Db2Column> columnlist;
	private List<Db2Table> localTablesList;
	private InfoDao infoDao;
	private XMLReader xr;
	private DefaultHandler handler;
    private ErrorManager errorManager;
    private String encoding;


    private TableMetaDataRetriever(List<Db2Table> localTablesList, InfoDao infoDao,
                                   RemoteFileConfig config, XMLReader xr, DefaultHandler handler, ErrorManager errorManager, String encoding) {
		this.config = config;
		this.infoDao = infoDao;
		this.xr = xr;
		this.handler = handler;
		this.localTablesList = localTablesList;
        this.errorManager = errorManager;
        this.encoding = encoding;
	}
	
	public static TableMetaDataRetriever getInstance(List<Db2Table> localTablesList,InfoDao infoDao,
                                                     RemoteFileConfig config, XMLReader xr, DefaultHandler handler, ErrorManager errorManager,String encoding) {
		return new TableMetaDataRetriever(localTablesList,infoDao,config,xr,handler,errorManager,encoding);
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
            db2Column.setSizeMultiplier(getSizeMultiplier());
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
                int decimalDigitsInt = (decimalDigits == null) ? 5 : Integer.valueOf(decimalDigits).intValue();
                db2Column.setDecimalDigits(decimalDigitsInt);
                String decimalPrecision = attributes.getValue(
                        config.getColumnAttributes().
                                getDecimalPrecisionAttribute());
                int decimalPrecisionInt = (decimalPrecision == null) ? 15 : Integer.valueOf(decimalPrecision).intValue();
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
			// TODO. Just ignoring schema from XML document.
			db2Table.setSchema(localTablesList.get(0).getSchema());
			db2Table.setTableParams(localTablesList.get(0).getTableParams());
			logger.debug(String.format("Program checks table %s",db2Table.toString()));
			if (!localTablesList.contains(db2Table)) {
				try {
					logger.debug(String.format("There is no equal %s in database.To be created",db2Table.toString()));
					infoDao.createTable(db2Table);
                    errorManager.addMessage(db2Table.toString());
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			else {
				logger.debug(String.format("Same %s exist in database.",db2Table.toString()));
			}
			logger.debug(String.format("Table checking and creation complete"));
			logger.debug("Setting loading handler");
			xr.setContentHandler(handler);
		}
	
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		string = new String(ch, start, length);
	}

    public int getSizeMultiplier() {
        int size = 1;
        if (encoding.equals("UTF-8")) {
            size = 2;
        }
        return size;
    }
}
