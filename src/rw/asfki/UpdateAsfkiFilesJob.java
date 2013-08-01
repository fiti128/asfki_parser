/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Properties;

import java.util.Set;



import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.tukaani.xz.XZInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import rw.asfki.JAXB2Entity.spisok.Root;
import rw.asfki.JAXB2Entity.spisok.SpisokColumn;
import rw.asfki.JAXB2Entity.spisok.SpisokRow;
import rw.asfki.dao.InfoDao;
import rw.asfki.dao.DaoFactory;
import rw.asfki.dao.YanushDataSource;
import rw.asfki.domain.ASFKI_RowColumn;
import rw.asfki.domain.ColumnAttributes;
import rw.asfki.domain.Db2Column;
import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.domain.Db2Table;
import rw.asfki.domain.RemoteFileConfig;
import rw.asfki.domain.TableAttributes;
import rw.asfki.error.ErrorManager;
import rw.asfki.filters.AsfkiCharFilter;
import rw.asfki.filters.AsfkiFilter;
import rw.asfki.sax.AsfkiHandler;
import rw.asfki.sax.TableMetaDataRetriever;
import rw.asfki.util.SimpleTimestampFormat;
import rw.asfki.util.UsefulMethods;


/**
 * Класс объединяющий все операции для выполнения одной задачи - 
 * обновить ASFKI таблицы.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 *
 */

public class UpdateAsfkiFilesJob implements Runnable {
	protected static Logger logger = Logger.getLogger(UpdateAsfkiFilesJob.class);
	private String errorFolder = "error";
	public static int LIST_SIZE = 0;
	private boolean regularJob;
	private String config = "conf/parser.properties";
	private String mailTo;
	private String additionalUrls;
	private String defaultTime;
	private String inputFile;
	private String columnTag;
	private String rowTag;
	private String spisokUrlFolder;
	private String spisokFileName;
	private String tempFolder = "temp";
	private String archiveExtention;
	private String db2lExtention = ".txt";
	private String spisokRootTag;
	private String spisokRowTag;
	private String spisokColumnTag;
	private String spisokColumnAttribute1;
	private String spisokColumnAttribute2;
	private String spisokFilterRegex;
	private String delimeter = "~";
	private String schema;
	private String user;
	private String password;
	private String dbUrl;
	private String driver;
	private String tableParams;
	
	// Table Meta Data with default values
	private String tableTag = "table";
	private String tableNameAttribute = "originalName";
	private String schemaNameAttribute = "originalSchema";
	private String headerRootTag = "thead";
	private String headerColumnTag = "th";
	private String headerColumnAttribute = "type";
	private String headerColumnSizeAttribute = "length";
	private String headerColumnCommentAttribute = "comment";
	private String headerColumnNullableAttribute ="isNullable";
	private String headerColumnFormatAttribute = "format";
	private String headerColumnDecimalDigitsAttribute = "";
	private String headerColumnDecimalScaleAttribute = "scale";
	private String headerColumnDecimalPrecisionAttribute = "precision";
	
	private List<ASFKI_RowColumn> downloadedList;
	private List<String> spisokRowAttributes = new ArrayList<String>();
	private List<String> spisokColumnAttributes = new ArrayList<String>();
	private boolean forceTableCreation;
	private String args[];
	private String proxyHost;
	private String proxyPort;
	protected String proxyUser;
	protected String proxyPassword;

	
	public UpdateAsfkiFilesJob(String args[]) {
		this.args = args;
	}
	private void initArgs() {
			Properties props;
			props = parseCommandLine(args);
			initParserProperties(props);

	}
	
	private void initConfig() {
		Properties props = null;
		try {
			props = UsefulMethods.loadProperties(config);
		} catch (IOException e1) {
			logger.error("Не смог прочитать файл с настройками: " + config );
		}
		initParserProperties(props);
	}
	
