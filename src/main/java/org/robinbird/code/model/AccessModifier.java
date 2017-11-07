package org.robinbird.code.model;

/**
 * Created by seokhyun on 5/31/17.
 */
public enum AccessModifier {
	PUBLIC("public"), PRIVATE("private"), PROTECTED("protected");

	private String description;

	AccessModifier(String description) {
		this.description = description;
	}

	public String getDescription() { return description; }

	public static AccessModifier fromDescription(String desc) throws IllegalArgumentException {
		return AccessModifier.valueOf(desc.toUpperCase());
	}
}
