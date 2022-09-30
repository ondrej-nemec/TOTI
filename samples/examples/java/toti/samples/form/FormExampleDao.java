package toti.samples.form;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FormExampleDao {
	
	private List<FormExampleEntity> allInputs;
	
	public FormExampleDao() {
		this.allInputs = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			allInputs.add(new FormExampleEntity(
				"Item #" + i,
				"Some long text " + i,
				"user_" + i + "@example.com",
				"#"
					+ Integer.toHexString(getNextInt(255, 0))
					+ Integer.toHexString(getNextInt(255, 0))
					+ Integer.toHexString(getNextInt(255, 0)),
				i%3==0,
				(i + i%7) * 0.1,
				"password", // high security :-D
				FormEnum.values()[i%4],
				(i + i%5) * 0.1,
				i%6,
				getRandomLocal(),
				getRandomLocal().toLocalDate(),
				getRandomLocal().toLocalTime(),
				getRandomLocal().toLocalDate().toString().substring(0, 7),
				"2022-W" + String.format("%02d", getRandomLocal().getDayOfMonth())
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
	
	public FormExampleEntity get(int i) {
		return allInputs.get(i);
	}
	
	public int update(Integer index, FormExampleEntity entity) {
		if (index == null) {
			allInputs.add(entity);
			return allInputs.size() - 1;
		}
		allInputs.set(index, entity);
		return index;
	}

}