	private Properties parseCommandLine(String[] args) {
		Assert.assertNotNull("Args should not be null in parseCommandLine(String[] args)",args);
		Assert.assertTrue("Args length should be greater than zero in parseCommandLine",args.length > 0);
		Properties props = new Properties();
		List<String> urls = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-r"))  {
				i++;
				props.setProperty("rowTag", args[i]);
			}
			else if (args[i].equals("--regular")) {
				i++;
				props.setProperty("regularJob", args[i]);
			}
			else if (args[i].equals("-c")) {
				i++;
				props.setProperty("columnTag", args[i]);
			}
			else if (args[i].equals("-u")) {
				i++;
				props.setProperty("user", args[i]);
			}
			else if (args[i].equals("-p")) {
				i++;
				props.setProperty("password", args[i]);
			}
			else if (args[i].equals("-d")) {
				i++;
				props.setProperty("database", args[i]);
			}
			else if (args[i].equals("-s")) {
				i++;
				props.setProperty("schema", args[i]);
			}
			else if (args[i].equals("--config")) {
				i++;
				props.setProperty("config", args[i]);
			}
			else {
				urls.add(args[i]);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < urls.size()-1; i++) {
			sb.append(urls.get(i));
			sb.append(",");
		}
		sb.append(urls.get(urls.size()-1));
		String additionalUrls = sb.toString();
		props.setProperty("additionalUrls", additionalUrls);
		Set<Entry<Object,Object>> set = props.entrySet();
		logger.info("Run configuration:");
		for (Entry<Object, Object> entry : set) {
			logger.info(entry.getKey() +" = " + entry.getValue());
		}
		return props;
	}
	private List<URL> getListToUpdate() throws IOException {
		
			// Creating folder for temporary files
		  createFolder(tempFolder);
		
			// Download spisok
		  URL fileUrl = new URL(spisokUrlFolder + spisokFileName);
		  File downloadedSpisok = new File(tempFolder + "/" + spisokFileName);
		  try {
			  	downloadFile(fileUrl, downloadedSpisok);
		  		} catch (FileNotFoundException e) {
					logger.error("File with table list was not found." +
							" Check spisokUrlFolder and spisokFileName properties at conf/parser.properties");
					System.exit(1);
				} catch (IOException e) {
					logger.error("IOException was catched",e);
					System.exit(1);
				}
		
		  
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
				String pattern = asfki_RowColumn.getAttributes().get("dateFormat");
				String correctionTime = asfki_RowColumn.getAttributes().get("changedDate");
				SimpleTimestampFormat stf = (pattern == null)
					? new SimpleTimestampFormat() : new SimpleTimestampFormat(pattern);
				// Just in case
				Timestamp value = (correctionTime == null || correctionTime.length() < 20)
				? stf.parse(defaultTime) : stf.parse(correctionTime);
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
					logger.debug(spisokUrlFolder + key + archiveExtention);
					URL url = new URL(spisokUrlFolder + key + archiveExtention);
					listToUpdate.add(url);
				}
			}
			
		return listToUpdate;
	}
	
	private void downloadFile(URL fileUrl, File downloadedSpisok) throws IOException {
		
		BufferedInputStream in = null;
    	FileOutputStream fout = null;

    		in = new BufferedInputStream(fileUrl.openStream());
    		fout = new FileOutputStream(downloadedSpisok);

    		byte data[] = new byte[2048];
    		int count;
    		while ((count = in.read(data, 0, 2048)) != -1)
    		{
    			fout.write(data, 0, count);
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
				logger.error("Не удалось создать файл с путем " + path);
				e.printStackTrace();
			}
		}
		return file;
	}
	
	private List<URL> parseUrls(String urls) throws MalformedURLException {
		List<URL> list = new ArrayList<URL>();
		String[] strs = urls.split(",");
		for (String string : strs) {
			URL url = new URL(string.trim());
			list.add(url);
		}
		return list;
	}
	private void updateList(List<Db2Table> errorTablesList) throws JAXBException {
		// Преобразуем апдейт лист в свою схему (просто копируем новые даные)
		
		Root root = new Root();
		SpisokRow row = new SpisokRow();
		List<SpisokColumn> spisokColumnList = new ArrayList<SpisokColumn>();
		List<String> errorTables = new ArrayList<String>();
		
			for (Db2Table table : errorTablesList) {
				errorTables.add(table.getName());
			}
		
		
		
		for (ASFKI_RowColumn asfkColumn : downloadedList) {
			if (errorTables.contains(asfkColumn.getBody())) {
				continue;
			}
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
	private void initParserProperties(Properties props) {

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
			if (field.getType() == boolean.class) {
				String temp = props.getProperty(field.getName());
				if (temp != null) {
					field.setAccessible(true);
					try {
						field.set(this, Boolean.valueOf(temp));
					} catch (Exception e) {
						logger.error("Ошибка при инициализации параметров");

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
 * Метод берет имена полей типа <code> String.class </code> этого класса и проверяет, есть ли 
 * параметры виртуальной машины с таким же именем. Если есть - то присваивает
 * значение параметра виртуалки.
 */
	private void initVm() {
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
	
	private void createConnectionToDatabase() {
//		ProcessBuilder processBuilder = new ProcessBuilder("db2cmd.exe", "/w", "/c","/i");
//		try {
//			processBuilder.start().waitFor();
//		} catch (InterruptedException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
		String[] partsOfDbUrl = dbUrl.split("/");
		String database = partsOfDbUrl[partsOfDbUrl.length-1];
		logger.debug(String.format("Parsing database name from url %s and we've got %s",dbUrl,database));
		StringBuilder sb = new StringBuilder();
		sb.append("connect to ").append(database).append(" user ").append(user)
		.append(" using ").append(password);
		String connectionCommand = sb.toString();
		logger.debug(String.format("Connection command created as : %n%s",connectionCommand));
		
		ProcessBuilder pb = new ProcessBuilder("db2.exe", connectionCommand);
		try {
			Process process = pb.start();
			int errorlevel = process.waitFor();
			if (errorlevel > 0) {
				logger.error(String.format("Connection to dababase %s with" +
						" user %s and password %s failed. Check once again user,password and dbUrl at" +
						" conf/parser.properties",database.toUpperCase(),user,password));
				System.exit(1);
				
			} else {
				logger.debug(String.format("Connection to dababase %s with" +
						" user %s and password %s was created successfuly",database,user,password));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void initSecurity() {
		// Global security
	    if (proxyUser !=null && !proxyUser.equals(""))	
	    Authenticator.setDefault(new Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
	        return new
	           PasswordAuthentication(proxyUser,proxyPassword.toCharArray());
	    }});
	    // PROXY
	    if (proxyHost !=null && !proxyHost.equals("") &&
	    		proxyPort !=null && !proxyPort.equals("")) {
	    	System.setProperty("http.proxyHost", proxyHost) ;
	    	System.setProperty("http.proxyPort", proxyPort) ;
     }
		
	}
	public void setRowTag(String rowTag) {
		this.rowTag = rowTag;
	}
	private RemoteFileConfig getConfig() {
	
		RemoteFileConfig config = RemoteFileConfig.
			getInstance(tableTag, 
					TableAttributes.getInstance(tableNameAttribute, schemaNameAttribute),
					headerRootTag,
					headerColumnTag,
					ColumnAttributes.getInstance(
							headerColumnAttribute,
							headerColumnSizeAttribute,
							headerColumnCommentAttribute,
							headerColumnNullableAttribute,
							headerColumnFormatAttribute,
							headerColumnDecimalDigitsAttribute,
							headerColumnDecimalScaleAttribute,
							headerColumnDecimalPrecisionAttribute));
		return config;
	}

//	private void updateExternalMetaData(RemoteFileConfig config, URL url, InfoDao infoDao) throws SAXException, IOException {
//		TableMetaDataRetriever handler = TableMetaDataRetriever.getInstance(infoDao, config);
//		XMLReader xr = XMLReaderFactory.createXMLReader();
//		xr.setContentHandler(handler);
//		
//		XZInputStream zis = new XZInputStream(new BufferedInputStream(url.openStream()));
//		InputSource is = new InputSource(new InputStreamReader(new AsfkiFilter(zis) ,"UTF-8"));
//	
//		try {
//			xr.parse(is);
//		} catch (ExpectedSaxException e) {
//			System.out.println(e.getMessage());
//		}
//		
//	}
	private void convertAndLoadToDb(List<Db2Table> localTablesList,URL url, Db2FileLoadProps db2FileProps, InfoDao infoDao, RemoteFileConfig config, ErrorManager errorManager, ExecutorService executorService) throws Exception {
    		// Создаем сакс ридер
			XMLReader xr = XMLReaderFactory.createXMLReader();
    		// Создаем обработчик загрузки
    		AsfkiHandler bodyReaderHandler = AsfkiHandler.getInstance(errorManager,db2FileProps, executorService, delimeter, rowTag, columnTag);
    		// Создаем обработчик всего файла
    		TableMetaDataRetriever handler = TableMetaDataRetriever.getInstance(localTablesList,infoDao, config, xr, bodyReaderHandler);
    		// Даем саксу наш обработчик
    		xr.setContentHandler(handler);
       		
       		// Открываем канал из урла, буфферезуем и прогоняем через LZMA декодер
    		XZInputStream zis = new XZInputStream(new BufferedInputStream(url.openStream()));
    		// Пропускаем через свой фильтр, декодируем в буквы по утф8 и преобразуем в понятный саксу сорс
    		InputSource is = new InputSource(new AsfkiCharFilter(new InputStreamReader(new AsfkiFilter(zis,delimeter.trim().charAt(0)) ,"UTF-8")));
            // Собственно запускаем сакс с нашим обработчиком      
    		xr.parse(is);
    		// Закрываем канал
    		zis.close();
    		logger.debug("-------------------------------------------------------------");
	}
	
	private Db2FileLoadProps createDb2FilePropsUrl(URL url) throws Exception {
		
		String fileNameWithExtention = new File(url.getPath()).getName();
		String fileName = fileNameWithExtention.substring(0, fileNameWithExtention.length()-archiveExtention.length());
		
		String db2FilePath = tempFolder + "/" + fileName + db2lExtention;
		File db2File = createFile(db2FilePath);
		// Create db2load properties for the file
		String absolutePath = db2File.getAbsolutePath();
		String db2FilePathForLoad = absolutePath.replaceAll("\\\\", "\\\\\\\\");
	
		Db2FileLoadProps db2fProperties = new Db2FileLoadProps();
		db2fProperties.setAbsPathToLogFolder(tempFolder);
		db2fProperties.setDelimeter(delimeter);
		db2fProperties.setSchema(schema);
		db2fProperties.setAbsPathToFile(db2FilePathForLoad);
		db2fProperties.setTable(fileName);
		return db2fProperties;

	}
	
	@Override
	public void run() {
		
		// Initializing
		initConfig(); 
		if (args != null && args.length > 0) {
			initArgs();
			}
		initVm();
		
		createConnectionToDatabase();
		
		initSecurity();
		
		// Starting job
		logger.info("Starting job");
		Date startTime = new Date();
				
		
		clean(tempFolder);
		createFolder(tempFolder);
		createFolder(errorFolder);
		
		try {
			List<URL> list = new ArrayList<URL>();
			if (regularJob) {
				list.addAll(getListToUpdate());
			}
			if (additionalUrls != null) {
				list.addAll(parseUrls(additionalUrls));
			}
			
			
			// Создаем строку для вывода всех таблиц требующих обновления,
			// плюс создаем список для сравнения метаданных
			List<Db2Table> localTablesList = new ArrayList<Db2Table>();
			StringBuilder sb = new StringBuilder();
			for (URL url : list) {
				String fileNameWithExtention = new File(url.getPath()).getName();
				String fileName = fileNameWithExtention.substring(
						0, fileNameWithExtention.length()-archiveExtention.length());
				Db2Table db2Table = new Db2Table();
				db2Table.setName(fileName);
				db2Table.setSchema(schema);
				db2Table.setTableParams(tableParams);
				localTablesList.add(db2Table);
				List<Db2Column> colList = new ArrayList<Db2Column>();
				db2Table.setColumns(colList);
				sb.append(fileName).append(" ");
			}
			// Показать в логе список таблиц, требующих обновления
			logger.info("Table files to update: " + sb.toString());
			
			
			// Update table
			LIST_SIZE = list.size();
			
			ErrorManager errorManager = ErrorManager.getInstance(new File(errorFolder), localTablesList);
			if (list.size() > 0) {
				
				// Выгружаем метаданные из локальной базы данных
				DataSource dataSource = YanushDataSource.getInstance(user, password, driver, dbUrl);
				Connection connection = dataSource.getConnection();
				InfoDao infoDao = DaoFactory.getInfoDao(connection);
				
				if (!forceTableCreation) {
					infoDao.updateTablesMetaData(localTablesList);
				}
			
				ExecutorService executorService = Executors.newCachedThreadPool();
				
				// Собственно грузим данные в базу
				RemoteFileConfig config = getConfig();
				for (URL url : list) {
					Db2FileLoadProps db2FileLoadProperties = createDb2FilePropsUrl(url);
					convertAndLoadToDb(localTablesList,url, db2FileLoadProperties, infoDao, config, errorManager, executorService);
				} 
				if (connection != null) {
					connection.close();
				}
				executorService.shutdown();
				executorService.awaitTermination(1, TimeUnit.MINUTES);
				
				if (regularJob) {
					updateList(errorManager.getErrorTablesList());
				}
			}
			else {
				logger.info("Everything is up to date");
			}
			errorManager.sendToMail(mailTo);
			} catch (Exception e) {
			logger.error("Unpredictable error", e);
		
		}
		finally {
			
			clean(tempFolder);
			clean(errorFolder);
		
		}
		//Sending error by email
	
		Date endTime = new Date();
		long jobTime = endTime.getTime() - startTime.getTime();
		String jobPrettyTime = UsefulMethods.millisToLongDHMSeng(jobTime);
		
		logger.info("Elapsed time: " + jobPrettyTime);
		logger.info("End of job");
		logger.info("--------------------------------------------------");
	}
	





	public static void main(String[] args) throws MalformedURLException {

			try{
				new UpdateAsfkiFilesJob(args).run();
			} catch (Exception e) {
				logger.error("Program stoped due to an error\n",e);
			}

	}

}
