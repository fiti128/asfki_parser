package rw.asfki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;



public class Test {

	
	private static String INPUT_FILE = "STRANA.xml";
	private static String FILTER_REGEX = "[^\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF[\\n\\r]]";
	private static String COLUMN_TAG = "col";
	private static String ROW_TAG = "row";
	private static String ROW_ATTRIBUTE = "num";
	private static String COLUMN_ATTRIBUTE = ROW_ATTRIBUTE;
	private static String ROOT_TAG = "table";
	
	public static void main(String[] args) throws IOException {

		List<String> rowAttributesList = new ArrayList<String>();
		rowAttributesList.add(ROW_ATTRIBUTE);
		List<String> columnAttributesList = new ArrayList<String>();
		columnAttributesList.add(COLUMN_ATTRIBUTE);
		Reader reader = new AsfkiReader.Builder(INPUT_FILE, ROW_TAG, COLUMN_TAG)
			.rowAttributes(rowAttributesList)
			.columnAttributes(columnAttributesList)
			.bodyRegexFilter(FILTER_REGEX)
			.rootTag(ROOT_TAG)
			.header(true)
			.build();
		
		
		File file = new File("temp/test.txt");
		// if file exists, then delete it
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		
		
		while(reader.hasNext()) {
			List<String> list = reader.next();
			
			Db2lWriter writer = new Db2lWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true)));
			writer.write(list);
			writer.flush();
			writer.close();
		}
		
		
	

	}

}
