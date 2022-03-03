package samples.examples.grid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.application.GridColumn;
import toti.application.GridOptions;
import toti.application.Task;
import toti.control.Grid;
import toti.control.columns.ActionsColumn;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.GroupAction;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
import toti.control.inputs.Date;
import toti.control.inputs.Datetime;
import toti.control.inputs.Month;
import toti.control.inputs.Number;
import toti.control.inputs.Option;
import toti.control.inputs.Range;
import toti.control.inputs.Select;
import toti.control.inputs.Text;
import toti.control.inputs.Time;
import toti.control.inputs.Week;
import toti.register.Register;
import toti.response.Response;
import toti.url.Link;
import toti.validation.Validator;

/**
 * Example shows options of grid and filtering
 * @author Ondřej Němec
 *
 */
@Controller("grid")
public class GridExample implements Module {
	
	private GridExampleDao dao;
	private Logger logger;
	
	public GridExample() {}
	
	public GridExample(GridExampleDao dao, Logger logger) {
		this.dao = dao;
		this.logger = logger;
	}
	
	/**
	 * Displays grid with all filters
	 * @return http://localhost:8080/examples/grid/all
	 */
	@Action("all")
	public Response allFilters() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		grid.addColumn(new ValueColumn("id").setTitle("ID"));
		grid.addColumn(
			new ValueColumn("text")
			.setTitle("Text")
			.setFilter(
				Text.filter()
				.setSize(7).setMaxLength(3).setMinLength(10)
				.setPlaceholder("Filter for Text")
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("number")
			.setTitle("Number")
			.setFilter(
				Number.filter()
				.setStep(0.1).setMax(10).setMin(-10)
				.setPlaceholder("Filter for Number")
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("range")
			.setTitle("Range")
			.setFilter(
				Range.filter().setStep(5).setMin(0).setMax(100)
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("select")
			.setTitle("Select")
			.setFilter(
				Select.filter(Arrays.asList(
					Option.create("", "---"),
					Option.create(true, "Yes"),
					Option.create(false, "No")
				))
				.setPlaceholder("Filter for Select")
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("datetime")
			.setTitle("Datetime")
			.setFilter(Datetime.filter().setStep(1)) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("date")
			.setTitle("Date")
			.setFilter(Date.filter()) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("time")
			.setTitle("Time")
			.setFilter(Time.filter().setStep(1))// .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("month")
			.setTitle("Month")
			.setFilter(Month.filter()) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("week")
			.setTitle("Week")
			.setFilter(
				Week.filter() // .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/**
	 * Returns data for all filters grid
	 * Called internally
	 * @return http://localhost:8080/examples/grid/all
	 */
	@Action(value = "all-data", validator = "allFilersValidator")
	public Response allFilters(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null));
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}
	
	public Validator allFilersValidator() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("text"),
			new GridColumn("number", Double.class),
			new GridColumn("range", Integer.class),
			new GridColumn("select", Boolean.class),
			new GridColumn("datetime"),
			new GridColumn("date"),
			new GridColumn("time"),
			new GridColumn("month"),
			new GridColumn("week")
		));
	}
	
	/******/
	
	/**
	 * Demonstrate grid buttons
	 * @return http://localhost:8080/examples/grid/buttons
	 */
	@Action("buttons")
	public Response buttons() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		grid.addColumn(
			new ButtonsColumn("buttons")
			.setResetFiltersButton(false) // disable reset-filter button
			.addButton(
				Button.create(
					Link.get()
					.addUrlParam("{id}")
					.addGetParam("name", "{text}")
					.create(getClass(), c->c.syncButtonLink(0, null)),
					"sync"
				)
				.setMethod("get").setAsync(false).setTitle("Sync")
			)
			.addButton(
				Button.create(
					Link.get()
					.addUrlParam("{id}")
					.addGetParam("name", "{text}")
					.create(getClass(), c->c.asyncButtonLink(0, null)),
					"async"
				)
				.setMethod("post").setAsync(true).setTitle("Async")
				.setConfirmation("Really async {id}:{text}")
				.setOnFailure("buttonOnFailure") // name of JS method, by default print message to flash
				.setOnSuccess("buttonOnSuccess") // name of JS method, by default print result to flash
			)
			.addButton(
				Button.create(
					Link.get()
					.addUrlParam("{id}")
					.addGetParam("name", "{text}")
					.create(getClass(), c->c.syncButtonPost(0, null, null)),
					"cond"
				)
				.setMethod("post").setAsync(true).setTitle("Cond")
				.setCondition("{id}%3==0", true) // display button on every third row
				.addRequestParam("postParam", "value in post param")
			)
		);
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	@Action("b-sync")
	public Response syncButtonLink(@ParamUrl("id") int id, @Param("name") String name) {
		return Response.getText(String.format("Sync link. ID: %s, Name: %s", id, name));
	}
	
	@Action("b-async")
	public Response asyncButtonLink(@ParamUrl("id") int id, @Param("name") String name) {
		return Response.getText(String.format("Async link. ID: %s, Name: %s", id, name));
	}
	
	@Action("b-post")
	@Method(HttpMethod.POST)
	public Response syncButtonPost(@ParamUrl("id") int id, @Param("name") String name, @Param("postParam") String postParam) {
		return Response.getText(String.format("Sync link. ID: %s, Name: %s. PostParam: %s", id, name, postParam));
	}
	
	/****/
	
	/**
	 * Demonstrate grid control - page sizes and paging buttons
	 * @return http://localhost:8080/examples/grid/control
	 */
	@Action("control")
	public Response gridControl() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		
		grid.setPagesSizes(Arrays.asList(7, 14, 28, 35, 42), 14);
		grid.setPagesButtonCount(3);
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Grid without control
	 * @return http://localhost:8080/examples/grid/empty
	 */
	@Action("empty")
	public Response empty() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		
		grid.setPagesSizes(null);
		grid.setPagesButtonCount(null);
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Demonstrate grid control - page sizes and paging buttons
	 * @return http://localhost:8080/examples/grid/renderer
	 */
	@Action("rederer")
	public Response renderers() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addColumn(new ValueColumn("id").setRenderer("onColumnRenderer"));
		grid.addColumn(new ValueColumn("text"));
		
		grid.setOnRowRenderer("onRowRenderer");
		grid.useRowSelection(true);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Demonstrate group actions
	 * Row unique is 'id'
	 * @return http://localhost:8080/examples/grid/actions
	 */
	@Action("actions")
	public Response actions() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addAction(
			new GroupAction("Async action", Link.get().create(getClass(), c->c.asyncActionLink(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
		);
		grid.addAction(
			new GroupAction("Sync action", Link.get().create(getClass(), c->c.syncActionLink(null)))
			.setAsync(false)
		);
		grid.addAction(
			new GroupAction("Post action", Link.get().create(getClass(), c->c.postActionLink(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
			.setMethod("post")
		);
		
		grid.addColumn(new ActionsColumn("Group actions"));
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	@Action("a-async")
	public Response asyncActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Async action. IDS: " + ids));
	}
	
	@Action("a-sync")
	public Response syncActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Sync action. IDS: " + ids));
	}
	
	@Action("a-post")
	@Method(HttpMethod.POST)
	public Response postActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Post action. IDS: " + ids));
	}
	
	/***/
	
	/**
	 * Grid actions with another row unique
	 * @return http://localhost:8080/examples/grid/row-unique
	 */
	@Action("row-unique")
	public Response rowUnique() {
		Grid grid = new Grid(Link.get().create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addAction(
			new GroupAction("Async action", Link.get().create(getClass(), c->c.asyncActionLinkText(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
		);
		
		grid.setUniqueRowIdentifier("text");
		grid.addColumn(new ActionsColumn(""));
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	@Action("a-async-text")
	public Response asyncActionLinkText(@Param("ids") List<String> ids) {
		return Response.getText(String.format("Async action with string ids. IDS: " + ids));
	}
	
	// TODO
	/*
	 * full filters
	 * buttons
	 * paging, page size
	 * grid bez veci okolo - strankovani apod
	 * column and row renderers, on row selection
	 * actions (execute)
	 * 
	 * equals, like, startswith, endswith
	 * substitutions
	 * memory vs db (indexing)
	 */

	/*******************************/
	
	@Override
	public String getName() {
		return "examples";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/grid";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/grid";
	}
	
	@Override
	public String getTranslationPath() {
		return "samples/examples/grid";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		GridExampleDao dao = new GridExampleDao();
		register.addFactory(GridExample.class, ()->new GridExample(dao, logger));
		return new LinkedList<>();
	}

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new GridExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				.setLanguageSettings(new LanguageSettings(Arrays.asList(new Locale("en", true, Arrays.asList()))))
				.get(modules, null, null);
			
			/* start */
			server.start();
	
			// sleep for 2min before automatic close
			try { Thread.sleep(10 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
