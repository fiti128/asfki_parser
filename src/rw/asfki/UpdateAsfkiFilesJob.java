package rw.asfki;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import rw.asfki.JAXB2Entity.spisok.Root;
import rw.asfki.JAXB2Entity.spisok.SpisokColumn;
import rw.asfki.JAXB2Entity.spisok.SpisokRow;

import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.dao.impl.DB2LoadDAOJDBCImpl;
import rw.asfki.domain.ASFKI_RowColumn;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.properties.DataSourceFromProperties;
import rw.asfki.util.SimpleTimestampFormat;
import rw.asfki.util.UnZip;
import rw.asfki.util.UsefulMethods;

public class UpdateAsfkiFilesJob implements Runnable {
	protected static Logger logger = Logger.getLogger("service");
	private String defaultTime;
	private String inputFile;
	private String filterRegex;
	private String columnTag;
	private String rowTag;
	private String rowAttribute;
	private String columnAttribute;
	private String rootTag;
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
	private String absPathToLogFile;
	private String delimeter;
	private String schema;
	private List<ASFKI_RowColumn> downloadedList;
	private List<String> rowAttributes = new ArrayList<String>();
	private List<String> columnAttributes = new ArrayList<String>();
	private List<String> spisokRowAttributes = new ArrayList<String>();
	private List<String> spisokColumnAttributes = new ArrayList<String>();
	
	public UpdateAsfkiFilesJob() {
		initParserProperties();
		initVmParams();
	}
	private List<String> getListToUpdate() throws IOException {
		
		
		//Creating folder for temporary files
		File folder = new File(downloadFolder);
		if(!folder.exists()){
			folder.mkdir();
		}
		
		//Downloading spisok
		  URL fileUrl = new URL(spisokUrlFolder + spisokFileName);
		  ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
		  File downloadedSpisok = new File(downloadFolder + "/" + spisokFileName);
		  FileOutputStream fos = new FileOutputStream(downloadedSpisok);
		  fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		  fos.flush();
		  fos.close();
		  fos = null;
		  System.gc();
		  
		 // Initializing reader for downloaded file

			List<String> columnAttributesList = new ArrayList<String>();
			columnAttributesList.add(spisokColumnAttribute1);
			columnAttributesList.add(spisokColumnAttribute2);
			
			Reader reader = new AsfkiReader.Builder(downloadedSpisok, spisokRowTag, spisokColumnTag)
			.columnAttributes(spisokColumnAttributes)
			.bodyRegexFilter(spisokFilterRegex)
			.rootTag(spisokRootTag)
			.header(true)
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
			List<String> listToUpdate = new ArrayList<String>();
			for (String key : listToCompare) {
				Timestamp oldTimeToCompare = (oldMap.get(key) == null) ? defaultTs : oldMap.get(key);
				Timestamp newTimeToCompare = newMap.get(key);
				if (newTimeToCompare.after(oldTimeToCompare)) {
					listToUpdate.add(key);
				}
			}
			
		return listToUpdate;
	}
	
