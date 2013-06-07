package rw.asfki.dao;

import java.sql.SQLException;
import java.util.List;

import rw.asfki.domain.Db2Table;

/**
 * @author Yanusheusky S.
 *	@since 05.06.20013
 */
public interface InfoDao {
	
	/**
	 *  «аполн€ет список таблиц метаданными,
	 *  т.е. информацию о таблице, колонках...
	 * @param db2TablesList
	 * @throws SQLException 
	 */

	public void updateTablesMetaData(List<Db2Table> db2TablesList) throws SQLException;
}
