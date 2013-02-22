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
	private Queue<Db2File> queue;
	private Thread thisThread;
	private DB2Load db2load;

	Db2LoadFromQueueTask(Queue<Db2File> queue, DataSource dataSource) {
		this.queue = queue;
		this.db2load = new DB2LoadJDBCImpl(dataSource);
	}

	public void run() {
		Thread currentThread = Thread.currentThread();
		while (currentThread == thisThread) {
			while (queue.size() == 0 || queue == null) {
				synchronized (queue) {
					try {
						if (thisThread == null)
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
		thisThread = null;
	}

}
