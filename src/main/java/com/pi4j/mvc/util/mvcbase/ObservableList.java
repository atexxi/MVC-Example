package com.pi4j.mvc.util.mvcbase;

import java.util.*;

/**
 * A basic implementation of the Observable-Pattern.
 * <p>
 * Be prepared to enhance this according to your requirements.
 */
public final class ObservableList<V> {
	// all these listeners will get notified whenever the value changes
	private final Set<ValueChangeListener<V>> listeners = new HashSet<>();

	private volatile List<V> value;

	public ObservableList(List<V> initialValue) {
		value = initialValue;
	}

	/**
	 * Registers a new observer (aka 'listener')
	 *
	 * @param listener
	 * 		specifies what needs to be done whenever the value is changed
	 */
	public void onChange(ValueChangeListener<V> listener) {
		listeners.add(listener);
		listener.update(value, value);  // listener is notified immediately
	}

	/**
	 * That's the core functionality of an 'ObservableValue'.
	 * <p>
	 * Every time the value changes, all the listeners will be notified.
	 * <p>
	 * This is method is 'package private', only 'ControllerBase' is allowed to set a new value.
	 * <p>
	 * For the UIs setValue is not accessible
	 *
	 * @param newValue
	 * 		the new value
	 */
	void setValue(List<V> newValue) {
		if (Objects.equals(value, newValue)) {  // no notification if value hasn't changed
			return;
		}
		List<V> oldValue = value;
		value = newValue;

		listeners.forEach(listener -> {
			if (value.equals(
					newValue)) { // pre-ordered listeners might have changed this and thus the callback no longer applies
				listener.update(oldValue, newValue);
			}
		});
	}

	void append(V newValue) {
		List<V> oldValue = value;
		List<V> newList = new ArrayList<>(oldValue);
		newList.add(newValue);

		listeners.forEach(listener -> {
			if (value.equals(
					newValue)) { // pre-ordered listeners might have changed this and thus the callback no longer applies
				listener.update(oldValue, newList);
			}
		});
	}

	/**
	 * It's ok to make this public.
	 *
	 * @return the value managed by this ObservableValue
	 */
	public List<V> getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@FunctionalInterface
	public interface ValueChangeListener<V> {
		void update(List<V> oldValue, List<V> newValue);
	}
}
