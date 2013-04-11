/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 *  
 */
package rw.asfki;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rw.asfki.domain.ASFKI_RowColumn;
/**
 * ����� ������������ ��� ������ ������, �������������� ����� �������
 * ���� ������ ��� ������� ��������� ASFKI. 
 * <p>
 * ����� ������� �� ������  <code> Scanner </code>, ������� � 
 * ������ ����������� ����� � ���������������� �������� � <code> Scanner </code> 
 * ��� ����� �������� ���������. 
 * 
 * <p>
 * ����� <code> hasNext()</code> ��������� true, ���� ���� ��� �������� �����,
 * false - ���� ��� ������ ��� ������ �������.
 * 
 * <p>
 * 
 * ����� <code> next() </code> ��������� ������ ������ ������� ����� ������ �������. �.�. � ������ ������ ����, 
 * ������������� � �������� ����� ������. (��� ������� ���� ������������� � ��������).
 * 
 * <p>
 * 
 * ����� ��������� � ������� ���������� ������ <code>Builder</code>
 * 
 *  * ������ ��������:
 * <p>
 * <code>
 * AsfkiReader reader = new AsfkiReader.Builder(downloadedSpisok, spisokRowTag, spisokColumnTag)
			.rowAttributes(spisokRowAttributes)
			.columnAttributes(spisokColumnAttributes)
			.bodyRegexFilter(spisokFilterRegex)
			.rootTag(spisokRootTag)
			.build();
 * </code>
 * 
 * @author Yanusheusky S.
 * @see Builder
 * @see #hasNext()
 * @see #next()
 * @since 27.02.2013
 */
public class AsfkiReader implements Reader {
	private static String NEXT_TOKEN_REGEX = "(?s).*\\S+.*";
	private File file;
	private Scanner rootScanner;
	private Scanner rowScanner;
	private Scanner columnScanner;
	private String rowTag;
	private String columnTag;
	private String bodyRegexFilter;
	private List<String> rowAttributes;
	private List<String> columnAttributes;
	private boolean isFirstToken = true;
	private String columnDelimeterRegex;
	private String rootTag;
	
	
	private AsfkiReader(Builder builder) throws FileNotFoundException {
		this.rowTag = builder.rowTag;
		this.columnTag = builder.columnTag;
		this.rowAttributes = builder.rowAttributes;
		this.columnAttributes = builder.columnAttributes;
		this.bodyRegexFilter = builder.bodyRegexFilter;
		this.file = builder.file;
		this.rootTag = builder.rootTag;
		setRowDelimeter();
		setColumnDelimeterRegex();
	}



/**
 * ����������� ������ �� �������� ������(���������)
 * ��� �������� �������� �������� ������.
 * ��� ������ ������ ���������� � ����� <code> build() </code> 
 * ��� �������� ������� <code> AsfkiReader </code>.
 * 
 * ������ ��������:
 * <p>
 * <code>
 * AsfkiReader reader = new AsfkiReader.Builder(downloadedSpisok, spisokRowTag, spisokColumnTag)
			.rowAttributes(spisokRowAttributes)
			.columnAttributes(spisokColumnAttributes)
			.bodyRegexFilter(spisokFilterRegex)
			.rootTag(spisokRootTag)
			.build();
 * </code>
 * 
 * @author Yanusheusky S.
 *
 */
	public static class Builder {
		private final File file;
		private final String rowTag;
		private final String columnTag;
		private String bodyRegexFilter;
		private String rootTag;
		private List<String> rowAttributes;
		private List<String> columnAttributes;
		
		public Builder(File file, String rowTag, String columnTag) {
			this.file = file;
			this.rowTag = rowTag;
			this.columnTag = columnTag;
		}
		public Builder(String filePath, String rowTag, String columnTag) {
			this.file = new File(filePath);
			this.rowTag = rowTag;
			this.columnTag = columnTag;
		}
		public Builder bodyRegexFilter(String bodyRegexFilter) {
			this.bodyRegexFilter = bodyRegexFilter;
			return this;
		}
	
		public Builder rootTag(String rootTag) {
			this.rootTag = rootTag;
			return this;
		}
		public Builder rowAttributes(List<String> rowAttributes) {
			this.rowAttributes = rowAttributes;
			return this;
		}
		public Builder columnAttributes(List<String> columnAttributes) {
			this.columnAttributes = columnAttributes;
			return this;
		}
		
