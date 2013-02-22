package rw.asfki.dao;

import java.sql.SQLException;
import java.util.List;

import rw.asfki.domain.Db2FileLoadProps;

public interface DB2Load {
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException;

	public void loadFile(Db2FileLoadProps db2File) throws SQLException;

	public void cleanTables(List<String> cleanList, String schema)
			throws SQLException;
}
