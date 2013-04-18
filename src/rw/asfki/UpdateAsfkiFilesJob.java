/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import rw.asfki.JAXB2Entity.spisok.Root;
import rw.asfki.JAXB2Entity.spisok.SpisokColumn;
import rw.asfki.JAXB2Entity.spisok.SpisokRow;
import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.dao.impl.DB2LoadDAOJDBCImpl;
import rw.asfki.domain.ASFKI_RowColumn;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.error.ErrorManager;
import rw.asfki.filters.AsfkiFilter;
import rw.asfki.properties.DataSourceFromProperties;
import rw.asfki.sax.AsfkiHandler;
import rw.asfki.util.SimpleTimestampFormat;
import rw.asfki.util.UnZip;
import rw.asfki.util.UsefulMethods;

/**
 * ����� ������������ ��� �������� ��� ���������� ����� ������ - 
 * �������� ASFKI �������.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 *
 */
public class UpdateAsfkiFilesJob implements Runnable {
	protected static Logger logger = Logger.getLogger("service");
	private String errorFolder = "error";
	public static int LIST_SIZE = 0;
	private boolean regularJob;
	private String additionalUrls;
	private String defaultTime;
	private String inputFile;
	private String columnTag;
	private String rowTag;
	private String spisokUrlFolder;
	private String spisokFileName;
	private String downloadFolder;
	private String asfkiDb2Folder;
	private String archiveExtention;
	private String db2lExtention;
	private String spisokRootTag;
	private String spisokRowTag;
	private String spisokColumnTag;
	private String spisokColumnAttribute1;
	private String spisokColumnAttribute2;
	private String spisokFilterRegex;
	private String absPathToLogFolder;
	private String delimeter;
	private String schema;
	private List<ASFKI_RowColumn> downloadedList;
	private List<String> spisokRowAttributes = new ArrayList<String>();
	private List<String> spisokColumnAttributes = new ArrayList<String>();	



	
	public UpdateAsfkiFilesJob() {
		initParserProperties();
		initVmParams();
	}
	private List<URL> getListToUpdate() throws IOException {
		
			// Creating folder for temporary files
		  createFolder(downloadFolder);
		
			// Download spisok
		  URL fileUrl = new URL(spisokUrlFolder + spisokFileName);
		  File downloadedSpisok = new File(downloadFolder + "/" + spisokFileName);
		  downloadFile(fileUrl, downloadedSpisok);
		  
		    // Initializing reader for downloaded file
			Reader reader = new AsfkiReader.Builder(downloadedSpisok, spisokRowTag, spisokColumnTag)
			.rowAttributes(spisokRowAttributes)
			.columnAttributes(spisokColumnAttributes)
			.bodyRegexFilter(spisokFilterRegex)
			.rootTag(spisokRootTag)
			.build();
			
			// Read file
			downloadedList = reader.next();
			reader.close();
			List<String> listToCompare  = new ArrayList<String>();
			
			// Parse data from file to map and Get values to compare list
			Map<String, Timestamp> newMap = new HashMap<String, Timestamp>();
			for (ASFKI_RowColumn asfki_RowColumn : downloadedList) {
				String key = asfki_RowColumn.getBody();
				String pattern = asfki_RowColumn.getAttributes().get("format");
				String correctionTime = asfki_RowColumn.getAttributes().get("cor_time");
				SimpleTimestampFormat stf = new SimpleTimestampFormat(pattern);
				Timestamp value = stf.parse(correctionTime);
				newMap.put(key, value);
				listToCompare.add(key);
			}
			
			// Read old data list from xml
			List<SpisokColumn> spisokColumnlist;
			try {
				JAXBContext context = JAXBContext.newInstance(Root.class);
				Unmarshaller um = context.createUnmarshaller();
				Root root = (Root)um.unmarshal(new File(inputFile));
				spisokColumnlist = root.getRow().getColumn();
			} catch (JAXBException e) {
				spisokColumnlist = new ArrayList<SpisokColumn>(0);
				}
			
			// Parse old data to map
			Map<String, Timestamp> oldMap = new HashMap<String, Timestamp> ();
			for (SpisokColumn spisokColumn : spisokColumnlist) {
				String key = spisokColumn.getBody();
				String pattern = spisokColumn.getFormat();
				String correctionTime = spisokColumn.getCor_time();
				SimpleTimestampFormat stf = new SimpleTimestampFormat(pattern);
				Timestamp value = stf.parse(correctionTime);
				oldMap.put(key, value);
			}
			
			// Creating update list
			Timestamp defaultTs = Timestamp.valueOf(defaultTime);
			List<URL> listToUpdate = new ArrayList<URL>();
			for (String key : listToCompare) {
				Timestamp oldTimeToCompare = (oldMap.get(key) == null) ? defaultTs : oldMap.get(key);
				Timestamp newTimeToCompare = newMap.get(key);
				if (newTimeToCompare.after(oldTimeToCompare)) {
					URL url = new URL(spisokUrlFolder + key + archiveExtention);
					listToUpdate.add(url);
				}
			}
			
		return listToUpdate;
	}
	
