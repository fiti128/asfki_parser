package rw.asfki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Test {

	
	private static String INPUT_FILE = "KOD_DET_VAG.xml";
	private static String FILTER_REGEX = "[^\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF[\\n\\r]]";
	private static String COLUMN_TAG = "col";
	private static String ROW_TAG = "row";
	private static String ROW_ATTRIBUTE = "num";
	private static String COLUMN_ATTRIBUTE = ROW_ATTRIBUTE;
	private static String ROOT_TAG = "table";
	
	public static void main(String[] args) throws Exception {

		String beforeFormatDate = "2011-07-01-10.03.36.328001";
		String formatToParse = "yyyy-mm-dd-hh.mm.ss.nnnnnn";
		System.out.println(formatToParse.contains("n"));
		Pattern pattern = Pattern.compile("n+");
		Matcher matcher = pattern.matcher(formatToParse);
		int nanoLength = 0;
		while (matcher.find()) {
			System.out.println(matcher.group().length());
			System.out.println(matcher.group());
			nanoLength = matcher.group().length();
		}
		String cutFormatDate = beforeFormatDate.substring(0, beforeFormatDate.length() - nanoLength - 1);
		String cutFormatToParse = formatToParse.substring(0, formatToParse.length() - nanoLength - 1);
		SimpleDateFormat parserSDF = new SimpleDateFormat(cutFormatToParse);
		Date date = parserSDF.parse(cutFormatDate);
		SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
		String newDateString = parser.format(date);
		System.out.println(newDateString);


		Timestamp ts = new Timestamp(date.getTime());
		String nanos = beforeFormatDate.substring(beforeFormatDate.length() - nanoLength);
		ts.setNanos(ts.getNanos() + Integer.valueOf(nanos)*1000);
		System.out.println(ts);
		

	}

}
