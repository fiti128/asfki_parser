package rw.asfki.dao.impl;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import rw.asfki.dao.InfoDao;
import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2Table;

public class Db2InfoDaoJdbcImpl implements InfoDao {
	protected Logger logger = Logger.getLogger(Db2InfoDaoJdbcImpl.class);
	private Connection connection;
	
	private Db2InfoDaoJdbcImpl(Connection connection) {
		super();
		this.connection = connection;
	}

	public static InfoDao getInstance(Connection connection) {
		return new Db2InfoDaoJdbcImpl(connection);
	}
	
	
	
	@Override
	public void updateTablesMetaData(List<Db2Table> db2TablesList) throws SQLException {
		logger.info("Getting local Meta Data");
		DatabaseMetaData md = connection.getMetaData();
		String schema = db2TablesList.get(0).getSchema();
		ResultSet columnsResultSet;
		
			columnsResultSet = md.getColumns(
					null, schema, null , null);
		while(columnsResultSet.next()) {
			String tableName = columnsResultSet.getString("TABLE_NAME");
			
			for (Db2Table db2Table : db2TablesList) {
				if (tableName.equals(db2Table.getName())) {
					Db2Column db2Column = new Db2Column();
					db2Column.setDataType(columnsResultSet.getInt("DATA_TYPE"));
					db2Column.setDecimalDigits(columnsResultSet.getInt("DECIMAL_DIGITS"));
					db2Column.setName(columnsResultSet.getString("COLUMN_NAME"));
					db2Column.setNullable(columnsResultSet.getInt("NULLABLE"));
					if (db2Column.getDataType() == Types.CHAR || db2Column.getDataType() == Types.VARCHAR) {
						db2Column.setSize(columnsResultSet.getInt("COLUMN_SIZE"));
					}
					db2Table.getColumns().add(db2Column);
				}
			}
		}
		
		logger.info("Local Meta Data info updated");
	}

	@Override
	public void createTable(Db2Table db2Table) throws SQLException {
		// Создаем строку команды
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(db2Table)
			.append(" (");
		List<Db2Column> list = db2Table.getColumns();
		for (Db2Column db2Column : list) {
			sb.append(db2Column);
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(")");
		String createTableCommand = sb.toString();

		// Создаем строку удаления
		sb.setLength(0);
		String dropTableCommand = sb.append("DROP TABLE ").append(db2Table).toString();
		
		Statement statement = connection.createStatement();
		try {
			statement.execute(dropTableCommand);
		} catch (SQLException e) {
			logger.info(db2Table + "isnt existing");
		}
		statement.clearWarnings();
		try {
			statement.execute(createTableCommand);
		} catch (SQLException e) {
			logger.error("loc message" + e.getLocalizedMessage());
			logger.error("message" + e.getMessage());
			throw e;
		}
		SQLWarning warnings = statement.getWarnings();
		if (warnings !=	null) {
			logger.error("Failed to create table");
			logger.error(warnings.getMessage());
		}
		else {
			logger.info(db2Table + " created");
		}
		statement.close();
		
	}

}
