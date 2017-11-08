package org.robinbird.common.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seokhyun on 5/31/17.
 */
@Slf4j
public class Repository<T extends Repositable> {
	private List<T> repositableList;
	private Map<String, Integer> repositableMap;

	public Repository() {
		this.repositableList = new ArrayList<T>();
		this.repositableMap = new HashMap<String, Integer>();
	}

	public void clean() {
		repositableMap.clear();
		repositableList.clear();
	}

	public T getRepositable(String name) {
		Integer idx = repositableMap.get(name);
		if (idx == null) { return null; }
		return repositableList.get(idx);
	}

	public T getRepositable(int idx) {
		if (idx<0 || idx>=repositableList.size()) { return null; }
		return repositableList.get(idx);
	}

	public List<T> getRepositableList() {
		return repositableList;
	}

	public int size() { return repositableList.size(); }

	public boolean isExisting(String name) {
		return (repositableMap.get(name) != null);
	}

	public T register(T newRepositable) {
		T r = getRepositable(newRepositable.getName());
		if (r != null) { return r; }
		newRepositable.setId(repositableList.size());
		log.debug("Registering to repository: " + newRepositable.getName() + " as " + newRepositable.getClass().getName());
		repositableList.add(newRepositable);
		repositableMap.put(newRepositable.getName(), newRepositable.getId());
		return newRepositable;
	}

}