/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


import rw.asfki.domain.ASFKI_RowColumn;
/**
 * Класс для создания файлов, пригодных для загрузки в дб2
 *  
 * @author Yanusheusky S.
 * @since 27.02.2013
 * @see #writeLine(List)
 *
 */
public class Db2lWriter extends Writer {
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
/**
 *  Из листа пишет одну строчку в файл. Строчка в файле соответствует записи(строчки) 
 *  в базе данных.
 *  
 * @param stringList
 * @throws Exception
 */
	public void writeLine(List<String> stringList) throws Exception {
		if (stringList == null | stringList.size() == 0) throw new Exception("No data to write");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringList.size() - 1; i++) {
			sb.setLength(0);
			sb.append(stringList.get(i)).append(columnDelimeter);
			bw.write(sb.toString());
		}
		bw.write(stringList.get(stringList.size() - 1));
		bw.newLine();

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
/**
 * 
 * @param list
 * @throws Exception
 */
	public void writeDomain(List<ASFKI_RowColumn> list) throws Exception {
		if (list == null | list.size() == 0) throw new Exception("No data to write");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size() - 1; i++) {
			sb.setLength(0);
			sb.append(list.get(i).getBody()).append(columnDelimeter);
			bw.write(sb.toString());
		}
		bw.write(list.get(list.size() - 1).getBody());
		bw.newLine();
	}

	public String getColumnDelimeter() {
		return columnDelimeter;
	}

	public void setColumnDelimeter(String columnDelimeter) {
		this.columnDelimeter = columnDelimeter;
	}
	
}
