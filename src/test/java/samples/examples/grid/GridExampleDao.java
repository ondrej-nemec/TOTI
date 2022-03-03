package samples.examples.grid;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
			return e.equals(options.getFilters());
		}).collect(Collectors.toList());
		int size = aux.size();
		GridRange range = GridRange.create(aux.size(), options.getPageIndex(), options.getPageSize());
		if (range.isPresent()) {
			aux = aux.subList(range.getOffset(), range.getOffset() + range.getLimit());
		}
		return new GridDataSet<>(aux, size, range.getPageIndex());
	}
	
}
