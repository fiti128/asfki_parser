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
