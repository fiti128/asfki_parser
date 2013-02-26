package rw.asfki.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.domain.Db2FileLoadProps;

public class DB2LoadDAOJDBCImpl implements DB2LoadDAO {
	protected static Logger logger = Logger.getLogger("service");
	private DataSource dataSource;
	private static String PREPARE_SQL = "CALL SYSPROC.DB2LOAD (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int a =	1;
	
	public DB2LoadDAOJDBCImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void loadFile(String absPathToFile, String delimeter, String absPathToLogFile,
			String schema, String table) throws SQLException {
		logger.info(table + " begin loading");
		Connection c = dataSource.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
			.append(" OF DEL modified by nochardel coldel").append(delimeter)
			.append(" MESSAGES ").append("\"").append(absPathToLogFile).append("\"")
			.append(" INSERT INTO ").append(schema).append(".").append(table);
		String loadCommand = sb.toString();
		
		
		CallableStatement pstm = c.prepareCall(PREPARE_SQL);
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
	public void loadFile(Db2FileLoadProps db2File) throws SQLException {
		String absPathToFile = db2File.getAbsPathToFile();
		String delimeter = db2File.getDelimeter();
		String absPathToLogFile = db2File.getAbsPathToLogFile();
		String schema = db2File.getSchema();
		String table = db2File.getTable();
		loadFile(absPathToFile,delimeter,absPathToLogFile,schema,table);
		
	}

	@Override
	public void cleanTables(List<String> cleanList, String schema) throws SQLException{
		StringBuilder sb = new StringBuilder();
		logger.info("Начата очистка таблиц");
		Connection c = dataSource.getConnection();
		Statement stm = c.createStatement();
		for (String table : cleanList) {
			sb.setLength(0);
			sb.append("alter table ").append(schema).append(".")
				.append(table).append(" activate not logged initially with empty table");
			String sql = sb.toString();
			try {
				stm.execute(sql);
				logger.info(schema + "." + table + " очищена");
			} catch (Exception e) {
				logger.info(schema + "." + table + " не найдена");
			}
		}
		stm.close();
		c.close();
		
		
	}

	@Override
	public void loadFromQueue(Queue<Db2FileLoadProps> db2props)
			throws SQLException {
		Connection c = dataSource.getConnection();
		logger.info("Connected");
		CallableStatement pstm = c.prepareCall(PREPARE_SQL);
		pstm.setNull(1, Types.INTEGER);
		pstm.setNull(2, Types.VARCHAR);
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
		
		Db2FileLoadProps db2prop;
		Db2FileLoadProps props;
		Queue<Db2FileLoadProps> props2 = new PriorityBlockingQueue<Db2FileLoadProps>();
		// Создаем очередь, которая не меняется
		while ((db2prop = db2props.poll()) != null) {
			props2.offer(db2prop);
		}
		// И вот только ее и грузим в базу
		while ((props = props2.poll()) != null) {
			String absPathToFile = props.getAbsPathToFile();
			String delimeter = props.getDelimeter();
			String absPathToLogFile = props.getAbsPathToLogFile();
			String schema = props.getSchema();
			String table = props.getTable();
			StringBuilder sb = new StringBuilder();
			sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
				.append(" OF DEL modified by nochardel coldel").append(delimeter)
				.append(" MESSAGES ").append("\"").append(absPathToLogFile).append("\"")
				.append(" INSERT INTO ").append(schema).append(".").append(table);
			String loadCommand = sb.toString();
			pstm.setString(3, loadCommand);
			logger.info(table + " start loading");
			pstm.execute();
			logger.info(table + " end loading");
			
		}
		pstm.close();
		c.close();
		logger.info("Connection closed");
	}
	
}
