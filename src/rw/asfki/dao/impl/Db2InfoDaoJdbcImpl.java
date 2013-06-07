package rw.asfki.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import rw.asfki.dao.InfoDao;
import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2Table;

public class Db2InfoDaoJdbcImpl implements InfoDao {
	private DataSource dataSource;
	
	private Db2InfoDaoJdbcImpl(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public static InfoDao getInstance(DataSource dataSource) {
		return new Db2InfoDaoJdbcImpl(dataSource);
	}

	@Override
	public void updateTablesMetaData(List<Db2Table> db2TablesList) throws SQLException {
		Connection connection = dataSource.getConnection();
		DatabaseMetaData md = connection.getMetaData();
		System.out.println("Connected");
		ResultSet columnsResultSet = md.getColumns(
				null, db2TablesList.get(0).getSchema(), null , null);
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
		connection.close();
		
	}

}
