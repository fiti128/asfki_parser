package rw.asfki;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rw.asfki.util.SimpleTimestampFormat;




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
		
		SimpleTimestampFormat stf = new SimpleTimestampFormat(formatToParse);
		Timestamp ts = stf.parse(beforeFormatDate);
		System.out.println(ts);
//		Timestamp nts = new Timestamp(1111,11,11,11,11,11,111111*1000);
		Timestamp nts = Timestamp.valueOf("1111-11-11 11:11:11.111111");
		System.out.println(nts);

	}

}
