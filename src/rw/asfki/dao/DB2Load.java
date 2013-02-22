package rw.asfki.dao;

import java.sql.SQLException;

import rw.asfki.domain.Db2File;

public interface DB2Load {
	public void loadFile(String absPathToFile, String delimeter,String absPathToLogFile,
			String schema, String table) throws SQLException;
	public void loadFile(Db2File db2File) throws SQLException;
}
