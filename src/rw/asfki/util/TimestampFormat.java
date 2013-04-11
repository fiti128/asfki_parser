/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.util;

import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
/**
 * Класс создан по аналогии с DateFormat, 
 * для наличии абстракции SimpleTimestampFormat.
 * 
 * @author Yanusheusky S.
 * @since 21.02.2013
 *
 */
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
