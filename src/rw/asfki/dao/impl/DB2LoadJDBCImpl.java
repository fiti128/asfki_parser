package rw.asfki.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.dao.DB2Load;
import rw.asfki.domain.Db2File;

public class DB2LoadJDBCImpl implements DB2Load {
	protected static Logger logger = Logger.getLogger("service");
	private DataSource dataSource;
	
	public DB2LoadJDBCImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void loadFile(String absPathToFile, String delimeter, String absPathToLogFile,
			String schema, String table) throws SQLException {
		logger.info(table + " begin loading");
		Connection c = dataSource.getConnection();
		String prepareSQL = "CALL SYSPROC.DB2LOAD (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
			.append(" OF DEL modified by nochardel coldel").append(delimeter)
			.append(" MESSAGES ").append("\"").append(absPathToLogFile).append("\"")
			.append(" INSERT INTO ").append(schema).append(".").append(table);
		String loadCommand = sb.toString();
		
		
		CallableStatement pstm = c.prepareCall(prepareSQL);
		pstm.setNull(1, Types.INTEGER);
		pstm.setNull(2, Types.VARCHAR);
		pstm.setString(3, loadCommand);
		pstm.setNull(4, Types.INTEGER);
		pstm.setNull(5, Types.VARCHAR);
		pstm.setNull(6, Types.BIGINT);
		pstm.setNull(7, Types.BIGINT);
		pstm.setNull(8, Types.BIGINT);
		pstm.setNull(9, Types.BIGINT);
		pstm.setNull(10, Types.BIGINT);
		pstm.setNull(11, Types.BIGINT);
		pstm.setNull(12, Types.BIGINT);
		pstm.setNull(13, Types.BIGINT);
		pstm.setNull(14, Types.BIGINT);
		pstm.setNull(15, Types.VARCHAR);
		pstm.execute();
		pstm.close();
		c.close();
		logger.info(table + " was loaded");
	}

	@Override
	public void loadFile(Db2File db2File) throws SQLException {
		String absPathToFile = db2File.getAbsPathToFile();
		String delimeter = db2File.getDelimeter();
		String absPathToLogFile = db2File.getAbsPathToLogFile();
		String schema = db2File.getSchema();
		String table = db2File.getTable();
		loadFile(absPathToFile,delimeter,absPathToLogFile,schema,table);
		
	}

}
