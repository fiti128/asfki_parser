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
/**
 * Интерфейс для загрузки данных в базу данных, используя 
 * процедуру db2load. Следует приминять с осторожностью, 
 * т.к. данная процедура официально не поддерживается и 
 * подробно не задокументированна. Как минимум известно, что
 * не следует несколькими потоками грузить через лоад в одну таблицу.
 * Применять только с разрешения Вашего руководителя.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 * @see #loadFile(Db2FileLoadProps)
 * @see #loadFile(String, String, String, String, String)
 * @see #loadFromQueue(Queue)
 * @see #cleanTables(List, String)
 *
 */
public interface DB2LoadDAO {
	public void loadFile(String absPathToFile, String delimeter,
			String absPathToLogFile, String schema, String table)
			throws SQLException;
	/**
	 * Загружает файлы, используя настройки для загрузки по каждому файлу
	 * @param db2File
	 * @throws SQLException
	 */
	public void loadFile(Db2FileLoadProps db2File) throws SQLException;
	
	/**
	 * Удаляет все строки в заданном списке таблиц(очищает таблицы)
	 * @param cleanList
	 * @param schema
	 * @throws SQLException
	 */
	public void cleanTables(List<String> cleanList, String schema)
			throws SQLException;
	
	/**
	 * Загружает группу файлов, используя очередь с настройками для загрузки по каждому файлу
	 * @param db2probs
	 * @throws SQLException
	 */
	public void loadFromQueue(Queue<Db2FileLoadProps> db2probs)
			throws SQLException;
}
