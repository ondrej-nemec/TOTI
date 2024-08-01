package ji.querybuilder.builder_impl.share;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.structures.Tuple2;
import ji.querybuilder.Builder;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.Functions;
import ji.querybuilder.enums.Join;
import ji.querybuilder.enums.Where;
import ji.querybuilder.structures.Joining;
import ji.querybuilder.structures.SubSelect;

public abstract class SelectImpl<P> implements SingleExecute, ParametrizedSql, Builder {

	private final DbInstance instance;
	
	private final Map<String, String> parameters;

	private final List<String> selects;
	private Tuple2<SubSelect, String> from;
	private final List<Joining> joins;
	private final List<Tuple2<String, Where>> wheres;
	private final List<String> groupBy;
	private final List<String> having;
	private final List<String> orderBy;
	private Integer limit;
	private Integer offset;
	
	public SelectImpl(DbInstance instance) {
		this.instance = instance;
		this.parameters = new HashMap<>();
		
		this.selects = new LinkedList<>();
		this.joins = new LinkedList<>();
		this.wheres = new LinkedList<>();
		this.groupBy = new LinkedList<>();
		this.having = new LinkedList<>();
		this.orderBy = new LinkedList<>();
	}

	abstract protected P getThis();
	
	public List<String> getSelects() {
		return selects;
	}
	
	public Tuple2<SubSelect, String> getFrom() {
		return from;
	}
	
	public List<Joining> getJoins() {
		return joins;
	}
	
	public List<Tuple2<String, Where>> getWheres() {
		return wheres;
	}
	
	public List<String> getGroupBy() {
		return groupBy;
	}
	
	public List<String> getHaving() {
		return having;
	}
	
	public List<String> getOrderBy() {
		return orderBy;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public Integer getLimit() {
		return limit;
	}

	@Override
	public String createSql() {
		return parse(getSql(), parameters);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public P select(Function<Functions, String> select) {
		this.selects.add(select.apply(instance));
		return getThis();
	}
	
	public P _from(SubSelect select, String alias) {
		this.from = new Tuple2<>(select, alias);
		return getThis();
	}

	public P groupBy(String groupBy) {
		this.groupBy.add(groupBy);
		return getThis();
	}

	public P having(Function<Functions, String> having) {
		this.having.add(having.apply(instance));
		return getThis();
	}

	public P limit(int limit, int offset) {
		this.limit = limit;
		this.offset = offset;
		return getThis();
	}

	public P where(Function<Functions, String> where, Where join) {
		this.wheres.add(new Tuple2<>(where.apply(instance), join));
		return getThis();
	}

	public P _join(SubSelect builder, String alias, Join join, Function<Functions, String> on) {
		this.joins.add(new Joining(builder, alias, join, on.apply(instance)));
		return getThis();
	}

	public P orderBy(Function<Functions, String> orderBy) {
		this.orderBy.add(orderBy.apply(instance));
		return getThis();
	}

	public P addParameter(String name, Object value) {
		parameters.put(name, Escape.escape(value));
		return getThis();
	}


}
