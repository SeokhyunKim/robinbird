package org.robinbird.code.model;

import org.robinbird.common.model.Repositable;
import org.robinbird.common.utils.Msgs;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.common.utils.Msgs.Key.LIST_FOR_PACKAGE_NAME_IS_EMPTY;

/**
 * Created by seokhyun on 7/23/17.
 */
public class Package extends Repositable {

	private static String DELIMITER = ".";

	private List<Class> classList = new ArrayList<>();

	public Package(String name) {
		super(name);
	}

	public Package(List<String> packageName) {
		super(Package.createPackageName(packageName));
	}

	public void addClass(Class c) {
		classList.add(c);
	}

	public List<Class> getClassList() { return classList; }

	public String toString() {
		return "Package: " + this.getName();
	}

	static String createPackageName(List<String> packageNameList) {
		checkState(packageNameList.size()>0, Msgs.get(LIST_FOR_PACKAGE_NAME_IS_EMPTY));
		StringBuffer sb = new StringBuffer();
		sb.append(packageNameList.get(0));
		for (int i=1; i<packageNameList.size(); ++i) {
			sb.append(DELIMITER).append(packageNameList.get(i));
		}
		return sb.toString();
	}
}
