package samples.examples.developtools;

import static org.mockito.Mockito.mock;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.database.DatabaseConfig;
import ji.querybuilder.enums.Join;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;

/**
 * Class shows available developers tools
 * @author Ondřej Němec
 *
 */
@Controller("develop-tools")
public class DevelopToolsExample implements Module {
	
	private Database database;
	private Translator translator;
	
	public DevelopToolsExample() {}

	public DevelopToolsExample(Translator translator, Database database) {
		this.translator = translator;
		this.database = database;
	}
	
	/**
	 * Page shows Profiler bar
	 * @return http://localhost:8080/examples/develop-tools/index
	 */
	@Action("index")
	public Response index() {
		translator.translate("path.to.translation");
		translator.translate(
			"path.to.another.translation",
			new MapInit<String, Object>().append("param1", "value1").append("param2", "value2").toMap()
		);

		try {
			// not executed, in profiler
			database.applyQuery((con)->{
				try (PreparedStatement stmt = con.prepareStatement("select * from OneTable where id = ?")) {
					stmt.setInt(1, 5);
				}
				return null;
			});
			// executed, in profiler
			database.applyQuery((con)->{
				try (PreparedStatement stmt = con.prepareStatement("select * from Table where Name = ?")) {
					stmt.setString(1, "Name #3");
					stmt.executeQuery();
				}
				return null;
			});
			// not executed, not in profiler
			database.applyBuilder((builder)->{
				builder.select("'Task - not executed', *, :const")
				.from("OneTable")
				.join("Join", Join.INNER_JOIN, "id = FK_id")
				.where("Col = :value")
				.addParameter(":const", 123)
				.addParameter(":value", "Col #6");
				return null;
			});
			// executed, in profiler
			database.applyBuilder((builder)->{
				builder.select("'Task - executed', *, :const")
				.from("OneTable")
				.join("JoinTable", Join.INNER_JOIN, "Table.id = JoinTable.FK_id")
				.where("Name like :value")
				.addParameter(":const", 123)
				.addParameter(":value", "%#1%")
				.fetchAll();
				return null;
			});
		} catch (SQLException e) {
			// query fail - no db connection
		}
		return Response.getTemplate("index.jsp", new HashMap<>());
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(DevelopToolsExample.class, (t, i, au, ar)->new DevelopToolsExample(t, database));
		return Arrays.asList(new DevelopToolsTask(translator, database));
	}

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new DevelopToolsExample());
		try {
			Env env = new Env("samples/examples/app.properties");
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				// uncomment for test response with disabled develop view
				//.setDevelopIpAdresses(Arrays.asList()) // no develop ips
				.setUseProfiler(true)
				.get(modules, env, new Database(
					new DatabaseConfig(
						env.getString("db.type"),
						env.getString("db.url"), 
						false, 
						env.getString("db.schema"), 
						env.getString("db.username"), 
						env.getString("db.password"),
						Arrays.asList(),
						1
					),
					mock(Logger.class) // real logger is not required
				));
			
			/* start */
			server.start();
	
			// sleep for 30min before automatic close
			try { Thread.sleep(30 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public String getName() {
		return "examples";
	}

	@Override
	public String getTemplatesPath() {
		return "samples/examples/developtools";
	}
	
	@Override
	public String getControllersPath() {
		return "samples/examples/developtools";
	}
	
	@Override
	public String getTranslationPath() {
		return "samples/examples/developtools";
	}

}
