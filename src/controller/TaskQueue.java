package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class TaskQueue<T extends Comparable<T>> implements Queue<T> {
	private final ArrayList<T> tasks;
	private int work;

	public TaskQueue() {
		tasks = new ArrayList<T>();
		work = 0;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) {
			if (!tasks.add(t))
				return false;
		}
		return true;
	}

	@Override
	public void clear() {
		tasks.clear();
		work = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		T t = (T) o;
		for (T t1 : tasks) {
			if (t1.equals(t))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object object : c) {
			if (!this.contains(object))
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return tasks.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return tasks.iterator();
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = tasks.remove(o);
		if (remove)
			work -= getCost(o);
		this.arrange();
		return remove;
	}

	public int getProspectiveCost(Object o) {
		return 1; // TODO: Implement prospective cost function.
	}

	public int getCost(Object o) {
		return 1; // TODO: Implement cost function
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object object : c) {
			if (!this.remove(object))
				return false;
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		for (Object object : c) {
			if (!this.contains(object)) {
				if (!this.remove(object))
					return false;
			}
		}
		return true;
	}

	@Override
	public int size() {
		return tasks.size();
	}

	@Override
	public Object[] toArray() {
		return tasks.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return tasks.toArray(a);
	}

	@Override
	public boolean add(T e) {
		boolean b = tasks.add(e);
		this.arrange();
		return b;
	}

	private void arrange() {
		// TODO implement arrange method

	}

	@Override
	public T element() {
		if (tasks.isEmpty()) {
			NoSuchElementException nsee = new NoSuchElementException(
					"TaskQueue is empty.");
			nsee.setStackTrace(Thread.currentThread().getStackTrace());
			throw nsee;
		}
		return tasks.get(0);
	}

	@Override
	public boolean offer(T e) {
		// do not use this.
		return false;
	}

	@Override
	public T peek() {
		if (!tasks.isEmpty())
			return tasks.get(0);
		return null;
	}

	@Override
	public T poll() {
		if (!tasks.isEmpty()) {
			return tasks.remove(0);
		}
		return null;
	}

	@Override
	public T remove() {
		T e = tasks.remove(0);
		work -= this.getCost(e);
		return e;
	}

	public int getWork() {
		return work;
	}

	public class Task implements Comparable<Task> {

		private final Integer floor;
		private final Integer fromDirection;

		public Task(Integer floor, Integer fromDirection) {
			this.floor = floor;
			this.fromDirection = fromDirection;
		}

		@Override
		public int compareTo(Task t) {
			return floor - t.getFloor();
		}

		public Integer getFloor() {
			return floor;
		}

		public Integer getFromDirection() {
			return fromDirection;
		}

	}
}
