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
 * Класс создан для многопоточной работы со списком. Цель - вносить в базу элементы, как только 
 * они появляются в списке. Поэтому при создании класса нам требуется сам список, и детали
 * соединения с базой данных.
 * <p>
 * Данный класс работает как <code>Thread</code>, но не наследуется от него.
 * Сделанно для реализации безопасной остановки, следуя официальным рекомендациям от
 * Oracle.
 * <p>
 * В методе <code>run()</code>, поток уходит в режим ожидания до
 * тех пор пока в списке не будет хотябы одного элемента. При наличии хотябы одного элемента 
 * вызывается метод загрузки всех элементов на данный момент в списке в базу данных. 
 * 
 * <p>
 * Метод <code>stop()</code> останавливает поток, но только после того как в списке не останется ни одного элемента.
 * Т.е. дает отработать действующему циклу до конца. 
 * <p>
 * Метод <code>isAlive()</code> показывает продолжает ли действовать поток этого класса.
 * <p>
 * Метод <code>start()</code> Запускает задачу
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
	 * Метод <code>isAlive()</code> показывает продолжает ли действовать поток этого класса.
	 * @return
	 */
	public boolean isAlive() {
		return thisThread.isAlive();
	}
	/**
	 *  В методе <code>run()</code>, поток уходит в режим ожидания до
	 * тех пор пока в списке не будет хотябы одного элемента. При наличии хотябы одного элемента 
	 * вызывается метод загрузки всех элементов на данный момент в списке в базу данных. 
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
						System.out.println("5 баллов. Вы это сделали! Вы прервали wait!");
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
 * Запускает задачу
 */
	public void start() {
		thisThread = new Thread(this);
		thisThread.setName("Пользуясь случаем, хочу передать привет Маме!");
		thisThread.setDaemon(false);
		thisThread.start();

	}
/**
 * Метод <code>stop()</code> останавливает поток, но только после того как в списке не останется ни одного элемента.
 * Т.е. дает отработать действующему циклу до конца.
 */
	public void stop() {
		while(queue.size() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("Ого Вы прервали sleep!!! Срочно звоните  по т. 289-80-46 и сообщите об этом!");
			}
		}
		stopFlag = true;
		synchronized(queue) {
			queue.notify();
		}
	}

}
