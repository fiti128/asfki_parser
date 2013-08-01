package rw.asfki.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;


import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class YanushDataSource implements DataSource {
	protected static Logger logger = Logger.getLogger(YanushDataSource.class);
	private String username;
	private String password;
	private String driver;
	private String db2Url;
	
	
	private YanushDataSource(String username, String password, String driver,
			String db2Url) {
		super();
		this.username = username;
		this.password = password;
		this.driver = driver;
		this.db2Url = db2Url;
	}
	public static DataSource getInstance(String username, String password, String driver,
			String db2Url) {
		return new YanushDataSource(username,password,driver,db2Url);
	}
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
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
		return getConnection(username, password);
	}

	@Override
	public Connection getConnection(String theUsername, String thePassword)
			throws SQLException {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.error("Driver was not found");
			throw new RuntimeException(e);
		}
		logger.debug(String.format("Achieving JDBC Connection with db2Url: %s, username: %s, password: %s",db2Url.toString(), theUsername, thePassword));
		Connection connection = DriverManager.getConnection(db2Url, theUsername, thePassword);
		logger.debug("Connection established successfully");
		return connection;
	}
	@Override
	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}



}
