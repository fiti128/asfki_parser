/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki;

import java.sql.SQLException;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.domain.*;
import rw.asfki.dao.DB2LoadDAO;
import rw.asfki.dao.impl.DB2LoadDAOJDBCImpl;
/**
 * ����� ������ ��� ������������� ������ �� �������. ���� - ������� � ���� ��������, ��� ������ 
 * ��� ���������� � ������. ������� ��� �������� ������ ��� ��������� ��� ������, � ������
 * ���������� � ����� ������.
 * <p>
 * ������ ����� �������� ��� <code>Thread</code>, �� �� ����������� �� ����.
 * �������� ��� ���������� ���������� ���������, ������ ����������� ������������� ��
 * Oracle.
 * <p>
 * � ������ <code>run()</code>, ����� ������ � ����� �������� ��
 * ��� ��� ���� � ������ �� ����� ������ ������ ��������. ��� ������� ������ ������ �������� 
 * ���������� ����� �������� ���� ��������� �� ������ ������ � ������ � ���� ������. 
 * 
 * <p>
 * ����� <code>stop()</code> ������������� �����, �� ������ ����� ���� ��� � ������ �� ��������� �� ������ ��������.
 * �.�. ���� ���������� ������������ ����� �� �����. 
 * <p>
 * ����� <code>isAlive()</code> ���������� ���������� �� ����������� ����� ����� ������.
 * <p>
 * ����� <code>start()</code> ��������� ������
 * 
 *  @see #start()
 *  @see #run()
 *  @see #stop()
 *  @see #isAlive()
 * 	@author Yanusheusky S.
 *	@since 27.02.2013
 */
public class Db2LoadFromQueueTask implements Runnable {
	protected static Logger logger = Logger.getLogger("service");
	private Queue<Db2FileLoadProps> queue;
	private volatile Thread thisThread;
	private DB2LoadDAO db2load;
	private volatile boolean stopFlag;

	Db2LoadFromQueueTask(Queue<Db2FileLoadProps> queue, DataSource dataSource) {
		this.queue = queue;
		this.db2load = new DB2LoadDAOJDBCImpl(dataSource);
	}
	/**
	 * ����� <code>isAlive()</code> ���������� ���������� �� ����������� ����� ����� ������.
	 * @return
	 */
	public boolean isAlive() {
		return thisThread.isAlive();
	}
	/**
	 *  � ������ <code>run()</code>, ����� ������ � ����� �������� ��
	 * ��� ��� ���� � ������ �� ����� ������ ������ ��������. ��� ������� ������ ������ �������� 
	 * ���������� ����� �������� ���� ��������� �� ������ ������ � ������ � ���� ������. 
	 */
	public void run() {
		Thread currentThread = Thread.currentThread();
		while (currentThread == thisThread) {
			while (queue.size() == 0 || queue == null) {
				synchronized (queue) {
					try {
						if (stopFlag)
							return;
						queue.wait();
					} catch (InterruptedException e) {
						System.out.println("5 ������. �� ��� �������! �� �������� wait!");
					}
				}
			}

			try {
				db2load.loadFromQueue(queue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
/**
 * ��������� ������
 */
	public void start() {
		thisThread = new Thread(this);
		thisThread.setName("��������� �������, ���� �������� ������ ����!");
		thisThread.setDaemon(false);
		thisThread.start();

	}
/**
 * ����� <code>stop()</code> ������������� �����, �� ������ ����� ���� ��� � ������ �� ��������� �� ������ ��������.
 * �.�. ���� ���������� ������������ ����� �� �����.
 */
	public void stop() {
		while(queue.size() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("��� �� �������� sleep!!! ������ �������  �� �. 289-80-46 � �������� �� ����!");
			}
		}
		stopFlag = true;
		synchronized(queue) {
			queue.notify();
		}
	}

}
