package org.robinbird.main.oldmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.main.oldoldrepository.Repository;
import org.robinbird.exception.ExistingTypeNameException;
import org.robinbird.util.Msgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.util.Msgs.Key.NULL_POINTER_ENCOUNTERED;

/**
 * Created by seokhyun on 6/4/17.
 */
@Slf4j
public class AnalysisContext {

	private Repository<Type> types;
	private Repository<Package> packages;
	private Stack<Class> currentClasses = new Stack<>();

	@Getter @Setter
	private Package currentPackage;

	@Getter @Setter
	private List<Pattern> terminalClassPatterns;

	@Getter @Setter
	private List<Pattern> excludedClassPatterns;

	private Map<Relation.Key, Relation> relationMap;

	@Getter @Setter
	private boolean isParsingEnum;

	public AnalysisContext() {
		this.types = new Repository<>();
		this.packages = new Repository<>();
	}

	public AnalysisContext(Repository<Type> types, Repository<Package> packages) {
		this.types = types;
		this.packages = packages;
	}

	public void pushCurrentClass(Class c) {
		if (c != null && currentPackage != null) {
			currentPackage.addClass(c);
			c.setClassPackage(currentPackage);
		}
		currentClasses.push(c);
	}

	public Class popCurrentClass() {
		if (currentClasses.empty()) {
			return null;
		}
		return currentClasses.pop();
	}

	public Class getCurrentClass() {
		if (currentClasses.empty()) {
			return null;
		}
		return currentClasses.peek();
	}

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

	public boolean isTerminal(String identifier) {
		if (terminalClassPatterns == null) { return false; }
		for (Pattern pattern : terminalClassPatterns) {
			if (pattern.matcher(identifier).matches()) {
				return true;
			}
		}
		return false;
	}

	public boolean isCurrentClassTerminal() {
		if (getCurrentClass() == null) { return false; }
		return isTerminal(getCurrentClass().getName());
	}

	public boolean isExcluded(String identifier) {
		if (excludedClassPatterns == null) { return false; }
		for (Pattern pattern : excludedClassPatterns) {
			if (pattern.matcher(identifier).matches()) {
				return true;
			}
		}
		return false;
	}

	public boolean isCurrentClassExcluded() {
		if (getCurrentClass() == null) { return false; }
		return isExcluded(getCurrentClass().getName());
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
		t = new Type(name, Type.Kind.REFERENCE);
		System.out.println("register type: " + name);
		types.register(t);
		return t;
	}

	public Class registerClass(String name, ClassType ctype) {
		log.debug("register class: {}, {}", name, ctype.toString());
		Class c = getClass(name, ctype);
		if (c != null) { return c; }
		if (types.isExisting(name)) {
			throw new ExistingTypeNameException(
				String.format("Type name %s is already registered with different Type. Failed to register %s as Class.", name, name));
		}

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
		log.info("Starting to analyse relations among classes...");
		relationMap = new HashMap<>();
		for (Class classObj : getClasses()) {
			log.debug("updating relations for " + classObj.getName());
			if (isTerminal(classObj.getName())) {
				log.debug("not update relations for terminal {}...", classObj.getName());
				continue;
			}
			for (Member m : classObj.getMemberVariables().values()) {
				log.debug("member " + m.getName() + "...");
				Type memberType = m.getType();
				Type associated = memberType;
				String cardinality = "1";
				if (associated instanceof Collection) {
					Collection c = (Collection)associated;
					associated = c.getAssociatedType();
					log.debug("associated is changed for collection: {}", associated.toString());
					cardinality = "*";
				}
				if (associated.isPrimitiveType()) {
					log.debug("associated type {} is primitive and skip to create relation.", associated.getName());
					continue;
				}
				if (isExcluded(associated.getName())) {
					log.debug("associated type {} is excluded type and skip to create relation.", associated.getName());
					continue;
				}
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

	public Package getPackage(String name) {
		return (Package)packages.getRepositable(name);
	}

	public List<Package> getPackages() {
		return packages.getRepositableList();
	}

	public Package registerPackage(List<String> packageNameList) {
		String packageName = Package.createPackageName(packageNameList);
		if (packages.getRepositable(packageName) != null) {
			return getPackage(packageName);
		}
		Package newPackage = new Package(packageName);
		return packages.register(newPackage);
	}

}
