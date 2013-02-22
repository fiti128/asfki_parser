package rw.asfki;

public interface DefaultParams {
	public final static String DEFAULT_TIME = "1111-11-11 11:11:11.111111";
	public final static String DEFAULT_INPUT_FILE = "spisok.xml";
	public final static String DEFAULT_FILTER_REGEX = "[^\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF[\\n\\r]]";
	public final static String DEFAULT_COLUMN_TAG = "col";
	public final static String DEFAULT_ROW_TAG = "row";
	public final static String DEFAULT_ROW_ATTRIBUTE = "num";
	public final static String DEFAULT_COLUMN_ATTRIBUTE = DEFAULT_ROW_ATTRIBUTE;
	public final static String DEFAULT_ROOT_TAG = "table";
	public final static String DEFAULT_SPISOK_URL_FOLDER = "http://ircm-srv.mnsk.rw/ASFKI_XML/";
	public final static String DEFAULT_SPISOK_FILE_NAME = "spisok.xml";
	public final static String DEFAULT_DOWNLOAD_FOLDER = "temp";
	public final static String DEFAULT_ASFKI_DB2_FOLDER = "asfki_db2_files";
	public final static String DEFAULT_ARCHIVE_EXTENTION = ".zip";
	public final static String DEFAULT_DB2L_EXTENTION = ".txt";
	public final static String DEFAULT_SPISOK_COLUMN_ATTRIBUTE1 = "cor_time";
	public final static String DEFAULT_SPISOK_COLUMN_ATTRIBUTE2 = "format";
	public final static String DEFAULT_DB2FILE_DELIMETER = "0x7c";
	public final static String DEFAULT_ABSOLUTE_PATH_TO_LOG = "D:\\\\itsmylog.txt";
	public final static String DEFAULT_SCHEMA = "IA00";
}
