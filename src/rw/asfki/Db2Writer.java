package rw.asfki;

import java.io.IOException;
import java.util.List;

public interface Db2Writer {
	public void writeLine(List<String> stringList) throws Exception;
	public void close() throws IOException;
	public void flush() throws IOException;
}
