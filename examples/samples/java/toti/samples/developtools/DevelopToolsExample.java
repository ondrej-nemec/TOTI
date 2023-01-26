package toti.samples.developtools;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.querybuilder.enums.Join;
import ji.translator.Translator;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;
import toti.url.Link;

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
	//	translator.translate("some.translation", "someLocale");

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
				try (PreparedStatement stmt = con.prepareStatement("select * from OneTable where Name = ?")) {
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
				.join("JoinTable", Join.INNER_JOIN, "OneTable.id = JoinTable.FK_id")
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
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addFactory(DevelopToolsExample.class, (t, i, au, ar)->new DevelopToolsExample(t, database));
		return Arrays.asList(new DevelopToolsTask(translator, database));
	}

	@Override
	public String getName() {
		return "tools-examples";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/developtools";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/developtools";
	}

}
