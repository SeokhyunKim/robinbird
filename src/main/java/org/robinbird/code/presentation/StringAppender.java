package org.robinbird.code.presentation;

/**
 * Created by seokhyun on 6/23/17.
 */
public class StringAppender {
	private StringBuffer stringBuffer = new StringBuffer();

	public StringAppender append(String str) {
		stringBuffer.append(str);
		return this;
	}

	public StringAppender appendLine(String str) {
		stringBuffer.append(str + "\n");
		return this;
	}

	public StringAppender newLine() {
		stringBuffer.append("\n");
		return this;
	}

	public String toString() {
		return stringBuffer.toString();
	}
}
