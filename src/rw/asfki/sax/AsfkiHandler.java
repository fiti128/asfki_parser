package rw.asfki.sax;

import ibm.Pipes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rw.asfki.Db2Writer;
import rw.asfki.Db2WriterPipeImpl;
import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.dao.impl.Db2LoadDaoClpImpl;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.error.ErrorManager;

public class AsfkiHandler extends DefaultHandler {
	protected Logger logger = Logger.getLogger(AsfkiHandler.class);
	private static final int ERROR_PIPE_CONNECTED = 535;
	private static final int PIPE_BUFFER = 131072;
	private ErrorManager errorManager;
	private String root = "table";
	private Db2Writer writer;
	private String string;
	private List<String> list = new ArrayList<String>();
	private String rowTag;
	private String colTag;
	private int namedPipeHandle;
	private String pipeName;
	private Db2FileLoadProps db2File;
	private String delimeter;
	private boolean firstTime = true;
	private ExecutorService executorService;
	
	private AsfkiHandler(ErrorManager errorManager, Db2FileLoadProps db2File,ExecutorService executorService, String delimeter, String rowTag,String colTag) {
		super();
		this.executorService = executorService;
		this.errorManager = errorManager;
		this.pipeName = "\\\\.\\pipe\\" +db2File.getTable();
		this.db2File = db2File;
		this.delimeter = delimeter;
		this.rowTag = rowTag.intern();
		this.colTag = colTag.intern();
		
	}
	public static AsfkiHandler getInstance(ErrorManager errorManager, Db2FileLoadProps db2File, ExecutorService executorService, String delimeter, String rowTag,String colTag) {
		return new AsfkiHandler(errorManager,db2File,executorService, delimeter,rowTag,colTag);
	}
		
	private boolean createPipe()
	{
		boolean ok = false;
		namedPipeHandle = Pipes.CreateNamedPipe(pipeName, 0x00000003, 0x00000000, 2, PIPE_BUFFER, PIPE_BUFFER, 0xffffffff, 0);
		if (namedPipeHandle == -1)
		{
			logger.debug("CreateNamedPipe failed for " + pipeName + 
					" for error " + " Message " + Pipes.FormatMessage(Pipes.GetLastError()));
			ok = false;
		} else
		{
			logger.debug("Named Pipe " + pipeName + " created successfully Handle=" + namedPipeHandle);
			ok = true;
		}
		return ok;
	}
	
	private boolean connectToPipe()
	{
		logger.debug("Waiting for a client to connect to pipe " + pipeName);
		boolean connected = Pipes.ConnectNamedPipe(namedPipeHandle, 0);
		if (!connected)
		{
			int lastError = Pipes.GetLastError();
			if (lastError == ERROR_PIPE_CONNECTED)
				connected = true;
		}
		if (connected)
		{
			logger.debug("Connected to the pipe " + pipeName);
		} else
		{
			logger.debug("Falied to connect to the pipe " + pipeName);
		}
		return connected;
	}
	
	private void openDbLoad() {
		executorService.execute(new Runnable(){
    		public void run() {
    			try {
					DB2LoadDAO db2LoadDao = Db2LoadDaoClpImpl.getInstance(errorManager);
					db2LoadDao.loadFile(db2File);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
    		}
    	});
		
	}
	
	

	public void initPipe() throws SAXException {
		
		if (createPipe()) {
			openDbLoad();
			connectToPipe();
			writer = new Db2WriterPipeImpl(namedPipeHandle ,delimeter);
		}
		else {
			throw new SAXException("Could not create Pipe");
		}
	}
	public void startElement(String namespaceURI,
			String localName,
			String qName, 
			Attributes atts)
		throws SAXException {
		if(firstTime) {
			initPipe();
			firstTime = false;
		}
				if (qName == rowTag) {
				list.clear();
			}
				if (qName == colTag) {
					string = "";
					
				}
		}

		public void endElement (String uri, String name, String qName)
		{
			if(qName == rowTag){
				
						try {
							if (list.size() > 0) {
								
								writer.writeLine(list);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						

					}
			if(qName == colTag) {
				list.add(string.trim());
			}
			if(qName == root) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}


		public void characters (char[] ch, int start, int length) 
		{
			string = new String(ch,start,length);
		}
		


}
		
		

