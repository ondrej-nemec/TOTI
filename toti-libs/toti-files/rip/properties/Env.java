import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.functions.FileExtension;
import ji.common.structures.MapInit;

public class Env {

	
	public static void main(String[] args) {
		Map<String, Object> a = MapInit.create()
			.append("item", "b")
			.append("list", Arrays.asList("a","b"))
			.append("map", MapInit.create().append("a", "b").toMap())
			.toMap();

		Map<String, Object> b = MapInit.create()
			.append("item", "1")
			.append("list", Arrays.asList("1","2"))
			.append("map", MapInit.create().append("1", "2").toMap())
			.toMap();
		
		Map<String, Object> res = new HashMap<>();
		res.putAll(a);
		res.putAll(b);
		System.err.println(res);
	}
	
	private static void merge(Map<String, Object> main, Map<String, Object> toMerge) {
		toMerge.forEach((key, value)->{
			if (!main.containsKey(key)) {
				main.put(key, value);
			} else if (Map.class.isInstance(value) && Map.class.isInstance(main.get(key))) {
				merge(Map.class.cast(main.get(key)), Map.class.cast(value));
			} else if (List.class.isInstance(value) && List.class.isInstance(main.get(key))) {
				List.class.cast(main.get(key)).addAll(List.class.cast(value));
			} else {
				main.put(key, value);
			}
		});
	}
	
	private static void mergeValues(Object origin, Object newOne) {
		if (Map.class.isInstance(origin) && Map.class.isInstance(newOne)) {
			merge(Map.class.cast(origin), Map.class.cast(newOne));
		} else if (List.class.isInstance(origin) && List.class.isInstance(newOne)) {
			List.class.cast(origin).addAll(List.class.cast(newOne));
		} 
	}
	
	
	private static void print(Map<String, Object> main, String prefix) {
		main.forEach((key, value)->{
			if (Map.class.isInstance(value)) {
				System.out.println(prefix + key);
			} else if (List.class.isInstance(value)) {
				System.out.println(prefix + key);
				
			} else {
				System.out.println(prefix + key + ": " + value);
			}
		});
	}
}
