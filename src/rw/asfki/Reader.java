/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.util.List;

import rw.asfki.domain.ASFKI_RowColumn;
/**
 *  Интерфейс предназначен для чтения файлов, представляющих собой внешнюю
 * базу данных под кодовым названием ASFKI. 
 * <p>
 * Метод <code> hasNext()</code> должен возращать true, если есть еще валидный токен,
 * false - если уже больше нет ничего ценного.
 * 
 * <p>
 * 
 * Метод <code> next() </code>  должен возращать список данных колонок одной строки таблицы. Т.е. в списке каждый член, 
 * ассоциируется с колонкой одной строки. (Сам входной файл ассоциируется с таблицей).
 * 
 * 	@author Yanusheusky S.
 *	@since 27.02.2013
 *	@see #hasNext()
 *	@see #next()
 *	@see #close()
 */
public interface Reader {
	/**
	 * Метод <code> hasNext()</code> должен возращать true, если есть еще валидный токен,
	 * false - если уже больше нет ничего ценного.
	 * @return логическое определение наличия валидного токена
	 */
	public boolean hasNext();
	/**
	 * Метод <code> next() </code>  должен возращать список данных колонок одной строки таблицы. Т.е. в списке каждый член, 
	 * ассоциируется с колонкой одной строки. (Сам входной файл ассоциируется с таблицей).
	 * @return список данных колонок
	 */
	public List<ASFKI_RowColumn> next();
	/**
	 * Закрывает ридер
	 */
	public void close();
	

}
