package rw.asfki.dao.impl;





import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import rw.asfki.UpdateAsfkiFilesJob;
import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.error.ErrorManager;

public class Db2LoadDaoClpImpl implements DB2LoadDAO {
	protected static Logger logger = Logger.getLogger("service");
	private ErrorManager errorManager;
	static int counter = 0;
	private String batchFileName;
	private String scriptFileName;
	private ProcessBuilder processBuilder;
	private Properties db2properties;
	private File tempDir;
	private File batchFile;
	private File scriptFile;
	
	
	
	private Db2LoadDaoClpImpl(Properties props, ErrorManager errorManager) throws IOException {
		this.errorManager = errorManager;
		this.db2properties = props;
		tempDir = new File("temp");
		if (!tempDir.isDirectory()) {
			tempDir.mkdir();
		}
		
//		batchFileName = Integer.toHexString(this.hashCode()) + ".bat";
		scriptFileName = Integer.toHexString(this.hashCode()) +".db2";
		scriptFile = new File(tempDir, scriptFileName);
//		batchFile = new File(tempDir,batchFileName);
//		FileWriter fw = new FileWriter(batchFile, false);
//		fw.write("db2 -t -f " + scriptFileName);
//		fw.close();
		ProcessBuilder processBuilder = new ProcessBuilder("db2.exe","-t","-f", scriptFileName);
		processBuilder.directory(scriptFile.getParentFile());
		this.processBuilder = processBuilder;
			
	}
	
	public static Db2LoadDaoClpImpl getInstance(Properties props, ErrorManager errorManager) throws IOException {
		return new Db2LoadDaoClpImpl(props,errorManager);
	}
	@Override
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException {

	}

	@Override
	public void loadFile(Db2FileLoadProps props) throws SQLException {
		
		String delimeter = props.getDelimeter();
		delimeter = toDb2Hex(delimeter);
		String absPathToLogFolder = props.getAbsPathToLogFolder();
		String schema = props.getSchema();
		String table = props.getTable();
		
		File logFile = new File(absPathToLogFolder, table + "_log.txt");
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"\\\\.\\pipe\\").append(table).append("\"")
			.append(" OF DEL modified by codepage=1208 nochardel coldel").append(delimeter)
			.append(" MESSAGES ").append("\"").append(logFile.getAbsolutePath()).append("\"")
			.append(" REPLACE INTO ").append(schema).append(".").append(table);
		String loadCommand = sb.toString();
		
		ProcessBuilder pb = new ProcessBuilder("db2.exe", loadCommand);
		
		logger.info(table + " start loading\n" + loadCommand);
		try {
			Process process = pb.start();
			int errorlevel = process.waitFor();
			if (errorlevel > 0) {
				logger.error(table + " proccessed with errors");
				errorManager.addErrorFile(logFile);

			} else {
				logger.info(table + " loaded");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int size = UpdateAsfkiFilesJob.LIST_SIZE;
		counter++;
		logger.info("Files remaining: " + (size - counter) + " from " + size);
	}

	@Override
	public void cleanTables(List<String> cleanList, String schema)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		String databaseName = db2properties.getProperty("database");
		String userName = db2properties.getProperty("user");
		String password = db2properties.getProperty("password");
		sb.setLength(0);
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
//			bw.newLine();
			bw.write(alterTable);
			bw.write("commit;");
		}
		bw.close();
		FileInputStream  fis = new FileInputStream(new File(scriptFileName));
		int i;
		while ((i = fis.read()) >= 0) {
			System.out.print((char)i);
		}
		Process process = processBuilder.start();
		process.waitFor();
		System.out.println("");
		logger.info("Список очищен");


	}

	@Override
	public void loadFromQueue(Queue<Db2FileLoadProps> db2props)
			throws SQLException, IOException, InterruptedException {
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
			String absPathToLogFolder = props.getAbsPathToLogFolder();
			String schema = props.getSchema();
			String table = props.getTable();
			
			File logFile = new File(absPathToLogFolder, table + "_log.txt");
			StringBuilder sb = new StringBuilder();
			sb.append("LOAD FROM ").append("\"\\\\.\\pipe\\").append(table).append("1\"")
				.append(" OF DEL modified by codepage=1208 nochardel coldel").append(delimeter)
				.append(" MESSAGES ").append("\"").append(logFile.getAbsolutePath()).append("\"")
				.append(" REPLACE INTO ").append(schema).append(".").append(table).append(";");
			String loadCommand = sb.toString();
			
			String databaseName = db2properties.getProperty("database");
			String userName = db2properties.getProperty("user");
			String password = db2properties.getProperty("password");
			sb.setLength(0);
			sb.append("CONNECT TO ").append(databaseName).append(" USER ")
			.append(userName).append(" USING ").append(password).append(";");
			String connectCommand = sb.toString();

			
			// Перезаписываем load script файл
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFile,false));
//			bw.write(connectCommand);
//			bw.newLine();
			bw.write(loadCommand);
			bw.close();
			
			logger.info(table + " start loading");
			Process process = processBuilder.start();
			int errorlevel = process.waitFor();
			if (errorlevel > 0) {
				logger.error(table + " proccessed with errors");
				errorManager.addErrorFile(logFile);
				
			} else {
				logger.info(table + " loaded");
			}
			int size = UpdateAsfkiFilesJob.LIST_SIZE;
			counter++;
			logger.info("Files remaining: " + (size - counter) + " from " + size);
			
			
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

}
