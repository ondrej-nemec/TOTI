package samples.examples.grid;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ObjectBuilder;
import toti.application.GridDataSet;
import toti.application.GridOptions;
import toti.application.GridRange;

public class GridExampleDao {

	private final List<GridExampleEntity> data = new LinkedList<>();
	
	public GridExampleDao() {
		for (int i = 0; i < 100; i++) {
			data.add(new GridExampleEntity(
				i,
				"Item #" + i,
				getNextInt(100, -100) / 10.0, 
				getNextInt(20, 0)*5,
				i%3 == 2 ? null : (i%3 == 1),
				getRandomLocal(), 
				getRandomLocal().toLocalDate(),
				getRandomLocal().toLocalTime(),
				getRandomLocal().toLocalDate().toString().substring(0, 7), 
				"2022-W" + String.format("%02d", getRandomLocal().getDayOfMonth()%7)
			));
		}
	}
	
	private LocalDateTime getRandomLocal() {
		return LocalDateTime.of(
			2022, getNextInt(12, 1), getNextInt(28, 1), 
			getNextInt(24, 0), getNextInt(60, 0), getNextInt(60, 0)
		);
	}
	
	private int getNextInt(int max, int min) {
		return new Random().nextInt(max - min) + min;
	}
	
	public GridDataSet<GridExampleEntity> getAll(GridOptions options, Collection<Object> forOwners) throws SQLException {
		List<GridExampleEntity> aux = data.stream().filter((e)->{
			ObjectBuilder<Boolean> result = new ObjectBuilder<>(true);
			Map<String, Object> map = e.toMap();
			options.getFilters().forEach((name, filter)->{
				if (!result.get()) {
					return;
				}
				switch (filter.getMode()) {
					case ENDS_WITH:
						result.set(map.get(name).toString().endsWith(filter.getValue().toString()));
						break;
					case LIKE:
						result.set(map.get(name).toString().contains(filter.getValue().toString()));
						break;
					case STARTS_WITH:
						result.set(map.get(name).toString().startsWith(filter.getValue().toString()));
						break;
					case EQUALS:
					default: result.set(filter.getValue().equals(map.get(name))); break;
				}
			});
			return result.get();
		}).collect(Collectors.toList());
		Collections.sort(aux, (item1, item2)->{
			Map<String, Object> v1 = item1.toMap();
			Map<String, Object> v2 = item2.toMap();
			
			ObjectBuilder<Integer> index = new ObjectBuilder<>((int)Math.pow(10, options.getSorting().size()));
			ObjectBuilder<Integer> compared = new ObjectBuilder<>(0);
			options.getSorting().forEach((name, sort)->{
				compared.set(compared.get() + (compare(v1.get(name), v2.get(name)) * index.get()));
				index.set(index.get()/10);
			});
			return compared.get();
		});
		int size = aux.size();
		GridRange range = GridRange.create(aux.size(), options.getPageIndex(), options.getPageSize());
		if (range.isPresent()) {
			aux = aux.subList(range.getOffset(), range.getOffset() + range.getLimit());
		}
		return new GridDataSet<>(aux, size, range.getPageIndex());
	}
	
	private int compare(Object v1, Object v2) {
		DictionaryValue w1 = new DictionaryValue(v1);
		DictionaryValue w2 = new DictionaryValue(v2);
		if (v1.getClass().isAssignableFrom(Boolean.class) || v2.getClass().isAssignableFrom(Boolean.class) ) {
			return w1.getBoolean().compareTo(w2.getBoolean());
		}
		if (v1.getClass().isAssignableFrom(Number.class) && v2.getClass().isAssignableFrom(Number.class)) {
			return w1.getDouble().compareTo(w2.getDouble());
		}
		return w1.toString().compareTo(w2.toString());
	}
	
}
