package rw.asfki;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;

public class Db2lWriter extends Writer {
	protected static Logger logger = Logger.getLogger("service");
	private static String DEFAULT_DELIMETER = "|";
	private String columnDelimeter = DEFAULT_DELIMETER;
	private BufferedWriter bw;

	Db2lWriter(BufferedWriter bw) {
		this.bw = bw;
	}

	Db2lWriter(BufferedWriter bw, String columnDelimeter) {
		this.bw = bw;
		this.columnDelimeter = columnDelimeter;
	}

	public void write(List<String> stringList) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringList.size() - 1; i++) {
			sb.setLength(0);
			sb.append(stringList.get(i)).append(columnDelimeter);
			bw.write(sb.toString());
		}
		bw.write(stringList.get(stringList.size() - 1));
		bw.newLine();
		logger.info("line written");

	}
	public void flush() throws IOException {
		bw.flush();
	}
	public void close() throws IOException {
		bw.close();
	}
/**
 * Not implemented. Just standart BufferedWriter <code>write</code>
 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		bw.write(cbuf, off, len);
		bw.newLine();
		
	}

	public String getColumnDelimeter() {
		return columnDelimeter;
	}

	public void setColumnDelimeter(String columnDelimeter) {
		this.columnDelimeter = columnDelimeter;
	}
	
}
