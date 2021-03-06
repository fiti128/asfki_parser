/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * ����� ������ �� �������� � SimpleDateFormat,
 * �� ��� ������ � Timestamp. 
 * ������ ������ �������� ��������������.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 *
 */
public class SimpleTimestampFormat extends TimestampFormat {
	private static Logger logger = Logger.getLogger(SimpleTimestampFormat.class);
	private String timestampPattern;
	private static String DEFAULT_PATTERN = "yyyy-mm-dd-hh.mm.ss.nnnnnn";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5435287139000135642L;
	
	public SimpleTimestampFormat() {
		this.timestampPattern = DEFAULT_PATTERN;
	}
	public SimpleTimestampFormat (String pattern) {
		this.timestampPattern = pattern;
	}
	@Override
	public Timestamp parse(String source, ParsePosition pos) {
		String beforeFormatDate = source;
		
		
		Pattern pattern = Pattern.compile("n+");
		Matcher matcher = pattern.matcher(timestampPattern);
		int nanoLength = 0;
		while (matcher.find()) {
			nanoLength = matcher.group().length();
		}
		
		Timestamp ts = null;

//		if (nanoLength == 6 && source.length()== 26) {
		if (nanoLength == 6) {
			char[] chars = beforeFormatDate.toCharArray();
		    chars[10] = ' ';
		    chars[13] = ':';
		    chars[16] = ':';
		    beforeFormatDate = new String(chars);
		    ts = Timestamp.valueOf(beforeFormatDate);
		}
		else {	
		String cutFormatDate = beforeFormatDate.substring(0, beforeFormatDate.length() - nanoLength - 1);
		String cutFormatToParse = timestampPattern.substring(0, timestampPattern.length() - nanoLength - 1);
		SimpleDateFormat parserSDF = new SimpleDateFormat(cutFormatToParse);
		Date date;
		try {
			logger.debug(cutFormatDate);
			date = parserSDF.parse(cutFormatDate);
			ts = new Timestamp(date.getTime());
			String nanos = beforeFormatDate.substring(beforeFormatDate.length() - nanoLength);
			ts.setNanos(ts.getNanos() + Integer.valueOf(nanos)*1000);
			
		} catch (ParseException e) {
			e.getMessage();
			e.printStackTrace();
		}
		}
		return ts;
	}
	
	public Timestamp parse(String source) {
		return parse(source,null);
	}
}
