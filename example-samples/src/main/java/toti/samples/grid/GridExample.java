package toti.samples.grid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.ui.backend.grid.FilterMode;
import toti.ui.backend.grid.GridColumn;
import toti.ui.backend.grid.GridOptions;
import toti.ui.backend.grid.GridRange;
import toti.ui.control.Grid;
import toti.ui.control.columns.ActionsColumn;
import toti.ui.control.columns.ButtonsColumn;
import toti.ui.control.columns.GroupAction;
import toti.ui.control.columns.TreeColumn;
import toti.ui.control.columns.ValueColumn;
import toti.ui.control.inputs.Text;
import toti.ui.control.inputs.Time;
import toti.ui.control.inputs.Week;
import toti.ui.validation.ItemRules;
import toti.ui.validation.Validator;
import toti.ui.control.inputs.Button;
import toti.ui.control.inputs.Date;
import toti.ui.control.inputs.Datetime;
import toti.ui.control.inputs.Month;
import toti.ui.control.inputs.Number;
import toti.ui.control.inputs.Option;
import toti.ui.control.inputs.Range;
import toti.ui.control.inputs.Select;

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
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Returns data for all filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/all-data
	 */
	@Action(path = "all-data")
	public ResponseAction allFiltersData() {
		return ResponseBuilder.get()
			.validate(
				GridOptions.getValidator(Arrays.asList(
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
				))
			)
			.createRequest((req, translator, identity)->{
			try {
				return Response.OK().getJson(dao.getAll(req.getBodyParams(GridOptions.class)));
			} catch (Exception e) {
				logger.error("all filters", e);
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Error occur: " + e.getMessage());
			}
		});
	}
	/******/
	
	/**
	 * Usage of global buttons
	 * @return http://localhost:8080/examples-grid/grid/reset
	 */
	@Action(path="reset")
	public ResponseAction reset() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
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
							getClass(), c->c.asyncButtonLink(0),
							MapInit.create().append("name", "Grid button name").toMap(),
							"123"
						),
						"global-button"
					).setTitle("Global Button")
				)
			);
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/******/
	
	/**
	 * Demonstrate grid buttons
	 * @return http://localhost:8080/examples-grid/grid/buttons
	 */
	@Action(path="buttons")
	public ResponseAction buttons() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			grid.addColumn(
				new ButtonsColumn("buttons")
				.addButton(
					Button.create(
						link.create(
							getClass(), c->c.syncButtonLink(0),
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
							getClass(), c->c.asyncButtonLink(0),
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
							getClass(), c->c.syncButtonPost(0),
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	@Action(path="b-sync")
	public ResponseAction syncButtonLink(int id) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format("Sync link. ID: %s, Name: %s", id, req.getBodyParam("name")));
		});
	}
	
	@Action(path="b-async")
	public ResponseAction asyncButtonLink(int id) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format("Async link. ID: %s, Name: %s", id, req.getBodyParam("name")));
		});
	}
	
	@Action(path="b-post", methods=HttpMethod.POST)
	public ResponseAction syncButtonPost(int id) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format(
				"Sync link. ID: %s, Name: %s. PostParam: %s",
				id, req.getBodyParam("name"), req.getBodyParam("postParam")
			));
		});
	}
	
	/****/
	
	/**
	 * Demonstrate grid control - page sizes and paging buttons
	 * @return http://localhost:8080/examples-grid/grid/control
	 */
	@Action(path="control")
	public ResponseAction gridControl() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.setPagesSizes(Arrays.asList(7, 14, 28, 35, 42), 14);
			grid.setPagesButtonCount(3);
			
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}

	
	/**
	 * Demonstrate grid load - add more results
	 * @return http://localhost:8080/examples-grid/grid/load
	 */
	@Action(path="load")
	public ResponseAction gridLoad() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.setPagesSizes(Arrays.asList(7, 14, 28, 35, 42), 14);
			grid.setPagesButtonCount(0);
			grid.setUseLoadButton(true);
			
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/****/
	
	/**
	 * Grid without control
	 * @return http://localhost:8080/examples-grid/grid/empty
	 */
	@Action(path="empty")
	public ResponseAction empty() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.setPagesSizes(null);
			grid.setPagesButtonCount(0);
			
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/****/
	
	/**
	 * Shows usage of custom cell renderer
	 * @return http://localhost:8080/examples-grid/grid/renderer
	 */
	@Action(path="renderer")
	public ResponseAction renderers() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.addColumn(new ValueColumn("id").setRenderer("onColumnRenderer"));
			grid.addColumn(new ValueColumn("text"));
			
			grid.setRowRenderer("rowRenderer");
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Shows behaviour of row selection
	 * @return http://localhost:8080/examples-grid/grid/selection
	 */
	@Action(path="selection")
	public ResponseAction selection() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			grid.useRowSelection(true);
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/****/
	
	/**
	 * Demonstrate group actions
	 * Row unique is 'id'
	 * @return http://localhost:8080/examples-grid/grid/actions
	 */
	@Action(path="actions")
	public ResponseAction actions() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.addAction(
				new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLink()))
				.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
				.setConfirmation("Really?")
			);
			grid.addAction(
				new GroupAction("Sync action", link.create(getClass(), c->c.syncActionLink()))
				.setAsync(false)
			);
			grid.addAction(
				new GroupAction("Post action", link.create(getClass(), c->c.postActionLink()))
				.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
				.setMethod("post")
			);
			
			grid.addColumn(new ActionsColumn("Group actions"));
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	@Action(path="a-async")
	public ResponseAction asyncActionLink() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format("Async action. IDS: " + req.getBodyParam("ids").getList()));
		});
	}
	
	@Action(path="a-sync")
	public ResponseAction syncActionLink() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format("Sync action. IDS: " + req.getBodyParam("ids").getList()));
		});
	}
	
	@Action(path="a-post", methods=HttpMethod.POST)
	public ResponseAction postActionLink() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText(String.format("Post action. IDS: " + req.getBodyParam("ids").getList()));
		});
	}
	
	/***/
	
	/**
	 * Grid actions with another row unique
	 * @return http://localhost:8080/examples-grid/grid/row-unique
	 */
	@Action(path="row-unique")
	public ResponseAction rowUnique() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
			
			grid.addAction(
				new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLinkText()))
				.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
			);
			
			grid.addColumn(new ActionsColumn("byText").setUniqueRowIdentifier("text"));
			grid.addColumn(new ActionsColumn("byId").setUniqueRowIdentifier("id"));
			grid.addColumn(new ValueColumn("id"));
			grid.addColumn(new ValueColumn("text"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	@Action(path="a-async-text")
	public ResponseAction asyncActionLinkText() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("Async action with string ids. IDS: " + req.getBodyParam("ids").getList());
		});
	}
	
	/****/
	
	/**
	 * Rows in grid with tree sorting
	 * @return http://localhost:8080/examples-grid/grid/tree-structure
	 */
	@Action(path="tree-structure")
	public ResponseAction treeStructure() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
		
	/****/
	
	/**
	 * Ways of filtering, EQUALS is default
	 * @return http://localhost:8080/examples-grid/grid/filtering
	 */
	@Action(path="filtering")
	public ResponseAction filtering() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.filteringData()), "get");
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/filtering-data
	 */
	@Action(path = "filtering-data")
	public ResponseAction filteringData() {
		return ResponseBuilder.get()
		.validate(
			GridOptions.getValidator(Arrays.asList(
				new GridColumn("text").setFilterMode(FilterMode.LIKE),
				new GridColumn("number").setFilterMode(FilterMode.STARTS_WITH),
				new GridColumn("range").setFilterMode(FilterMode.ENDS_WITH),
				new GridColumn("select_col").setFilterMode(FilterMode.EQUALS)
			))
		)
		.createRequest((req, translator, identity)->{
			try {
				return Response.OK().getJson(dao.getAll(req.getBodyParams(GridOptions.class)));
			} catch (Exception e) {
				logger.error("all filters", e);
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Error occur: " + e.getMessage());
			}
		});
	}
	
	/****/
	
	/**
	 * Shows sorting substitution
	 * @return http://localhost:8080/examples-grid/grid/subst
	 */
	@Action(path="subst")
	public ResponseAction sortingSubst() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.substitutionData()), "get");
			
			grid.addColumn(new ValueColumn("id").setTitle("ID"));
			grid.addColumn(
				new ValueColumn("number")
				.setTitle("Number")
				.setUseSorting(true)
			);
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/substitution-data
	 */
	@Action(path = "substitution-data")
	public ResponseAction substitutionData() {
		return ResponseBuilder.get()
		.validate(
			GridOptions.getValidator(Arrays.asList(
				new GridColumn("number").setSortingName("id")
			))
		)
		.createRequest((req, translator, identity)->{
			try {
				return Response.OK().getJson(dao.getAll(req.getBodyParams(GridOptions.class)));
			} catch (Exception e) {
				logger.error("all filters", e);
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Error occur: " + e.getMessage());
			}
		});
	}
	
	/******************/
	
	/**
	 * Usage of automatic refresh
	 * @return http://localhost:8080/examples-grid/grid/refresh
	 */
	@Action(path="refresh")
	public ResponseAction refresh() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.refreshRandomData()), "get");
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
							getClass(), c->c.syncButtonLink(0),
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Returns data for all filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/random-data
	 */
	@Action(path="random-data")
	public ResponseAction refreshRandomData() {
		return ResponseBuilder.get()
		.validate(
			GridOptions.getValidator(Arrays.asList())
		)
		.createRequest((req, translator, identity)->{
			List<Object> data = new LinkedList<>();
			Random random = new Random();
			for (int i = 0; i < 20; i++) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", i);
				row.put("text", "Row #" + i);
				row.put("number", random.nextInt());
				data.add(row);
			}
			return Response.OK().getJson(MapInit.create()
				.append("itemsCount", 20)
				.append("pageIndex", 1)
				.append("data", data)
			.toMap());
		});
	}
	
	/*****/
	
	/**
	 * Usage methods without GridDataSet and GridOptions
	 * @return http://localhost:8080/examples-grid/grid/raw
	 */
	@Action(path="raw")
	@Deprecated
	public ResponseAction raw() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.rawData()), "get");
			
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/raw-data
	 */
	@Action(path = "raw-data")
	@Deprecated
	public ResponseAction rawData() {
		return ResponseBuilder.get()
		.validate(
			new Validator(true)
			.addRule(ItemRules.numberRules("pageIndex", true, Integer.class))
			.addRule(ItemRules.numberRules("pageSize", true, Integer.class))
			.addRule(ItemRules.mapRules("filters", true, new Validator(true)
				.addRule(ItemRules.numberRules("id", false, Integer.class))
				.addRule(ItemRules.objectRules("text", false).setType(String.class))
			))
			.addRule(ItemRules.mapRules("sorting", true, new Validator(true)
				.addRule(ItemRules.objectRules("id", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
				.addRule(ItemRules.objectRules("text", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
			))
		)
		.createRequest((req, translator, identity)->{
			try {
				int pageIndex = req.getBodyParam("pageIndex").getInteger();
				int pageSize = req.getBodyParam("pageSize").getInteger();
			//	Map<String, Object> filters = req.getBodyParam("filtering").getMap();
			//	Map<String, Object> sorting = req.getBodyParam("sorting").getMap();
				
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
				return Response.OK().getJson(json);
			} catch (Exception e) {
				logger.error("all filters", e);
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Error occur: " + e.getMessage());
			}
		});
	}
	
	/*****/
	
	/**
	 * XSS check
	 * @return http://localhost:8080/examples-grid/grid/xss
	 */
	@Action(path="xss")
	public ResponseAction xss() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.xssData()), "get");
			
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
			return Response.OK().getTemplate("filters.jsp", params);
		});
	}
	
	@Action(path="xss-load")
	public ResponseAction xssLoad() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Map<String, Object> json = new HashMap<>();
			json.put("value", 1);
			json.put("title", "<script>alert('select');</script>");
			return Response.OK().getJson(Arrays.asList(json));
		});
	}
	
	/**
	 * Returns data for filters grid
	 * Called internally
	 * @return http://localhost:8080/examples-grid/grid/xss-data
	 */
	@Action(path = "xss-data")
	public ResponseAction xssData() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
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
				return Response.OK().getJson(json);
			} catch (Exception e) {
				logger.error("all filters", e);
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Error occur: " + e.getMessage());
			}
		});
	}
	
	/*************/
	
	/**
	 * Shows usage of custom template with all settings
	 * @return http://localhost:8080/examples-grid/grid/custom-template
	 */
	@Action(path="custom-template")
	public ResponseAction customTemplate() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Grid grid = new Grid(link.create(getClass(), c->c.allFiltersData()), "get");
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
						getClass(), c->c.asyncButtonLink(0),
						MapInit.create().append("name", "Grid button name").toMap(),
						"123"
					),
					"global-button"
				).setTitle("Global"))
				.addButton(
					Button.create(
						link.create(
							getClass(), c->c.syncButtonLink(0),
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
							getClass(), c->c.asyncButtonLink(0),
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
							getClass(), c->c.syncButtonPost(0),
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
				new GroupAction("Async action", link.create(getClass(), c->c.asyncActionLink()))
				.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
				.setConfirmation("Really?")
			);
			grid.addAction(
				new GroupAction("Sync action", link.create(getClass(), c->c.syncActionLink()))
				.setAsync(false)
			);
			grid.addAction(
				new GroupAction("Post action", link.create(getClass(), c->c.postActionLink()))
				.setOnFailure("actionOnFailure").setOnSuccess("actionOnSuccess")
				.setMethod("post")
			);
			
			
			grid.useRowSelection(true);
			
			Map<String, Object> params = new HashMap<>();
			params.put("grid", grid);
			return Response.OK().getTemplate("customTemplate.jsp", params);
		});
	}
	
	/*******************************/
	
	@Override
	public String getName() {
		return "examples-grid";
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
