/*
 * Класс и код в нем принадлежит Белорусской Железной Дороге.
 * Использование разрешается только внутри БЖД.
 */
package rw.asfki.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Класс, включающие в себя пару полезных методов.
 * Все методы в классе - статические.
 * 
 * @author Yanusheusky S.
 * @since 21.02.2012
 */
public class UsefulMethods {
	
	public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;


    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
      StringBuffer res = new StringBuffer();
      long temp = 0;
      if (duration >= ONE_SECOND) {
        temp = duration / ONE_DAY;
        if (temp > 0) {
          duration -= temp * ONE_DAY;
          res.append(temp).append(temp > 1 ? " дней" : " день")
             .append(duration >= ONE_MINUTE ? ", " : "");
        }

        temp = duration / ONE_HOUR;
        if (temp > 0) {
          duration -= temp * ONE_HOUR;
          res.append(temp).append(temp > 1 ? " часов" : " час")
             .append(duration >= ONE_MINUTE ? ", " : "");
        }

        temp = duration / ONE_MINUTE;
        if (temp > 0) {
          duration -= temp * ONE_MINUTE;
          res.append(temp).append(temp > 1 ? " минут" : " минута");
        }

        if (!res.toString().equals("") && duration >= ONE_SECOND) {
          res.append(" и ");
        }

        temp = duration / ONE_SECOND;
        if (temp > 0) {
          res.append(temp).append(temp > 1 ? " секунд" : " секунда");
        }
        return res.toString();
      } else {
        return "0 секунд";
      }
    }

	
	/**
	 *  Метод читает внешние <code> Properties </code>
	 *  <p>Если там нет, читает внутренние
	 * @param Имя файла пропертей
	 * @return Пропертя
	 * @throws Кидает IOException только если в самом джарнике(программе) нету такого файла.
	 */
	
	public static Properties loadProperties(String name) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = null;
		
		// Сперва программа читает внешний файл
		try {
			inputStream = new FileInputStream(name);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
		}
		// Если там ничего, то внутренний
		if (inputStream == null)
			inputStream = prop.getClass().getResourceAsStream(name);

		prop.load(inputStream);
		if (inputStream != null)
			inputStream.close();

		return prop;
	}
}
