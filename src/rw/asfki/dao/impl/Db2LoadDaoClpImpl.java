package rw.asfki.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

public abstract class Db2LoadDaoClpImpl implements DB2LoadDAO {
	private static final String DEFAULT_CODEPAGE = "1251";
	private static Logger logger = Logger.getLogger(Db2LoadDaoClpImpl.class);
	private ErrorManager errorManager;
	static int counter = 0;
	private File tempDir;
//	private  Db2LoadDaoClpImpl instance;
	
	
	
	protected Db2LoadDaoClpImpl(ErrorManager errorManager) throws IOException {
		this.errorManager = errorManager;
		tempDir = new File("temp");
		if (!tempDir.isDirectory()) {
			tempDir.mkdir();
		}
	
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
		String codepage = getCodepage();
		
		
		File logFile = new File(absPathToLogFolder, table + ".txt");
		String absolutePathToLogFile = logFile.getAbsolutePath();

		logger.debug(String.format("Abs path to log file of db2 load command is %s",absolutePathToLogFile));
		StringBuilder sb = new StringBuilder();
		sb.append("LOAD FROM ").append("\"\\\\.\\pipe\\").append(table).append("\"")
			.append(" OF DEL modified by codepage=").append(codepage).append(" nochardel coldel").append(delimeter)
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

	protected String getCodepage() {
		return DEFAULT_CODEPAGE;
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
