package rw.asfki.dao;

import java.io.IOException;
import java.sql.Connection;

import rw.asfki.dao.impl.Db2InfoDaoJdbcImpl;
import rw.asfki.dao.impl.Db2LoadDaoClpImpl;
import rw.asfki.error.ErrorManager;

public class DaoFactory {
	public static InfoDao getInfoDao(Connection connection) {
		return Db2InfoDaoJdbcImpl.getInstance(connection);
	 }
	public static DB2LoadDAO getDbLoadDao(ErrorManager errorManager) throws IOException {
		return Db2LoadDaoClpImpl.getInstance(errorManager);
	}
}

