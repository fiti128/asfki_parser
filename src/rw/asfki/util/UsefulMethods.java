/*
 * ����� � ��� � ��� ����������� ����������� �������� ������.
 * ������������� ����������� ������ ������ ���.
 */
package rw.asfki.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * �����, ���������� � ���� ���� �������� �������.
 * ��� ������ � ������ - �����������.
 * 
 * @author Yanusheusky S.
 * @since 21.02.2012
 */
public class UsefulMethods {
	/**
	 *  ����� ������ ������� <code> Properties </code>
	 *  <p>���� ��� ���, ������ ����������
	 * @param ��� ����� ���������
	 * @return ��������
	 * @throws ������ IOException ������ ���� � ����� ��������(���������) ���� ������ �����.
	 */
	
	public static Properties loadProperties(String name) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = null;
		
		// ������ ��������� ������ ������� ����
		try {
			inputStream = new FileInputStream(name);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
		}
		// ���� ��� ������, �� ����������
		if (inputStream == null)
			inputStream = prop.getClass().getResourceAsStream(name);

		prop.load(inputStream);
		if (inputStream != null)
			inputStream.close();

		return prop;
	}
}
