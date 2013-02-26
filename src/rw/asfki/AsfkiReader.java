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
	private boolean header;
	private String columnDelimeterRegex;
	private String rootTag;
	
	
	private AsfkiReader(Builder builder) throws FileNotFoundException {
		this.rowTag = builder.rowTag;
		this.columnTag = builder.columnTag;
		this.rowAttributes = builder.rowAttributes;
		this.columnAttributes = builder.columnAttributes;
		this.bodyRegexFilter = builder.bodyRegexFilter;
		this.header = builder.header;
		this.file = builder.file;
		this.rootTag = builder.rootTag;
		// unimplemented yet
//		setRootDelimeter();
		setRowDelimeter();
		setColumnDelimeterRegex();
	}




	public static class Builder {
		private final File file;
		private final String rowTag;
		private final String columnTag;
		private String bodyRegexFilter;
		private boolean header = true;
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
			
		public Builder header(boolean header) {
			this.header = header;
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

	@Override
	public List<ASFKI_RowColumn> next() {
		String next = rowScanner.next();
		List<ASFKI_RowColumn> list = new ArrayList<ASFKI_RowColumn>();
		if (next.length() > 0) {
			// Just ignoring first token. Header expected
			if (header) {
				header = false;
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
