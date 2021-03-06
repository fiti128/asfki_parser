/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Queue;

import rw.asfki.domain.Db2FileLoadProps;
import rw.asfki.domain.Db2Table;
/**
 * ��������� ��� �������� ������ � ���� ������, ��������� 
 * ��������� db2load. ������� ��������� � �������������, 
 * �.�. ������ ��������� ���������� �� �������������� � 
 * �������� �� ������������������. ��� ������� ��������, ���
 * �� ������� ����������� �������� ������� ����� ���� � ���� �������.
 * ��������� ������ � ���������� ������ ������������.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 * @see #loadFile(Db2FileLoadProps)
 * @see #loadFile(String, String, String, String, String)
 * @see #loadFromQueue(Queue)
 * @see #cleanTables(List, String)
 *
 */
/**
 * @author Yanusheusky S.
 *
 */
public interface DB2LoadDAO {
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException;
	/**
	 * ��������� �����, ��������� ��������� ��� �������� �� ������� �����
	 * @param db2File
	 * @throws SQLException
	 */
	public void loadFile(Db2FileLoadProps db2File) throws SQLException;
	
	/**
	 * ������� ��� ������ � �������� ������ ������(������� �������)
	 * @param cleanList
	 * @param schema
	 * @throws SQLException
	 * @throws Exception 
	 */
	public void cleanTables(List<String> cleanList, String schema)
			throws Exception;
	
	/**
	 * ��������� ������ ������, ��������� ������� � ����������� ��� �������� �� ������� �����
	 * @param db2probs
	 * @throws Exception
	 */
	public void loadFromQueue(Queue<Db2FileLoadProps> db2probs)
			throws Exception;
	
	
	/**
	 *  ������� �������
	 * @param db2Table
	 */
	public void createTable(Db2Table db2Table);
}
