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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import rw.asfki.JAXB2Entity.spisok.Root;
import rw.asfki.JAXB2Entity.spisok.SpisokColumn;
import rw.asfki.JAXB2Entity.spisok.SpisokRow;

import rw.asfki.domain.ASFKI_RowColumn;
import rw.asfki.util.SimpleTimestampFormat;
import rw.asfki.util.UnZip;

public class UpdateAsfkiFilesJob implements Runnable, DefaultParams {
	protected static Logger logger = Logger.getLogger("service");
	private String defaultTime = DEFAULT_TIME;
	private String inputFile = DEFAULT_INPUT_FILE;
	private String filterRegex = DEFAULT_FILTER_REGEX;
	private String columnTag = DEFAULT_COLUMN_TAG;
	private String rowTag = DEFAULT_ROW_TAG;
	private String rowAttribute = DEFAULT_ROW_ATTRIBUTE;
	private String columnAttribute = rowAttribute;
	private String rootTag = DEFAULT_ROW_ATTRIBUTE;
	private String spisokUrlFolder = DEFAULT_SPISOK_URL_FOLDER;
	private String spisokFileName = DEFAULT_SPISOK_FILE_NAME;
	private String downloadFolder = DEFAULT_DOWNLOAD_FOLDER;
	private String asfkiDb2Folder = DEFAULT_ASFKI_DB2_FOLDER;
	private String archiveExtention = DEFAULT_ARCHIVE_EXTENTION;
	private String db2lExtention = DEFAULT_DB2L_EXTENTION;
	private String spisokColumnAttribute1 = DEFAULT_SPISOK_COLUMN_ATTRIBUTE1;
	private String spisokColumnAttribute2 = DEFAULT_SPISOK_COLUMN_ATTRIBUTE2;
	private static List<ASFKI_RowColumn> downloadedList;
	
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
			
			Reader reader = new AsfkiReader.Builder(downloadedSpisok, rowTag, columnTag)
			.columnAttributes(columnAttributesList)
			.bodyRegexFilter(filterRegex)
			.rootTag(rootTag)
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
		
		for (String fileName : list) {
			// Download file
			String db2FilePath = asfkiDb2Folder + "/" + fileName + db2lExtention;
			File db2File = new File(db2FilePath);
			if(!db2File.exists()) {
				db2File.createNewFile();
			}
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
			.columnAttributes(columnAttributesList)
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

				writer.write(bodyList);
				writer.flush();
				writer.close();
			
			}
			reader.close();
			logger.info(db2File.getAbsolutePath() + " is written");
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
	private void clean() {
		File tempFolder = new File(downloadFolder);
		
		        	List<String> filesInTemp = Arrays.asList(tempFolder.list());
		        	for (String string : filesInTemp) {
		        		String pathToDelete = (tempFolder.toString() + "/" +string).trim();
		        		File phantom = new File(pathToDelete);
		        		phantom.setWritable(true);
		        		phantom.delete();
		        	}


		        tempFolder.delete();
		    }

	private void initParams() {
		List<String> paramsList = new ArrayList<String>();
		paramsList.add("defaultTime");
		paramsList.add("inputFile");
		paramsList.add("filterRegex");
		paramsList.add("columnTag");
		paramsList.add("rowTag");
		paramsList.add("rowAttribute");
		paramsList.add("columnAttribute");
		paramsList.add("rootTag");
		paramsList.add("spisokUrlFolder");
		paramsList.add("spisokFileName");
		paramsList.add("downloadFolder");
		paramsList.add("asfkiDb2Folder");
		paramsList.add("archiveExtention");
		paramsList.add("db2lExtention");
		paramsList.add("spisokColumnAttribute1");
		paramsList.add("spisokColumnAttribute2");
		
		
		for (String string : paramsList) {
			String temp = System.getProperty(string);
			if (temp != null) {
				try {
					Field field = this.getClass().getDeclaredField(string);
					field.setAccessible(true);
					try {
						field.set(this, temp);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void run() {
		logger.info("Job started");
			initParams();
		try {
			List<String> list = getListToUpdate();
			if (list.size() > 0) {
			updateFiles(list);
			updateList(); }
			else {
				logger.info("Everything is up to date");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			clean();
		
		}
		logger.info("End of job");

	}
	public static void main(String[] args) {
		new Thread(new UpdateAsfkiFilesJob()).start();
	}
}
