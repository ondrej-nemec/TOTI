package toti.examples.getStarted.core.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import toti.core.application.Task;
import toti.examples.getStarted.core.services.Counter;

public class CounterTask implements Task {

	private final Counter counter;
	private final Logger logger;
	private final ScheduledExecutorService pool;
	private Future<?> future;

	public CounterTask(Counter counter, Logger logger) {
		this.counter = counter;
		this.logger = logger;
		this.pool = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void start() throws Exception {
		future = pool.scheduleWithFixedDelay(()->{
			counter.increase();
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void stop() throws Exception {
		try {
			if (future != null) {
				future.cancel(true);
			}
			pool.shutdownNow();
		} catch (Exception e) {
			logger.error("Cannot stop pool", e);
		}
	}

}
