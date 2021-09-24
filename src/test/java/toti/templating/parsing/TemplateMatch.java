package toti.templating.parsing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import common.structures.MapInit;
import core.text.Text;
import core.text.basic.WriteText;
import toti.logging.TotiLogger;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import translator.LanguageSettings;
import translator.Locale;
import translator.LocaleTranslator;

public class TemplateMatch {
	
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
	
	public TemplateMatch() {
		//  "" : "";
		// "compareTemplate.jsp"
		Future<?> oldOne = pool.scheduleWithFixedDelay(
			getTask(true, "temp/old", "compareTemplate", "toti/templating/parsing"),
			0, 4, TimeUnit.SECONDS
		);
		Future<?> newOne = pool.scheduleWithFixedDelay(
			getTask(false, "temp/new", "compareTemplate", "toti/templating/parsing2"),
			2, 4, TimeUnit.SECONDS
		);
		
		try {
			Thread.sleep(60000);
			oldOne.cancel(true);
			newOne.cancel(true);
			pool.shutdown();
		} catch (Exception e) {
			
		}
		
	}

	private Runnable getTask(boolean useOldImp, String tempPath, String template, String templatePath) {
		String result = "temp/result-" + (useOldImp ? "old" : "new") + ".csv"; 
		try {
			Text.get().write((bw)->{
				WriteText.get().write(
					bw, "TIME1;MEMORY1;TIME2;MEMORY2;TIME3;MEMORY3;PARSING TIME;PARSING MEMORY;GET TIME;GET MEMORY"
				);
				WriteText.get().write(
					bw, 
					"=AVERAGE(A3:A20);"
					+ "=AVERAGE(B3:B20);"
					+ "=AVERAGE(C3:C20);"
					+ "=AVERAGE(D3:D20);"
					+ "=AVERAGE(E3:E20);"
					+ "=AVERAGE(F3:F20);"
					+ "=C2-A2;"
					+ "=D2-B2;"
					+ "=E2-C2;"
					+ "=F2-D2"
				);
			}, result, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return ()->{
			try {
				System.err.println("RUN " + tempPath);
				File f = new File(tempPath + "/cache/" + templatePath.replace(".", "_") + "/" + template + ".class");
				f.delete();
				File f2 = new File(tempPath + "/cache/" + templatePath.replace(".", "_") + "/layout.class");
				f2.delete();
				TemplateFactory test = new TemplateFactory(
					tempPath, templatePath, "", new HashMap<>(), false, false, TotiLogger.getLogger("parsing")
				);
			//	test.useOldImpl = useOldImp;
				
				long tFirst = System.currentTimeMillis();
				long mFirst = getConsumedMemory();
				
				Template t = test.getTemplate(template + ".jsp");
				
				long tSecond = System.currentTimeMillis();
				long mSecond = getConsumedMemory();
				
				String html = t.create(test, 
						new MapInit<String, Object>()
						.append("title", "Testing page")
						.append("switch", "value")
						.append("list", Arrays.asList("listItem#1", "listItem#2", "listItem#3"))
						.append(
							"map",
							new MapInit<>()
								.append("key#1", "value#1").append("key#2", "value#2").append("key#3", "value#3").toMap()
						)
						.toMap(),
						new LocaleTranslator(
							new LanguageSettings(Arrays.asList(new Locale("cs", true, Arrays.asList()))),
							new HashSet<>(),
							TotiLogger.getLogger("parsing")
						),
						null
				);
				
				long tThird = System.currentTimeMillis();
				long mThird = getConsumedMemory();

				Text.get().write((bw)->{
					try {
						WriteText.get().write(bw, html);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, tempPath + "/index.html", false);
				
				Text.get().write((bw)->{
					WriteText.get().write(bw, String.format(
						"%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",
						tFirst, mFirst, tSecond, mSecond, tThird, mThird,
						(tSecond - tFirst), (mSecond - mFirst),
						(tThird - tSecond), (mThird - mSecond)
					));
				}, result, true);
				
			//	System.err.println("TIMES: " + (tSecond - tFirst) + " " + (tThird - tSecond));
			//	System.err.println("MEMORY: " + (mSecond - mFirst) + " " + (mThird - mSecond));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
	
	private long getConsumedMemory() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}

	
	public static void main(String[] args) {
		new TemplateMatch();
	}
	
}
