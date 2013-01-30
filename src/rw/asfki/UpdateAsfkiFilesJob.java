package rw.asfki;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

public class UpdateAsfkiFilesJob implements Runnable {
	protected static Logger logger = Logger.getLogger("service");
	private static String DEFAULT_TIME = "1111-11-11 11:11:11.111111";
	private static String INPUT_FILE = "spisok.xml";
	private static String FILTER_REGEX = "[^\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF[\\n\\r]]";
	private static String COLUMN_TAG = "col";
	private static String ROW_TAG = "row";
	private static String ROW_ATTRIBUTE = "num";
	private static String COLUMN_ATTRIBUTE = ROW_ATTRIBUTE;
	private static String ROOT_TAG = "table";
	private static String SPISOK_URL_FOLDER = "http://ircm-srv.mnsk.rw/ASFKI_XML/";
	private static String SPISOK_FILE_NAME = "spisok.xml";
	private static String DOWNLOAD_FOLDER = "temp";
	private static String ASFKI_DB2_FOLDER = "asfki_db2_files";
	private static String ARCHIVE_EXTENTION = ".zip";
	private static String DB2L_EXTENTION = ".txt";
	private static String COLUMN_ATTRIBUTE1 = "cor_time";
	private static String COLUMN_ATTRIBUTE2 = "format";
	private static List<ASFKI_RowColumn> downloadedList;
	
	private List<String> getListToUpdate() throws IOException {
		
		//Creating folder for temporary files
		File folder = new File(DOWNLOAD_FOLDER);
		if(!folder.exists()){
			folder.mkdir();
		}
		
		//Downloading spisok
		  URL fileUrl = new URL(SPISOK_URL_FOLDER + SPISOK_FILE_NAME);
		  ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
		  File downloadedSpisok = new File(DOWNLOAD_FOLDER + "/" + SPISOK_FILE_NAME);
		  FileOutputStream fos = new FileOutputStream(downloadedSpisok);
		  fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		  fos.flush();
		  fos.close();
		  fos = null;
		  System.gc();
		  
		 // Initializing reader for downloaded file

			List<String> columnAttributesList = new ArrayList<String>();
			columnAttributesList.add(COLUMN_ATTRIBUTE1);
			columnAttributesList.add(COLUMN_ATTRIBUTE2);
			
			Reader reader = new AsfkiReader.Builder(downloadedSpisok, ROW_TAG, COLUMN_TAG)
			.columnAttributes(columnAttributesList)
			.bodyRegexFilter(FILTER_REGEX)
			.rootTag(ROOT_TAG)
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
				Root root = (Root)um.unmarshal(new File(INPUT_FILE));
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
			Timestamp defaultTs = Timestamp.valueOf(DEFAULT_TIME);
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
		File folder = new File(ASFKI_DB2_FOLDER);
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
			String db2FilePath = ASFKI_DB2_FOLDER + "/" + fileName + DB2L_EXTENTION;
			File db2File = new File(db2FilePath);
			if(!db2File.exists()) {
				db2File.createNewFile();
			}
			URL fileUrl = new URL(SPISOK_URL_FOLDER + fileName + ARCHIVE_EXTENTION);
			String filePath = DOWNLOAD_FOLDER + "/" + fileName + ARCHIVE_EXTENTION;
			
			ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			  fos.flush();
			  fos.close();
			  fos = null;
			  System.gc();
			
//			File newFile = new File(filePath);
	

			
			// Unzip it
			unzip.unZipIt(filePath, DOWNLOAD_FOLDER);
			
			// Convert it
			List<String> rowAttributesList = new ArrayList<String>();
			rowAttributesList.add(ROW_ATTRIBUTE);
			List<String> columnAttributesList = new ArrayList<String>();
			columnAttributesList.add(COLUMN_ATTRIBUTE);
			File unzipedFile = new File(DOWNLOAD_FOLDER + "/" + fileName + ".xml");
			Reader reader = new AsfkiReader.Builder(unzipedFile, ROW_TAG, COLUMN_TAG)
			.columnAttributes(columnAttributesList)
			.bodyRegexFilter(FILTER_REGEX)
			.rootTag(ROOT_TAG)
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
			col.setCor_time(asfkColumn.getAttributes().get(COLUMN_ATTRIBUTE1));
			col.setFormat(asfkColumn.getAttributes().get(COLUMN_ATTRIBUTE2));
			spisokColumnList.add(col);
		}
		row.setColumn(spisokColumnList);
		root.setRow(row);
		
		JAXBContext context = JAXBContext.newInstance(Root.class);
		Marshaller mar = context.createMarshaller();
		mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		mar.marshal(root, new File(INPUT_FILE));
		logger.info(INPUT_FILE + " updated");
		
	}
	private void clean() {
		File tempFolder = new File(DOWNLOAD_FOLDER);
		
		        	List<String> filesInTemp = Arrays.asList(tempFolder.list());
		        	for (String string : filesInTemp) {
		        		String pathToDelete = (tempFolder.toString() + "/" +string).trim();
		        		File phantom = new File(pathToDelete);
		        		phantom.setWritable(true);
		        		phantom.delete();
		        	}


		        tempFolder.delete();
		    }


	@Override
	public void run() {
		logger.info("Job started");
		try {
			updateFiles(getListToUpdate());
			updateList();
			
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
