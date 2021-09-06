package example;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import common.structures.MapInit;
import example.dao.ExampleDao;
import toti.application.Task;
import translator.Translator;

public class ExampleTask implements Task {
	
	private final ExampleDao dao;
	private final Translator trans;
	
	private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private Future<?> future;
	
	private int count = 0;
	
	public ExampleTask(ExampleDao dao, Translator trans) {
		this.dao = dao;
		this.trans = trans;
	}

	@Override
	public void start() throws Exception {
		future = pool.scheduleWithFixedDelay(()->{
			if (count > 3) {
				return;
			}
			count += 1;
			try {
				dao.get(1);
				dao.getDatabase().applyBuilder((builder)->{
					builder.select("*").addParameter("p1", "param # " + count);
					return null;
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
			trans.translate("not.existing.file", new MapInit<String, Object>().append("b", "BB").toMap());
		}, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void stop() throws Exception {
		if (future != null) {
			future.cancel(true);
		}
		pool.shutdownNow();
	}

}
