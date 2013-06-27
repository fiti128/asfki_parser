/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.io.IOException;
import java.util.List;

public interface Db2Writer {
	public void writeLine(List<String> stringList) throws Exception;
	public void close() throws IOException;
	public void flush() throws IOException;
}
