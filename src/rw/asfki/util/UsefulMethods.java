/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
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
			System.out.println(new File(name).getAbsolutePath());
		
		// Сперва программа читает внешний файл
		try {
			inputStream = new FileInputStream(name);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
		}
		// Если там ничего, то внутренний
		if (inputStream == null)
			inputStream = UsefulMethods.class.getResourceAsStream("/" + name);

		prop.load(inputStream);
		if (inputStream != null)
			inputStream.close();

		return prop;
	}
	
	public static void copyFile(File sourceFile, File destFile, final boolean isOverwrite) throws IOException {
		  if (destFile.isDirectory())
		     destFile = new File(destFile, sourceFile.getName());
		  if (destFile.exists())
		  {
		     if (isOverwrite)
		        destFile.delete();
		     else
		        throw new IOException(destFile.getAbsolutePath() + " exists");
		  }
		  if (!destFile.exists()) {
		    destFile.createNewFile();
		  }
		  FileInputStream fIn = null;
		  FileOutputStream fOut = null;
		  FileChannel source = null;
		  FileChannel destination = null;
		  try {
		    fIn = new FileInputStream(sourceFile);
		    source = fIn.getChannel();
		    fOut = new FileOutputStream(destFile);
		    destination = fOut.getChannel();
		    long transfered = 0;
		    long bytes = source.size();
		    while (transfered < bytes) {
		      transfered += destination.transferFrom(source, 0, source.size());
		      destination.position(transfered);
		    }
		  } finally {
		    if (source != null) {
		      source.close();
		    } else if (fIn != null) {
		      fIn.close();
		    }
		    if (destination != null) {
		      destination.close();
		    } else if (fOut != null) {
		      fOut.close();
		    }
		  }
		}
}
