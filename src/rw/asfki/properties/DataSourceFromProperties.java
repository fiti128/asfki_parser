/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.properties;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.util.UsefulMethods;
/**
 * Класс отвечает за настройку 
 * соединения с базой данных. Все данные беруться из
 * соответствующего файла с пропертей. 
 * <p>Реализован только <p> <code> getConnection() <code>
 * <p> Остальные методы не делают ничего
 * @author Yanusheusky S.
 * @since 27.02.2013
 * 
 */
public class DataSourceFromProperties implements DataSource {
	protected static Logger logger = Logger.getLogger("service");
	private String username;
	private String password;
	private String driver;
	private String db2Url;
	
	public DataSourceFromProperties() {
		
		try {
		 Properties	dbProps = UsefulMethods.loadProperties("database.properties");
		 username = dbProps.getProperty("username");
		 password = dbProps.getProperty("password");
		 driver = dbProps.getProperty("driver");
		 db2Url = dbProps.getProperty("db2Url");
		} catch (Exception e) {
			logger.error("Database properties are not loaded");
		}
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.error("Driver for database was not found");
		}
	}
	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(db2Url, username, password);
	}

	@Override
	public Connection getConnection(String theUsername, String thePassword)
			throws SQLException {
		return DriverManager.getConnection(db2Url, theUsername, thePassword);
	}

}
