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
	private int namedPipeHandle;

	
	public Db2WriterPipeImpl(int namedPipeHandle, String columnDelimeter) {
		this.namedPipeHandle = namedPipeHandle;
		this.columnDelimeter = columnDelimeter;
		lineSeparator  = java.security.AccessController.doPrivileged(
	            new sun.security.action.GetPropertyAction("line.separator"));

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

	

}
