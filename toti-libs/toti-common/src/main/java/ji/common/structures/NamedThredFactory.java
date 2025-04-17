package ji.common.structures;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class allows to give custom name to thread in {@link Executors}.
 * 
 * <pre>
 * Executors.newFixedThreadPool(threadCount, new NamedThredFactory(threadName));
 * </pre>
 * OR
 * <pre>
 * Executors.newScheduledThreadPool(threadCount, new NamedThredFactory(threadName));
 * </pre>
 * 
 * @author Ondřej Němec
 *
 */
// via java.util.concurrent.Executors::596
public class NamedThredFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	/**
	 * Create new factory. Final name will be looks like: {@code givenName-}<i>thread-number</i>
	 * 
	 * @param name String prefix 
	 */
	public NamedThredFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "" + name + "-"; //;"pool-" + name +  "-thread-";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new MonitoredThread(group, r, namePrefix + threadNumber.getAndIncrement(), 0, LocalDateTime.now());
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
	
	class MonitoredThread extends Thread {
		
		private final LocalDateTime start;
		
		public MonitoredThread(ThreadGroup group, Runnable target, String name, long stackSize, LocalDateTime start) {
			super(group, target, name, stackSize);
			this.start = start;
		}

		public LocalDateTime getStartTime() {
			return start;
		}
		public String toString() {
			ThreadGroup group = getThreadGroup();
			return "Thread{"
				+ "name:" + getName()
				+ ", priority:" + getPriority()
				+ (group == null ? "" : ", group: " + group.getName())
				+ ", startTime:" + start
				+ "}";
		}
		
	}

}