	private void updateFiles(List<String> list) throws Exception {
		
		UnZip unzip = new UnZip();
		File folder = new File(asfkiDb2Folder);
		if(!folder.exists()){
			folder.mkdir();
		}
		StringBuilder sb = new StringBuilder();
		for (String file : list) {
			sb.append(file).append(" ");
		}
		logger.info("Table files to update: " + sb.toString());
		Db2FileLoadProps dbFile = new Db2FileLoadProps();
			dbFile.setAbsPathToLogFile(absPathToLogFile);
			dbFile.setDelimeter(delimeter);
			dbFile.setSchema(schema);
		Queue<Db2FileLoadProps> db2Queue = new PriorityBlockingQueue<Db2FileLoadProps>();
		
		Db2LoadFromQueueTask db2Task = new Db2LoadFromQueueTask(db2Queue, new DataSourceFromProperties());
		DB2LoadDAO db2load = new DB2LoadDAOJDBCImpl(new DataSourceFromProperties());
		db2load.cleanTables(list, schema);
		db2Task.start();
		for (String fileName : list) {
			// Download file
			String db2FilePath = asfkiDb2Folder + "/" + fileName + db2lExtention;
			
			File db2File = new File(db2FilePath);
			if(!db2File.exists()) {
				db2File.createNewFile();
			}
			String t = db2File.getAbsolutePath();
			String db2FilePathForLoad = t.replaceAll("\\\\", "\\\\\\\\");
			Db2FileLoadProps db2f = dbFile.clone();
			db2f.setAbsPathToFile(db2FilePathForLoad);
			db2f.setTable(fileName);
			
			URL fileUrl = new URL(spisokUrlFolder + fileName + archiveExtention);
			String filePath = downloadFolder + "/" + fileName + archiveExtention;
			
			ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			  fos.flush();
			  fos.close();
			  fos = null;
			  System.gc();
			
//			File newFile = new File(filePath);
	

			
			// Unzip it
			unzip.unZipIt(filePath, downloadFolder);
			
			// Convert it
			List<String> rowAttributesList = new ArrayList<String>();
			rowAttributesList.add(rowAttribute);
			List<String> columnAttributesList = new ArrayList<String>();
			columnAttributesList.add(columnAttribute);
			File unzipedFile = new File(downloadFolder + "/" + fileName + ".xml");
			
			Reader reader = new AsfkiReader.Builder(unzipedFile, rowTag, columnTag)
				.rowAttributes(rowAttributes)
				.columnAttributes(columnAttributes)
				.bodyRegexFilter(filterRegex)
				.rootTag(rootTag)
				.header(true)
				.build();
			while(reader.hasNext()) {
				List<ASFKI_RowColumn> rowList = reader.next();
				Db2lWriter writer = new Db2lWriter(new BufferedWriter(new FileWriter(db2File.getAbsoluteFile(),true)));
				List<String> bodyList = new ArrayList<String>();
					for (ASFKI_RowColumn column : rowList) {
						String body = column.getBody();
						bodyList.add(body);
					}

				writer.writeLine(bodyList);
				writer.flush();
				writer.close();
			
			}
			reader.close();
			logger.info(db2File.getAbsolutePath() + " сконвертирован");
			logger.info("Oсталось файлов: " + (list.size() - list.indexOf(fileName)-1) + " из " + list.size());
			db2Queue.offer(db2f);
			synchronized(db2Queue) {
				db2Queue.notify();
			}
		}
//		DB2Load dao = new DB2LoadJDBCImpl(new DataSourceFromProperties());
//		dao.loadFromQueue(db2Queue);
		

		db2Task.stop();
		while (db2Task.isAlive()) {
			Thread.sleep(200);
		}
		
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
			logger.error("Не смог прочитать файл с настройками: " + configName );
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
						logger.error("Ошибка при инициализации параметров");

					}
				}
			}
		}
		initAttributes(props, "rowAttribute", rowAttributes);
		initAttributes(props, "columnAttribute", columnAttributes);
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
 * Метод берет имена полей типа <code> String.class </code> этого класса и проверяет, есть ли 
 * параметры виртуальной машины с таким же именем. Если есть - то присваивает
 * значение параметра виртуалки.
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
						logger.error("Ошибка при инициализации параметров");

					}
				}
			}
		}

	}
	
	@Override
	public void run() {
		logger.info("Начало работы");
		Date startTime = new Date();
		try {
			List<String> list = getListToUpdate();
			if (list.size() > 0) {
			updateFiles(list);
			updateList(); }
			else {
				logger.info("Все обновленно");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			clean(downloadFolder);
			clean(asfkiDb2Folder);
		
		}
		Date endTime = new Date();
		long jobTime = endTime.getTime() - startTime.getTime();
		String jobPrettyTime = UsefulMethods.millisToLongDHMS(jobTime);
		logger.info("Конец работы");
		logger.info("Затраченно времени: " + jobPrettyTime);
	}
	public static void main(String[] args) {
		new Thread(new UpdateAsfkiFilesJob()).start();
	}
}