	private void downloadFile(URL fileUrl, File downloadedSpisok) throws IOException {
		BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
    		in = new BufferedInputStream(fileUrl.openStream());
    		fout = new FileOutputStream(downloadedSpisok);

    		byte data[] = new byte[2048];
    		int count;
    		while ((count = in.read(data, 0, 2048)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
		
	}
	private void createFolder(String downloadFolder) {
		File folder = new File(downloadFolder);
		if(!folder.isDirectory()){
			folder.mkdirs();
		}
		
	}
	
	private File createFile(String path) {
		File file = new File(path);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("�� ������� ������� ���� � ����� " + path);
				e.printStackTrace();
			}
		}
		return file;
	}
	private List<URL> parseUrls(String urls) throws MalformedURLException {
		List<URL> list = new ArrayList<URL>();
		String[] strs = urls.split(",");
		for (String string : strs) {
			URL url = new URL(string);
			list.add(url);
		}
		return list;
	}
	private void updateList() throws JAXBException {
		
		Root root = new Root();
		SpisokRow row = new SpisokRow();
		List<SpisokColumn> spisokColumnList = new ArrayList<SpisokColumn>();
		
		for (ASFKI_RowColumn asfkColumn : downloadedList) {
			SpisokColumn col = new SpisokColumn();
			col.setBody(asfkColumn.getBody());
			col.setCor_time(asfkColumn.getAttributes().get(spisokColumnAttribute1));
			col.setFormat(asfkColumn.getAttributes().get(spisokColumnAttribute2));
			spisokColumnList.add(col);
		}
		row.setColumn(spisokColumnList);
		root.setRow(row);
		
		JAXBContext context = JAXBContext.newInstance(Root.class);
		Marshaller mar = context.createMarshaller();
		mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		mar.marshal(root, new File(inputFile));
		logger.info(inputFile + " updated");
		
	}
	private void clean(String folder) {
		File tempFolder = new File(folder);
		
		        	if (tempFolder.isDirectory()) {
						List<String> filesInTemp = Arrays.asList(tempFolder
								.list());
						for (String string : filesInTemp) {
							String pathToDelete = (tempFolder.toString() + "/" + string)
									.trim();
							File phantom = new File(pathToDelete);
							phantom.setWritable(true);
							phantom.delete();
						}
						tempFolder.delete();
					}
		       
		    }
	private void initParserProperties() {
		Properties props = null;
		String configName = "parser.properties";
		try {
			props = UsefulMethods.loadProperties(configName);
		} catch (IOException e1) {
			logger.error("�� ���� ��������� ���� � �����������: " + configName );
		}
		Field[] thisFields = this.getClass().getDeclaredFields();
		for (Field field : thisFields) {
			if (field.getType() == String.class) {
				String temp = props.getProperty(field.getName());
				if (temp != null) {
					field.setAccessible(true);
					try {
						field.set(this, temp);
					} catch (Exception e) {
						logger.error("������ ��� ������������� ����������");

					}
				}
			}
			if (field.getType() == boolean.class) {
				String temp = props.getProperty(field.getName());
				if (temp != null) {
					field.setAccessible(true);
					try {
						field.set(this, Boolean.valueOf(temp));
					} catch (Exception e) {
						logger.error("������ ��� ������������� ����������");

					}
				}
			}
		}
		initAttributes(props, "spisokRowAttribute", spisokRowAttributes);
		initAttributes(props, "spisokColumnAttribute", spisokColumnAttributes);
		
	}
private void initAttributes(Properties props, String attributeTarget, List<String> attributesList) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 10; i++) {
			sb.setLength(0);
			sb.append(attributeTarget);
			if (i != 0) sb.append(String.valueOf(i));
			String param = sb.toString();
			String attribute;
			if ((attribute = props.getProperty(param)) != null) {
				attributesList.add(attribute);
			}
		}
		
		
	}
