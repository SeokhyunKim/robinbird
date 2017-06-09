package org.robinbird.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.robinbird.utils.Msgs;

import static org.robinbird.utils.Msgs.Key.ALREADY_EXISTING_TYPE_NAME;

/**
 * Created by seokhyun on 6/4/17.
 */
@Data
public class AnalysisContext {

	private Repository<Type> types;
	private Repository<Class> classes;
	private Class currentClass;

	public AnalysisContext(Repository<Type> types, Repository<Class> classes) {
		this.types = types;
		this.classes = classes;
	}

	public Type getType(String name) {
		Type t = types.getRepositable(name);
		if (t != null ) { return t; }
		t = new Type(name);
		types.register(t);
		return t;
	}

	/*
	public Collection getCollection(String name, Type ctype) throws IllegalStateException {
		Type t = types.getRepositable(name);
		if (t != null && (t instanceof Collection)) { return (Collection)t; }
		if (t != null) { throw new IllegalArgumentException(Msgs.get(ALREADY_EXISTING_TYPE_NAME, name)); }
		Collection c = new Collection(name, ctype);
		types.register(c);
		return c;
	}

	public Map getMap(String name, Type key, Type value) throws IllegalStateException {
		Type t = types.getRepositable(name);
		if (t != null && (t instanceof Map)) { return (Map)t; }
		if (t != null) { throw new IllegalArgumentException(Msgs.get(ALREADY_EXISTING_TYPE_NAME, name)); }
		Map m = new Map(name, key, value);
		types.register(m);
		return m;
	}*/

	public Class getClass(String name, ClassType ctype) {
		Class c = classes.getRepositable(name);
		if (c != null ) {
			setCurrentClass(c);
			return c;
		}
		c = new Class(name, ctype);
		classes.register(c);
		setCurrentClass(c);
		return c;
	}

}
