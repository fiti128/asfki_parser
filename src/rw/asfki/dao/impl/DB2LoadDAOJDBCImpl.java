/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.dao.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.domain.Db2FileLoadProps;
/**
 * JDBC Реализация интерфейса <code>DB2LoadDAO</code>.
 * <p>
 * Следует приминять с осторожностью, 
 * т.к. данная процедура официально не поддерживается и 
 * подробно не задокументированна. Как минимум известно, что
 * не следует несколькими потоками грузить через лоад в одну таблицу.
 * Применять только с разрешения Вашего руководителя.
 * <p>
 * По методам, см <code>interface DB2LoadDAO</code>
 * @see DB2LoadDAO
 * @author Yanusheusky S.
 * @since 27.02.2013
 *
 */
public class DB2LoadDAOJDBCImpl implements DB2LoadDAO {
	protected static Logger logger = Logger.getLogger("service");
	private DataSource dataSource;
	private static String PREPARE_SQL = "CALL SYSPROC.DB2LOAD (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public DB2LoadDAOJDBCImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void loadFile(String absPathToFile, String delimeter, String absPathToLogFile,
			String schema, String table) throws SQLException {
		logger.info(table + " begin loading");
		String hexDelimeter = toDb2Hex(delimeter);
		Connection c = dataSource.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
			.append(" OF DEL modified by nochardel coldel").append(hexDelimeter)
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
			delimeter = toDb2Hex(delimeter);
			String absPathToLogFile = props.getAbsPathToLogFile();
			String schema = props.getSchema();
			String table = props.getTable();
			StringBuilder sb = new StringBuilder();
			sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
				.append(" OF DEL modified by nochardel coldel").append(delimeter)
				.append(" MESSAGES ").append("\"").append(absPathToLogFile).append("\"")
				.append(" REPLACE INTO ").append(schema).append(".").append(table);
			String loadCommand = sb.toString();
			pstm.setString(3, loadCommand);
			logger.info(table + " start loading");
			SQLWarning warning = pstm.getWarnings();
			if (warning !=null) System.out.println("Before execute warning");
			boolean isProcesed = pstm.execute();
			if (!isProcesed) System.out.println("Boom at" + table);
			warning = pstm.getWarnings();
			
		    while (warning != null) {
		        System.out.println("Message: " + warning.getMessage());
		        System.out.println("SQLState: " + warning.getSQLState());
		        System.out.print("Vendor error code: ");
		        System.out.println(warning.getErrorCode());
		        System.out.println("");
		        warning = warning.getNextWarning();
		    }
			logger.info(table + " end loading");
			
			
		}
		pstm.close();
		c.close();
		logger.info("Connection closed");
	}

	private String toDb2Hex(String delimeter) {
			String db2HexDelimeter = null;
		   try {
			db2HexDelimeter =  String.format("%x", new BigInteger(delimeter.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			logger.error("Не смог преобразовать в хекс");
		}
		   db2HexDelimeter = "0x" + db2HexDelimeter;
		return db2HexDelimeter;
	}
	
}