/**
 * ����� ����� ����� ����� ���� <code> String.class </code> ����� ������ � ���������, ���� �� 
 * ��������� ����������� ������ � ����� �� ������. ���� ���� - �� �����������
 * �������� ��������� ���������.
 */
	private void initVmParams() {
		Field[] thisFields = this.getClass().getDeclaredFields();
		for (Field field : thisFields) {
			if (field.getType() == String.class) {
				String temp = System.getProperty(field.getName());
				if (temp != null) {
					field.setAccessible(true);
					try {
						field.set(this, temp);
					} catch (Exception e) {
						logger.error("������ ��� ������������� ����������");

					}
				}
			}
		}

	}

	
	public void setRowTag(String rowTag) {
		this.rowTag = rowTag;
	}

	private void convert(File xmlFile, File db2File) throws Exception{
		XMLReader xr = XMLReaderFactory.createXMLReader();
		Db2Writer writer = new Db2WriterImpl(new BufferedWriter(new FileWriter(db2File,false)));
		AsfkiHandler asfkiHandler = AsfkiHandler.getInstance(writer, rowTag, columnTag);
		xr.setContentHandler(asfkiHandler);
//		is =             new InputSource(new InputStreamReader(new AsfkiFilter(new FileInputStream(unzipedFile)) ,"UTF-8"));
		InputSource is = new InputSource(new InputStreamReader(new AsfkiFilter(new FileInputStream(xmlFile)) ,"UTF-8"));
		
		// Converting
		xr.parse(is);
		writer.flush();
		writer.close();
	}
	
	private void processUrl(URL url, Queue<Db2FileLoadProps> db2Queue) throws Exception {
		
		String fileNameWithExtention = new File(url.getPath()).getName();
		String fileName = fileNameWithExtention.substring(0, fileNameWithExtention.length()-4);
		
		String db2FilePath = asfkiDb2Folder + "/" + fileName + db2lExtention;
		File db2File = createFile(db2FilePath);
		// Create db2load properties for the file
		String absolutePath = db2File.getAbsolutePath();
		String db2FilePathForLoad = absolutePath.replaceAll("\\\\", "\\\\\\\\");
	
		Db2FileLoadProps db2fProperties = new Db2FileLoadProps();
		db2fProperties.setAbsPathToLogFolder(absPathToLogFolder);
		db2fProperties.setDelimeter(delimeter);
		db2fProperties.setSchema(schema);
		db2fProperties.setAbsPathToFile(db2FilePathForLoad);
		db2fProperties.setTable(fileName);
		
		// Download file
		String filePath = downloadFolder + "/" + fileNameWithExtention;
		File file = new File(filePath);
				
		downloadFile(url, file);
		
		// Unzip it
		UnZip unzip = new UnZip();
		unzip.unZipIt(filePath, downloadFolder);
		
		// Convert it
		File unzipedFile = new File(downloadFolder + "/" + fileName + ".xml");
		
		convert(unzipedFile, db2File);
		
		logger.info(db2File.getAbsolutePath() + " ��������������");
		
		// Offer to list complete details to load this file to db
		db2Queue.offer(db2fProperties);
		// Notifying another threads of new element in the queue
		synchronized(db2Queue) {
			db2Queue.notify();
		}
	
	}
	
	@Override
	public void run() {
		logger.info("������ ������");
		Date startTime = new Date();
		clean(downloadFolder);
		clean(asfkiDb2Folder);
		createFolder(asfkiDb2Folder);
		createFolder(downloadFolder);
		createFolder(errorFolder);
		try {
			List<URL> list = new ArrayList<URL>();
			if (regularJob) {
				list.addAll(getListToUpdate());
			}
			if (additionalUrls != null) {
				list.addAll(parseUrls(additionalUrls));
			}
			
			StringBuilder sb = new StringBuilder();
			for (URL url : list) {
				String fileNameWithExtention = new File(url.getPath()).getName();
				String fileName = fileNameWithExtention.substring(0, fileNameWithExtention.length()-4);
				sb.append(fileName).append(" ");
			}
			// �������� � ���� ������ ������, ��������� ����������
			logger.info("Table files to update: " + sb.toString());
			
			// Update table
			LIST_SIZE = list.size();
			
			if (list.size() > 0) {
				ErrorManager errorManager = new ErrorManager(new File(errorFolder));
				Queue<Db2FileLoadProps> db2Queue = new PriorityBlockingQueue<Db2FileLoadProps>();
				Properties databaseProperties = UsefulMethods.loadProperties("database.properties");
				Db2LoadFromQueueTask db2Task = new Db2LoadFromQueueTask(db2Queue, databaseProperties, errorManager);
				db2Task.start();
				Thread.yield();
				
				for (URL url : list) {
					processUrl(url, db2Queue);
				} 
				// end of list
				
			
				db2Task.stop();
				// Just to handle bug of terminating jvm without waiting all threads end their work
				while (db2Task.isAlive()) {
					Thread.sleep(200);
				}
				errorManager.sendToMail("ircm_yanusheusky@mnsk.rw.by");
				updateList();
			}
			else {
				logger.info("��� ����������");
			}
			} catch (Exception e) {
			e.printStackTrace();
		
		}
		finally {
			clean(downloadFolder);
			clean(asfkiDb2Folder);
			clean(errorFolder);
		
		}
		//Sending error by email
	
		Date endTime = new Date();
		long jobTime = endTime.getTime() - startTime.getTime();
		String jobPrettyTime = UsefulMethods.millisToLongDHMS(jobTime);
		
		logger.info("����� ������");
		logger.info("���������� �������: " + jobPrettyTime);
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
		new UpdateAsfkiFilesJob().run();
		}
		else {
			UpdateAsfkiFilesJob job = new UpdateAsfkiFilesJob();
			
			
			for (String string : args) {
				
			}
			
		}
	}

}
