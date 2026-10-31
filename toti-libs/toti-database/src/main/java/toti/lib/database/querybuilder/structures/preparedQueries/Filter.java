package toti.lib.database.querybuilder.structures.preparedQueries;

import java.time.temporal.TemporalAccessor;
import java.util.Collection;

import toti.lib.database.querybuilder.Functions;

public abstract class Filter {

	public static Filter LIKE(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("LIKE", name, value) {
			@Override public String create(Functions functions) {
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " LIKE "
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
			@Override
			public Object getValue() {
				return "%" + value + "%";
			}
		};
	}

	public static Filter NOT_LIKE(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("NOT_LIKE", name, value) {
			@Override public String create(Functions functions) {
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " NOT LIKE "
				+ compare(functions,":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
			@Override
			public Object getValue() {
				return "%" + value + "%";
			}
		};
	}

	public static Filter EQUALS(String name, Object value) {
		return _EQUALS(name, value, false, false);
	}

	public static Filter EQUALS(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return _EQUALS(name, value, isCaseInsensitive, isIgnoreDiactritics);
	}

	private static Filter _EQUALS(String name, Object value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("EQUALS", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					return name + " IS NULL";
				}
				return compare(functions, name, isCaseInsensitive, isIgnoreDiactritics)
				+ " = "
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
		};
	}

	public static Filter NOT_EQUALS(String name, Object value) {
		return _NOT_EQUALS(name, value, false, false);
	}

	public static Filter NOT_EQUALS(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return _NOT_EQUALS(name, value, isCaseInsensitive, isIgnoreDiactritics);
	}

	private static Filter _NOT_EQUALS(String name, Object value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("NOT_EQUALS", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					return name + " IS NOT NULL";
				}
				return compare(functions, name, isCaseInsensitive, isIgnoreDiactritics)
				+ " != "
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
		};
	}

	public static Filter STARTS_WITH(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("STARTS_WITH", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " LIKE "
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
			@Override
			public Object getValue() {
				return value + "%";
			}
		};
	}

	public static Filter ENDS_WITH(String name, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("ENDS_WITH", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " LIKE "
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics);
			}
			@Override
			public Object getValue() {
				return "%" + value;
			}
		};
	}

	public static Filter MORE_OR_EQUALS(String name, Number value) {
		return _MORE_OR_EQUALS(name, value);
	}

	public static Filter MORE_OR_EQUALS(String name, TemporalAccessor value) {
		return _MORE_OR_EQUALS(name, value);
	}

	private static Filter _MORE_OR_EQUALS(String name, Object value) {
		return new Filter("MORE_OR_EQUALS", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return name + " >= :" + name + "Value";
			}
		};
	}

	public static Filter MORE(String name, Number value) {
		return _MORE(name, value);
	}

	public static Filter MORE(String name, TemporalAccessor value) {
		return _MORE(name, value);
	}

	private static Filter _MORE(String name, Object value) {
		return new Filter("MORE", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return name + " > :" + name + "Value";
			}
		};
	}

	public static Filter LESS_OR_EQUALS(String name, Number value) {
		return _LESS_OR_EQUALS(name, value);
	}

	public static Filter LESS_OR_EQUALS(String name, TemporalAccessor value) {
		return _LESS_OR_EQUALS(name, value);
	}

	private static Filter _LESS_OR_EQUALS(String name, Object value) {
		return new Filter("LESS_OR_EQUALS", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return name + " <= :" + name + "Value";
			}
		};
	}
	
	public static Filter LESS(String name, Number value) {
		return _LESS(name, value);
	}
	
	public static Filter LESS(String name, TemporalAccessor value) {
		return _LESS(name, value);
	}

	private static Filter _LESS(String name, Object value) {
		return new Filter("LESS", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
				}
				return name + " < :" + name + "Value";
			}
		};
	}

	public static Filter IN(String name, Collection<Object> value) {
		return IN(name, value, false, false);
	}

	public static Filter IN(String name, Collection<Object> value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("IN", name, value) {
			@Override public String create(Functions functions) {
				if (value == null || value.isEmpty()) {
					return "1=2"; // never true condition
				}
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " IN ("
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics)
				+ ")";
			}
		};
	}

	public static Filter NOT_IN(String name, Collection<Object> value) {
		return NOT_IN(name, value, false, false);
	}

	public static Filter NOT_IN(String name, Collection<Object> value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		return new Filter("NOT_IN", name, value) {
			@Override public String create(Functions functions) {
				if (value == null) {
					throw new RuntimeException("Value for '" + name + "' cannot be null");
					//return name + " IS NOT NULL";
				}
				if (value.isEmpty()) {
					return "1=1"; // always true condition
				}
				return compare(functions, functions.concat(":empty", name), isCaseInsensitive, isIgnoreDiactritics)
				+ " NOT IN ("
				+ compare(functions, ":" + name + "Value", isCaseInsensitive, isIgnoreDiactritics)
				+ ")";
			}
		};
	}

	private static String compare(Functions functions, String value, boolean isCaseInsensitive, boolean isIgnoreDiactritics) {
		// mysql: SELECT * FROM tabulka WHERE sloupec COLLATE utf8mb4_0900_ai_ci = 'cafe';
		// sqlserver: SELECT * FROM tabulka WHERE sloupec COLLATE Latin1_General_CI_AI = 'Pilis';
		// postgres: CREATE EXTENSION IF NOT EXISTS unaccent; | SELECT unaccent('Příliš žluťoučký kůň');

		// TODO use sql func. class
		// TODO use sql func for collate latin
		// WHERE Name COLLATE Latin1_general_CI_AI Like '%cafe%' COLLATE Latin1_general_CI_AI
		// latin1_general_cs
		/*if (filter.isCI() && filter.isIgnoreDiacritics()) {
			 return String.format("unacce", value);
		}
		if (filter.isIgnoreDiacritics()) {
		   //   result = String.format("unaccent(%s)", result);
			 return String.format("%s COLLATE latin1_general_cs", value);
		}*/

		if (isCaseInsensitive) {
			return functions.lower(value);
		}
		return value;

	}

	private final String name;
	private final String mode;
	private final Object value;

	private Filter(String mode, String name, Object value) {
		this.mode = mode;
		this.name = name;
		this.value = value;
	}

	public abstract String create(Functions functions);

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Filter [mode=" + mode + ", name=" + name + ", value=" + value + "]";
	}

}
/*
public record Filter(String name, FilterMode mode, Object value, boolean isCI, boolean isIgnoreDiactritics) {

	@Override
	public String toString() {
		return "Filter [mode=" + mode + ", name=" + name + ", value=" + value + "]";
	}

}
*/