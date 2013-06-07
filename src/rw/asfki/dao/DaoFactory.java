package rw.asfki.dao;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import rw.asfki.dao.impl.Db2InfoDaoJdbcImpl;
import rw.asfki.dao.impl.Db2LoadDaoClpImpl;
import rw.asfki.error.ErrorManager;

public class DaoFactory {
	public static InfoDao getInfoDao(DataSource dataSource) {
		return Db2InfoDaoJdbcImpl.getInstance(dataSource);
	 }
	public static DB2LoadDAO getDbLoadDao(ErrorManager errorManager) throws IOException {
		return Db2LoadDaoClpImpl.getInstance(errorManager);
	}
}

