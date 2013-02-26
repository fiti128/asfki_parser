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
          res.append(temp).append(temp > 1 ? " ����" : " ����")
             .append(duration >= ONE_MINUTE ? ", " : "");
        }

        temp = duration / ONE_HOUR;
        if (temp > 0) {
          duration -= temp * ONE_HOUR;
          res.append(temp).append(temp > 1 ? " �����" : " ���")
             .append(duration >= ONE_MINUTE ? ", " : "");
        }

        temp = duration / ONE_MINUTE;
        if (temp > 0) {
          duration -= temp * ONE_MINUTE;
          res.append(temp).append(temp > 1 ? " �����" : " ������");
        }

        if (!res.toString().equals("") && duration >= ONE_SECOND) {
          res.append(" � ");
        }

        temp = duration / ONE_SECOND;
        if (temp > 0) {
          res.append(temp).append(temp > 1 ? " ������" : " �������");
        }
        return res.toString();
      } else {
        return "0 ������";
      }
    }

	
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
