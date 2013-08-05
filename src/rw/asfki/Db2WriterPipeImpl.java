/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import ibm.Pipes;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 
 * Sun writer like class that writes data into the named pipe
 * 
 * @author Yanusheusky S.
 * @since 26.05.2013
 */
public class Db2WriterPipeImpl implements Db2Writer {
//	private static final String UTF8_CHARSET_NAME = "UTF-8";
	private static final String CP1251_CHARSET_NAME = "windows-1251";
	private static Logger logger = Logger.getLogger(Db2WriterPipeImpl.class);
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
			byte[] bytes = sb.toString().getBytes(CP1251_CHARSET_NAME);
			Pipes.WriteFile(namedPipeHandle, bytes, bytes.length);
			
	}

	@Override
	public void close() throws IOException {
		Pipes.FlushFileBuffers(namedPipeHandle);
	    Pipes.CloseHandle(namedPipeHandle);
	    Pipes.DisconnectNamedPipe(namedPipeHandle);
	    logger.debug(String.format("Pipe %s closed",namedPipeHandle));

	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}

	

}
