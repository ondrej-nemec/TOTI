package controllers.other;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersonDao implements EntityDao<Person> {

	private final static Map<Integer, Person> DATA = new HashMap<>();
	{
		if (DATA.isEmpty()) {
			for (int i = 0; i < 50; i++) {
				DATA.put(i, new Person(
						i, // id: hidden | no filter
						"Name #" + i, // name: text | text
						(i%10)*20, // age: number | number
						i%3 == 0, // maried: checkbox | select
						"2020-02-12 12:21", // born: datetime | datetime
						"name." + i + "@example.com",  // email: email
						"1234", // password: password
						Person.Sex.valueOf((i%2 == 0 ? "male" : "female").toUpperCase()), // sex: radiolist
						i%10 // department: select
				));
			}
		}
	}
	
	@Override
	public List<Map<String, Object>> getData(int pageIndex, int pageSize, Map<String, Object> filters, Map<String, Object> sorting) {
		List<Map<String, Object>> data = new LinkedList<>(DATA.values()).subList(
				Math.min(DATA.size() - 1, (pageIndex-1)*pageSize+1), 
				Math.min(DATA.size(), pageIndex*pageSize+1)
		).stream().map(item->item.toParams()).collect(Collectors.toList());
		return data;
	}

	@Override
	public Person get(int id) {
		return DATA.get(id);
	}

	@Override
	public void delete(int id) {
		DATA.remove(id);
	}

	@Override
	public void update(int id, Person entity) {
		DATA.put(id, entity);
	}

	@Override
	public int insert(Person entity) {
		int id = new LinkedList<>(DATA.keySet()).getLast() + 1;
		DATA.put(id, entity.withId(id));
		return id;
	}

	@Override
	public List<Person> getAll() {
		return DATA.values().stream().collect(Collectors.toList());
	}

}
