package toti.samples.grid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import ext.FilterMode;
import ext.GridColumn;
import ext.GridOptions;
import ext.GridRange;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.control.Grid;
import toti.control.columns.ActionsColumn;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.GroupAction;
import toti.control.columns.TreeColumn;
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
import toti.validation.ItemRules;
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
	private Link link;	
	
	public GridExample() {}
	
	public GridExample(GridExampleDao dao, Link link, Logger logger) {
		this.dao = dao;
		this.link = link;
		this.logger = logger;
	}
	
	/**
	 * Displays grid with all filters
	 * @return http://localhost:8080/examples-grid/grid/all
	 */
	@Action(path="all")
	public ResponseAction allFilters() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
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
			new ValueColumn("select_col")
			.setTitle("Select")
			.setFilter(
				Select.filter(Arrays.asList(
					Option.create("", "---"),
					Option.create(true, "Yes"),
					Option.create(false, "No")
				))
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("datetime_col")
			.setTitle("Datetime")
			.setFilter(Datetime.filter().setStep(1)) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("date_col")
			.setTitle("Date")
			.setFilter(Date.filter()) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("time_col")
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
	 * @return http://localhost:8080/examples-grid/grid/all-data
	 */
	@Action(value = "all-data", validator = "allFilersValidator")
	public ResponseAction allFilters(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null, null));
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}
	
	public Validator allFilersValidator() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("id"),
			new GridColumn("text").setFilterMode(FilterMode.LIKE),
			new GridColumn("number", Double.class),
			new GridColumn("range", Integer.class),
			new GridColumn("select_col", Boolean.class),
			new GridColumn("datetime_col").setFilterMode(FilterMode.LIKE),
			new GridColumn("date_col"),
			new GridColumn("time_col"),
			new GridColumn("month"),
			new GridColumn("week"),
			new GridColumn("parent")
		));
	}
	
	/******/
	
	/**
	 * Usage of global buttons
	 * @return http://localhost:8080/examples-grid/grid/reset
	 */
	@Action(path="reset")
	public ResponseAction reset() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(
				new ValueColumn("text")
				.setTitle("Text")
				.setFilter(Text.filter())
				.setUseSorting(true)
			);
		grid.addColumn(
				new ValueColumn("datetime_col")
				.setTitle("Datetime")
				.setFilter(Datetime.filter().setStep(1))
				.setUseSorting(true)
			);
		grid.addColumn(
			new ButtonsColumn("buttons")
			.setResetFiltersButton(Button.reset("reset").setTitle("Reset filter"))
			.addGlobalButton(
				Button.create(
					link.create(
						getClass(), c->c.asyncButtonLink(0, null),
						MapInit.create().append("name", "Grid button name").toMap(),
						"123"
					),
					"global-button"
				).setTitle("Global Button")
			)
		);
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/******/
	
	/**
	 * Demonstrate grid buttons
	 * @return http://localhost:8080/examples-grid/grid/buttons
	 */
	@Action(path="buttons")
	public ResponseAction buttons() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		grid.addColumn(
			new ButtonsColumn("buttons")
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.syncButtonLink(0, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"sync"
				)
				.setMethod("get").setAsync(false).setTitle("Sync")
			)
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.asyncButtonLink(0, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"async"
				)
				.setMethod("post").setAsync(true).setTitle("Async")
				.setConfirmation("Really async {id}:{text}")
				.setOnFailure("buttonOnFailure") // name of JS method, by default print message to flash
				.setOnSuccess("buttonOnSuccess") // name of JS method, by default print result to flash
			)
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.syncButtonPost(0, null, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
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
	
	@Action(path="b-sync")
	public ResponseAction syncButtonLink(@ParamUrl("id") int id, @Param("name") String name) {
		return Response.getText(String.format("Sync link. ID: %s, Name: %s", id, name));
	}
	
	@Action(path="b-async")
	public ResponseAction asyncButtonLink(@ParamUrl("id") int id, @Param("name") String name) {
		return Response.getText(String.format("Async link. ID: %s, Name: %s", id, name));
	}
	
	@Action(path="b-post")
	@Method(HttpMethod.POST)
	public ResponseAction syncButtonPost(@ParamUrl("id") int id, @Param("name") String name, @Param("postParam") String postParam) {
		return Response.getText(String.format("Sync link. ID: %s, Name: %s. PostParam: %s", id, name, postParam));
	}
	
	/****/
	
	/**
	 * Demonstrate grid control - page sizes and paging buttons
	 * @return http://localhost:8080/examples-grid/grid/control
	 */
	@Action(path="control")
	public ResponseAction gridControl() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.setPagesSizes(Arrays.asList(7, 14, 28, 35, 42), 14);
		grid.setPagesButtonCount(3);
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}

	
	/**
	 * Demonstrate grid load - add more results
	 * @return http://localhost:8080/examples-grid/grid/load
	 */
	@Action(path="load")
	public ResponseAction gridLoad() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.setPagesSizes(Arrays.asList(7, 14, 28, 35, 42), 14);
		grid.setPagesButtonCount(0);
		grid.setUseLoadButton(true);
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Grid without control
	 * @return http://localhost:8080/examples-grid/grid/empty
	 */
	@Action(path="empty")
	public ResponseAction empty() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.setPagesSizes(null);
		grid.setPagesButtonCount(0);
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Shows usage of custom cell renderer
	 * @return http://localhost:8080/examples-grid/grid/renderer
	 */
	@Action(path="renderer")
	public ResponseAction renderers() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addColumn(new ValueColumn("id").setRenderer("onColumnRenderer"));
		grid.addColumn(new ValueColumn("text"));
		
		grid.setRowRenderer("rowRenderer");
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/**
	 * Shows behaviour of row selection
	 * @return http://localhost:8080/examples-grid/grid/selection
	 */
	@Action(path="selection")
	public ResponseAction selection() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		grid.useRowSelection(true);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/****/
	
	/**
	 * Demonstrate group actions
	 * Row unique is 'id'
	 * @return http://localhost:8080/examples-grid/grid/actions
	 */
	@Action(path="actions")
	public ResponseAction actions() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addAction(
			new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLink(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
			.setConfirmation("Really?")
		);
		grid.addAction(
			new GroupAction("Sync action", link.create(getClass(), c->c.syncActionLink(null)))
			.setAsync(false)
		);
		grid.addAction(
			new GroupAction("Post action", link.create(getClass(), c->c.postActionLink(null)))
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
	
	@Action(path="a-async")
	public ResponseAction asyncActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Async action. IDS: " + ids));
	}
	
	@Action(path="a-sync")
	public ResponseAction syncActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Sync action. IDS: " + ids));
	}
	
	@Action(path="a-post")
	@Method(HttpMethod.POST)
	public ResponseAction postActionLink(@Param("ids") List<Integer> ids) {
		return Response.getText(String.format("Post action. IDS: " + ids));
	}
	
	/***/
	
	/**
	 * Grid actions with another row unique
	 * @return http://localhost:8080/examples-grid/grid/row-unique
	 */
	@Action(path="row-unique")
	public ResponseAction rowUnique() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		
		grid.addAction(
			new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLinkText(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
		);
		
		grid.addColumn(new ActionsColumn("byText").setUniqueRowIdentifier("text"));
		grid.addColumn(new ActionsColumn("byId").setUniqueRowIdentifier("id"));
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("text"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	@Action(path="a-async-text")
	public ResponseAction asyncActionLinkText(@Param("ids") List<String> ids) {
		return Response.getText(String.format("Async action with string ids. IDS: " + ids));
	}
	
	/****/
	
	/**
	 * Rows in grid with tree sorting
	 * @return http://localhost:8080/examples-grid/grid/tree-structure
	 */
	@Action(path="tree-structure")
	public ResponseAction treeStructure() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		grid.setPageSize(50);

		grid.addColumn(new TreeColumn("id", "parent"));
		grid.addColumn(
			new ValueColumn("id").setTitle("ID")
			.setUseSorting(true).setFilter(Number.filter())
		);
		grid.addColumn(
			new ValueColumn("text").setTitle("Name")
			.setUseSorting(true).setFilter(Text.filter())
		);
		grid.addColumn(
			new ValueColumn("parent").setTitle("Parent")
			.setUseSorting(true).setFilter(Number.filter())
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
		
	/****/
	
	/**
	 * Ways of filtering, EQUALS is default
	 * @return http://localhost:8080/examples-grid/grid/filtering
	 */
	@Action(path="filtering")
	public ResponseAction filtering() {
		Grid grid = new Grid(link.create(getClass(), c->c.filtering(null)), "get");
		grid.addColumn(new ValueColumn("id").setTitle("ID"));
		// like, startswith, endswith, equasl
		// LIKE
		grid.addColumn(
			new ValueColumn("text")
			.setTitle("Text - Like")
			.setFilter(Text.filter())
			.setUseSorting(true)
		);
		// starts with
		grid.addColumn(
			new ValueColumn("number")
			.setTitle("Number - Starts with")
			.setFilter(Number.filter())
			.setUseSorting(true)
		);
		// ends with
		grid.addColumn(
			new ValueColumn("range")
			.setTitle("Range - Ends with")
			.setFilter(Range.filter().setStep(5).setMax(5).setMin(0))
			.setUseSorting(true)
		);
		// equals
		grid.addColumn(
			new ValueColumn("select_col")
			.setTitle("Select - Equals")
			.setFilter(
				Select.filter(Arrays.asList(
					Option.create("", "---"),
					Option.create(true, "Yes"),
					Option.create(false, "No")
				))
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/filtering-data
	 */
	@Action(value = "filtering-data", validator = "filteringValidator")
	public ResponseAction filtering(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null, null));
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}
	
	public Validator filteringValidator() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("text").setFilterMode(FilterMode.LIKE),
			new GridColumn("number").setFilterMode(FilterMode.STARTS_WITH),
			new GridColumn("range").setFilterMode(FilterMode.ENDS_WITH),
			new GridColumn("select_col").setFilterMode(FilterMode.EQUALS)
		));
	}
	
	/****/
	
	/**
	 * Shows sorting substitution
	 * @return http://localhost:8080/examples-grid/grid/subst
	 */
	@Action(path="subst")
	public ResponseAction sortingSubst() {
		Grid grid = new Grid(link.create(getClass(), c->c.substitution(null)), "get");
		
		grid.addColumn(new ValueColumn("id").setTitle("ID"));
		grid.addColumn(
			new ValueColumn("number")
			.setTitle("Number")
			.setUseSorting(true)
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/substitution-data
	 */
	@Action(value = "substitution-data", validator = "substitutionValidator")
	public ResponseAction substitution(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null, null));
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}
	
	public Validator substitutionValidator() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("number").setSortingName("id")
		));
	}
	
	/******************/
	
	/**
	 * Usage of automatic refresh
	 * @return http://localhost:8080/examples-grid/grid/refresh
	 */
	@Action(path="refresh")
	public ResponseAction refresh() {
		Grid grid = new Grid(link.create(getClass(), c->c.refreshRandomData(null)), "get");
		grid.addColumn(new ValueColumn("id"));
		grid.setRefreshInterval(30000); // 0.5 min
		grid.addColumn(
			new ValueColumn("text")
			.setTitle("Text")
				//.setFilter(Text.filter())
				//.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("number")
			.setTitle("Number")
			//	.setFilter(Number.filter())
			//	.setUseSorting(true)
		);
		grid.addColumn(
			new ButtonsColumn("buttons")
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.syncButtonLink(0, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"sync"
				)
				.setMethod("get").setAsync(false).setTitle("Sync")
			)
		);
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	public Validator randomDataValidator() {
		return GridOptions.getValidator(Arrays.asList());
	}
	
	/**
	 * Returns data for all filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/random-data
	 */
	@Action(value="random-data", validator="randomDataValidator")
	public ResponseAction refreshRandomData(@Params GridOptions options) {
		List<Object> data = new LinkedList<>();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			Map<String, Object> row = new HashMap<>();
			row.put("id", i);
			row.put("text", "Row #" + i);
			row.put("number", random.nextInt());
			data.add(row);
		}
		return Response.getJson(MapInit.create()
			.append("itemsCount", 20)
			.append("pageIndex", 1)
			.append("data", data)
		.toMap());
	}
	
	/*****/
	
	/**
	 * Usage methods without GridDataSet and GridOptions
	 * @return http://localhost:8080/examples-grid/grid/raw
	 */
	@Action(path="raw")
	@Deprecated
	public ResponseAction raw() {
		Grid grid = new Grid(link.create(getClass(), c->c.raw(0, 0, null, null)), "get");
		
		grid.addColumn(
			new ValueColumn("id").setTitle("ID")
			.setUseSorting(true)
			.setFilter(Number.filter())
		);
		grid.addColumn(
			new ValueColumn("text")
			.setTitle("Text")
			.setUseSorting(true)
			.setFilter(Text.filter())
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/raw-data
	 */
	@Action(value = "raw-data", validator = "rawValidator")
	@Deprecated
	public ResponseAction raw(
			@Param("pageIndex") int pageIndex, 
			@Param("pageSize") int pageSize,
			@Param("filters") Map<String, Object> filters,
			@Param("sorting") Map<String, Object> sorting) {
		try {
			List<GridExampleEntity> dataset = dao.getAll();
			
			// there will be filtering and sorting
			// filters: name is column name and value is filtering value
			// sorting: name is column name and value is "DESC" or "ASC"
			
			GridRange range = GridRange.create(dao.getAll().size(), pageIndex, pageSize);
			dataset = dataset.subList(range.getOffset(), range.getLimit() + range.getLimit() + 1);
			
			Map<String, Object> json = new HashMap<>();
			json.put("data", dataset);
			json.put("itemsCount", dataset.size());
			json.put("pageIndex", pageIndex);
			return Response.getJson(json);
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}

	@Deprecated
	public Validator rawValidator() {
		return new Validator(true)
		.addRule(ItemRules.forName("pageIndex", true))
		.addRule(ItemRules.forName("pageSize", true))
		.addRule(ItemRules.forName("filters", true).setMapSpecification(new Validator(true)
			.addRule(ItemRules.forName("id", false).setType(Integer.class))
			.addRule(ItemRules.forName("text", false).setType(String.class))
		))
		.addRule(ItemRules.forName("sorting", true).setMapSpecification(new Validator(true)
			.addRule(ItemRules.forName("id", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
			.addRule(ItemRules.forName("text", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
		));
	}
	
	/*****/
	
	/**
	 * XSS check
	 * @return http://localhost:8080/examples-grid/grid/xss
	 */
	@Action(path="xss")
	public ResponseAction xss() {
		Grid grid = new Grid(link.create(getClass(), c->c.xss(null)), "get");
		
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(
			new ValueColumn("text")
			.setFilter(Text.filter())
		);
		grid.addColumn(
			new ValueColumn("select_col")
			.setFilter(
				Select.filter(Arrays.asList(
					Option.create("", "---"),
					Option.create("0", "<script>alert('select');</script>")
				)).setLoadData(link.create(getClass(), c->c.xssLoad()), "get")
			)
		);
		grid.addColumn(
			new ValueColumn("datetime_col")
			.setFilter(Datetime.filter())
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("filters.jsp", params);
	}
	
	@Action(path="xss-load")
	public ResponseAction xssLoad() {
		Map<String, Object> json = new HashMap<>();
		json.put("value", 1);
		json.put("title", "<script>alert('select');</script>");
		return Response.getJson(Arrays.asList(json));
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/xss-data
	 */
	@Action(value = "xss-data")
	public ResponseAction xss(@Params GridOptions options) {
		try {
			List<Object> dataset = new LinkedList<>();
			dataset.add(
				new MapInit<>()
				.append("id", "1")
				.append("text", "<script>alert('value');</script>")
				.append("select", "1")
				.append("datetime", "<script>alert('datetime');</script>")
				.toMap()
			);
			
			Map<String, Object> json = new HashMap<>();
			json.put("data", dataset);
			json.put("itemsCount", dataset.size());
			json.put("pageIndex", 1);
			return Response.getJson(json);
		} catch (Exception e) {
			logger.error("all filters", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Error occur: " + e.getMessage());
		}
	}
	
	/*************/
	
	/**
	 * Shows usage of custom template with all settings
	 * @return http://localhost:8080/examples-grid/grid/custom-template
	 */
	@Action(path="custom-template")
	public ResponseAction customTemplate() {
		Grid grid = new Grid(link.create(getClass(), c->c.allFilters(null)), "get");
		grid.setPageSize(50);
		
		grid.addColumn(new TreeColumn("id", "parent"));
		grid.addColumn(new ActionsColumn("main").setTitle("Group Actions"));
		
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
			new ValueColumn("parent")
			.setTitle("ParentId")
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
			new ValueColumn("select_col")
			.setTitle("Select")
			.setFilter(
				Select.filter(Arrays.asList(
					Option.create("", "---"),
					Option.create(true, "Yes"),
					Option.create(false, "No")
				))
				// .setDefaultValue(...)
			)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("datetime_col")
			.setTitle("Datetime")
			.setFilter(Datetime.filter().setStep(1)) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("date_col")
			.setTitle("Date")
			.setFilter(Date.filter()) // .setDefaultValue(...)
			.setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("time_col")
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

		grid.addColumn(
			new ButtonsColumn("buttons")
			.setTitle("Buttons")
			.setResetFiltersButton(Button.reset("reset"))
			.addGlobalButton(Button.create(
				link.create(
					getClass(), c->c.asyncButtonLink(0, null),
					MapInit.create().append("name", "Grid button name").toMap(),
					"123"
				),
				"global-button"
			).setTitle("Global"))
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.syncButtonLink(0, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"sync"
				)
				.setMethod("get").setAsync(false).setTitle("Sync")
			)
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.asyncButtonLink(0, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"async"
				)
				.setMethod("post").setAsync(true).setTitle("Async")
				.setConfirmation("Really async {id}:{text}")
				.setOnFailure("buttonOnFailure") // name of JS method, by default print message to flash
				.setOnSuccess("buttonOnSuccess") // name of JS method, by default print result to flash
			)
			.addButton(
				Button.create(
					link.create(
						getClass(), c->c.syncButtonPost(0, null, null),
						MapInit.create().append("name", "{text}").toMap(),
						"{id}"
					),
					"cond"
				)
				.setMethod("post").setAsync(true).setTitle("Cond")
				.setCondition("{id}%3==0", true) // display button on every third row
				.addRequestParam("postParam", "value in post param")
			)
		);
		
		
		grid.addAction(
			new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLink(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
			.setConfirmation("Really?")
		);
		grid.addAction(
			new GroupAction("Sync action", link.create(getClass(), c->c.syncActionLink(null)))
			.setAsync(false)
		);
		grid.addAction(
			new GroupAction("Post action", link.create(getClass(), c->c.postActionLink(null)))
			.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
			.setMethod("post")
		);
		
		
		grid.useRowSelection(true);
		
		Map<String, Object> params = new HashMap<>();
		params.put("grid", grid);
		return Response.getTemplate("customTemplate.jsp", params);
	}
	
	/*******************************/
	
	@Override
	public String getName() {
		return "examples-grid";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/grid";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/grid";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		GridExampleDao dao = new GridExampleDao(database);
		dao.initDb();
		register.addFactory(GridExample.class, ()->new GridExample(dao, link, logger));
		return new LinkedList<>();
	}

}
