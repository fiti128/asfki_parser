package rw.asfki;

import ibm.Pipes;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

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
	
	public Db2WriterPipeImpl(String name, String columnDelimeter) {
		this.pipeName = "\\\\.\\pipe\\" +name + "1";
		this.columnDelimeter = columnDelimeter;
		lineSeparator  = java.security.AccessController.doPrivileged(
	            new sun.security.action.GetPropertyAction("line.separator"));
	    if(createPipe()) {
	    	connectToPipe();
	    }
	}
		
	@Override
	public void writeLine(List<String> stringList) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringList.size() - 1; i++) {
			sb.setLength(0);
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
