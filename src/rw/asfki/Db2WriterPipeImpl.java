package rw.asfki;

import ibm.Pipes;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.dao.impl.Db2LoadDaoClpImpl;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.error.ErrorManager;

public class Db2WriterPipeImpl implements Db2Writer {
	private static Logger logger = Logger.getLogger("Service");
	private static String DEFAULT_DELIMETER = "|";
	private String columnDelimeter = DEFAULT_DELIMETER;
	private String lineSeparator; 
	static final int ERROR_PIPE_CONNECTED = 535;
	static final int ERROR_BROKEN_PIPE = 109;
	private int namedPipeHandle;
	private String pipeName; 
	private int pipeBuffer = 131072; 
	final Db2FileLoadProps db2File;
	
	public Db2WriterPipeImpl(final Db2FileLoadProps db2File, String columnDelimeter) {
		this.pipeName = "\\\\.\\pipe\\" +db2File.getTable();
		this.db2File = db2File;
		this.columnDelimeter = columnDelimeter;
		lineSeparator  = java.security.AccessController.doPrivileged(
	            new sun.security.action.GetPropertyAction("line.separator"));
	    if(createPipe()) {
	    	new Thread(new Runnable(){
	    		public void run() {
	    			try {
						DB2LoadDAO db2LoadDao = Db2LoadDaoClpImpl.getInstance(new ErrorManager(new File("error")));
						db2LoadDao.loadFile(db2File);
					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
	    		}
	    	}).start();
	    	connectToPipe();
	    }
	}
		
	@Override
	public void writeLine(List<String> stringList) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		for (int i = 0; i < stringList.size() - 1; i++) {
			sb.append(stringList.get(i)).append(columnDelimeter);
		}
			sb.append(stringList.get(stringList.size() - 1));
			sb.append(lineSeparator);
			byte[] bytes = sb.toString().getBytes("UTF-8");
			Pipes.WriteFile(namedPipeHandle, bytes, bytes.length);
			
	}

	@Override
	public void close() throws IOException {
		Pipes.FlushFileBuffers(namedPipeHandle);
	    Pipes.CloseHandle(namedPipeHandle);
	    Pipes.DisconnectNamedPipe(namedPipeHandle);

	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}
	private boolean createPipe()
	{
		boolean ok = false;
		namedPipeHandle = Pipes.CreateNamedPipe(pipeName, 0x00000003, 0x00000000, 2, pipeBuffer, pipeBuffer, 0xffffffff, 0);
		if (namedPipeHandle == -1)
		{
			logger.info("CreateNamedPipe failed for " + pipeName + 
					" for error " + " Message " + Pipes.FormatMessage(Pipes.GetLastError()));
			ok = false;
		} else
		{
			logger.info("Named Pipe " + pipeName + " created successfully Handle=" + namedPipeHandle);
			ok = true;
		}
		return ok;
	}
	
	private boolean connectToPipe()
	{
		logger.info("Waiting for a client to connect to pipe " + pipeName);
		boolean connected = Pipes.ConnectNamedPipe(namedPipeHandle, 0);
		if (!connected)
		{
			int lastError = Pipes.GetLastError();
			if (lastError == ERROR_PIPE_CONNECTED)
				connected = true;
		}
		if (connected)
		{
			logger.info("Connected to the pipe " + pipeName);
		} else
		{
			logger.info("Falied to connect to the pipe " + pipeName);
		}
		return connected;
	}
}
