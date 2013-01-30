package rw.asfki.util;

import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

public abstract class TimestampFormat extends Format {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Not implemented yet
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		// TODO Auto-generated method stub
		return parse(source,pos);
	}

	public abstract Timestamp parse(String source, ParsePosition pos); 	

}
