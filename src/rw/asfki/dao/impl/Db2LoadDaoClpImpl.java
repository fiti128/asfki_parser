package rw.asfki.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.domain.Db2FileLoadProps;

public class Db2LoadDaoClpImpl implements DB2LoadDAO {
	protected static Logger logger = Logger.getLogger("service");
	private String batchFileName = "db2script.bat";
	private String scriptFileName = "script.db2";
	private ProcessBuilder processBuilder;
	private Properties db2properties;
	
	
	private Db2LoadDaoClpImpl(Properties props) throws IOException {
		this.db2properties = props;
		File batchFile = new File(batchFileName);
		FileWriter fw = new FileWriter(batchFile,false);
		fw.write("db2 +c -tf \"" +scriptFileName+"\"");
		fw.close();
		ProcessBuilder processBuilder = new ProcessBuilder("db2cmd","/w","/c","/i",batchFileName);
		processBuilder.directory(batchFile.getParentFile());
		this.processBuilder = processBuilder;
		
				
	}
	@Override
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFile(Db2FileLoadProps db2File) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanTables(List<String> cleanList, String schema)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		String databaseName = db2properties.getProperty("database");
		String userName = db2properties.getProperty("user");
		String password = db2properties.getProperty("database");
		sb.append("CONNECT TO ").append(databaseName).append(" USER ")
			.append(userName).append(" USING ").append(password).append(";");
		String connectCommand = sb.toString();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(scriptFileName),false));
		bw.write(connectCommand);
		
		
		
		for (String table : cleanList) {
			sb.setLength(0);
			sb.append("alter table ").append(schema).append(".")
				.append(table).append(" activate not logged initially with empty table").append(";");
			String alterTable = sb.toString();
			bw.newLine();
			bw.write(alterTable);
			
				Process process = processBuilder.start();
				int errorlevel = process.exitValue();
				if (errorlevel == 0) {
				 logger.info(schema + "." + table + " очищена");
				} else {
				 logger.info(schema + "." + table + " не найдена");
				}
		}

	}

	@Override
	public void loadFromQueue(Queue<Db2FileLoadProps> db2props)
			throws SQLException, IOException {
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
			File dir = new File(absPathToFile).getParentFile();
			String delimeter = props.getDelimeter();
			delimeter = toDb2Hex(delimeter);
			String absPathToLogFile = props.getAbsPathToLogFile();
			String schema = props.getSchema();
			String table = props.getTable();
			StringBuilder sb = new StringBuilder();
			sb.append("LOAD FROM ").append("\"").append(absPathToFile).append("\"")
				.append(" OF DEL modified by nochardel coldel").append(delimeter)
				.append(" MESSAGES ").append("\"").append(absPathToLogFile).append("\"")
				.append(" INSERT INTO ").append(schema).append(".").append(table).append(";");
			String loadCommand = sb.toString();
			
			sb.setLength(0);
			String databaseName = db2properties.getProperty("database");
			String userName = db2properties.getProperty("user");
			String password = db2properties.getProperty("database");
			sb.append("CONNECT TO ").append(databaseName).append(" USER ")
				.append(userName).append(" USING ").append(password).append(";");
			String connectCommand = sb.toString();
			
			// Перезаписываем load script файл
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(scriptFileName),false));
			bw.write(connectCommand);
			bw.newLine();
			bw.write(loadCommand);
			bw.close();
			
			logger.info(table + " start loading");
			processBuilder.start();
			logger.info(table + " end loading");
			
			
		}

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
	protected void finalize() {
		File batchFile = new File(batchFileName);
		if (batchFile.isFile()) {
			batchFile.delete();
		}
		File scriptFile = new File(scriptFileName);
		if (scriptFile.isFile()) {
			scriptFile.delete();
		}
	}
}
