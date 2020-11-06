package controllers.other;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Person implements Entity {

	enum Sex { MALE, FEMALE }
	
	private final Integer id;
	private final String name;
	private final int age;
	private final boolean maried;
	private final String bordDate;
	private final String email;
	private final String password;
	private final Sex sex;
	private final int departmentId;
	
	public Person(Properties prop) {
		this(
			prop.getProperty("id").isEmpty() ? null : Integer.parseInt(prop.getProperty("id")),
			prop.getProperty("name"),
			Integer.parseInt(prop.getProperty("age")),
			Boolean.parseBoolean(prop.getProperty("maried")),
			prop.getProperty("born"), 
			prop.getProperty("email"), 
			prop.getProperty("password"),
			Sex.valueOf(prop.getProperty("sex").toUpperCase()),
			Integer.parseInt(prop.getProperty("department"))
		);
	}

	@Override
	public Map<String, Object> toParams() {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("name", name);
		params.put("age", age);
		params.put("maried", maried);
		params.put("born", bordDate);
		
		params.put("email", email);
		params.put("password", password);
		params.put("sex", sex.toString().toLowerCase());
		params.put("department", departmentId);
		return params;
	}
	
	public Person(
			Integer id, String name, int age, boolean maried,
			String bordDate, String email, String password,
			Sex sex, int departmentId) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.maried = maried;
		this.bordDate = bordDate;
		this.email = email;
		this.password = password;
		this.sex = sex;
		this.departmentId = departmentId;
	}
	
	public Person withId(int id) {
		return new Person(id, name, age, maried, bordDate, email, password, sex, departmentId);
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public boolean isMaried() {
		return maried;
	}

	public String getBordDate() {
		return bordDate;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public Sex getSex() {
		return sex;
	}

	public int getDepartmentId() {
		return departmentId;
	}
	
	@Override
	public String toString() {
		return toParams().toString();
	}

}
