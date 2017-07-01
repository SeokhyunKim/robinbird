package org.robinbird.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.robinbird.utils.Msgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.utils.Msgs.Key.ALREADY_EXISTING_TYPE_NAME;
import static org.robinbird.utils.Msgs.Key.NULL_POINTER_ENCOUNTERED;

/**
 * Created by seokhyun on 6/4/17.
 */
public class AnalysisContext {

	private Repository<Type> types;
	private Class currentClass;

	private Map<Relation.Key, Relation> relationMap;

	public AnalysisContext(Repository<Type> types) {
		this.types = types;
	}

	public Class getCurrentClass() { return currentClass; }
	public void setCurrentClass(Class c) { currentClass = c; }

	public Type getType(String name) {
		return types.getRepositable(name);
	}

	public Class getClass(String name) {
		Type t = getType(name);
		if (t == null || !(t instanceof Class)) {
			return null;
		}
		return (Class)t;
	}

	public Class getClass(String name, ClassType ctype) {
		Class c = getClass(name);
		if (c == null) { return null; }
		if (c.getClassType() != ctype) { return null; }
		return c;
	}

	public Type registerType(String name) {
		Type t = getType(name);
		if (t != null) { return t; }
		t = new Type(name);
		types.register(t);
		return t;
	}

	public Class registerClass(String name, ClassType ctype) {
		Class c = getClass(name, ctype);
		if (c != null) { return c; }
		c = new Class(name, ctype);
		types.register(c);
		return c;
	}

	public List<Type> getTypes() {
		return types.getRepositableList();
	}

	public List<Class> getClasses() {
		List<Class> classes = new ArrayList<>();
		for (Type t : getTypes()) {
			if (t instanceof Class) {
				classes.add((Class)t);
			}
		}
		return classes;
	}

	public Set<Relation> getRelations() {
		checkState(relationMap != null, Msgs.get(NULL_POINTER_ENCOUNTERED, "AnalysisContext"));
		return new HashSet<>(relationMap.values());
	}

	public void update() {
		relationMap = new HashMap<>();
		for (Class classObj : getClasses()) {
			for (Member m : classObj.getMemberVariables().values()) {
				Type memberType = m.getType();
				Type associated = memberType;
				String cardinality = "1";
				if (associated instanceof Collection) {
					Collection c = (Collection)associated;
					checkState(c.getTypes().size() > 0);
					associated = c.getTypes().get(c.getTypes().size()-1);
					cardinality = "*";
				}
				if (associated.isPrimitiveType()) { continue; }
				Relation.Key k = Relation.createKey(classObj, associated);
				Relation r = relationMap.get(k);
				if (r == null) {
					r = Relation.create(classObj, associated);
					relationMap.put(k, r);
				}
				if (r.getFirst().getName().equals(classObj.getName())) {
					r.setSecondCardinality(cardinality);
				} else {
					r.setFirstCardinality(cardinality);
				}
			}
		}

	}

}
