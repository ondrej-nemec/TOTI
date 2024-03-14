package toti.tutorial1.tasks;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import toti.application.Task;
import toti.tutorial1.entity.State;
import toti.tutorial1.services.DeviceStateDao;
import toti.tutorial1.services.DevicesDao;

public class DeviceStateCheckTask implements Task {
	
	private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
	private final List<String> details = Arrays.asList(
		"success-1",
		"success-2",
		"warning-1",
		"warning-2",
		"failure-1",
		"failure-2",
		"failure-3",
		"info-1",
		"info-2",
		"info-3"
	);
	
	private final DevicesDao deviceDao;
	private final DeviceStateDao stateDao;
	private final Logger logger;
	
	private Future<?> future;
	
	public DeviceStateCheckTask(DevicesDao deviceDao, DeviceStateDao stateDao, Logger logger) {
		this.deviceDao = deviceDao;
		this.stateDao = stateDao;
		this.logger = logger;
	}

	@Override
	public void start() throws Exception {
		future = pool.scheduleWithFixedDelay(()->{
			try {
				Random random = new Random();
				deviceDao.getAll().forEach((device)->{
					try {
						if (device.isRunning()) {
							stateDao.saveState(device.getId(), new State(
								device.getId(),
								random.nextBoolean(),
								details.get(random.nextInt(10))
							));
						} else {
							stateDao.deleteState(device.getId());
						}
					} catch (SQLException e) {
						logger.error("Device " + device.getId() + " state updade fail", e);
					}
				});
			} catch (SQLException e) {
				logger.error("Devices state update fail", e);
			}
		}, 10, 30, TimeUnit.SECONDS); // after 10 seconds after start, executed every 30 seconds
	}

	@Override
	public void stop() throws Exception {
		if (future != null) {
			future.cancel(true);
		}
		pool.shutdownNow();
	}

}
