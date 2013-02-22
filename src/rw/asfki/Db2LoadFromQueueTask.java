package rw.asfki;

import java.sql.SQLException;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import rw.asfki.domain.*;
import rw.asfki.dao.DB2Load;
import rw.asfki.dao.impl.DB2LoadJDBCImpl;

public class Db2LoadFromQueueTask implements Runnable {
	protected static Logger logger = Logger.getLogger("service");
	private Queue<Db2FileLoadProps> queue;
	private volatile Thread thisThread;
	private DB2Load db2load;
	private volatile boolean stopFlag;

	Db2LoadFromQueueTask(Queue<Db2FileLoadProps> queue, DataSource dataSource) {
		this.queue = queue;
		this.db2load = new DB2LoadJDBCImpl(dataSource);
	}
	public boolean isAlive() {
		return thisThread.isAlive();
	}
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
						System.out.println("You did it. You interrupt me!");
					}
				}
			}

			try {
				db2load.loadFile(queue.poll());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		thisThread = new Thread(this);
		thisThread.setName("Pasha privet");
		thisThread.start();

	}

	public void stop() {
		while(queue.size() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("Ого меня прервали!!!");
			}
		}
		stopFlag = true;
		synchronized(queue) {
			queue.notify();
		}
	}

}
