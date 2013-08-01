package rw.asfki.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;

import rw.asfki.UpdateAsfkiFilesJob;
import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.domain.Db2Table;
import rw.asfki.error.ErrorManager;

public class Db2LoadDaoClpImpl implements DB2LoadDAO {
	protected static Logger logger = Logger.getLogger(Db2LoadDaoClpImpl.class);
	private ErrorManager errorManager;
	static int counter = 0;
	private File tempDir;
//	private  Db2LoadDaoClpImpl instance;
	
	
	
	private Db2LoadDaoClpImpl(ErrorManager errorManager) throws IOException {
		this.errorManager = errorManager;
		tempDir = new File("temp");
		if (!tempDir.isDirectory()) {
			tempDir.mkdir();
		}
	
	}
	
	public static Db2LoadDaoClpImpl getInstance(ErrorManager errorManager) throws IOException {
		return new Db2LoadDaoClpImpl(errorManager);
	}
	@Override
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException {
		// TODO

	}
	@Override
	public void createTable(Db2Table db2Table) {
		
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
		ProcessBuilder processBuilder = new ProcessBuilder("db2.exe", dropTableCommand);
		try {
			processBuilder.start().waitFor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		// Вводим команду удаления таблицы
		
		
		// Вводим команду в окружении дб2. Выводим сообщение в лог об успешности/неуспешности
		ProcessBuilder pb = new ProcessBuilder("db2.exe", createTableCommand);
		logger.info("Trying to create " + db2Table);
		try {
			Process process = pb.start();
			int errorlevel = process.waitFor();
			if (errorlevel > 0) {
				logger.error(db2Table + " was not created");
				logger.error("Create command: " + createTableCommand);

			} else {
				logger.info(db2Table + " successfuly created");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
			
	}
	
	@Override
	public void loadFile(Db2FileLoadProps props) throws SQLException {
		
		String delimeter = props.getDelimeter();
		delimeter = toDb2Hex(delimeter);
		String absPathToLogFolder = props.getAbsPathToLogFolder();
		String schema = props.getSchema();
		String table = props.getTable();
		
		File logFile = new File(absPathToLogFolder, table + ".txt");
		File logLinkFile = new File(absPathToLogFolder,table);
		
		Path logLinkPath = logLinkFile.toPath();
		if (Files.isSymbolicLink(logLinkPath)) {
			try {
				logger.debug(String.format("Symbolic link already exist. The target of the link '%s' is '%s'",
						logLinkPath, Files.readSymbolicLink(logLinkPath)));
			} catch (IOException e) {
				logger.error("Symbolic link is not symbolic link");
				System.exit(1);
			}
			try {
				Files.delete(logLinkPath);
				logger.debug(String.format("link %s succesefuly deleted", logLinkPath.toString()));
			} catch (IOException e) {
				logger.error("Could not delete symbolic link" + logLinkPath);
				System.exit(1);
			}
		}
		Path logTargetPath = logFile.toPath();
		logTargetPath = logTargetPath.toAbsolutePath();
		logger.debug("logTargetPath = " + logTargetPath);
		Path link = null;
		try {
			link = Files.createSymbolicLink(logLinkPath, logTargetPath);
			logger.debug("New link created " +link);
			logger.debug("Target of new link is " + Files.readSymbolicLink(link));
		} catch (IOException e2) {
			logger.error(String.format("Could not create sybolic link %s from file %s%nCall programmer", logLinkPath.toString(),logTargetPath));
			System.exit(1);
		}
		String absolutePathToLogFile = link.toString();

		logger.debug(String.format("Abs path to log file of db2 load command is %s",absolutePathToLogFile));
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"\\\\.\\pipe\\").append(table).append("\"")
			.append(" OF DEL modified by codepage=1208 nochardel coldel").append(delimeter)
			.append(" MESSAGES ").append("\"").append(absolutePathToLogFile).append("\"")
			.append(" REPLACE INTO ").append(schema).append(".").append(table).append(" DATA BUFFER 10000");
		String loadCommand = sb.toString();
		logger.debug(String.format("Db2 load commans is %s", loadCommand));
		
	
		ProcessBuilder pb = new ProcessBuilder("db2.exe", loadCommand);
		
		try {
			File outputFile = new File(absPathToLogFolder,"output.txt");
			if (!outputFile.isFile()) {
					outputFile.createNewFile();
			}

			File errorFile = new File(absPathToLogFolder,"error.txt");
			if (!errorFile.isFile()) {
				errorFile.createNewFile();
			}
			File inputFile = new File(absPathToLogFolder,"input.txt");
			if (!inputFile.isFile()) {
				inputFile.createNewFile();
			}
			pb.redirectError(errorFile);
			pb.redirectInput(inputFile);
			pb.redirectOutput(outputFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sb.setLength(0);
		sb.append(table).append(" start loading");
		logger.info(sb.toString());
		try {
			Process process = pb.start();
			int errorlevel = process.waitFor();
			if (errorlevel > 0) {
			
				sb.setLength(0);
				sb.append(schema).append(".").append(table).append(" proccessed with errors.");
				logger.error(sb.toString());
				errorManager.addErrorFile(logFile);

			} else {
				sb.setLength(0);
				sb.append(schema).append(".").append(table).append(" loaded.");
				logger.info(sb.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int size = UpdateAsfkiFilesJob.LIST_SIZE;
		counter++;
		sb.setLength(0);
		sb.append("Files remaining: ").append(size - counter).append(" from ").append(size);
		logger.info(sb.toString());
	}

	@Override
	public void cleanTables(List<String> cleanList, String schema)
			throws Exception {
		// TODO

	}

	@Override
	public void loadFromQueue(Queue<Db2FileLoadProps> db2props)
			throws SQLException  {
				// TODO
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
