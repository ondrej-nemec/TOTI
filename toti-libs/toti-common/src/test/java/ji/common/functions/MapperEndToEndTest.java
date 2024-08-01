package ji.common.functions;

import java.util.HashMap;
import java.util.Map;

import ji.common.annotations.MapperIgnored;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;

public class MapperEndToEndTest {
	
	@MapperParameter({@MapperType(value="user_id", key="KeyA"), @MapperType(value="userId", key="KeyB")})
//	@MapperParameter({@MapperType(value="user_id")})
	private int id;
	@MapperParameter({@MapperType(value="userName"), @MapperType(value="Name", key="KeyA")})
	private String name;
	@MapperIgnored({"KeyA"})
//	@MapperIgnored()
	private int age;
	
	@Override
	public String toString() {
		return "MapperEndToEndTest [id=" + id + ", name=" + name + ", age=" + age + "]";
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Mapper.get().serialize(new MapperEndToEndTest(), "KeyA"));
		System.out.println(Mapper.get().serialize(new MapperEndToEndTest(), "KeyB"));
		System.out.println(Mapper.get().serialize(new MapperEndToEndTest(), "KeyC"));
		System.out.println(Mapper.get().serialize(new MapperEndToEndTest()));
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("user_id", 42);
		map.put("userId", 24);
		map.put("id", 1);
		map.put("name", "Name #42");
		map.put("Name", "Name #24");
		map.put("userName", "Name #1");
		System.out.println(Mapper.get().parse(MapperEndToEndTest.class, map, "KeyA"));
		System.out.println(Mapper.get().parse(MapperEndToEndTest.class, map, "KeyB"));
		System.out.println(Mapper.get().parse(MapperEndToEndTest.class, map, "KeyC"));
		System.out.println(Mapper.get().parse(MapperEndToEndTest.class, map));
	}

}