		public Reader build() throws FileNotFoundException {
			return new AsfkiReader(this);
		}
	
	
	}
/**
 * ����� <code> hasNext()</code> ��������� true, ���� ���� ��� �������� �����,
 * false - ���� ��� ������ ��� ������ �������.
 */
	@Override
	public boolean hasNext() {
		String rootEndTag = getRootEndTag();
		boolean hasNext = rowScanner.hasNext();
		boolean isNextTokenHaveSomeSymbols = rowScanner.hasNext(NEXT_TOKEN_REGEX);
		if (hasNext && !isNextTokenHaveSomeSymbols) {
			rowScanner.next();
			hasNext = hasNext();
		}
		if (rowScanner.hasNext(rootEndTag)) hasNext = false;
		return hasNext;
	}

	private String getRootEndTag() {
		StringBuilder sb = new StringBuilder();
		sb.append("</").append(rootTag).append(">");
		return sb.toString();
	}
/**
 * ����� <code> next() </code> ��������� ������ ������ ������� ����� ������ �������. �.�. � ������ ������ ����, 
 * ������������� � �������� ����� ������. (��� ������� ���� ������������� � ��������).
 */
	@Override
	public List<ASFKI_RowColumn> next() {
		String next = rowScanner.next();
		List<ASFKI_RowColumn> list = new ArrayList<ASFKI_RowColumn>();
		if (next.length() > 0) {
			// Just ignoring first token. Header expected
			if (isFirstToken) {
				isFirstToken = false;
				next = rowScanner.next();
			}
			StringBuilder sb = new StringBuilder();
			sb.append("</").append(rootTag).append(">");
			String rootEndTag = sb.toString();
			if (next.equals(rootEndTag)){
				return list;
			}
			columnScanner = new Scanner(next);
			columnScanner.useDelimiter(columnDelimeterRegex);

			while (columnScanner.hasNext()) {
				if (!columnScanner.hasNext(NEXT_TOKEN_REGEX)) {
					columnScanner.next();
					continue;
				}
				String colNext = columnScanner.next();
				
				
				Map<String, String> map = new HashMap<String,String>();
					for (String attribute : columnAttributes) {
						StringBuilder regexBuilder = new StringBuilder();
						regexBuilder.append(attribute).append("=\".+?\"");
						Pattern pattern = Pattern.compile(regexBuilder.toString());
						Matcher matcher = pattern.matcher(colNext);
						if (matcher.find()) {
							String wantedAttribute = matcher.group().split("\"")[1];
							map.put(attribute, wantedAttribute);
						}
					}
					int bodyIndex = colNext.indexOf(">") + 1;
					String body = colNext.substring(bodyIndex);
					if (bodyRegexFilter != null) {
						body = body.replaceAll(bodyRegexFilter, "");
					}
					body = body.trim();
					ASFKI_RowColumn rowColumn = new ASFKI_RowColumn();
					rowColumn.setAttributes(map);
					rowColumn.setBody(body);
					list.add(rowColumn);
			}
		
		}

		return list;
	}


	private void setRowDelimeter() throws FileNotFoundException {
		this.rowScanner = new Scanner(file, "UTF-8");
		StringBuilder sbDelimiter = new StringBuilder();
		sbDelimiter.append("(<").append(this.rowTag);
		if (this.rowAttributes != null) {
			for (String attribute: this.rowAttributes) {
				sbDelimiter.append(" ").append(attribute).append("=\".*?\"");
			}
		}
		sbDelimiter.append(">|</").append(rowTag).append(">|</")
			.append(rootTag).append(">)");
		rowScanner.useDelimiter(sbDelimiter.toString()); 
		
	}
	
	private void setColumnDelimeterRegex() {
		StringBuilder sbDelimeter = new StringBuilder();
		sbDelimeter.append("(<").append(this.columnTag).append("|</").append(this.columnTag).append(">)");
		columnDelimeterRegex = sbDelimeter.toString();
		
	}

	@Override
	public void close() {
		if (rootScanner != null) rootScanner.close();
		if (rowScanner != null)	rowScanner.close();
		if (columnScanner != null) columnScanner.close();
		
	}
}
