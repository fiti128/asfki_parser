package rw.asfki.dao;

import java.io.IOException;
import java.sql.Connection;

import rw.asfki.dao.impl.Db2InfoDaoJdbcImpl;

import rw.asfki.dao.impl.Db2LoadDaoClpUtf8Impl;
import rw.asfki.dao.impl.Db2LoadDaoClpWin1251Impl;
import rw.asfki.error.ErrorManager;

public class DaoFactory {
	public static InfoDao getInfoDao(Connection connection) {
		return Db2InfoDaoJdbcImpl.getInstance(connection);
	 }
	public static DB2LoadDAO getDbLoadDao(ErrorManager errorManager, String encoding) throws IOException {
		DB2LoadDAO loadDao = (("UTF-8").equals(encoding)) ?
				Db2LoadDaoClpUtf8Impl.getInstance(errorManager) :  
				Db2LoadDaoClpWin1251Impl.getInstance(errorManager);
		return loadDao;
	}
	public static DB2LoadDAO getDbLoadDao(ErrorManager errorManager) throws IOException {
		return Db2LoadDaoClpWin1251Impl.getInstance(errorManager);
	}
}

