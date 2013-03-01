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
 *  ��������� ������������ ��� ������ ������, �������������� ����� �������
 * ���� ������ ��� ������� ��������� ASFKI. 
 * <p>
 * ����� <code> hasNext()</code> ������ ��������� true, ���� ���� ��� �������� �����,
 * false - ���� ��� ������ ��� ������ �������.
 * 
 * <p>
 * 
 * ����� <code> next() </code>  ������ ��������� ������ ������ ������� ����� ������ �������. �.�. � ������ ������ ����, 
 * ������������� � �������� ����� ������. (��� ������� ���� ������������� � ��������).
 * 
 * 	@author Yanusheusky S.
 *	@since 27.02.2013
 *	@see #hasNext()
 *	@see #next()
 *	@see #close()
 */
public interface Reader {
	/**
	 * ����� <code> hasNext()</code> ������ ��������� true, ���� ���� ��� �������� �����,
	 * false - ���� ��� ������ ��� ������ �������.
	 * @return ���������� ����������� ������� ��������� ������
	 */
	public boolean hasNext();
	/**
	 * ����� <code> next() </code>  ������ ��������� ������ ������ ������� ����� ������ �������. �.�. � ������ ������ ����, 
	 * ������������� � �������� ����� ������. (��� ������� ���� ������������� � ��������).
	 * @return ������ ������ �������
	 */
	public List<ASFKI_RowColumn> next();
	/**
	 * ��������� �����
	 */
	public void close();
	

}
