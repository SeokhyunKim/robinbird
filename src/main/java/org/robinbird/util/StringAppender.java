package org.robinbird.util;

public class StringAppender {
	private StringBuffer stringBuffer = new StringBuffer();

	public StringAppender append(String str) {
		stringBuffer.append(str);
		return this;
	}

	public StringAppender repeatedAppend(String str, int numRpeated) {
		while (numRpeated-- > 0) {
			stringBuffer.append(str);
		}
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

	public boolean isEmpty() {
		return stringBuffer.length() <= 0;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public String toString() {
		return stringBuffer.toString();
	}
}
