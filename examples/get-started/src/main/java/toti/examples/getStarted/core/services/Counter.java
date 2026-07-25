package toti.examples.getStarted.core.services;

public class Counter {

	private int count;

	public int getCount() {
		return count;
	}

	public void increase() {
		count = count + 1;
		if (count == Integer.MAX_VALUE) {
			count = 0;
		}
	}

}
